package it.uniroma2.factory;

import it.uniroma2.model.weka.ClassifierMeta;
import it.uniroma2.model.weka.WekaClassifier;
import it.uniroma2.model.weka.WekaClassifiers;
import weka.attributeSelection.GreedyStepwise;
import weka.classifiers.Classifier;
import weka.classifiers.meta.FilteredClassifier;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;
import weka.filters.supervised.instance.ClassBalancer;
import weka.filters.supervised.instance.SpreadSubsample;

public class ClassifierEvalFactory {
    // Contains all the classifiers
    private static WekaClassifiers classifiers;

    // Contains all the feature selection methods
    private static AttributeSelection forewardSearch;
    private static AttributeSelection backwardSearch;

    // Contains all the sampling methods
    private static ClassBalancer oversampling;
    private static SpreadSubsample undersampling;

    static {
        classifiers = new WekaClassifiers();

        forewardSearch = new AttributeSelection();
        backwardSearch = new AttributeSelection();

        GreedyStepwise greedyF = new GreedyStepwise();
        greedyF.setSearchBackwards(false);
        forewardSearch.setSearch(greedyF);

        GreedyStepwise greedyB = new GreedyStepwise();
        greedyB.setSearchBackwards(true);
        backwardSearch.setSearch(greedyB);

        oversampling = new ClassBalancer();
        undersampling = new SpreadSubsample();
        undersampling.setDistributionSpread(1.0);
    }

    public static WekaClassifier buildClassifier(ClassifierMeta eval) {

        FilteredClassifier innerClassifier = new FilteredClassifier();
        innerClassifier.setClassifier(getClassifier(eval));

        Filter sampler = getSampler(eval);
        AttributeSelection featureSel = getFeatureSel(eval);
        FilteredClassifier externalClassifier = null;
        if (sampler != null) {
            innerClassifier.setFilter(sampler);

            if (featureSel != null) {
                externalClassifier = new FilteredClassifier();
                externalClassifier.setFilter(featureSel);
                externalClassifier.setClassifier(innerClassifier);
            }

        } else if (featureSel != null) {
            innerClassifier.setFilter(featureSel);
        }

        FilteredClassifier classifier = externalClassifier != null ? externalClassifier : innerClassifier;

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

    private static AttributeSelection getFeatureSel(ClassifierMeta eval) {
        switch (eval.getMethod().getFeatureSel()) {
            case NOFEATURESEL:
                return null;
            case BACKWARD_SEARCH:
                return backwardSearch;
            case FORWARD_SEARCH:
                return forewardSearch;
            default:
                throw new IllegalArgumentException("Feature selection not supported");
        }
    }

    private static Filter getSampler(ClassifierMeta eval) {

        switch (eval.getMethod().getSampling()) {
            case NOSAMPLING:
                return null;
            case OVERSAMPLING:
                return oversampling;
            case UNDERSAMPLING:
                return undersampling;
            default:
                throw new IllegalArgumentException("Sampling not supported");
        }
    }

}
