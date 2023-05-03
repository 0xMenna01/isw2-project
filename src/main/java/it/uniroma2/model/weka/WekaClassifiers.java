package it.uniroma2.model.weka;

import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.lazy.IBk;
import weka.classifiers.trees.RandomForest;

public class WekaClassifiers {

    private RandomForest randomForest;
    private NaiveBayes naiveBayes;
    private IBk iBk;

    public WekaClassifiers() {
        this.randomForest = new RandomForest();
        this.naiveBayes = new NaiveBayes();
        this.iBk = new IBk();
    }

    public RandomForest getRandomForest() {
        return randomForest;
    }

    public NaiveBayes getNaiveBayes() {
        return naiveBayes;
    }

    public IBk getiBk() {
        return iBk;
    }
}
