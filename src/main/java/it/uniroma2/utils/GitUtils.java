package it.uniroma2.utils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Pattern;

import org.assertj.core.util.diff.Delta;
import org.assertj.core.util.diff.DiffUtils;
import org.assertj.core.util.diff.Patch;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffEntry.ChangeType;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.util.io.DisabledOutputStream;

import it.uniroma2.exception.TicketException;
import it.uniroma2.model.FixCommit;
import it.uniroma2.model.GenericPair;
import it.uniroma2.model.TicketIssue;
import it.uniroma2.model.javaclass.JavaClass;
import it.uniroma2.model.releases.Release;
import it.uniroma2.model.releases.ReleaseMeta;
import it.uniroma2.model.releases.Releases;

public class GitUtils {

    // Date that will be used to retrieve commits from the first release
    private static Date firstDate = new Date(0);

    private GitUtils() {
        throw new IllegalStateException("Utility class");
    }

    // Returns an ordered by date list of commits
    public static List<RevCommit> getRelCommitsOrderedByDate(List<RevCommit> commitsList, ReleaseMeta release) {

        Map<Date, RevCommit> orderedCommits = new TreeMap<>();
        List<RevCommit> matchingCommits = new ArrayList<>();

        for (RevCommit commit : commitsList) {
            Date commitDate = commit.getCommitterIdent().getWhen();

            if (commitDate.after(firstDate) && !commitDate.after(release.getDate())) {
                orderedCommits.put(commitDate, commit);
            }
        }
        firstDate = release.getDate(); // preparing for next release

        for (Map.Entry<Date, RevCommit> com : orderedCommits.entrySet()) {
            matchingCommits.add(com.getValue());
        }
        return matchingCommits;
    }

    public static List<RevCommit> getCommitsForClass(Repository repository, List<RevCommit> commitList,
            String classPath)
            throws IOException {

        List<RevCommit> commitsForClass = new ArrayList<>();

        for (RevCommit commit : commitList) {
            try (TreeWalk treeWalk = new TreeWalk(repository)) {
                treeWalk.addTree(commit.getTree());
                treeWalk.setRecursive(true);

                while (treeWalk.next()) {
                    String filePath = treeWalk.getPathString();
                    if (filePath.equals(classPath) || filePath.endsWith("/" + classPath)) {
                        // If the file has changed in this commit, add it to the list
                        commitsForClass.add(commit);
                        break;
                    }
                }
            }
        }

        return commitsForClass;
    }

    public static List<String> getModifiedClasses(Repository repo, RevCommit commit) throws IOException {

        List<String> modifiedClasses = new ArrayList<>(); // Here there will be the names of the classes that have been
                                                          // modified by the commit

        try (DiffFormatter diffFormatter = new DiffFormatter(DisabledOutputStream.INSTANCE);

                ObjectReader reader = repo.newObjectReader()) {

            CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
            ObjectId newTree = commit.getTree();
            newTreeIter.reset(reader, newTree);

            RevCommit commitParent = commit.getParent(0); // It's the previous commit of the commit we are considering
            CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
            ObjectId oldTree = commitParent.getTree();
            oldTreeIter.reset(reader, oldTree);

            diffFormatter.setRepository(repo);
            List<DiffEntry> entries = diffFormatter.scan(oldTreeIter, newTreeIter);

            // Every entry contains info for each file involved in the commit (old path
            // name, new path name, change type (that could be MODIFY, ADD, RENAME, etc.))
            for (DiffEntry entry : entries) {
                // We are keeping only Java classes that are not involved in tests
                if (entry.getChangeType().equals(ChangeType.MODIFY) && entry.getNewPath().contains(".java")
                        && !entry.getNewPath().contains("/test/")) {
                    modifiedClasses.add(entry.getNewPath());
                }
            }

        } catch (ArrayIndexOutOfBoundsException e) {
            // commit has no parents: skip this commit, return an empty list and go on

        }

        return modifiedClasses;

    }

    public static List<FixCommit> getTicketCommitsReleases(Releases releases, TicketIssue issue)
            throws TicketException {
        Set<FixCommit> fixCommits = new HashSet<>();
        for (Release release : releases.getReleases()) {
            for (RevCommit commit : release.getCommits()) {
                if (hasValidMatch(commit, release, issue)) {
                    fixCommits.add(new FixCommit(commit, release, release.getClassesModifiedByCommit(commit)));
                }
            }
        }
        return new ArrayList<>(fixCommits);
    }

