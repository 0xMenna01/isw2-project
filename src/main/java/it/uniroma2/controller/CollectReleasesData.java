package it.uniroma2.controller;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import it.uniroma2.model.ReleaseMeta;
import it.uniroma2.utils.JSONUtil;

public class CollectReleasesData {

    LinkedHashMap<ReleaseMeta, Boolean> affectedReleasesMap; //Ordered map by date of releases

    public CollectReleasesData(String projKey) throws JSONException, IOException, ParseException {
        Map<Date, String> orderedReleasesMap = new TreeMap<>();
        LinkedHashMap<ReleaseMeta, Boolean> releasesAffectedMap = new LinkedHashMap<>();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

        String url = "https://issues.apache.org/jira/rest/api/latest/project/" + projKey + "/version";
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

                    orderedReleasesMap.put(releaseDate, releaseName);

                } catch (JSONException e) {
                    // No release date: go on

                }
            }
        }
        
        int i = 1;
        for (Map.Entry<Date, String> entry : orderedReleasesMap.entrySet()) {
            releasesAffectedMap.put(new ReleaseMeta(i, entry.getValue(), entry.getKey()), false);
            i++;
        }

        this.affectedReleasesMap = releasesAffectedMap;
    }
    
    public List<ReleaseMeta> getReleasesList() {
        return new ArrayList<>(affectedReleasesMap.keySet());
    }

}
