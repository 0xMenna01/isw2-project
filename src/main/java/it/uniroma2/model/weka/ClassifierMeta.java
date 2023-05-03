package it.uniroma2.model.weka;

import it.uniroma2.enums.ProjectKey;
import weka.classifiers.Evaluation;

public class ClassifierMeta {

    protected ProjectKey proj;
    protected int walkForwardIterationIndex;
    protected double trainingPercent;
    protected ClassifierMethod method;
    private EvalMetrics evalMetrics;
    private PredictionResults results;

    public ClassifierMeta(ProjectKey proj, int walkForwardIterationIndex, double trainingPercent,
            ClassifierMethod method) {
        this.proj = proj;
        this.walkForwardIterationIndex = walkForwardIterationIndex;
        this.trainingPercent = trainingPercent;
        this.method = method;

    }

    public EvalMetrics getEvalMetrics() {
        return evalMetrics;
    }

    public PredictionResults getResults() {
        return results;
    }

    public ProjectKey getProj() {
        return proj;
    }

    public int getWalkForwardIterationIndex() {
        return walkForwardIterationIndex;
    }

    public double getTrainingPercent() {
        return trainingPercent;
    }

    public ClassifierMethod getMethod() {
        return method;
    }

    public void setEvaluation(Evaluation eval) {
        this.evalMetrics = new EvalMetrics(eval.precision(0), eval.recall(0), eval.areaUnderROC(0), eval.kappa());
        this.results = new PredictionResults(eval.numTruePositives(0), eval.numFalsePositives(0),
                eval.numTrueNegatives(0), eval.numFalseNegatives(0));
    }

}
