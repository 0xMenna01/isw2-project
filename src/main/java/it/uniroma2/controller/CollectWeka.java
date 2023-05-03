package it.uniroma2.controller;

import java.util.ArrayList;
import java.util.List;

import it.uniroma2.enums.ProjectKey;
import it.uniroma2.factory.ClassifierEvalFactory;
import it.uniroma2.model.weka.ClassifierMeta;
import it.uniroma2.model.weka.ClassifierMethod;
import it.uniroma2.model.weka.WekaClassifier;
import it.uniroma2.utils.ClassifierUtils;
import it.uniroma2.writer.EvaluationWriter;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instances;

public class CollectWeka {

    private final ProjectKey proj;
    private int numOfIter;
    private List<WekaClassifier> evals;

    public CollectWeka(ProjectKey proj, int numOfIter) {
        this.proj = proj;
        this.numOfIter = numOfIter;
        this.evals = new ArrayList<>();
    }

    public void execute() throws Exception {

        EvaluationWriter evalWriter = new EvaluationWriter(proj.toString());
        List<ClassifierMethod> methods = ClassifierUtils.generateClassifierComb();

        for (int i = 1; i <= numOfIter; i++) {

            Instances training = ClassifierUtils.getTrainSet(proj.toString(), i);
            training.setClassIndex(training.numAttributes() - 1);
            Instances testing = ClassifierUtils.getTestSet(proj.toString(), i);

            double tempTrainingPercent = 100.0 * training.numInstances()
                    / (training.numInstances() + testing.numInstances());

            Evaluation eval = new Evaluation(testing);

            // We make the evaluation based on different strategies (methods)
            for (ClassifierMethod method : methods) {
                ClassifierMeta classEval = new ClassifierMeta(proj, i, tempTrainingPercent, method);

                WekaClassifier wekaClassifier = ClassifierEvalFactory.buildClassifier(classEval);
                Classifier classifier = wekaClassifier.getClassifier();
                classifier.buildClassifier(training);

                eval.evaluateModel(wekaClassifier.getClassifier(), testing);
                wekaClassifier.setEvaluation(eval);

                this.evals.add(wekaClassifier);
            }

        }

        evalWriter.writeClassifiersEvaluation(evals);
    }

    public List<WekaClassifier> getEvals() {
        return evals;
    }

}
