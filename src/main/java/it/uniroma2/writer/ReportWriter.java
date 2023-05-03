package it.uniroma2.writer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import it.uniroma2.exception.TicketException;
import it.uniroma2.model.TicketIssue;
import it.uniroma2.model.javaclass.JavaClass;
import it.uniroma2.model.releases.Release;
import it.uniroma2.model.releases.ReleaseMeta;

public class ReportWriter {

    private FileWriter relFile;
    private FileWriter issuesFile;
    private FileWriter proportionFile;
    private FileWriter gitFile;

    public ReportWriter(String projName) throws IOException {
        Files.createDirectories(PathBuilder.buildReportPath(projName));

        Path relPath = PathBuilder.buildReportReleasesPath(projName);
        Path issuesPath = PathBuilder.buildReportIssuesPath(projName);
        Path proportionPath = PathBuilder.buildReportProportionPath(projName);
        Path gitPath = PathBuilder.buildReportGitPath(projName);

        File relFile = new File(relPath.toString());
        File issuesFile = new File(issuesPath.toString());
        File proportionFile = new File(proportionPath.toString());
        File gitFile = new File(gitPath.toString());

        this.relFile = new FileWriter(relFile);
        this.issuesFile = new FileWriter(issuesFile);
        this.proportionFile = new FileWriter(proportionFile);
        this.gitFile = new FileWriter(gitFile);
    }

    public static void writeln(String message, FileWriter writer) {
        try {
            writer.append(message + "\n");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void write(String message, FileWriter writer) {
        try {
            writer.append(message);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeTitle(String title, FileWriter writer) {
        writeln("------ " + title + " ------\n", writer);
    }

    public void writeMargin(FileWriter writer) {
        writeln("---------------------------------------------------------", writer);
    }

    // RELEASE ReportWriter
    public void writeRelease(ReleaseMeta release) {
        writeMargin(relFile);
        writeln("ID: " + release.getId(), relFile);
        writeln("Name: " + release.getName(), relFile);
        writeln("Date: " + release.getDate(), relFile);
    }

    public void writeReleases(List<ReleaseMeta> releases) {
        writeTitle("RELEASES", relFile);
        for (ReleaseMeta release : releases) {
            writeRelease(release);
        }
        writeMargin(relFile);
        writeln("TOTAL NUMBER OF RELEASES: " + releases.size() + "\n", relFile);
    }

    // PROPORTION ReportWriter
    public void writeProportion(double prop, List<TicketIssue> prevIssues) {
        writeMargin(proportionFile);
        writeTitle("PROPORTION", proportionFile);
        if (prevIssues.size() < 5)
            writeln("COLD START (CROSS-PROJECT) APPLIED", proportionFile);
        else
            writeln("INCREMENTAL APPLIED", proportionFile);
        writeln("Proportion: " + prop, proportionFile);
        writeln("TOTAL NUMBER OF VALID ISSUES: " + prevIssues.size() + "\n", proportionFile);
    }

    // ISSUE ReportWriter
    public void writeAv(List<ReleaseMeta> av) {
        writeln("AFFECTED VERSIONS: ", issuesFile);
        int i = 0;
        for (ReleaseMeta release : av) {
            write((release.getName()) + (i == av.size() - 1 ? "" : " -- "), issuesFile);
            i++;
        }
    }

    public void writeIssue(TicketIssue issue) throws TicketException {
        writeMargin(issuesFile);
        writeln("Key: " + issue.getKey(), issuesFile);
        writeln("IV: " + issue.getIV().getName(), issuesFile);
        writeln("OV: " + issue.getOv().getName(), issuesFile);
        writeln("FV: " + issue.getFv().getName(), issuesFile);
        writeAv(issue.getAv());
    }

    public void writeIssues(List<TicketIssue> issues) throws TicketException {
        writeTitle("ISSUES", issuesFile);

        List<ReleaseMeta> totalAvs = new ArrayList<>();
        for (TicketIssue issue : issues) {
            writeIssue(issue);

            List<ReleaseMeta> av = issue.getAv();
            for (ReleaseMeta releaseMeta : av) {
                if (!totalAvs.contains(releaseMeta)) {
                    totalAvs.add(releaseMeta);
                }
            }
        }
        writeMargin(issuesFile);
        writeln("TOTAL NUMBER OF ISSUES: " + issues.size(), issuesFile);
        writeln("TOTAL NUMBER OF AFFECTED VERSIONS: " + totalAvs.size() + "\n", issuesFile);
    }

    // GIT ReportWriter
    public void writeNumOfCommits(int numberCommits) {
        writeTitle("GIT", gitFile);
        writeln("Number of total commits: " + numberCommits, gitFile);
    }

    public void writeNumOfCommitsForRelease(int numOfCommits, String releaseName) {
        writeMargin(gitFile);
        writeln("Number of commits for release " + releaseName + ": " + numOfCommits, gitFile);
    }

    public void writeTotalNumOfCommitsForReleases(int totalNumOfCommits) {
        writeMargin(gitFile);
        writeln("Total number of commits for all releases: " + totalNumOfCommits, gitFile);
    }

    public void writeNumOfClassesForRelease(int numOfClasses, String releaseName) {
        writeMargin(gitFile);
        writeln("Number of classes for release " + releaseName + ": " + numOfClasses, gitFile);
    }

    public void writeReleasesCommitsForClasses(List<Release> releases) {
        writeMargin(gitFile);
        writeln("COMMITS THAT CHANGED A CLASS OF EACH RELEASE", gitFile);
        for (Release release : releases) {
            writeMargin(gitFile);
            writeln("Release: " + release.getName(), gitFile);
            for (JavaClass clazz : release.getClasses()) {
                writeMargin(gitFile);
                writeln("Class PATH: " + clazz.toString(), gitFile);
                writeln("", gitFile);
                writeln("Number of commits: " + release.getClassesCommits().get(clazz).size(), gitFile);
            }
        }
    }

    // CLOSE FILES
    public void closeFiles() throws IOException {
        relFile.close();
        proportionFile.close();
        issuesFile.close();
        gitFile.close();

    }

}
