package it.uniroma2.controller.issues;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import it.uniroma2.enums.ColdStartState;
import it.uniroma2.enums.ProjectKey;
import it.uniroma2.exception.EnumException;
import it.uniroma2.exception.PropException;
import it.uniroma2.exception.ReleaseException;
import it.uniroma2.exception.TicketException;
import it.uniroma2.factory.IssuesFactory;
import it.uniroma2.model.GenericPair;
import it.uniroma2.model.TicketIssue;
import it.uniroma2.model.releases.ReleaseMeta;
import it.uniroma2.utils.JiraUtils;
import it.uniroma2.utils.ReleasesUtils;
import it.uniroma2.writer.ReportWriter;

public class CollectIssues {

    private List<TicketIssue> issues;
    private ColdStartState state; // State that represents if the system is in the cold-start mode

    private ReportWriter reportWriter;

    public CollectIssues(ReportWriter reportWriter) {
        this.issues = new ArrayList<>();
        this.state = ColdStartState.INACTIVE;
        this.reportWriter = reportWriter;
    }

    public CollectIssues() {
        this.issues = new ArrayList<>();
        this.state = ColdStartState.INACTIVE;
    }

    public void retrieveIssues(ProjectKey key, List<ReleaseMeta> releasesList)
        throws JSONException, IOException, ParseException, InterruptedException, ExecutionException, EnumException,
        TicketException, ReleaseException, PropException {

        this.state = key.getColdStartState();

        Integer computedTickets = 0;
        Integer tempMaxTickets = 0;
        Integer totalTickets = 0;

        JSONArray jsonIssues = new JSONArray();

        // Only gets a maximum of 500 tickets at a time => multiple times if bugs > 500
        do {

            tempMaxTickets = computedTickets + 1000;

            GenericPair<JSONArray, Integer> res = JiraUtils.queryTickets(key.toString(), computedTickets,
                tempMaxTickets);

            totalTickets = res.getSecond();

            while (computedTickets < totalTickets && computedTickets < tempMaxTickets) {
                int i = computedTickets % 1000; // index of the issue in the json
                Object elem = res.getFirst().get(i);
                jsonIssues.put(elem);

                computedTickets++;
            }

        } while (computedTickets < totalTickets);

        JiraUtils.orderTicketsByFixDate(jsonIssues);

        for (int j = 0; j < jsonIssues.length(); j++) {

            TicketIssue tmpTicket = IssuesFactory.getInstance().createIssue(j, jsonIssues, releasesList);

            ReleaseMeta iv = null;

            // The following variable represents weather the ticket has been computed through cold start
            boolean isColdStartTicket = false;
            if (tmpTicket.isValid(releasesList.get(0))) {

                if (tmpTicket.hasValidIV())
                    iv = tmpTicket.getIV();

                else if (this.state != ColdStartState.EXECUTING) {
                    // Enters Proportion
                    Proportion.getInstance(tmpTicket.getOv(), tmpTicket.getFv()).compute(reportWriter, issues);
                    // Retrieving the id of the iv
                    iv = ReleasesUtils.getReleaseById(Proportion.getInstance().getIdIV(),
                        releasesList);
                    isColdStartTicket = Proportion.getInstance().isColdStart();
                }
            }
            if (iv != null)
                this.issues.add(new TicketIssue(
                    tmpTicket.getKey(), tmpTicket.getOv(), tmpTicket.getFv(),
                    ReleasesUtils.getAVs(iv, tmpTicket.getFv(), releasesList), isColdStartTicket));
        }


    }

    public List<TicketIssue> getIssues() {
        return issues;
    }

}
