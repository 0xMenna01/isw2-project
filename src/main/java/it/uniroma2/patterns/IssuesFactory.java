package it.uniroma2.patterns;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import it.uniroma2.model.JSONTicket;
import it.uniroma2.model.ReleaseMeta;
import it.uniroma2.utils.ReleasesUtil;

public class IssuesFactory {

    private static IssuesFactory instance = null;

    private IssuesFactory() {}

    public static IssuesFactory getInstance() {
        if (instance == null) {
            instance = new IssuesFactory();
        }
        return instance;
    }

    public JSONTicket createIssue(int i, JSONArray issues, List<ReleaseMeta> releasesList) throws ParseException {
        JSONTicket ticket = null;

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
            ReleaseMeta release = ReleasesUtil.getReleaseByName(name, releasesList);
            if (release != null) {
                av.add(release);
            }
        }

        ReleaseMeta openVersion = ReleasesUtil.getReleaseByDate(creationDate, releasesList);
        ReleaseMeta fixVersion = ReleasesUtil.getReleaseByDate(resolutionDate, releasesList);

        if (openVersion != null && fixVersion != null) {
            ticket = new JSONTicket(key, creationDate, resolutionDate, av);
        }

        return ticket;

    }
}
