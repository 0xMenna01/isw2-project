package it.uniroma2.utils;

import java.util.ArrayList;
import java.util.List;

import it.uniroma2.enums.ClassifierName;
import it.uniroma2.enums.FeatureSel;
import it.uniroma2.enums.Sampling;
import it.uniroma2.model.weka.ClassifierMethod;

public class ClassifierUtils {
    
    private ClassifierUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static List<ClassifierMethod> generateClassifierComb() {
        List<ClassifierMethod> classifierMethods = new ArrayList<>();
        for (ClassifierName classifier : ClassifierName.values()) {
            for (FeatureSel featureSel : FeatureSel.values()) {
                for (Sampling sampling : Sampling.values()) {
                    classifierMethods.add(new ClassifierMethod(classifier, featureSel, sampling));
                }
            }
        }
        return classifierMethods;
    }
}
