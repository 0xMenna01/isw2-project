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
import it.uniroma2.model.AffectedReleases;
import it.uniroma2.model.Release;
import it.uniroma2.model.ReleaseMeta;
import it.uniroma2.model.Releases;
import it.uniroma2.model.TicketIssue;
import it.uniroma2.model.javaclass.JavaClass;
import it.uniroma2.utils.GitUtils;
import it.uniroma2.view.MainView;

public class CollectGitInfo {

    private Repository repo;
    private Git git;
    private AffectedReleases affRel;
    private List<TicketIssue> issues;

    // Associations (Release, Class) containing all measurament information
    private Releases rels;

    public CollectGitInfo(String repoUrl, AffectedReleases affRel, List<TicketIssue> issues)
            throws GitException, InvalidRemoteException, TransportException, GitAPIException, IOException {
        this.affRel = affRel;
        this.issues = issues;
        this.rels = new Releases();

        File directory = new File("temp"); // Directory for cloning the repo

        if (directory.exists()) {
            this.repo = new FileRepository("temp/.git");
            this.git = new Git(this.repo);
        } else {
            System.out.println("CLONING REPO...");

            this.git = Git.cloneRepository()
                    .setURI(repoUrl)
                    .setDirectory(
                            directory)
                    .call();

            System.out.println("REPO CLONED SUCCESSFULLY!");

            this.repo = git.getRepository();
        }
    }

    public void computeRelClassesCommits() throws IOException, GitAPIException, GitException {
        // Getting all commits
        List<RevCommit> allCommits = retrieveCommits();
        // Print number of commits
        MainView.printNumberCommits(allCommits.size());

        // Delete this later on
        int num = 0;
        List<RevCommit> tempMatchCommits = null;
        for (ReleaseMeta rel : affRel.list()) {
            tempMatchCommits = GitUtils.getRelCommitsOrderedByDate(allCommits, rel);

            // Delete this later on
            num += tempMatchCommits.size();
            MainView.printNumOfCommitsFoRelease(tempMatchCommits.size(), rel.getName());

            // Creating all classes associated to the last release commit
            if (!tempMatchCommits.isEmpty()) {
                List<JavaClass> relClasses = ReleaseClassesFactory.getInstance()
                        .buildClasses(tempMatchCommits.get(tempMatchCommits.size() - 1), repo);
                // Prining number of classes for release
                MainView.printNumOfClassesForRelease(relClasses.size(), rel.getName());

                // Updating the releases state by creating a Release instace that maps a release
                // to its classes, specifying all commits that changed a class
                this.rels.add(
                        ReleaseClassesFactory.getInstance().buildReleaseCommits(repo, rel,
                                tempMatchCommits, relClasses));
            }
        }

        // Printing number of commits for all releases
        MainView.printTotalNumOfCommitsForReleases(num);

        // Printing commits of all releases associated to the classes they changed
        MainView.printReleasesCommitsForClasses(this.rels.getReleases());

        this.git.close();
        // GitUtils.deleteDirectory("temp");
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

    public void labelClasses() {

        for (Release rel : rels.getReleases()) {
            for (JavaClass clazz : rel.getClasses()) {
                if (GitUtils.existsBug(rel.getCommitsForClass(clazz), issues)) {
                    if (affRel.get(new ReleaseMeta(rel.getId(), rel.getName(), rel.getDate()))) {
                        rel.setBug(clazz);
                    }
                }
            }
        }

    }

    public Releases getRel() {
        return rels;
    }

}
