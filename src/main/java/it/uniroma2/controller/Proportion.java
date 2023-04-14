package it.uniroma2.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import it.uniroma2.exception.TicketException;
import it.uniroma2.model.TicketIssue;
import it.uniroma2.utils.ProportionUtils;

public class Proportion {

    private static Proportion instance = null;
    private Float prop;

    private Proportion() {
        this.prop = null;
    }

    public static Proportion getInstance() {
        if (instance == null) {
            instance = new Proportion();
        }
        return instance;
    }

    // This method implements the Proportion technique
    // if number of previous valid issues is at least 5 then applies incremental
    // else cold start
    public void compute(List<TicketIssue> prevIssues) throws TicketException, InterruptedException, ExecutionException {
        if (prevIssues.size() >= 5) {
            this.prop = ProportionUtils.computeProportion(prevIssues);
        } else {
            // TODO
            String[] projectKeys = { "project1", "project2", "project3", "project4", "project5" };

            ExecutorService executorService = Executors.newFixedThreadPool(5);

            List<Callable<Float>> tasks = new ArrayList<>();
            for (String projectKey : projectKeys) {
                tasks.add(() -> {
                    ColdStart coldStart = new ColdStart(projectKey);
                    coldStart.start();
                    return coldStart.getProportion();
                });
            }

            // Submit all tasks to the pool and wait for results
            List<Future<Float>> results = executorService.invokeAll(tasks);

            // Compute the total proportion
            float totalProportion = 0;
            for (Future<Float> result : results) {
                totalProportion += result.get();
            }

            // Shutdown the pool
            executorService.shutdown();

            // Print the total proportion
            System.out.println("Total proportion: " + totalProportion);

        }

    }

}
