package it.uniroma2.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import it.uniroma2.exception.TicketException;
import it.uniroma2.model.TicketIssue;

public class ProportionUtils {

    private static int threshold = 5;

    private ProportionUtils() {
        throw new IllegalStateException("Utility class");
    }

    // compute the average of [ (fv - iv) / (fv - ov) ] based on issues
    public static double computeProportion(List<TicketIssue> prevIssues) throws TicketException {
        // Checks if number of issues for the proportion computation are at least a given
        // threshold
        // The method returns -1 if there are not enough tickets based on previous VALID issues
        // Previous issues are valid if they've not been computed through cold start

        int numTickets = prevIssues.size();
        if (numTickets < threshold)
            return -1;

        double proportion = 0.0;
        for (TicketIssue issue : prevIssues) {
            double num = (double) issue.getFv().getId() - issue.getIV().getId();
            double den = (double) issue.getFv().getId() - issue.getOv().getId();
            den = den == 0 ? 1 : den;
            proportion += num / den;
        }

        return proportion / numTickets;
    }

    public static List<TicketIssue> getValidTicketsForIncremental(List<TicketIssue> issues) {
        List<TicketIssue> validIssues = new ArrayList<>();
        for (TicketIssue issue : issues) {
            if (!issue.isColdStart())
                validIssues.add(issue);
        }

        return validIssues;
    }

    public static double computeMedian(List<Double> values) {
        Collections.sort(values);
        int n = values.size();
        double median;
        if (n % 2 == 0) {
            median = (values.get((n / 2) - 1) + values.get(n / 2)) / 2.0;
        } else {
            median = values.get(n / 2);
        }
        return median;
    }

}
