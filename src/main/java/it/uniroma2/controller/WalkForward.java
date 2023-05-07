package it.uniroma2.controller;

import java.io.IOException;
import java.util.List;

import it.uniroma2.enums.CsvType;
import it.uniroma2.exception.TicketException;
import it.uniroma2.model.FixCommit;
import it.uniroma2.model.TicketIssue;
import it.uniroma2.model.javaclass.JavaClass;
import it.uniroma2.model.releases.Release;
import it.uniroma2.model.releases.Releases;
import it.uniroma2.utils.GitUtils;
import it.uniroma2.writer.DatasetWriter;

public class WalkForward {

    private WalkForward() {
        throw new IllegalStateException("Class with static methods only");
    }

    public static void execute(Releases releases, List<TicketIssue> issues, String projName)
        throws TicketException, IOException {
        Releases dataSet = new Releases();
        DatasetWriter writer = new DatasetWriter(projName);

        int lastReleaseIndex = releases.getReleases().size() - 1;
        int halfIndex = releases.getReleases().size() / 2;
        for (int i = 0; i < releases.getReleases().size(); i++) {
            resetBug(dataSet);

            dataSet.add(releases.get(i));

            if (i < halfIndex || i == lastReleaseIndex) {

                computeBug(issues, releases.get(i), releases, dataSet);

                if (i == lastReleaseIndex) {
                    int lastTrainingIndex = (int) Math.ceil((double) lastReleaseIndex / 2);
                    writer.writeSet(i + 1, new Releases(dataSet.getReleases().subList(1, lastTrainingIndex + 1)),
                        CsvType.TESTING);
                } else
                    writer.writeSet(i + 1, dataSet, CsvType.TRAINING);
            }
        }
    }

    private static void resetBug(Releases releases) {
        for (Release rel : releases.getReleases()) {
            for (JavaClass javaClass : rel.getClasses()) {
                javaClass.setBuggy(false);
            }
        }
    }

    private static void computeBug(List<TicketIssue> issues, Release rel, Releases releases, Releases dataSet) throws TicketException {
        for (int j = 0; j < issues.size() && !issues.get(j).getFv().isAfter(rel); j++) {
            List<FixCommit> fixCommits = GitUtils.getTicketCommitsReleases(dataSet, issues.get(j));

            for (FixCommit fixCommit : fixCommits) {
                GitUtils.setBugginess(fixCommit, releases, issues.get(j));
            }
        }
    }
}