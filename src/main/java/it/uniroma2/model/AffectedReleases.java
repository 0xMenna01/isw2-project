package it.uniroma2.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class AffectedReleases {
    private LinkedHashMap<ReleaseMeta, Boolean> affectedReleasesMap;

    public AffectedReleases() {
        this.affectedReleasesMap = new LinkedHashMap<>();
    }

    public void put(ReleaseMeta rel, Boolean bool) {
        affectedReleasesMap.put(rel, bool);

    }

    public Boolean get(ReleaseMeta rel) {
        return affectedReleasesMap.get(rel);
    }

    public List<ReleaseMeta> list() {
        return new ArrayList<>(affectedReleasesMap.keySet());
    }

    public void set(List<TicketIssue> issues) {
        for (TicketIssue issue : issues) {
            List<ReleaseMeta> affectedVersions = issue.getAv();
            for (ReleaseMeta affectedVersion : affectedVersions) {
                if (affectedReleasesMap.containsKey(affectedVersion)) {
                    this.affectedReleasesMap.put(affectedVersion, true);
                }
            }
        }
    }
}
