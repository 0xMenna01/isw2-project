package it.uniroma2.controller;

import it.uniroma2.controller.issues.CollectIssues;
import it.uniroma2.enums.ProjectKey;
import it.uniroma2.exception.ProjectNameException;
import it.uniroma2.utils.CsvWriter;
import it.uniroma2.view.MainView;

public class ExecuteDataCollection {

    private final ProjectKey projKey;
    private final String repoUrl;

    public ExecuteDataCollection(String projName, String repoUrl) {
        try {
            this.projKey = ProjectKey.fromString(projName);
        } catch (ProjectNameException e) {
            throw new RuntimeException(e);
        }
        this.repoUrl = repoUrl;
    }

    public void collectData() {
        System.out.println("RETRIEVING RELEASES...");
        // Collecting releases ordered by date
        CollectReleasesData releasesControl = null;
        try {
            releasesControl = new CollectReleasesData(this.projKey);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // Printing releases
        MainView.printReleases(releasesControl.getReleasesList());

        System.out.println("RETRIEVING ISSUES...");
        // Collecting issues
        CollectIssues issuesControl = new CollectIssues();
        try {
            issuesControl.retrieveIssues(this.projKey, releasesControl.getReleasesList());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Printing issues
        try {
            MainView.printIssues(issuesControl.getIssues());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        System.out.println("RETRIEVING GIT DATA...");
        // Collecting git data
        CollectGitInfo gitControl = null;
        try {
            gitControl = new CollectGitInfo(repoUrl, releasesControl.getReleasesList(), issuesControl.getIssues(), this.projKey.toString());
            gitControl.computeRelClassesCommits();
            gitControl.labelClasses();

            // Compute Measurment of classes metrics
            new ComputeMetrics(gitControl.getReleases(), issuesControl.getIssues(), gitControl.getRepo()).compute();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Writing data to CSV file
        CsvWriter.writeCsv(projKey.toString(), gitControl.getReleases());
        // Closing created temp files (delete this later on)
        MainView.closeFiles();
    }

}
