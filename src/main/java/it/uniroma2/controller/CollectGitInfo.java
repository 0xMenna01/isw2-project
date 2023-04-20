package it.uniroma2.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand.ListMode;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.errors.RevisionSyntaxException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

import it.uniroma2.exception.GitException;
import it.uniroma2.factory.ReleaseClassesFactory;
import it.uniroma2.model.ReleaseMeta;
import it.uniroma2.model.Releases;
import it.uniroma2.model.TicketIssue;
import it.uniroma2.model.javaclass.JavaClass;
import it.uniroma2.utils.GitUtils;

public class CollectGitInfo {

    private Repository repo;
    private Git git;
    private List<ReleaseMeta> relMeta;
    private List<TicketIssue> issues;

    // Associations (Release, Class) containing all measurament information
    private Releases rel;

    public CollectGitInfo(String repoUrl, List<ReleaseMeta> relMeta, List<TicketIssue> issues)
            throws GitException, InvalidRemoteException, TransportException, GitAPIException, IOException {
        this.relMeta = relMeta;
        this.issues = issues;
        this.rel = new Releases();

        File directory = new File("temp"); // Directory for cloning the repo

        if (directory.exists()) {
            this.repo = new FileRepository("temp/.git");
            this.git = new Git(this.repo);
        } else {
            System.out.println("---- CLONING THE PROJECT REPO.. ----");
            this.git = Git.cloneRepository()
                    .setURI(repoUrl)
                    .setDirectory(
                            directory)
                    .call();
            this.repo = git.getRepository();
        }

        System.out.println("---- PROJECT REPO CLONED SUCCESSFULLY ----");
    }

    public void computeRelClassesCommits() throws IOException, GitAPIException, GitException {
        System.out.println("---- RETRIEVING ALL COMMITS.. ----");
        List<RevCommit> allCommits = retrieveCommits();
        System.out.println("---- OK ----");

        List<RevCommit> tempMatchCommits = null;
        for (ReleaseMeta rel : relMeta) {
            tempMatchCommits = GitUtils.getRelCommitsOrderedByDate(allCommits, rel);
            // Creating all classes associated to the last release commit
            System.out.println("---- BUILDING CLASSES OF RELEASE " + rel.getName() + " ----");
            List<JavaClass> relClasses = ReleaseClassesFactory.getInstance()
                    .buildClasses(tempMatchCommits.get(tempMatchCommits.size() - 1), repo);
            System.out.println("---- OK ----");
            // Updating the releases state by creating a Release instace that maps a release
            // to its classes, specifying all commits that changed a class
            System.out.println("---- ASSOCIATING COMMITS TO CLASSES ----");
            this.rel.add(
                    ReleaseClassesFactory.getInstance().buildReleaseCommits(repo, rel,
                            tempMatchCommits, relClasses));
            System.out.println("---- OK ----");
        }

        this.git.close();
        // GitUtils.deleteDirectory("temp");
        // System.out.println("---- OK: REPO CLEANED ----");
        System.out.println("---- DONE ----");
    }

    private List<RevCommit> retrieveCommits() throws GitAPIException, RevisionSyntaxException, IOException {
        List<RevCommit> commits = new ArrayList<>();
        List<Ref> branchesList = this.git.branchList().setListMode(ListMode.ALL).call();

        for (Ref branch : branchesList) {
            Iterable<RevCommit> commitsList = this.git.log().add(this.repo.resolve(branch.getName())).call();

            for (RevCommit commit : commitsList) {
                if (!commits.contains(commit)) {
                    commits.add(commit);
                }
            }
        }

        return commits;

    }

    public Releases getRel() {
        return rel;
    }

}
