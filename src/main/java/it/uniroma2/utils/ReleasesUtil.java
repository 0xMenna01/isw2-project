package it.uniroma2.utils;

import java.util.Date;
import java.util.List;

import it.uniroma2.model.ReleaseMeta;

public class ReleasesUtil {
    
    private ReleasesUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static ReleaseMeta getReleaseByName(String name, List<ReleaseMeta> releasesList) {
        for (ReleaseMeta release : releasesList) {
            if (release.getName().equals(name)) {
                return release;
            }
        }
        return null;
    }

    // This method assumes that the releases are ordered by date
    public static ReleaseMeta getReleaseByDate(Date date, List<ReleaseMeta> releasesList) {
        for (ReleaseMeta release : releasesList) {
            if (release.getDate().after(date)) {
                return release;
            }
        }
        return null;
    }
}
