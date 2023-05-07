package it.uniroma2.controller.issues;

import java.util.List;
import java.util.concurrent.ExecutionException;

import it.uniroma2.exception.EnumException;
import it.uniroma2.exception.PropException;
import it.uniroma2.exception.TicketException;
import it.uniroma2.factory.ParallelColdStartFactory;
import it.uniroma2.model.TicketIssue;
import it.uniroma2.model.releases.ReleaseMeta;
import it.uniroma2.utils.ProportionUtils;
import it.uniroma2.writer.ReportWriter;

public class Proportion {

    private static Proportion instance = null;
    private ReleaseMeta ov;
    private ReleaseMeta fv;
    private Double prop = null;

    private Proportion() {
    }

    public static Proportion getInstance(ReleaseMeta ov, ReleaseMeta fv) {
        if (instance == null) {
            instance = new Proportion();
        }
        instance.ov = ov;
        instance.fv = fv;
        return instance;
    }

    public static Proportion getInstance() throws PropException {
        if (instance == null) {
            throw new PropException("Must firt set ov, and fv when getting the instance");
        }
        return instance;
    }

    // This method implements the Proportion technique
    // if the number of previous valid issues is at least 5 then applies incremental
    // else cold start (cross-project) in a parallel manner where each thread
    // computes the proportion of one project
    public void compute(ReportWriter reportWriter, List<TicketIssue> prevIssues)
        throws InterruptedException, ExecutionException, EnumException, TicketException {
        if ((this.prop = ProportionUtils.computeProportion(prevIssues)) == -1) {
            // if method returns -1 it means there are not enough tickets,
            // so apply cold start
            ParallelColdStartFactory.getInstance().initConcurrency();
            this.prop = ParallelColdStartFactory.getInstance().getProportion();
        }
        // Print the proportion (Remove later on)
        reportWriter.writeProportion(prop, prevIssues);
    }

    public int getIdIV() throws PropException {
        if (this.prop == null)
            throw new PropException("Proportion has not been computed");
        double diffFvOv = Math.max(1, fv.getId() - ov.getId());
        int id = (int) Math.ceil(fv.getId() - diffFvOv * this.prop);
        if (id <= 0)
            id = 1;
        else if (id >= ov.getId())
            id = ov.getId() - 1;

        return id;
    }

}
