package it.uniroma2.controller;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;

import it.uniroma2.factory.ReleasesFactory;
import it.uniroma2.model.GenericPair;
import it.uniroma2.model.ReleaseMeta;
import it.uniroma2.utils.JiraUtils;

public class CollectReleasesData {

    LinkedHashMap<ReleaseMeta, Boolean> affectedReleasesMap; // Ordered map by date of the releases

    public CollectReleasesData(String projKey) throws ParseException, JSONException, IOException {

        LinkedHashMap<ReleaseMeta, Boolean> releasesAffectedMap = new LinkedHashMap<>();
        GenericPair<JSONArray, Integer> res = JiraUtils.queryReleases(projKey);
        Map<Date, String> releasesMap = ReleasesFactory.getInstance().orderedReleasesByDate(res.getFirst(),
                res.getSecond());

        // Ordered releases by date associated to their bugginess: set false by default
        int i = 1;
        for (Map.Entry<Date, String> entry : releasesMap.entrySet()) {
            releasesAffectedMap.put(new ReleaseMeta(i, entry.getValue(), entry.getKey()), false);
            i++;
        }

        this.affectedReleasesMap = releasesAffectedMap;
    }

    public List<ReleaseMeta> getReleasesList() {
        return new ArrayList<>(affectedReleasesMap.keySet());
    }

}
