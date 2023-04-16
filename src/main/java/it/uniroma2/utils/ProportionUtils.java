package it.uniroma2.utils;

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

        double prop = 0;

        double num;
        double den;
        int i = 0;
        for (; i < prevIssues.size(); i++) {
            num = prevIssues.get(i).getFv().getId() - prevIssues.get(i).getIV().getId();
            den = prevIssues.get(i).getFv().getId() - prevIssues.get(i).getOv().getId();
            den = den == 0 ? 1 : den; // if fv == ov then set the denominator to 1
            prop += num / den;
        }

        return prop / (double) i;
    }

}
