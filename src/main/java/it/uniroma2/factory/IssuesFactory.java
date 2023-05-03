package it.uniroma2.factory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import it.uniroma2.model.TicketIssue;
import it.uniroma2.model.releases.ReleaseMeta;
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

    public TicketIssue createIssue(int i, JSONArray issues, List<ReleaseMeta> releasesList) throws ParseException {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        String key = issues.getJSONObject(i).get("key").toString();
        JSONObject fields = issues.getJSONObject(i).getJSONObject("fields");
        String resolutionDateStr = fields.get("resolutiondate").toString();
        String creationDateStr = fields.get("created").toString();
        JSONArray listAV = fields.getJSONArray("versions");

        Date creationDate = format.parse(creationDateStr);
        Date resolutionDate = format.parse(resolutionDateStr);

        List<ReleaseMeta> av = new ArrayList<>();
        for (int j = 0; j < listAV.length(); j++) {
            String name = listAV.getJSONObject(j).get("name").toString();
            ReleaseMeta release = ReleasesUtils.getReleaseByName(name, releasesList);
            if (release != null) {
                av.add(release);
            }
        }

        ReleaseMeta openVersion = ReleasesUtils.getReleaseByDate(creationDate, releasesList);
        ReleaseMeta fixVersion = ReleasesUtils.getReleaseByDate(resolutionDate, releasesList);

        return new TicketIssue(key, openVersion, fixVersion, av);

    }
}
