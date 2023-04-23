package it.uniroma2.utils;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffEntry.ChangeType;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.util.io.DisabledOutputStream;

import it.uniroma2.exception.GitException;
import it.uniroma2.model.Release;
import it.uniroma2.model.ReleaseMeta;
import it.uniroma2.model.TicketIssue;

public class GitUtils {

    // Date that will be used to retrieve commits from the first release
    private static Date firstDate; // set to 1970-01-01
    static {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            firstDate = formatter.parse("1970-01-01");
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private GitUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static void deleteDirectory(String path) {
        File fileOrDirectory = new File(path);
        if (fileOrDirectory.isDirectory()) {
            File[] contents = fileOrDirectory.listFiles();
            for (File file : contents) {
                deleteDirectory(file.getAbsolutePath());
            }
        }
        fileOrDirectory.delete();
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
            throws IOException, GitAPIException, GitException {

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

    public static boolean hasMatch(RevCommit commit, TicketIssue issue) {
        Pattern pattern = Pattern.compile(issue.getKey() + "\\b");
        return pattern.matcher(commit.getFullMessage()).find();

    }

    
    // We are assuming that Jira's fixed version info is true,
    // if release's commit is equal or after the fixed version,
    // then it means that the commit message is NOT consistent,
    // thus we skip it.
    public static boolean existsBug(Release rel, List<TicketIssue> issues) {
        for (RevCommit commit : rel.getCommits()) {
            for (TicketIssue issue : issues) {
                if (hasMatch(commit, issue) && issue.getFv().isAfter(rel)) 
                    return true;
            }
        }
        return false;
    }
}
