package it.uniroma2.controller;

import it.uniroma2.controller.issues.CollectIssues;
import it.uniroma2.enums.ProjectKey;
import it.uniroma2.exception.ProjectNameException;
import it.uniroma2.writer.ReportWriter;

public class ExecuteDataCollection {

    private final ProjectKey projKey;
    private final String repoUrl;

    public ExecuteDataCollection(String projName, String repoUrl) throws ProjectNameException {

        this.projKey = ProjectKey.fromString(projName);
        this.repoUrl = repoUrl;
    }

    public void collectData() throws Exception {
        ReportWriter reportWriter = new ReportWriter(this.projKey.toString());

        // Collecting releases ordered by date
        CollectReleasesData releasesControl = null;

        releasesControl = new CollectReleasesData(this.projKey);

        // Collecting git data
        CollectGitInfo gitControl = null;

        gitControl = new CollectGitInfo(repoUrl, releasesControl.getReleasesList(),
            this.projKey.toString());
        gitControl.computeRelClassesCommits(reportWriter);

        // Writing releases
        reportWriter.writeReleases(gitControl.getReleases().getMeta());

        // Collecting issues
        CollectIssues issuesControl = new CollectIssues(reportWriter);

        issuesControl.retrieveIssues(this.projKey, gitControl.getReleases().getMeta());

        // Writing issues
        reportWriter.writeIssues(issuesControl.getIssues());

        // Compute measurement of classes metrics
        new ComputeMetrics(gitControl.getReleases(), issuesControl.getIssues(), gitControl.getRepo()).compute();

        // For labeling classes depends on weather we are considering training or testing set
        // As dataset validation we are using the walk forward approach
        // First we label the training set and then the testing set
        WalkForward.execute(gitControl.getReleases(), issuesControl.getIssues(), projKey.toString());

        // Compute evaluation through Weka
        CollectWeka wekaControl = new CollectWeka(projKey, gitControl.getNumOfRel() / 2);
        wekaControl.execute();

        // Closing created outputs files
        reportWriter.closeFiles();
    }
}
