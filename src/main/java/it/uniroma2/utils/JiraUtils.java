package it.uniroma2.utils;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import it.uniroma2.model.GenericPair;

public class JiraUtils {
    
    private static Integer MAX_RES = 100;

    private JiraUtils() {
        throw new IllegalStateException("Utility class");
    }

    // Returns a pair of the json array and the total number of releases
    public static GenericPair<JSONArray, Integer> queryReleases(String projKey) throws JSONException, IOException {

        String url = "https://issues.apache.org/jira/rest/api/latest/project/" + projKey + "/version?maxResults="
                + MAX_RES.toString();
        JSONObject json = JSONUtils.readJsonFromUrl(url);
        JSONArray releases = json.getJSONArray("values");
        Integer total = json.getInt("total");

        return new GenericPair<>(releases, total);
    }

    /*
     * The query in Jira is:
     * project = <projKey> AND issuetype = Bug AND (status = Closed OR status =
     * Resolved) AND resolution = Fixed
     */
    // Method returns the values json array and the total number of tickets as a
    // pair
    public static GenericPair<JSONArray, Integer> queryTickets(String projKey, Integer computedTickets,
            Integer tempMaxTickets) throws JSONException, IOException {

        String url = "https://issues.apache.org/jira/rest/api/2/search?jql=project=%22"
                + projKey + "%22AND%22issueType%22=%22Bug%22AND(%22status%22=%22closed%22OR"
                + "%22status%22=%22resolved%22)AND%22resolution%22=%22fixed%22&fields=key,resolutiondate,versions,created&startAt="
                + computedTickets.toString() + "&maxResults=" + tempMaxTickets.toString();

        JSONObject json = JSONUtils.readJsonFromUrl(url);
        Integer totalTickets = json.getInt("total");
        // The issues are in the inverted order
        JSONArray jsonIssues = JSONUtils.readJsonFromUrl(url).getJSONArray("issues");

        // Invert the order
        JSONArray orderedIssues = new JSONArray();
        for (int i = jsonIssues.length() - 1; i >= 0; i--) {
            Object elem = jsonIssues.get(i);
            orderedIssues.put(elem);
        }

        return new GenericPair<>(orderedIssues, totalTickets);
    }

    public static void orderTicketsByFixDate(JSONArray jsonIssues){
        List<JSONObject> list = new ArrayList<>();
        for (int i = 0; i < jsonIssues.length(); i++) {
            list.add(jsonIssues.getJSONObject(i));
        }
        Collections.sort(list, new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject o1, JSONObject o2) {
                try {
                    String date1String = o1.getJSONObject("fields").getString("resolutiondate");
                    String date2String = o2.getJSONObject("fields").getString("resolutiondate");
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
                    Date date1 = format.parse(date1String);
                    Date date2 = format.parse(date2String);
                    return date1.compareTo(date2);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        for (int i = 0; i < jsonIssues.length(); i++) {
            jsonIssues.put(i, list.get(i));
        }
        
    }
}