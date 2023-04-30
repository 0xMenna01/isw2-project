package it.uniroma2.model;

import java.util.ArrayList;
import java.util.List;

public class Releases {
    List<Release> rels;

    public Releases() {
        this.rels = new ArrayList<>();
    }

    public Releases(List<Release> rels) {
        this.rels = rels;
    }

    public List<Release> getReleases() {
        return rels;
    }

    public void add(Release rel) {
        rels.add(rel);
    }

    public Release get(int i) {
        return rels.get(i);
    }

    public List<ReleaseMeta> getMeta() {
        List<ReleaseMeta> relsMeta = new ArrayList<>();
        for (Release rel : rels) {
            relsMeta.add(rel.getMeta());
        }
        return relsMeta;
    }
}
