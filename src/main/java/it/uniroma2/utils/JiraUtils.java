package it.uniroma2.utils;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import it.uniroma2.model.GenericPair;

public class JiraUtils {

    private JiraUtils() {
        throw new IllegalStateException("Utility class");
    }

    // Returns a pair of the json array and the total number of releases
    public static GenericPair<JSONArray, Integer> queryReleases(String projKey) throws JSONException, IOException{
        
        String url = "https://issues.apache.org/jira/rest/api/latest/project/" + projKey + "/version";
        JSONObject json = JSONUtil.readJsonFromUrl(url);
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

        JSONObject json = JSONUtil.readJsonFromUrl(url);
        JSONArray jsonIssues = JSONUtil.readJsonFromUrl(url).getJSONArray("issues");
        Integer totalTickets = json.getInt("total");

        return new GenericPair<>(jsonIssues, totalTickets);
    }
}