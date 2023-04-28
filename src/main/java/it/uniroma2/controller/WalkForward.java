package it.uniroma2.controller;

import java.util.List;

import it.uniroma2.exception.TicketException;
import it.uniroma2.model.FixCommit;
import it.uniroma2.model.Release;
import it.uniroma2.model.Releases;
import it.uniroma2.model.TicketIssue;
import it.uniroma2.model.javaclass.JavaClass;
import it.uniroma2.utils.GitUtils;

public class WalkForward {

    private WalkForward() {
        throw new IllegalStateException("Class with static methods only");
    }

    public static void execute(Releases rels, List<TicketIssue> issues) throws TicketException {
        Releases dataSet = new Releases();

        for (int i = 0; i < rels.getReleases().size(); i++) {
            resetBugginess(dataSet);

            dataSet.add(rels.get(i));

            if (i < rels.getReleases().size() / 2 || i == rels.getReleases().size() - 1) {

                for (int j = 0; j < issues.size() && !issues.get(j).getFv().isAfter(rels.get(i)); j++) {
                    List<FixCommit> fixCommits = GitUtils.getTicketCommitsReleases(dataSet, issues.get(j));

                    for (FixCommit fixCommit : fixCommits) {
                        GitUtils.setBugginess(fixCommit, rels, issues.get(j));
                    }
                }

                if (i == rels.getReleases().size() - 1) {
                    // Testing Set
                } else {
                    // Training Set
                }
            }
        }
    }

    private static void resetBugginess(Releases rels) {
        for (Release rel : rels.getReleases()) {
            for (JavaClass javaClass : rel.getClasses()) {
                javaClass.setBuggy(false);
            }
        }
    }
}