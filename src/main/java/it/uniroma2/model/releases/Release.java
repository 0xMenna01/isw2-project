package it.uniroma2.model.releases;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.eclipse.jgit.revwalk.RevCommit;

import it.uniroma2.model.javaclass.JavaClass;

public class Release extends ReleaseMeta {

    /*
     * A mapping of the classes that were present at the time of the release and the
     * list commits that changed the class.
     * If a class hasn't been changed by any commit the list is set tu null.
     * N.B. All commits of the release stored
     */
    Map<JavaClass, List<RevCommit>> classesCommits;
    List<JavaClass> classes;
    List<RevCommit> commits;

    public Release(int id, String name, Date date, Map<JavaClass, List<RevCommit>> classesCommits) {
        super(id, name, date);
        this.classesCommits = classesCommits;
        this.classes = new ArrayList<>();
        this.commits = new ArrayList<>();
    }

    public Map<JavaClass, List<RevCommit>> getClassesCommits() {
        return classesCommits;
    }

    public void setClassesCommits(Map<JavaClass, List<RevCommit>> classesCommits) {
        this.classesCommits = classesCommits;
    }

    // Returns the list of commits that are associated to the release based on the
    // ones that changed the classes.
    public List<RevCommit> getCommits() {
        if (this.commits.isEmpty()) {
            List<RevCommit> commitList = new ArrayList<>();
            for (Map.Entry<JavaClass, List<RevCommit>> entry : classesCommits.entrySet()) {

                List<RevCommit> commitsClass = entry.getValue();
                for (RevCommit commit : commitsClass) {

                    if (!commits.contains(commit)) {
                        commitList.add(commit);
                    }
                }
            }
            this.commits.addAll(commitList);
        }

        return this.commits;
    }

    public List<JavaClass> getClasses() {

        if (this.classes.isEmpty()) {
            List<JavaClass> classList = new ArrayList<>();
            for (Map.Entry<JavaClass, List<RevCommit>> entry : classesCommits.entrySet()) {
                classList.add(entry.getKey());
            }
            this.classes.addAll(classList);
        }
        return this.classes;
    }

    public List<RevCommit> getCommitsForClass(JavaClass javaClass) {
        return classesCommits.get(javaClass);
    }

    public List<JavaClass> getClassesModifiedByCommit(RevCommit commit) {
        List<JavaClass> classList = new ArrayList<>();
        for (Map.Entry<JavaClass, List<RevCommit>> entry : classesCommits.entrySet()) {
            if (entry.getValue().contains(commit)) {
                classList.add(entry.getKey());
            }
        }
        return classList;
    }

    public void setBug(JavaClass clazz) {
        getClasses().get(getClasses().indexOf(clazz)).setBuggy(true);
    }

    public ReleaseMeta getMeta() {
        return new ReleaseMeta(this.id, this.name, this.date);
    }
}