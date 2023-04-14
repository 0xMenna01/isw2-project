package it.uniroma2.controller;

import java.io.IOException;
import java.text.ParseException;

import org.json.JSONException;

import it.uniroma2.enums.ProjectKey;
import it.uniroma2.exception.ProjectNameException;
import it.uniroma2.exception.TicketException;
import it.uniroma2.utils.ProportionUtils;

public class ColdStart {

    private final ProjectKey key;
    private Float proportion;

    public ColdStart(String key) throws ProjectNameException {
        this.key = ProjectKey.fromString(key);
        this.proportion = null;
    }

    public void start() throws JSONException, ParseException, IOException, TicketException {
        CollectReleasesData controlData = new CollectReleasesData(this.key);
        CollectIssues issuesControl = new CollectIssues();

        issuesControl.retrieveIssues(this.key, controlData.getReleasesList());
        this.proportion = ProportionUtils.computeProportion(issuesControl.getIssues());

    }

    public Float getProportion() {
        return proportion;
    }

}
