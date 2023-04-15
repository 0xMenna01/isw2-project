package it.uniroma2.controller;

import it.uniroma2.enums.ProjectKey;
import it.uniroma2.utils.ProportionUtils;

public class ColdStart {

    private final ProjectKey key;
    private Double proportion;

    public ColdStart(ProjectKey key) {
        this.key = key;
        this.proportion = null;
    }

    public void start() throws Exception {
        CollectReleasesData controlData = new CollectReleasesData(this.key);
        CollectIssues issuesControl = new CollectIssues();
        
        issuesControl.retrieveIssues(this.key, controlData.getReleasesList());
        this.proportion = ProportionUtils.computeProportion(issuesControl.getIssues());

    }

    public double getProportion() {
        return proportion;
    }

}
