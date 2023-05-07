package it.uniroma2.model.weka;

import it.uniroma2.enums.ClassifierName;
import it.uniroma2.enums.CostSensitive;
import it.uniroma2.enums.FeatureSel;
import it.uniroma2.enums.Sampling;

public class ClassifierMethod {

    private final ClassifierName classifier;
    private final FeatureSel featureSel;
    private final Sampling sampling;
    private final CostSensitive costSensitive;

    public ClassifierMethod(ClassifierName classifier, FeatureSel featureSel, Sampling sampling, CostSensitive costSensitive) {
        this.classifier = classifier;
        this.featureSel = featureSel;
        this.sampling = sampling;
        this.costSensitive = costSensitive;
    }

    public ClassifierName getClassifier() {
        return classifier;
    }

    public FeatureSel getFeatureSel() {
        return featureSel;
    }

    public Sampling getSampling() {
        return sampling;
    }

    public CostSensitive getCostSensitive() {
        return costSensitive;
    }

    @Override
    public String toString() {
        return classifier.toString() + "&" + featureSel.toString() + "&" + sampling.toString()
            + "&" + costSensitive.toString();
    }


}
