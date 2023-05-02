package it.uniroma2.controller.issues;

import java.io.IOException;
import java.text.ParseException;
import java.util.concurrent.ExecutionException;

import org.json.JSONException;

import it.uniroma2.controller.CollectReleasesData;
import it.uniroma2.enums.ProjectKey;
import it.uniroma2.exception.EnumException;
import it.uniroma2.exception.PropException;
import it.uniroma2.exception.ReleaseException;
import it.uniroma2.exception.TicketException;
import it.uniroma2.utils.ProportionUtils;

public class ColdStart {

    private final ProjectKey key;
    private Double prop;

    public ColdStart(ProjectKey key) {
        this.key = key;
        this.prop = null;
    }

    public void start() throws JSONException, ParseException, IOException, TicketException, InterruptedException, ExecutionException, EnumException, ReleaseException, PropException {
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
