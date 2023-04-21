package it.uniroma2.model;

import java.util.ArrayList;
import java.util.List;

import it.uniroma2.model.javaclass.JavaClass;

public class Releases {
    List<Release> releases;

    public Releases() {
        this.releases = new ArrayList<>();
    }

    public List<JavaClass> getClasses(ReleaseMeta rel) {
        for (Release release : releases) {
            if (release.getId() == rel.getId())
                return release.getClasses();
        }
        return null;
    }

    public List<Release> getReleases(){
        return releases;
    }

    public void add(Release rel) {
        releases.add(rel);
    }

    public Release get(int i) {
        return releases.get(i);
    }
}
