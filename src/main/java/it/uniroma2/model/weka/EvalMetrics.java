package it.uniroma2.model.weka;

public class EvalMetrics {

    private double precision;
    private double recall;
    private double auc;
    private double kappa;

    public EvalMetrics(double precision, double recall, double auc, double kappa) {
        this.precision = precision;
        this.recall = recall;
        this.auc = auc;
        this.kappa = kappa;
    }

    public double getPrecision() {
        return precision;
    }

    public double getRecall() {
        return recall;
    }

    public double getAuc() {
        return auc;
    }

    public double getKappa() {
        return kappa;
    }

}
