package it.uniroma2.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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
    HashMap<JavaClass, List<RevCommit>> classesCommits;

    public Release(int id, String name, Date date, HashMap<JavaClass, List<RevCommit>> classesCommits) {
        super(id, name, date);
        this.classesCommits = classesCommits;
    }

    public HashMap<JavaClass, List<RevCommit>> getClassesCommits() {
        return classesCommits;
    }

    public void setClassesCommits(HashMap<JavaClass, List<RevCommit>> classesCommits) {
        this.classesCommits = classesCommits;
    }

    // Returns the list of commits that are associated to the release based on the
    // ones that changed the classes.
    public List<RevCommit> getCommits() {

        List<RevCommit> commits = new ArrayList<>();
        for (Map.Entry<JavaClass, List<RevCommit>> entry : classesCommits.entrySet()) {

            List<RevCommit> commitsClass = entry.getValue();
            for (RevCommit commit : commitsClass) {

                if (!commits.contains(commit)) {
                    commits.add(commit);
                }
            }
        }
        return commits;
    }

    public List<JavaClass> getClasses() {

        List<JavaClass> classes = new ArrayList<>();

        for (Map.Entry<JavaClass, List<RevCommit>> entry : classesCommits.entrySet()) {
            classes.add(entry.getKey());
        }
        return classes;
    }

    public List<RevCommit> getCommitsForClass(JavaClass javaClass) {
        return classesCommits.get(javaClass);
    }

    public void setBug(JavaClass clazz) {
        getClasses().get(getClasses().indexOf(clazz)).setBuggy(true);
    }

    public ReleaseMeta getMeta() {
        return new ReleaseMeta(this.id, this.name, this.date);
    }
}