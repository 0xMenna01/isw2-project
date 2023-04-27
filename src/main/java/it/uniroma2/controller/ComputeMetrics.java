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

    private final Releases releases;
    private final List<TicketIssue> issues;
    private final Repository repo;

    private HashMap<Release, HashMap<JavaClass, List<String>>> mapRelClassForContent;

    public ComputeMetrics(Releases releases, List<TicketIssue> issues, Repository repo)
            throws IOException {
        this.releases = releases;
        this.issues = issues;
        this.repo = repo;

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
                clazz.setSize(computeSize(clazz));
                clazz.setnFix(computeNFixAndFixChurn(clazz, rel).getFirst());
                clazz.setnAuthors(computeNauthors(clazz, rel));

                clazz.setAvgLocAdded(
                        (double) computeLocAddedAndDel(clazz, rel).getFirst()
                                / mapRelClassForContent.get(rel).get(clazz).size());

                clazz.setChurn(computeChurn(clazz, rel));

                clazz.setAvgChurn((double) computeChurn(clazz, rel) / mapRelClassForContent.get(rel).get(clazz).size());

                clazz.setAge(computeReleaseAge(rel));
                clazz.setAvgFixChurn(computeAvgFixChurn(rel, clazz));
                clazz.setFanOut(computeFanOut(clazz.getContent()));
                clazz.setNumOfRevisions(rel.getCommitsForClass(clazz).size());
            }
        }
    }

    private int computeSize(JavaClass clazz) {

        return GitUtils.splitContentToLines(clazz.getContent()).size();
    }

    private GenericPair<Integer, Integer> computeNFixAndFixChurn(JavaClass clazz, Release rel)
            throws TicketException, IOException {

        List<String> fixCommitsContents = new ArrayList<>();

        for (RevCommit commit : rel.getCommitsForClass(clazz)) {
            for (TicketIssue issue : issues) {

                if (GitUtils.hasValidMatch(commit, rel, issue)) {
                    fixCommitsContents.add(GitUtils.getContentOfClassByCommit(clazz.getPathName(), commit, repo));
                }
            }
        }

        Integer fixChurn = 0;
        int i = 0;
        for (; i < fixCommitsContents.size(); i++) {
            if (i == 0)
                fixChurn += computeSize(new JavaClass(clazz.getPathName(), fixCommitsContents.get(i)));
            else {
                GenericPair<Integer, Integer> addedAndDel = GitUtils.getAddedAndDeletedLines(
                        fixCommitsContents.get(i - 1),
                        fixCommitsContents.get(i));

                fixChurn += addedAndDel.getFirst() + addedAndDel.getFirst();
            }
        }

        return new GenericPair<>(i, fixChurn);
    }

    private int computeNauthors(JavaClass clazz, Release rel) {
        Set<String> authors = new HashSet<>();
        for (RevCommit commit : rel.getCommitsForClass(clazz)) {
            PersonIdent author = commit.getAuthorIdent();
            authors.add(author.getName());
        }

        return authors.size();
    }

    private GenericPair<Integer, Integer> computeLocAddedAndDel(JavaClass clazz, Release rel)
            throws GitException {
        int i = 0;
        Integer locAdded = 0;
        Integer delLoc = 0;
        String prevContent = null;
        for (String classContent : mapRelClassForContent.get(rel).get(clazz)) {
            if (i == 0) {
                prevContent = classContent;
                locAdded += computeSize(new JavaClass(clazz.getPathName(), classContent));
            }

            else {
                GenericPair<Integer, Integer> addedAndDel = GitUtils.getAddedAndDeletedLines(prevContent,
                        classContent);
                locAdded += addedAndDel.getFirst();
                delLoc += addedAndDel.getFirst();
                prevContent = classContent;
            }
            i++;
        }

        if (i == 0)
            throw new GitException("MUST not be emmpy because commits modifies the class");

        return new GenericPair<>(locAdded, delLoc);
    }

    private Integer computeChurn(JavaClass clazz, Release rel) throws GitException {
        GenericPair<Integer, Integer> locAddedAndDel = computeLocAddedAndDel(clazz, rel);
        return locAddedAndDel.getFirst() + locAddedAndDel.getSecond();

    }

    private int computeReleaseAge(Release rel) {

        LocalDate relDate = rel.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        Period period = Period.between(relDate, LocalDate.now());

        return period.getYears();
    }

    public int computeFanOut(String classContent) {
        Set<String> dependencies = new HashSet<>();
        String[] lines = classContent.split("\\n");

        for (String line : lines) {
            GitUtils.addDependencies(dependencies, line);
        }
        return dependencies.size();
    }

    public double computeAvgFixChurn(Release rel, JavaClass clazz) throws TicketException, IOException {
        GenericPair<Integer, Integer> fixPair = computeNFixAndFixChurn(clazz, rel);
        return fixPair.getFirst() == 0 ? 0 : (double) fixPair.getSecond() / fixPair.getFirst();
    }
}
