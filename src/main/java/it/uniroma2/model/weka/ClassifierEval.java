package it.uniroma2.model.weka;

import it.uniroma2.enums.ProjectKey;

public class ClassifierEval extends ClassifierMeta {

    private boolean costSensitive;
    private EvalMetrics evalMetrics;
    private PredictionResults results;

    public ClassifierEval(ProjectKey proj, int walkForwardIterationIndex, double trainingPercent,
            ClassifierMethod method, boolean costSensitive, EvalMetrics evalMetrics, PredictionResults results) {
        super(proj, walkForwardIterationIndex, trainingPercent, method);
        this.costSensitive = costSensitive;
        this.evalMetrics = evalMetrics;
        this.results = results;
    }

    public boolean isCostSensitive() {
        return costSensitive;
    }

    public EvalMetrics getEvalMetrics() {
        return evalMetrics;
    }

    public PredictionResults getResults() {
        return results;
    }

    

}
