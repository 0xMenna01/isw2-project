package it.uniroma2.controller;

import java.util.List;

import it.uniroma2.model.ReleaseMeta;

public class ExecuteDataCollection {

    private final String projKey;
    
    
    public ExecuteDataCollection(String projName){
        this.projKey = projName.toUpperCase();
    }

    public void collectData() {
        // Collecting releases ordered by date
        CollectReleasesData releasesControl = null;
        try {
            releasesControl = new CollectReleasesData(this.projKey);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        List<ReleaseMeta> releasesList = releasesControl.getReleasesList();
        //Collecting issues
        CollectIssues issuesControl = new CollectIssues();
        try {
            issuesControl.retrieveIssues(this.projKey, releasesList);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
            

    }


}
