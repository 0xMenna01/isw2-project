package it.uniroma2.factory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.treewalk.TreeWalk;

import it.uniroma2.exception.GitException;
import it.uniroma2.model.Release;
import it.uniroma2.model.ReleaseMeta;
import it.uniroma2.model.javaclass.ClassMetrics;
import it.uniroma2.model.javaclass.JavaClass;
import it.uniroma2.utils.GitUtils;

public class ReleaseClassesFactory {

    private static ReleaseClassesFactory instance = null;

    private ReleaseClassesFactory() {
    }

    public static ReleaseClassesFactory getInstance() {
        if (instance == null) {
            instance = new ReleaseClassesFactory();
        }
        return instance;
    }

    public List<JavaClass> buildClasses(RevCommit lastCommit, Repository repo) throws IOException {
        List<JavaClass> classes = new ArrayList<>();

        // Tree of the files and directories belonigng to the repo when commit was
        // pushed
        RevTree tree = lastCommit.getTree();
        // Tree walk to iterate over all files in the Tree recursively
        TreeWalk treeWalk = new TreeWalk(repo);
        treeWalk.addTree(tree);
        treeWalk.setRecursive(true);

        while (treeWalk.next()) {
            // We are keeping only Java classes that are not involved in tests
            if (treeWalk.getPathString().contains(".java") && !treeWalk.getPathString().contains("/test/")) {
                // Adding classes with name and content
                classes.add(new JavaClass(treeWalk.getPathString(), new ClassMetrics(),
                        new String(repo.open(treeWalk.getObjectId(0)).getBytes(), StandardCharsets.UTF_8)));

            }
        }
        treeWalk.close();

        return classes;
    }

    // This method creates a Release instance with all the associated java classes
    // and
    // maps the commits to the modified classes
    // Note that both commits and classes must be related to the input release
    public Release buildReleaseCommits(Repository repo, ReleaseMeta rel, List<RevCommit> commits,
            List<JavaClass> classes) throws IOException, GitAPIException, GitException {
        HashMap<JavaClass, List<RevCommit>> classesCommitsMap = new HashMap<>();

        for (JavaClass javaClass : classes) {
            classesCommitsMap.put(javaClass, GitUtils.getCommitsForClass(repo, commits, javaClass.toString()));
        }

        return new Release(rel.getId(), rel.getName(), rel.getDate(), classesCommitsMap);
    }
}
