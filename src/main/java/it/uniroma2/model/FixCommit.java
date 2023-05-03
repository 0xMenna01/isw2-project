package it.uniroma2.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.revwalk.RevCommit;

import it.uniroma2.model.javaclass.JavaClass;
import it.uniroma2.model.releases.ReleaseMeta;

public class FixCommit {
    
    private RevCommit commit;
    private ReleaseMeta rel;
    private List<String> modifiedClasses;


    public FixCommit(RevCommit commit, ReleaseMeta rel, List<JavaClass> modifiedClasses) {
        this.commit = commit;
        this.rel = rel;
        this.modifiedClasses = new ArrayList<>();
        for (JavaClass clazz : modifiedClasses) {
            this.modifiedClasses.add(clazz.getPathName());
        }
    }


    public RevCommit getCommit() {
        return commit;
    }


    public ReleaseMeta getRelease() {
        return rel;
    }


    public List<String> getModifiedClasses() {
        return modifiedClasses;
    }

    
}
