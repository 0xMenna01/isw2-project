package it.uniroma2.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand.ListMode;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.RevisionSyntaxException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;

import it.uniroma2.factory.ReleaseClassesFactory;
import it.uniroma2.model.ReleaseMeta;
import it.uniroma2.model.Releases;
import it.uniroma2.model.javaclass.JavaClass;
import it.uniroma2.utils.GitUtils;
import it.uniroma2.writer.ReportWriter;

public class CollectGitInfo {

    private Repository repo;
    private Git git;
    private List<ReleaseMeta> releases;

    // Associations (Release, Class) containing all measurament information
    private Releases rels;

    public CollectGitInfo(String repoUrl, List<ReleaseMeta> releases, String projKey)
            throws GitAPIException, IOException {
        this.releases = releases;
        this.rels = new Releases();

        File directory = new File("temp/" + projKey); // Directory for cloning the repo

        if (directory.exists()) {
            this.repo = new FileRepository("temp/" + projKey + "/.git");
            this.git = new Git(this.repo);
        } else {

            this.git = Git.cloneRepository()
                    .setURI(repoUrl)
                    .setDirectory(
                            directory)
                    .call();

            this.repo = git.getRepository();
        }
    }

    public void computeRelClassesCommits(ReportWriter reportWriter) throws IOException, GitAPIException {
        // Getting all commits
        List<RevCommit> allCommits = retrieveCommits();
        // Print number of commits
        reportWriter.writeNumOfCommits(allCommits.size());

        int num = 0;
        List<RevCommit> tempMatchCommits = null;
        for (ReleaseMeta rel : releases) {
            tempMatchCommits = GitUtils.getRelCommitsOrderedByDate(allCommits, rel);

            num += tempMatchCommits.size();
            reportWriter.writeNumOfCommitsForRelease(tempMatchCommits.size(), rel.getName());

            // Creating all classes associated to the last release commit
            if (!tempMatchCommits.isEmpty()) {
                List<JavaClass> relClasses = ReleaseClassesFactory.getInstance()
                        .buildClasses(tempMatchCommits.get(tempMatchCommits.size() - 1), repo);
                // Prining number of classes for release
                reportWriter.writeNumOfClassesForRelease(relClasses.size(), rel.getName());

                // Updating the releases state by creating a Release instace that maps a release
                // to its classes, specifying all commits that changed a class
                this.rels.add(
                        ReleaseClassesFactory.getInstance().buildReleaseCommits(repo, rel,
                                tempMatchCommits, relClasses));
            }
        }
        GitUtils.fixRelIds(rels);

        // Printing number of commits for all releases
        reportWriter.writeTotalNumOfCommitsForReleases(num);

        // Printing commits of all releases associated to the classes they changed
        reportWriter.writeReleasesCommitsForClasses(this.rels.getReleases());

        this.git.close();
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

    public Releases getReleases() {
        return rels;
    }

    public Repository getRepo() {
        return repo;
    }

    public int getNumOfRel(){
        return rels.getReleases().size();
    }

}