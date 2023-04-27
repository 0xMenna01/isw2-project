package it.uniroma2.controller.issues;

import it.uniroma2.controller.CollectReleasesData;
import it.uniroma2.enums.ProjectKey;
import it.uniroma2.exception.PropException;
import it.uniroma2.utils.ProportionUtils;

public class ColdStart {

    private final ProjectKey key;
    private Double prop;

    public ColdStart(ProjectKey key) {
        this.key = key;
        this.prop = null;
    }

    public void start() throws Exception {
        CollectReleasesData controlData = new CollectReleasesData(this.key);
        CollectIssues issuesControl = new CollectIssues();

        issuesControl.retrieveIssues(this.key, controlData.getReleasesList());
        this.prop = ProportionUtils.computeProportion(issuesControl.getIssues());

    }

    public double getProportion() throws PropException {
        if (prop == null)
            throw new PropException("Must start the cold start first");
        return prop;
    }

}
