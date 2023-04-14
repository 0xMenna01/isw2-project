package it.uniroma2.controller;

import it.uniroma2.enums.ProjectKey;
import it.uniroma2.exception.ProjectNameException;

public class ExecuteDataCollection {

    private final ProjectKey projKey;

    public ExecuteDataCollection(String projName) {
        try {
            this.projKey = ProjectKey.fromString(projName);
        } catch (ProjectNameException e) {
            throw new RuntimeException(e);
        }
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
        try {
            issuesControl.retrieveIssues(this.projKey, releasesControl.getReleasesList());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}
