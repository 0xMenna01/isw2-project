package it.uniroma2.model.weka;

import it.uniroma2.enums.ProjectKey;
import weka.classifiers.Classifier;

public class WekaClassifier extends ClassifierMeta {

    private Classifier classifier;

    public WekaClassifier(Classifier classifier, ProjectKey proj, int walkForwardIterationIndex, double trainingPercent,
            ClassifierMethod method) {
        super(proj, walkForwardIterationIndex, trainingPercent, method);
        this.classifier = classifier;
    }

    public Classifier getClassifier() {
        return classifier;
    }

}
