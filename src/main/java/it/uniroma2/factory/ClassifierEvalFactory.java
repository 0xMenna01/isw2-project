package it.uniroma2.factory;

import it.uniroma2.enums.FeatureSel;
import it.uniroma2.enums.Sampling;
import it.uniroma2.model.weka.ClassifierMeta;
import it.uniroma2.model.weka.WekaBestFirst;
import it.uniroma2.model.weka.WekaClassifier;
import it.uniroma2.model.weka.WekaClassifiers;
import weka.classifiers.Classifier;
import weka.classifiers.meta.FilteredClassifier;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;
import weka.filters.supervised.instance.Resample;
import weka.filters.supervised.instance.SpreadSubsample;

public class ClassifierEvalFactory {
    // Contains all the classifiers
    private static WekaClassifiers classifiers;

    // Feature selection based on different Best first strategies
    private static AttributeSelection featureSelection;
    private static WekaBestFirst bestFirst;

    // Contains all the sampling methods
    private static Resample overSampling;
    private static SpreadSubsample underSampling;

    static {
        classifiers = new WekaClassifiers();
        // Setup feature selection
        featureSelection = new AttributeSelection();
        bestFirst = new WekaBestFirst();

        buildSampling();
    }

    public static WekaClassifier buildClassifier(ClassifierMeta eval) {

        Classifier classifier = getClassifier(eval);

        Filter sampler = getSampler(eval);
        AttributeSelection featureSel = getFeatureSelection(eval);

        FilteredClassifier innerClassifier = null;
        FilteredClassifier externalClassifier = null;
        if (sampler != null) {
            innerClassifier = new FilteredClassifier();
            innerClassifier.setClassifier(classifier);
            innerClassifier.setFilter(sampler);

            classifier = innerClassifier;

            if (featureSel != null) {
                externalClassifier = new FilteredClassifier();
                externalClassifier.setFilter(featureSel);
                externalClassifier.setClassifier(classifier);

                classifier = externalClassifier;
            }

        } else if (featureSel != null) {
            innerClassifier = new FilteredClassifier();
            innerClassifier.setFilter(featureSel);

            classifier = innerClassifier;
        }

        return new WekaClassifier(classifier, eval.getProj(), eval.getWalkForwardIterationIndex(),
            eval.getTrainingPercent(), eval.getMethod());
    }

    private static Classifier getClassifier(ClassifierMeta eval) {
        switch (eval.getMethod().getClassifier()) {
            case RANDOM_FOREST:
                return classifiers.getRandomForest();
            case NAIVE_BAYES:
                return classifiers.getNaiveBayes();
            case IBK:
                return classifiers.getiBk();
            default:
                throw new IllegalArgumentException("Classifier not supported");
        }
    }

    private static AttributeSelection getFeatureSelection(ClassifierMeta eval) {
        FeatureSel selection = eval.getMethod().getFeatureSel();

        switch (selection) {
            case NO_FEATURE_SEL:
                return null;
            default:
                featureSelection.setSearch(bestFirst.getBestFirst(selection));
                return featureSelection;
        }
    }

    private static Filter getSampler(ClassifierMeta eval) {
        Sampling sampling = eval.getMethod().getSampling();

        switch (sampling) {
            case NO_SAMPLING:
                return null;
            case OVER_SAMPLING:

                double percentStandardOversampling = ((
                    100.0 * sampling.getMajorityClassSize()) / (sampling.getMajorityClassSize() +
                    sampling.getMinorityClassSize())) * 2;

                overSampling.setSampleSizePercent(percentStandardOversampling);
                return overSampling;

            case UNDER_SAMPLING:
                return underSampling;
            default:
                throw new IllegalArgumentException("Sampling not supported");
        }
    }


    private static void buildSampling() {
        underSampling = new SpreadSubsample();
        underSampling.setDistributionSpread(1.0);
        overSampling = new Resample();
        overSampling.setBiasToUniformClass(1.0);
    }

}
