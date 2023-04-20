package it.uniroma2.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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
import it.uniroma2.model.ReleaseMeta;

public class GitUtils {

    private static final Date FIRST_DATE = new Date(0); // set to 1970-01-01

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

        Date lastDate = release.getDate();

        for (RevCommit commit : commitsList) {
            Date commitDate = commit.getCommitterIdent().getWhen();

            if (commitDate.after(FIRST_DATE) && (commitDate.before(lastDate) || commitDate.equals(lastDate))) {
                orderedCommits.put(commitDate, commit);
            }

        }

        for (Map.Entry<Date, RevCommit> commit : orderedCommits.entrySet()) {
            matchingCommits.add(commit.getValue());
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

}
