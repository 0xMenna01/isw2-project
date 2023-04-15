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

public class ParallelColdStartFactory {

    private static volatile ParallelColdStartFactory instance = null;
    private ProjectKey[] keys;
    // The executor manages the threads that compute the proportion for each project
    private ExecutorService parallelExec = null;
    // These are the tasks that will be executed in parallel and will return the
    // proportion
    private List<Callable<Double>> tasks = null;
    private ExecutorState state;

    private Double prop;

    private ParallelColdStartFactory() {
        this.keys = new ProjectKey[] { ProjectKey.AVRO, ProjectKey.OPENJPA,
                ProjectKey.STORM, ProjectKey.ZOOKEEPER };
        this.state = ExecutorState.NOT_READY;
        this.prop = null;

    }

    public static ParallelColdStartFactory getInstance() {
        if (instance == null) {
            synchronized (ParallelColdStartFactory.class) {
                if (instance == null) {
                    instance = new ParallelColdStartFactory();
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
                    return coldStart.getProportion();
                });
            }
        } else {
            this.state = ExecutorState.ERROR;
            throw new ParallelColdStartException("Executor not initialized");
        }
    }

    public double getProportion() throws InterruptedException, ExecutionException, ParallelColdStartException {
        switch (state) {
            case DONE:
                return prop;
            case READY: {
                List<Future<Double>> results = parallelExec.invokeAll(tasks);
                // Compute the average proportion
                double totalProportion = 0;
                for (Future<Double> res : results) {
                    totalProportion += res.get();
                }
                // Shutdown the pool and reset the tasks
                parallelExec.shutdown();
                tasks = null;
                this.prop = totalProportion / (double) keys.length;
                this.state = ExecutorState.DONE;
                return this.prop;
            }
            default: {
                this.state = ExecutorState.ERROR;
                throw new ParallelColdStartException("Executor not properly managed");
            }
        }
    }

}
