package it.uniroma2.controller;

import it.uniroma2.enums.ProjectKey;
import it.uniroma2.exception.ProjectNameException;
import it.uniroma2.model.AffectedReleases;
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

        // Setting the affected releases
        AffectedReleases affRel = new AffectedReleases();
        affRel.set(issuesControl.getIssues());

        System.out.println("RETRIEVING GIT DATA...");
        // Collecting git data
        try {
            CollectGitInfo gitControl = new CollectGitInfo(repoUrl, affRel, issuesControl.getIssues());
            gitControl.computeRelClassesCommits();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Closing created temp files (delete this later on)
        MainView.closeFiles();
    }

}
