package it.uniroma2.controller;

import it.uniroma2.controller.issues.CollectIssues;
import it.uniroma2.enums.ProjectKey;
import it.uniroma2.exception.ProjectNameException;
import it.uniroma2.utils.CsvWriter;
import it.uniroma2.utils.ReportWriter;

public class ExecuteDataCollection {

    private final ProjectKey projKey;
    private final String repoUrl;

    public ExecuteDataCollection(String projName, String repoUrl) throws ProjectNameException {

        this.projKey = ProjectKey.fromString(projName);
        this.repoUrl = repoUrl;
    }

    public void collectData() throws Exception {

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

        gitControl = new CollectGitInfo(repoUrl, releasesControl.getReleasesList(), issuesControl.getIssues(),
                this.projKey.toString());
        gitControl.computeRelClassesCommits();
        gitControl.labelClasses();

        // Compute Measurment of classes metrics
        new ComputeMetrics(gitControl.getReleases(), issuesControl.getIssues(), gitControl.getRepo()).compute();

        // Writing data to CSV file
        CsvWriter.writeCsv(projKey.toString(), gitControl.getReleases());
        // Closing created temp files (delete this later on)
        ReportWriter.closeFiles();
    }

}
