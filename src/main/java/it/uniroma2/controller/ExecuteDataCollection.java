package it.uniroma2.controller;

import java.io.IOException;
import java.text.ParseException;
import java.util.concurrent.ExecutionException;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.json.JSONException;

import it.uniroma2.controller.issues.CollectIssues;
import it.uniroma2.enums.ProjectKey;
import it.uniroma2.exception.GitException;
import it.uniroma2.exception.ParallelColdStartException;
import it.uniroma2.exception.ProjectNameException;
import it.uniroma2.exception.PropException;
import it.uniroma2.exception.ReleaseException;
import it.uniroma2.exception.TicketException;
import it.uniroma2.utils.ReportWriter;

public class ExecuteDataCollection {

    private final ProjectKey projKey;
    private final String repoUrl;

    public ExecuteDataCollection(String projName, String repoUrl) throws ProjectNameException {

        this.projKey = ProjectKey.fromString(projName);
        this.repoUrl = repoUrl;
    }

    public void collectData() throws JSONException, ParseException, IOException, InterruptedException,
            ExecutionException, ParallelColdStartException, TicketException, ReleaseException, PropException,
            GitAPIException, GitException {

        // Collecting releases ordered by date
        CollectReleasesData releasesControl = null;

        releasesControl = new CollectReleasesData(this.projKey);

        // Printing releases
        ReportWriter.writeReleases(releasesControl.getReleasesList());

        // Collecting issues
        CollectIssues issuesControl = new CollectIssues();

        issuesControl.retrieveIssues(this.projKey, releasesControl.getReleasesList());

        // Printing issues

        ReportWriter.writeIssues(issuesControl.getIssues());

        // Collecting git data
        CollectGitInfo gitControl = null;

        gitControl = new CollectGitInfo(repoUrl, releasesControl.getReleasesList(),
                this.projKey.toString());
        gitControl.computeRelClassesCommits();

        // Compute Measurment of classes metrics
        new ComputeMetrics(gitControl.getReleases(), issuesControl.getIssues(), gitControl.getRepo()).compute();

        // For labling classes it depends weather we are considering training or testing
        // set
        // As dataset validation we are using the walkforward approach
        // First we label the training set and then the testing set
        WalkForward.execute(gitControl.getReleases(), issuesControl.getIssues(), projKey.toString());

        // Closing created outputs files
        ReportWriter.closeFiles();
    }

}
