package it.uniroma2.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.treewalk.TreeWalk;

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

        // Get the tree id for the specified class file
        ObjectId classId = repository.resolve(classPath);
        if (classId == null) {
            throw new GitException(classPath + "not found");
        }

        // Loop through the commit list and check each commit for changes to the class
        // file
        for (RevCommit commit : commitList) {
            try (TreeWalk treeWalk = new TreeWalk(repository)) {
                treeWalk.addTree(commit.getTree());
                treeWalk.setRecursive(true);
                while (treeWalk.next()) {
                    if (treeWalk.getObjectId(0).equals(classId)) {
                        // If the file has changed in this commit, add it to the list
                        commitsForClass.add(commit);
                        break;
                    }
                }
            }
        }

        return commitsForClass;
    }

}
