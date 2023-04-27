package it.uniroma2.model;

import java.util.ArrayList;
import java.util.List;

public class Releases {
    List<Release> rels;

    public Releases() {
        this.rels = new ArrayList<>();
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
}
