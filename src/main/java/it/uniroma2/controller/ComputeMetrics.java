package it.uniroma2.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

import it.uniroma2.exception.GitException;
import it.uniroma2.exception.TicketException;
import it.uniroma2.model.GenericPair;
import it.uniroma2.model.Release;
import it.uniroma2.model.Releases;
import it.uniroma2.model.TicketIssue;
import it.uniroma2.model.javaclass.JavaClass;
import it.uniroma2.utils.GitUtils;

public class ComputeMetrics {

    private Releases releases;
    private List<TicketIssue> issues;

    private HashMap<Release, HashMap<JavaClass, List<String>>> mapRelClassForContent;

    public ComputeMetrics(Releases releases, List<TicketIssue> issues, Repository repo)
            throws IOException, GitException {
        this.releases = releases;
        this.issues = issues;

        this.mapRelClassForContent = new HashMap<>();
        for (Release rel : releases.getReleases()) {
            HashMap<JavaClass, List<String>> mapClassesRevisions = new HashMap<>();
            for (JavaClass clazz : rel.getClasses()) {
                List<String> contents = new ArrayList<>();
                for (RevCommit commit : rel.getCommitsForClass(clazz)) {
                    String classContent = GitUtils.getContentOfClassByCommit(clazz.getPathName(), commit, repo);
                    if (classContent != null) {
                        contents.add(classContent);
                    }
                }
                mapClassesRevisions.put(clazz, contents);
            }
            this.mapRelClassForContent.put(rel, mapClassesRevisions);
        }
    }

    public void compute() throws IOException, GitException, TicketException {
        for (Release rel : releases.getReleases()) {
            for (JavaClass clazz : rel.getClasses()) {
                clazz.setSize(computeSize(clazz, rel));
                clazz.setnFix(computeTimeToFix(clazz, rel).getSecond());
                clazz.setnAuthors(computeNauthors(clazz, rel));
                clazz.setAvgLocAdded(computeAvgLocAdded(clazz, rel));
                clazz.setChurn(computeChurn(clazz, rel).getFirst());
                clazz.setAvgChurn(computeAvgChurn(clazz, rel));
                clazz.setAge(computeReleaseAge(rel));
                clazz.setAvgTimeFix(computeAvgTimeToFix(clazz, rel));
                clazz.setFanOut(computeFanout(clazz.getContent()));
                clazz.setChangeSetSize(computeChangeSetSize(rel, clazz));
            }
        }
    }

    private int computeSize(JavaClass clazz, Release rel) {
        int loc = 0;
        String content = clazz.getContent();
        String[] lines = content.split("\\n");
        for (String line : lines) {
            String trimmedLine = line.trim();
            if (!trimmedLine.startsWith("//") && !trimmedLine.startsWith("/*") && !trimmedLine.startsWith("*")
                    && !trimmedLine.isEmpty()) {
                loc++;
            }
        }
        return loc;
    }

    private GenericPair<Integer, Integer> computeTimeToFix(JavaClass clazz, Release rel) throws TicketException {
        Integer timeToFix = 0;
        int nFix = 0;
        for (RevCommit commit : rel.getCommitsForClass(clazz)) {
            for (TicketIssue issue : issues) {
                if (GitUtils.hasValidMatch(commit, rel, issue)) {
                    timeToFix += rel.getId() - issue.getIV().getId(); // Time to Fix => Number of versions that took to
                                                                      // resolve issue
                    nFix++;
                }
            }
        }
        return new GenericPair<Integer, Integer>(timeToFix, nFix);
    }

    private int computeNauthors(JavaClass clazz, Release rel) {
        Set<String> authors = new HashSet<>();
        for (RevCommit commit : rel.getCommitsForClass(clazz)) {
            PersonIdent author = commit.getAuthorIdent();
            authors.add(author.getName());
        }

        return authors.size();
    }

    private double computeAvgLocAdded(JavaClass clazz, Release rel) throws IOException, GitException {

        List<Integer> loc = new ArrayList<>();
        for (String classContent : mapRelClassForContent.get(rel).get(clazz)) {
            loc.add(computeSize(new JavaClass(clazz.getPathName(), classContent), rel));
        }

        double avgLocAdded = 0;
        for (int i = 0; i < loc.size(); i++) {
            if (i == 0)
                avgLocAdded += loc.get(i);
            else
                avgLocAdded += Math.max(0, loc.get(i) - loc.get(i - 1));
        }

        if (loc.isEmpty())
            throw new GitException("MUST not be emmpy because commits modifies the class");

        return avgLocAdded / loc.size();
    }

    private GenericPair<Integer, Integer> computeChurn(JavaClass clazz, Release rel) throws GitException {
        List<Integer> loc = new ArrayList<>();
        for (String classContent : mapRelClassForContent.get(rel).get(clazz)) {
            loc.add(computeSize(new JavaClass(clazz.getPathName(), classContent), rel));
        }

        int churn = 0;
        for (int i = 0; i < loc.size(); i++) {
            if (i == 0)
                churn += loc.get(i);
            else
                churn += Math.abs(loc.get(i) - loc.get(i - 1));
        }

        if (loc.isEmpty())
            throw new GitException("MUST not be emmpy because commits modifies the class");

        return new GenericPair<>(churn, loc.size());
    }

    private double computeAvgChurn(JavaClass clazz, Release rel) throws GitException {
        GenericPair<Integer, Integer> churnValueNum = computeChurn(clazz, rel);
        return (double) churnValueNum.getFirst() / churnValueNum.getSecond();
    }

    private int computeReleaseAge(Release rel) {

        LocalDate relDate = rel.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        Period period = Period.between(relDate, LocalDate.now());

        return period.getYears();
    }

    public int computeFanout(String classContent) {
        Set<String> dependencies = new HashSet<>();
        String[] lines = classContent.split("\\n");

        for (String line : lines) {
            // Check for import statements
            if (line.startsWith("import")) {
                String[] words = line.split("\\s+");
                dependencies.add(words[1]);
            }

            // Check for instance variables
            if (line.contains("new ")) {
                String[] words = line.split("\\s+");
                for (int i = 0; i < words.length - 1; i++) {
                    if (words[i].equals("new")) {
                        dependencies.add(words[i + 1]);
                    }
                }
            }

            // Check for method calls
            if (line.contains(".")) {
                String[] words = line.split("\\.");
                for (int i = 0; i < words.length - 1; i++) {
                    if (words[i].matches("[a-zA-Z][a-zA-Z0-9_]*")) {
                        dependencies.add(words[i]);
                    }
                }
            }
        }

        return dependencies.size();
    }

    private int computeChangeSetSize(Release rel, JavaClass clazz) {
        return rel.getCommitsForClass(clazz).size();
    }

    private double computeAvgTimeToFix(JavaClass clazz, Release rel) throws TicketException {
        GenericPair<Integer, Integer> pairFix = computeTimeToFix(clazz, rel);
        return pairFix.getSecond().equals(0) ? 0 : (double) pairFix.getFirst() / pairFix.getSecond(); // If there is no
                                                                                                      // fix, the
                                                                                                      // average time to
                                                                                                      // fix is 0
    }
}
