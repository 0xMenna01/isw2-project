package it.uniroma2.factory;

import it.uniroma2.enums.CostSensitive;
import it.uniroma2.enums.FeatureSel;
import it.uniroma2.enums.Sampling;
import it.uniroma2.model.weka.ClassifierMeta;
import it.uniroma2.model.weka.WekaBestFirst;
import it.uniroma2.model.weka.WekaClassifier;
import it.uniroma2.model.weka.WekaClassifiers;
import it.uniroma2.model.weka.WekaSampling;
import weka.classifiers.Classifier;
import weka.classifiers.CostMatrix;
import weka.classifiers.meta.CostSensitiveClassifier;
import weka.classifiers.meta.FilteredClassifier;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;

public class ClassifierEvaluationFactory {
    private static WekaClassifiers classifiers;

    // Feature selection based on different Best first strategies
    private static AttributeSelection featureSelection;
    private static WekaBestFirst bestFirst;

    // Contains all the sampling methods
    private static WekaSampling sampling;
    private static CostSensitiveClassifier costSensitiveClassifier;

    static {
        classifiers = new WekaClassifiers();
        // Setup feature selection
        featureSelection = new AttributeSelection();
        bestFirst = new WekaBestFirst();
        // Setup Sampling
        sampling = new WekaSampling();

        costSensitiveClassifier = buildCostSensitiveClassifier();
    }

    public static WekaClassifier buildClassifier(ClassifierMeta eval, int falseNumber, int trueNumber) {

        Classifier classifier = getClassifier(eval);

        Filter sampler = getSampler(eval, falseNumber, trueNumber);
        AttributeSelection featureSel = getFeatureSelection(eval);
        CostSensitiveClassifier costSensitive = getCostSensitiveClassifier(eval);

        if (sampler != null && costSensitive != null)
            throw new IllegalStateException("Cannot enable both sampling and cost sensitive");


        FilteredClassifier innerClassifier = null;
        if (sampler != null || costSensitive != null) {

            if (sampler != null) {
                innerClassifier = new FilteredClassifier();
                innerClassifier.setFilter(sampler);
                innerClassifier.setClassifier(classifier);
                classifier = innerClassifier;
            } else {
                costSensitive.setClassifier(classifier);
                classifier = costSensitive;
            }

            if (featureSel != null) {
                FilteredClassifier externalClassifier = new FilteredClassifier();
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

        if (selection.equals(FeatureSel.NO_FEATURE_SEL)) {
            return null;
        } else {
            featureSelection.setSearch(bestFirst.getBestFirst(selection));
            return featureSelection;
        }
    }


    private static Filter getSampler(ClassifierMeta eval, int falseNumber, int trueNumber) {
        Sampling samplingMethod = eval.getMethod().getSampling();

        switch (samplingMethod) {
            case NO_SAMPLING:
                return null;
            case OVER_SAMPLING:

                double percentStandardOversampling =
                    (100.0 * falseNumber) / (falseNumber + trueNumber);

                sampling.setSampleSizePercent(percentStandardOversampling);
                return sampling.getResample();

            case UNDER_SAMPLING:
                return sampling.getSpreadSubsample();

            case SMOTE:
                double smotePercentage = 0;
                if (trueNumber > 0)
                    smotePercentage = ((falseNumber - trueNumber) / ((double) trueNumber)) * 100;

                sampling.setSmotePercentage(smotePercentage);
                return sampling.getSmote();

            default:
                throw new IllegalArgumentException("Sampling not supported");
        }
    }


    private static CostSensitiveClassifier getCostSensitiveClassifier(ClassifierMeta eval) {
        CostSensitive costSensitive = eval.getMethod().getCostSensitive();
        if (costSensitive.equals(CostSensitive.COST_SENSITIVE))
            return costSensitiveClassifier;

        return null;
    }


    private static CostSensitiveClassifier buildCostSensitiveClassifier() {
        CostSensitiveClassifier costSensitive = new CostSensitiveClassifier();
        costSensitive.setMinimizeExpectedCost(true);

        CostMatrix costMatrix = new CostMatrix(2);
        costMatrix.setCell(0, 0, 0.0);
        costMatrix.setCell(1, 1, 0.0);
        costMatrix.setCell(0, 1, 10.0);
        costMatrix.setCell(1, 0, 1.0);

        costSensitive.setCostMatrix(costMatrix);

        return costSensitive;
    }

}
