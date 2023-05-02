package it.uniroma2.model.weka;

import it.uniroma2.enums.ClassifierName;
import it.uniroma2.enums.FeatureSel;
import it.uniroma2.enums.Sampling;

public class ClassifierMethod {

    private final ClassifierName classifier;
    private final FeatureSel featureSel;
    private final Sampling sampling;

    public ClassifierMethod(ClassifierName classifier, FeatureSel featureSel, Sampling sampling) {
        this.classifier = classifier;
        this.featureSel = featureSel;
        this.sampling = sampling;
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

    

}
