package it.uniroma2.controller;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;

import it.uniroma2.enums.ProjectKey;
import it.uniroma2.factory.ReleasesFactory;
import it.uniroma2.model.GenericPair;
import it.uniroma2.model.ReleaseMeta;
import it.uniroma2.utils.JiraUtils;

public class CollectReleasesData {

    private List<ReleaseMeta> releases; // Ordered map by date of the releases

    public CollectReleasesData(ProjectKey key) throws ParseException, JSONException, IOException {

        List<ReleaseMeta> rels = new ArrayList<>();
        GenericPair<JSONArray, Integer> res = JiraUtils.queryReleases(key.toString());
        Map<Date, String> releasesMap = ReleasesFactory.getInstance().orderedReleasesByDate(res.getFirst(),
                res.getSecond());

        // Ordered releases by date associated to their bugginess: set false by default
        int i = 1;
        for (Map.Entry<Date, String> entry : releasesMap.entrySet()) {
            rels.add(new ReleaseMeta(i, entry.getValue(), entry.getKey()));
            i++;
        }

        this.releases = rels;
    }

    public List<ReleaseMeta> getReleasesList() {
        return releases;

    }
}
