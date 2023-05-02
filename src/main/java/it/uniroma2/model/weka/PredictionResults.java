package it.uniroma2.model.weka;

public class PredictionResults {
    private double tp;
    private double fp;
    private double tn;
    private double fn;

    public PredictionResults(double tp, double fp, double tn, double fn) {
        this.tp = tp;
        this.fp = fp;
        this.tn = tn;
        this.fn = fn;
    }

    public double getTp() {
        return tp;
    }

    public double getFp() {
        return fp;
    }

    public double getTn() {
        return tn;
    }

    public double getFn() {
        return fn;
    }

}
