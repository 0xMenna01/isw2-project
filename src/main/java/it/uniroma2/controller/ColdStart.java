package it.uniroma2.controller;

import java.util.List;

import it.uniroma2.enums.ProjectKey;
import it.uniroma2.model.TicketIssue;

public class ColdStart {

    private final ProjectKey key;
    private List<TicketIssue> issues;

    public ColdStart(ProjectKey key) {
        this.key = key;
        this.issues = null;
    }

    public void start() throws Exception {
        CollectReleasesData controlData = new CollectReleasesData(this.key);
        CollectIssues issuesControl = new CollectIssues();
        
        issuesControl.retrieveIssues(this.key, controlData.getReleasesList());
        this.issues = issuesControl.getIssues();

    }

    public List<TicketIssue> getIssues() throws Exception {
        if(issues == null) throw new Exception("Must start the cold start first");
        return issues;
    }

}
