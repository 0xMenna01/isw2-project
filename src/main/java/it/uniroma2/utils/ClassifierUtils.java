package it.uniroma2.utils;

import java.util.ArrayList;
import java.util.List;

import it.uniroma2.enums.ClassifierName;
import it.uniroma2.enums.CostSensitive;
import it.uniroma2.enums.FeatureSel;
import it.uniroma2.enums.Sampling;
import it.uniroma2.model.GenericPair;
import it.uniroma2.model.weka.ClassifierMethod;
import it.uniroma2.writer.PathBuilder;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class ClassifierUtils {

    private ClassifierUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static List<ClassifierMethod> generateClassifierComb() {
        List<ClassifierMethod> classifierMethods = new ArrayList<>();
        for (ClassifierName classifier : ClassifierName.values()) {
            for (FeatureSel featureSel : FeatureSel.values()) {
                for (CostSensitive costSensitive : CostSensitive.values()) {
                    for (Sampling sampling : Sampling.values()) {
                        addIfValid(classifier, featureSel, costSensitive, sampling, classifierMethods);
                    }
                }
            }
        }
        return classifierMethods;
    }


    private static void addIfValid(ClassifierName classifier, FeatureSel featureSel, CostSensitive costSensitive, Sampling sampling,
                                   List<ClassifierMethod> classifierMethods) {
        if (costSensitive.equals(CostSensitive.NO_COST_SENSITIVE) ||
            sampling.equals(Sampling.NO_SAMPLING)) {
            classifierMethods.add(new ClassifierMethod(classifier, featureSel, sampling, costSensitive));
        }
    }

    private static GenericPair<Instances, Instances> getTrainingAndTesting(String proj, int i) throws Exception {

        DataSource source1 = new DataSource(
            PathBuilder.buildWekaTrainFile(proj).toString() + i + ".arff");
        DataSource source2 = new DataSource(
            PathBuilder.buildWekaTestFile(proj).toString() + i + ".arff");

        Instances training = source1.getDataSet();
        Instances testing = source2.getDataSet();

        training.setClassIndex(training.numAttributes() - 1);
        testing.setClassIndex(testing.numAttributes() - 1);

        return new GenericPair<>(training, testing);
    }

    public static Instances getTrainSet(String proj, int i) throws Exception {
        return getTrainingAndTesting(proj, i).getFirst();

    }

    public static Instances getTestSet(String proj, int i) throws Exception {
        return getTrainingAndTesting(proj, i).getSecond();

    }


}