    public static void setBugginess(FixCommit fixCommit, Releases releases, TicketIssue issue) throws TicketException {
        for (Release rel : releases.getReleases()) {
            for (JavaClass clazz : rel.getClasses()) {
                if (fixCommit.getModifiedClasses().contains(clazz.getPathName()) && !rel.isBefore(issue.getIV())
                        && rel.isBefore(fixCommit.getRelease()))
                    clazz.setBuggy(true);
            }
        }
    }

    // Returns wether the commit has a valid match with a ticket in jira.
    // We are assuming that Jira's fixed version info is true,
    // if release's commit is equal or after the fixed version,
    // then it means that the commit message is NOT consistent,
    // thus is not valid and the IV must not be after the release.
    // The rel input refers to the input commit
    public static boolean hasValidMatch(RevCommit commit, ReleaseMeta rel, TicketIssue issue) throws TicketException {
        Pattern pattern = Pattern.compile(issue.getKey() + "\\b");
        return pattern.matcher(commit.getFullMessage()).find() && !issue.getFv().isBefore(rel)
                && !issue.getIV().isAfter(rel);
    }

    public static String getContentOfClassByCommit(String className, RevCommit commit, Repository repo)
            throws IOException {

        RevTree tree = commit.getTree();
        // Tree walk to iterate over all files in the Tree recursively
        TreeWalk treeWalk = new TreeWalk(repo);
        treeWalk.addTree(tree);
        treeWalk.setRecursive(true);

        while (treeWalk.next()) {
            // We are keeping only Java classes that are not involved in tests
            if (treeWalk.getPathString().equals(className)) {
                String content = new String(repo.open(treeWalk.getObjectId(0)).getBytes(), StandardCharsets.UTF_8);
                treeWalk.close();
                return content;
            }
        }
        treeWalk.close();
        // If here it mean no class with name className is present
        return null;
    }

    public static GenericPair<Integer, Integer> getAddedAndDeletedLines(String oldContent,
            String newContent) {

        List<String> addedLines = new ArrayList<>();
        List<String> deletedLines = new ArrayList<>();

        List<String> oldLines = splitContentToLines(oldContent);
        List<String> newLines = splitContentToLines(newContent);

        // Compute the differences between the two lists of lines
        Patch<String> patch = DiffUtils.diff(oldLines, newLines);

        // Iterate over the differences to identify added and deleted lines
        for (Delta<String> delta : patch.getDeltas()) {
            if (delta.getRevised().size() > 0) {
                // This delta represents an addition or change
                List<String> added = delta.getRevised().getLines();
                for (String line : added) {
                    addedLines.add(line);
                }
            }
            if (delta.getOriginal().size() > 0) {
                // This delta represents a deletion or change
                List<String> deleted = delta.getOriginal().getLines();
                for (String line : deleted) {
                    deletedLines.add(line);
                }
            }
        }

        return new GenericPair<>(addedLines.size(), deletedLines.size());
    }

    // Helper method to split the content of a Java class into lines
    public static List<String> splitContentToLines(String content) {
        List<String> lines = new ArrayList<>();
        String[] parts = content.split("\n");
        for (String part : parts) {
            lines.add(part.trim());
        }
        return lines;
    }

    public static void addDependencies(Set<String> dependencies, String line) {
        // Check for import statements
        if (line.startsWith("import")) {
            String[] words = line.split("\\s+");
            dependencies.add(words[1]);
        }

        // Check for instance variables
        else if (line.contains("new ")) {
            String[] words = line.split("\\s+");
            for (int i = 0; i < words.length - 1; i++) {
                if (words[i].equals("new")) {
                    dependencies.add(words[i + 1]);
                }
            }
        }

        // Check for method calls
        else if (line.contains(".")) {
            String[] words = line.split("\\.");
            for (int i = 0; i < words.length - 1; i++) {
                if (words[i].matches("[a-zA-Z]\\w*")) {
                    dependencies.add(words[i]);
                }
            }
        }
    }

    public static void fixRelIds(Releases releases) {
        List<Release> rels = releases.getReleases();
        for (int i = 0; i < rels.size(); i++) {
            rels.get(i).setId(i + 1);
        }
    }

    public static String getAlphaChars(String s) {
        return s.replaceAll("[^a-zA-Z]", "");
    }

}
