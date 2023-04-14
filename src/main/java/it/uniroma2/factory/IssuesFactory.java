package it.uniroma2.factory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import it.uniroma2.model.BaseTicket;
import it.uniroma2.model.ReleaseMeta;
import it.uniroma2.utils.ReleasesUtils;

public class IssuesFactory {

    private static IssuesFactory instance = null;

    private IssuesFactory() {
    }

    public static IssuesFactory getInstance() {
        if (instance == null) {
            instance = new IssuesFactory();
        }
        return instance;
    }

    public BaseTicket createIssue(int i, JSONArray issues, List<ReleaseMeta> releasesList) throws ParseException {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

        String key = issues.getJSONObject(i).getString("key");
        JSONObject fields = issues.getJSONObject(i).getJSONObject("fields");
        String resolutionDateStr = fields.getString("resolutiondate");
        String creationDateStr = fields.getString("created");
        JSONArray listAV = fields.getJSONArray("versions");

        Date creationDate = format.parse(creationDateStr);
        Date resolutionDate = format.parse(resolutionDateStr);

        List<ReleaseMeta> av = new ArrayList<>();
        for (int j = 0; j < listAV.length(); j++) {
            String name = listAV.getJSONObject(j).getString("name");
            ReleaseMeta release = ReleasesUtils.getReleaseByName(name, releasesList);
            if (release != null) {
                av.add(release);
            }
        }

        ReleaseMeta openVersion = ReleasesUtils.getReleaseByDate(creationDate, releasesList);
        ReleaseMeta fixVersion = ReleasesUtils.getReleaseByDate(resolutionDate, releasesList);

        return new BaseTicket(key, openVersion, fixVersion, av);

    }
}
