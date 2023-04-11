package it.uniroma2.controller;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import it.uniroma2.model.JiraTicket;
import it.uniroma2.model.ReleaseMeta;
import it.uniroma2.utils.JSONUtil;

public class CollectJiraData {

    private final String projKey;

    public CollectJiraData(String projName) {
        this.projKey = projName.toUpperCase();
    }

    public List<ReleaseMeta> retrieveReleasesMeta() throws JSONException, IOException, ParseException {
        
        Map<Date, String> releasesMap = new HashMap<>();
        List<ReleaseMeta> releasesList = new ArrayList<>();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        String url = "https://issues.apache.org/jira/rest/api/latest/project/" + this.projKey + "/version";
        JSONObject json = JSONUtil.readJsonFromUrl(url);
        JSONArray releases = json.getJSONArray("values");
        int total = json.getInt("total");
        
        JSONObject releaseJson = null;
        for (int i = 0; i < total; i++) {
            releaseJson = releases.getJSONObject(i);
            if (releaseJson.get("released").toString().equals("true")) {

                try {
                    String releaseDateString = releaseJson.get("releaseDate").toString();
                    Date releaseDate = formatter.parse(releaseDateString);
                    String releaseName = releaseJson.get("name").toString();

                    releasesMap.put(releaseDate, releaseName);

                } catch (JSONException e) {
                    // No release date: go on

                }
            }
        }
        // Sort releases by date
        Map<Date, String> orderedReleasesMap = new TreeMap<>(releasesMap); 

        int i = 1;
        for (Map.Entry<Date, String> entry : orderedReleasesMap.entrySet()) {
            releasesList.add(new ReleaseMeta(i, entry.getValue(), entry.getKey()));
            i++;
        }

        return releasesList;
    }


    public static List<JiraTicket> retrieveTicketIssues(List<ReleaseMeta> releasesList){
        //TODO
        return null;
    }

}
