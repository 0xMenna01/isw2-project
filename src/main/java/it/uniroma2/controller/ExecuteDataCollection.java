package it.uniroma2.controller;

import it.uniroma2.enums.ProjectKey;
import it.uniroma2.exception.ProjectNameException;

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
        // Collecting releases ordered by date
        CollectReleasesData releasesControl = null;
        try {
            releasesControl = new CollectReleasesData(this.projKey);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // Collecting issues
        CollectIssues issuesControl = new CollectIssues();
        System.out.println("---- RETRIEVING ALL RELEASES AND ISSUES ----");
        try {
            issuesControl.retrieveIssues(this.projKey, releasesControl.getReleasesList());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // Setting the affected releases
        releasesControl.setAffectedReleases(issuesControl.getIssues());

        try {
            CollectGitInfo gitControl = new CollectGitInfo(repoUrl, releasesControl.getReleasesList(),
                    issuesControl.getIssues());

            gitControl.computeRelClassesCommits();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}
