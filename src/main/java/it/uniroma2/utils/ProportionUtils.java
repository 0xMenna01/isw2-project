package it.uniroma2.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import it.uniroma2.exception.TicketException;
import it.uniroma2.model.TicketIssue;

public class ProportionUtils {

    private static int THRESHOLD = 5;

    private ProportionUtils() {
        throw new IllegalStateException("Utility class");
    }

    // compute the average of [ (fv - iv) / (fv - ov) ] based on issues
    public static double computeProportion(List<TicketIssue> prevIssues) throws TicketException {
        // Cheks if number of issues for the proportion computation are at least a given
        // threshold
        // The method returns -1 if there are not enough tickets
        int validForProp = prevIssues.size();
        List<Double> proportions = new ArrayList<>();

        for (TicketIssue issue : prevIssues) {
            double num = issue.getFv().getId() - issue.getIV().getId();
            double den = issue.getFv().getId() - issue.getOv().getId();
            if (den == 0) {
                validForProp--;
                continue;
            }

            double proportion = num / den;
            proportions.add(proportion);
        }

        if (validForProp < THRESHOLD)
            return -1;

        Collections.sort(proportions);
        int n = proportions.size();
        double median;
        if (n % 2 == 0) {
            median = (proportions.get((n / 2) - 1) + proportions.get(n / 2)) / 2.0;
        } else {
            median = proportions.get(n / 2);
        }

        return median;
    }

}
