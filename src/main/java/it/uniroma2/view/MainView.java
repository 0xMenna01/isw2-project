package it.uniroma2.view;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import it.uniroma2.exception.TicketException;
import it.uniroma2.model.ReleaseMeta;
import it.uniroma2.model.TicketIssue;

public class MainView {

    private static final String RELEASE_FILE = "outputs/releases.txt";
    private static final String ISSUE_FILE = "outputs/issues.txt";
    private static final String PROPORTION_FILE = "outputs/proportion.txt";
    private static final String GIT_FILE = "outputs/git.txt";
    private static FileWriter relFile;
    private static FileWriter issuesFile;
    private static FileWriter proportionFile;
    private static FileWriter gitFile;

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

    public static void print(String message, FileWriter writer) {
        try {
            writer.write(message + "\n");
            writer.flush();
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the file.");
            e.printStackTrace();
        }
    }

    public static void print2(String message, FileWriter writer) {
        try {
            writer.write(message);
            writer.flush();
            // writer.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void printTitle(String title, FileWriter writer) {
        print("------ " + title + " ------\n", writer);
    }

    public static void printMargins(FileWriter writer) {
        print("---------------------------------------------------------", writer);
    }

    // RELEASE VIEW

    public static void printRelease(ReleaseMeta release) {
        printMargins(relFile);
        print("ID: " + release.getId(), relFile);
        print("Name: " + release.getName(), relFile);
        print("Date: " + release.getDate(), relFile);
    }

    public static void printReleases(List<ReleaseMeta> releases) {
        printTitle("RELEASES", relFile);
        for (ReleaseMeta release : releases) {
            printRelease(release);
        }
        printMargins(relFile);
        print("TOTAL NUMBER OF RELEASES: " + releases.size() + "\n", relFile);
    }

    // PROPORTION VIEW

    public static void printProportion(double prop, List<TicketIssue> prevIssues) {
        printMargins(proportionFile);
        printTitle("PROPORTION", proportionFile);
        if (prevIssues.size() < 5)
            print("COLD START (CROSS-PROJECT) APPLIED", proportionFile);
        else
            print("INCREMENTAL APPLIED", proportionFile);
        print("Proportion: " + prop, proportionFile);
        print("TOTAL NUMBER OF VALID ISSUES: " + prevIssues.size() + "\n", proportionFile);
    }

    // ISSUE VIEW

    public static void printAffectedVersions(List<ReleaseMeta> av) {
        print("AFFECTED VERSIONS: ", issuesFile);
        int i = 0;
        for (ReleaseMeta release : av) {
            print2((release.getName()) + (i == av.size() - 1 ? "" : " -- "), issuesFile);
            i++;
        }
    }

    public static void printIssue(TicketIssue issue) throws TicketException {
        printMargins(issuesFile);
        print("Key: " + issue.getKey(), issuesFile);
        print("IV: " + issue.getIV().getName(), issuesFile);
        print("OV: " + issue.getOv().getName(), issuesFile);
        print("FV: " + issue.getFv().getName(), issuesFile);
        printAffectedVersions(issue.getAv());
    }

    public static void printIssues(List<TicketIssue> issues) throws TicketException {
        printTitle("ISSUES", issuesFile);

        List<ReleaseMeta> totalAvs = new ArrayList<>();
        for (TicketIssue issue : issues) {
            printIssue(issue);

            List<ReleaseMeta> av = issue.getAv();
            for (ReleaseMeta releaseMeta : av) {
                if (!totalAvs.contains(releaseMeta)) {
                    totalAvs.add(releaseMeta);
                }
            }
        }
        printMargins(issuesFile);
        print("TOTAL NUMBER OF ISSUES: " + issues.size(), issuesFile);
        print("TOTAL NUMBER OF AFFECTED VERSIONS: " + totalAvs.size() + "\n", issuesFile);
    }

    // GIT VIEW
    public static void printNumberCommits(int numberCommits) {
        printTitle("GIT", gitFile);
        print("Number of total commits: " + numberCommits, gitFile);
    }

    public static void printNumOfCommitsFoRelease(int numOfCommits, String releaseName) {
        printMargins(gitFile);
        print("Number of commits for release " + releaseName + ": " + numOfCommits, gitFile);
    }

    public static void printTotalNumOfCommitsForReleases(int totalNumOfCommits) {
        printMargins(gitFile);
        print("Total number of commits for all releases: " + totalNumOfCommits, gitFile);
    }

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
