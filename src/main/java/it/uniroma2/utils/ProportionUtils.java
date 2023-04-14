package it.uniroma2.utils;

import java.util.List;

import it.uniroma2.exception.TicketException;
import it.uniroma2.model.TicketIssue;

public class ProportionUtils {

    private ProportionUtils() {
        throw new IllegalStateException("Utility class");
    }

    // compute the average of [ (fv - iv) / (fv - ov) ] based on issues
    public static float computeProportion(List<TicketIssue> prevIssues) throws TicketException {
        float prop = 0;

        int num;
        int den;
        int i = 0;
        for (; i < prevIssues.size(); i++) {
            num = prevIssues.get(i).getFv().getId() - prevIssues.get(i).getIV().getId();
            den = prevIssues.get(i).getFv().getId() - prevIssues.get(i).getOv().getId();
            den = den == 0 ? 1 : den; // if fv == ov then set the denominator to 1
            prop += num / den;
        }

        return prop / i;
    }
}
