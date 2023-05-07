package it.uniroma2.factory;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

public class ReleasesFactory {

    private static ReleasesFactory instance = null;

    private ReleasesFactory() {
    }

    public static ReleasesFactory getInstance() {
        if (instance == null) {
            instance = new ReleasesFactory();
        }
        return instance;
    }

    // Returns an ordered by date map of releases' dates to names
    public Map<Date, String> orderedReleasesByDate(JSONArray jsonReleases, int total)
        throws ParseException {

        Map<Date, String> orderedReleasesMap = new TreeMap<>();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        JSONObject releaseJson = null;

        for (int i = 0; i < total; i++) {
            releaseJson = jsonReleases.getJSONObject(i);
            if (releaseJson.has("releaseDate") && releaseJson.has("name")) {

                String releaseDateString = releaseJson.get("releaseDate").toString();
                Date releaseDate = formatter.parse(releaseDateString);
                String releaseName = releaseJson.get("name").toString();

                orderedReleasesMap.put(releaseDate, releaseName);

            }
        }

        return orderedReleasesMap;
    }

}
