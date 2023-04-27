package it.uniroma2.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import it.uniroma2.exception.TicketException;
import it.uniroma2.model.Release;
import it.uniroma2.model.ReleaseMeta;
import it.uniroma2.model.TicketIssue;
import it.uniroma2.model.javaclass.JavaClass;

public class ReportWriter {

    private static final String RELEASE_FILE = "outputs/releases.txt";
    private static final String ISSUE_FILE = "outputs/issues.txt";
    private static final String PROPORTION_FILE = "outputs/proportion.txt";
    private static final String GIT_FILE = "outputs/git.txt";
    private static FileWriter relFile;
    private static FileWriter issuesFile;
    private static FileWriter proportionFile;
    private static FileWriter gitFile;

    private ReportWriter() {
        throw new IllegalStateException("Utility class");
    }

    static {
        try {
            relFile = new FileWriter(RELEASE_FILE, true);
            issuesFile = new FileWriter(ISSUE_FILE, true);
            proportionFile = new FileWriter(PROPORTION_FILE, true);
            gitFile = new FileWriter(GIT_FILE, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeln(String message, FileWriter writer) {
        try {
            writer.write(message + "\n");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void write(String message, FileWriter writer) {
        try {
            writer.write(message);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeTitle(String title, FileWriter writer) {
        writeln("------ " + title + " ------\n", writer);
    }

    public static void writeMargin(FileWriter writer) {
        writeln("---------------------------------------------------------", writer);
    }

    // RELEASE ReportWriter
    public static void writeRelease(ReleaseMeta release) {
        writeMargin(relFile);
        writeln("ID: " + release.getId(), relFile);
        writeln("Name: " + release.getName(), relFile);
        writeln("Date: " + release.getDate(), relFile);
    }

    public static void writeReleases(List<ReleaseMeta> releases) {
        writeTitle("RELEASES", relFile);
        for (ReleaseMeta release : releases) {
            writeRelease(release);
        }
        writeMargin(relFile);
        writeln("TOTAL NUMBER OF RELEASES: " + releases.size() + "\n", relFile);
    }

    // PROPORTION ReportWriter
    public static void writeProportion(double prop, List<TicketIssue> prevIssues) {
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
    public static void writeAv(List<ReleaseMeta> av) {
        writeln("AFFECTED VERSIONS: ", issuesFile);
        int i = 0;
        for (ReleaseMeta release : av) {
            write((release.getName()) + (i == av.size() - 1 ? "" : " -- "), issuesFile);
            i++;
        }
    }

    public static void writeIssue(TicketIssue issue) throws TicketException {
        writeMargin(issuesFile);
        writeln("Key: " + issue.getKey(), issuesFile);
        writeln("IV: " + issue.getIV().getName(), issuesFile);
        writeln("OV: " + issue.getOv().getName(), issuesFile);
        writeln("FV: " + issue.getFv().getName(), issuesFile);
        writeAv(issue.getAv());
    }

    public static void writeIssues(List<TicketIssue> issues) throws TicketException {
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
    public static void writeNumOfCommits(int numberCommits) {
        writeTitle("GIT", gitFile);
        writeln("Number of total commits: " + numberCommits, gitFile);
    }

    public static void writeNumOfCommitsForRelease(int numOfCommits, String releaseName) {
        writeMargin(gitFile);
        writeln("Number of commits for release " + releaseName + ": " + numOfCommits, gitFile);
    }

    public static void writeTotalNumOfCommitsForReleases(int totalNumOfCommits) {
        writeMargin(gitFile);
        writeln("Total number of commits for all releases: " + totalNumOfCommits, gitFile);
    }

    public static void writeNumOfClassesForRelease(int numOfClasses, String releaseName) {
        writeMargin(gitFile);
        writeln("Number of classes for release " + releaseName + ": " + numOfClasses, gitFile);
    }

    public static void writeReleasesCommitsForClasses(List<Release> releases) {
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
    public static void closeFiles() {
        try {
            relFile.close();
            proportionFile.close();
            issuesFile.close();
            gitFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
