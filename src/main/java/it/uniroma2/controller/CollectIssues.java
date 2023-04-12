package it.uniroma2.controller;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import it.uniroma2.model.JSONTicket;
import it.uniroma2.model.ReleaseMeta;
import it.uniroma2.model.TicketIssue;
import it.uniroma2.patterns.IssuesFactory;
import it.uniroma2.utils.JSONUtil;

public class CollectIssues {

    List<TicketIssue> issues;

    public CollectIssues() {
        this.issues = new ArrayList<>();
    }

    public void retrieveIssues(String projKey, List<ReleaseMeta> releasesList) throws JSONException, IOException, ParseException {

        List<TicketIssue> issuesList = new ArrayList<>();

        Integer computedTickets = 0;
        Integer tempMaxTickets = 0;
        int totalTickets = 0;

        // Only gets a maximum of 500 tickets at a time => multiple times if bugs > 500
        do {
            tempMaxTickets = computedTickets + 500;

            String url = "https://issues.apache.org/jira/rest/api/2/search?jql=project=%22"
                    + projKey + "%22AND%22issueType%22=%22Bug%22AND(%22status%22=%22closed%22OR"
                    + "%22status%22=%22resolved%22)AND%22resolution%22=%22fixed%22&fields=key,resolutiondate,versions,created&startAt="
                    + computedTickets.toString() + "&maxResults=" + tempMaxTickets.toString();

            JSONObject json = JSONUtil.readJsonFromUrl(url);
            JSONArray jsonIssues = JSONUtil.readJsonFromUrl(url).getJSONArray("issues");
            totalTickets = json.getInt("total");

            while (computedTickets < totalTickets && computedTickets < tempMaxTickets) {
                int i = computedTickets % 500; // index of the issue in the json array
                JSONTicket jTicket = IssuesFactory.getInstance().createIssue(i, jsonIssues, releasesList);

                computedTickets++;
            }

        } while (computedTickets < totalTickets);

        this.issues = issuesList;
    }

}
