package it.uniroma2.controller;

import java.util.List;
import java.util.concurrent.ExecutionException;

import it.uniroma2.exception.ParallelColdStartException;
import it.uniroma2.exception.TicketException;
import it.uniroma2.factory.ParallelColdStartFactory;
import it.uniroma2.model.ReleaseMeta;
import it.uniroma2.model.TicketIssue;
import it.uniroma2.utils.ProportionUtils;

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

    public static Proportion getInstance() throws Exception {
        if (instance == null) {
            throw new Exception("Must firt set ov, and fv when getting the instance");
        }
        return instance;
    }

    // This method implements the Proportion technique
    // if the number of previous valid issues is at least 5 then applies incremental
    // else cold start (cross-project) in a parallel manner where each thread
    // computes the proportion of one project
    public void compute(List<TicketIssue> prevIssues)
            throws InterruptedException, ExecutionException, ParallelColdStartException, TicketException {
        if ((this.prop = ProportionUtils.computeProportion(prevIssues)) == -1) {
            // if method returns -1 it means there are not enogh tickets,
            // so apply cold start
            ParallelColdStartFactory.getInstance().initConcurrecy();
            this.prop = ParallelColdStartFactory.getInstance().getProportion();
        }
    }

    public int getIdIV() throws Exception {
        if (this.prop == null)
            throw new Exception("Proportion has not been computed");
        int id = (int) Math.ceil(fv.getId() - (fv.getId() - ov.getId()) * this.prop);
        if (id <= 0)
            id = 1;
        else if (id >= ov.getId())
            id = ov.getId() - 1;

        return id;
    }

}
