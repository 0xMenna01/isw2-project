package it.uniroma2.controller.issues;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;

import it.uniroma2.enums.ColdStartState;
import it.uniroma2.enums.ProjectKey;
import it.uniroma2.exception.ParallelColdStartException;
import it.uniroma2.exception.PropException;
import it.uniroma2.exception.ReleaseException;
import it.uniroma2.exception.TicketException;
import it.uniroma2.factory.IssuesFactory;
import it.uniroma2.model.GenericPair;
import it.uniroma2.model.ReleaseMeta;
import it.uniroma2.model.TicketIssue;
import it.uniroma2.utils.JiraUtils;
import it.uniroma2.utils.ReleasesUtils;

public class CollectIssues {

    List<TicketIssue> issues;
    ColdStartState state; // State that represents if the system is in the cold-start mode

    public CollectIssues() {
        this.issues = new ArrayList<>();
        this.state = ColdStartState.INACTIVE;
    }

    public void retrieveIssues(ProjectKey key, List<ReleaseMeta> releasesList) throws JSONException, IOException, ParseException, InterruptedException, ExecutionException, ParallelColdStartException, TicketException, ReleaseException, PropException {

        this.state = key.getColdStartState();

        Integer computedTickets = 0;
        Integer tempMaxTickets = 0;
        Integer totalTickets = 0;

        // Only gets a maximum of 500 tickets at a time => multiple times if bugs > 500
        do {
            tempMaxTickets = computedTickets + 500;

            GenericPair<JSONArray, Integer> res = JiraUtils.queryTickets(key.toString(), computedTickets,
                    tempMaxTickets);

            totalTickets = res.getSecond();
            JiraUtils.orderTicketsByFixDate(res.getFirst());

            while (computedTickets < totalTickets && computedTickets < tempMaxTickets) {
                int i = computedTickets % 500; // index of the issue in the json array
                TicketIssue tmpTicket = IssuesFactory.getInstance().createIssue(i, res.getFirst(), releasesList);

                ReleaseMeta iv = null;
                if (tmpTicket.isValid(releasesList.get(0))) {

                    if (tmpTicket.hasValidIV())
                        iv = tmpTicket.getIV();

                    else if (this.state != ColdStartState.EXECUTING) {
                        // Enters Proportion
                        Proportion.getInstance(tmpTicket.getOv(), tmpTicket.getFv()).compute(issues);
                        // Retrieving the id of the iv
                        iv = ReleasesUtils.getReleaseById(Proportion.getInstance().getIdIV(),
                                releasesList);
                    }
                }
                if (iv != null)
                    this.issues.add(new TicketIssue(
                            tmpTicket.getKey(), tmpTicket.getOv(), tmpTicket.getFv(),
                            ReleasesUtils.getAVs(iv, tmpTicket.getFv(), releasesList)));

                computedTickets++;
            }

        } while (computedTickets < totalTickets);

    }

    public List<TicketIssue> getIssues() {
        return issues;
    }

}
