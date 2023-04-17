package it.uniroma2.utils;

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
        if (prevIssues.size() < THRESHOLD)
            return -1;

        double proportion = 0.0;
        int i = 0;
        for (; i < prevIssues.size(); i++) {
            double num = prevIssues.get(i).getFv().getId() - prevIssues.get(i).getIV().getId();
            double den = prevIssues.get(i).getFv().getId() - prevIssues.get(i).getOv().getId();
            den = den == 0 ? 1 : den;

            proportion += num / den;
        }

        return proportion / i;
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
