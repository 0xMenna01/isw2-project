package it.uniroma2.factory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

import it.uniroma2.controller.issues.ColdStart;
import it.uniroma2.enums.ExecutorState;
import it.uniroma2.enums.ProjectKey;
import it.uniroma2.exception.EnumException;
import it.uniroma2.utils.ProportionUtils;

public class ParallelColdStartFactory {

    private static final AtomicReference<ParallelColdStartFactory> instance = new AtomicReference<>(null);

    private ProjectKey[] keys;
    // The executor manages the threads that compute the proportion for each project
    private ExecutorService parallelExec = null;
    // These are the tasks that will be executed in parallel and will return the
    // proportion
    private List<Callable<Double>> tasks = null;
    private ExecutorState state;

    private Double prop;

    private ParallelColdStartFactory() {

        this.keys = new ProjectKey[]{ProjectKey.AVRO, ProjectKey.FALCON,
            ProjectKey.STORM, ProjectKey.ZOOKEEPER, ProjectKey.OPENJPA};

        this.state = ExecutorState.NOT_READY;
        this.prop = null;

    }

    public static ParallelColdStartFactory getInstance() throws EnumException {
        ParallelColdStartFactory result = instance.get();
        if (result == null) {
            ParallelColdStartFactory newValue = new ParallelColdStartFactory();
            if (instance.compareAndSet(null, newValue)) {
                result = newValue;

                for (ProjectKey key : instance.get().keys) {

                    if (key.equals(ProjectKey.BOOKEEPER) || key.equals(ProjectKey.SYNCOPE))
                        throw new EnumException("Error: Coldstart must be made cross-project");
                }
            } else {
                result = instance.get();
            }

        }
        return result;
    }

    public void initConcurrency() throws EnumException {

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

    private void addTasks() throws EnumException {
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
            throw new EnumException("Executor not initialized");
        }
    }

    public double getProportion()
        throws InterruptedException, ExecutionException, EnumException {
        switch (state) {
            case DONE:
                return prop;
            case READY: {
                List<Future<Double>> results = parallelExec.invokeAll(tasks);
                // Computes the median of proportions
                int validProj = keys.length; // number of projects with enough tickets
                // to compute proportion

                List<Double> props = new ArrayList<>();
                for (Future<Double> res : results) {
                    double proportion = res.get();
                    if (proportion == -1) { // not enough tickets
                        validProj--;
                        continue;
                    }
                    props.add(proportion);
                }
                // Shutdown the pool and reset the tasks
                parallelExec.shutdown();
                tasks = null;
                if (validProj == 0)
                    throw new EnumException("None of the projects is valid to compute proportion");
                this.prop = ProportionUtils.computeMedian(props);
                this.state = ExecutorState.DONE;
                return prop;
            }
            default: {
                this.state = ExecutorState.ERROR;
                throw new EnumException("Executor not properly managed");
            }
        }
    }

}
