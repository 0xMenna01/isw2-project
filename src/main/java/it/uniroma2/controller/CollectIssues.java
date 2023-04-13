package it.uniroma2.controller;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import it.uniroma2.exception.TicketException;
import it.uniroma2.factory.IssuesFactory;
import it.uniroma2.model.BaseTicket;
import it.uniroma2.model.GenericPair;
import it.uniroma2.model.ReleaseMeta;
import it.uniroma2.model.TicketIssue;
import it.uniroma2.utils.JiraUtils;
import it.uniroma2.utils.ReleasesUtils;

public class CollectIssues {

    List<TicketIssue> issues;

    public CollectIssues() {
        this.issues = new ArrayList<>();
    }

    public void retrieveIssues(String projKey, List<ReleaseMeta> releasesList)
            throws JSONException, IOException, ParseException, TicketException {

        Integer computedTickets = 0;
        Integer tempMaxTickets = 0;
        Integer totalTickets = 0;

        // Only gets a maximum of 500 tickets at a time => multiple times if bugs > 500
        do {
            tempMaxTickets = computedTickets + 500;

            GenericPair<JSONArray, Integer> res = JiraUtils.queryTickets(projKey, computedTickets, tempMaxTickets);
            totalTickets = res.getSecond();

            while (computedTickets < totalTickets && computedTickets < tempMaxTickets) {
                int i = computedTickets % 500; // index of the issue in the json array
                BaseTicket ticket = IssuesFactory.getInstance().createIssue(i, res.getFirst(), releasesList);

                if (ticket.isValid()) {

                    if (ticket.hasValidIV()) {
                        ReleaseMeta iv = ticket.getIV();
                        this.issues.add(new TicketIssue(projKey, ticket.getOv(), ticket.getFv(),
                                ReleasesUtils.getAVs(iv, ticket.getFv(), releasesList), iv));
                    } else {
                        //TODO
                    }
                }

                computedTickets++;
            }

        } while (computedTickets < totalTickets);

    }

}
