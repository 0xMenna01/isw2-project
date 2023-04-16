package it.uniroma2.factory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import it.uniroma2.controller.ColdStart;
import it.uniroma2.enums.ExecutorState;
import it.uniroma2.enums.ProjectKey;
import it.uniroma2.exception.ParallelColdStartException;
import it.uniroma2.exception.TicketException;
import it.uniroma2.model.TicketIssue;
import it.uniroma2.utils.ProportionUtils;

public class ParallelColdStartFactory {

    private static volatile ParallelColdStartFactory instance = null;
    private ProjectKey[] keys;
    // The executor manages the threads that compute the proportion for each project
    private ExecutorService parallelExec = null;
    // These are the tasks that will be executed in parallel and will return the
    // proportion
    private List<Callable<List<TicketIssue>>> tasks = null;
    private ExecutorState state;

    private Double prop;

    private ParallelColdStartFactory() {

        this.keys = new ProjectKey[] { ProjectKey.AVRO, ProjectKey.OPENJPA,
                ProjectKey.STORM, ProjectKey.ZOOKEEPER, ProjectKey.FALCON };
        this.state = ExecutorState.NOT_READY;
        this.prop = null;

    }

    public static ParallelColdStartFactory getInstance() throws ParallelColdStartException {
        if (instance == null) {
            synchronized (ParallelColdStartFactory.class) {
                if (instance == null) {
                    instance = new ParallelColdStartFactory();
                    for (ProjectKey key : instance.keys) {

                        if (key.equals(ProjectKey.BOOKEEPER) || key.equals(ProjectKey.SYNCOPE))
                            throw new ParallelColdStartException("Error: Coldstart must be made cross-project");
                    }
                }
            }
        }
        return instance;
    }

    public void initConcurrecy() throws ParallelColdStartException {

        if (state.equals(ExecutorState.NOT_READY)) {
            // Initialize the executor with a fixed thread pool
            parallelExec = Executors.newFixedThreadPool(keys.length);
            // Initialize and add execution tasks
            tasks = new ArrayList<>();
            this.state = ExecutorState.INIT;
            addTasks();
            this.state = ExecutorState.READY;
        }
    }

    private void addTasks() throws ParallelColdStartException {
        if (state.equals(ExecutorState.INIT)) {
            for (ProjectKey key : keys) {
                tasks.add(() -> {
                    ColdStart coldStart = new ColdStart(key);
                    coldStart.start();
                    return coldStart.getIssues();
                });
            }
        } else {
            this.state = ExecutorState.ERROR;
            throw new ParallelColdStartException("Executor not initialized");
        }
    }

    public double getProportion()
            throws InterruptedException, ExecutionException, ParallelColdStartException, TicketException {
        switch (state) {
            case DONE:
                return prop;
            case READY: {
                List<Future<List<TicketIssue>>> results = parallelExec.invokeAll(tasks);
                // Compute the average proportion based on valid tickets of other projects
                List<TicketIssue> totalTickets = new ArrayList<>();
                for (Future<List<TicketIssue>> res : results) {
                    totalTickets.addAll(res.get());
                }
                // Shutdown the pool and reset the tasks
                parallelExec.shutdown();
                tasks = null;
                this.prop = ProportionUtils.computeProportion(totalTickets);
                this.state = ExecutorState.DONE;
                return prop;
            }
            default: {
                this.state = ExecutorState.ERROR;
                throw new ParallelColdStartException("Executor not properly managed");
            }
        }
    }

}
