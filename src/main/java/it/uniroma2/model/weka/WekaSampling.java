package it.uniroma2.model.weka;

import weka.filters.supervised.instance.Resample;
import weka.filters.supervised.instance.SMOTE;
import weka.filters.supervised.instance.SpreadSubsample;

public class WekaSampling {

    private Resample resample;
    private SpreadSubsample spreadSubsample;
    private SMOTE smote;

    public WekaSampling() {
        // Under sampling
        spreadSubsample = new SpreadSubsample();
        spreadSubsample.setDistributionSpread(1.0);

        // Over sampling
        resample = new Resample();
        resample.setNoReplacement(false);
        resample.setBiasToUniformClass(1.0);

        // SMOTE
        smote = new SMOTE();
        smote.setClassValue("1");
    }

    public Resample getResample() {
        return resample;
    }

    public SpreadSubsample getSpreadSubsample() {
        return spreadSubsample;
    }

    public SMOTE getSmote() {
        return smote;
    }

    public void setSampleSizePercent(double percent) {
        resample.setSampleSizePercent(2 * percent);
    }

    public void setSmotePercentage(double percent) {
        smote.setPercentage(percent);
    }
}
