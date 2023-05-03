package it.uniroma2.writer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.util.List;

import it.uniroma2.model.weka.WekaClassifier;
import it.uniroma2.utils.CsvUtils;

public class EvaluationWriter {

    private static final String[] HEADER = new String[] {
            "TRAINING_RELEASES", "TRAINING_PERCENT", "CLASSIFIER",
            "PRECISION", "RECALL",
            "AUC", "KAPPA", "TRUE_POSITIVES", "FALSE_POSITIVES",
            "TRUE_NEGATIVES", "FALSE_NEGATIVES" };

    private String projName;

    public EvaluationWriter(String projName) throws IOException {
        this.projName = projName;
    }

    public void writeClassifiersEvaluation(List<WekaClassifier> wekaEvaluationList)
            throws IOException {
        Path outputPath = PathBuilder.buildWekaEvaluationPath(projName);

        File csvFile = new File(outputPath.toString());
        try (FileWriter fileWriter = new FileWriter(csvFile)) {
            CsvUtils.writeHeader(HEADER, fileWriter);
            for (WekaClassifier wekaEvaluation : wekaEvaluationList) {
                writeEvaluationInfo(fileWriter, wekaEvaluation);
            }
        }
    }

    private void writeEvaluationInfo(Writer writer, WekaClassifier wekaClassifier) throws IOException {
        String evaluationString = wekaClassifier.getWalkForwardIterationIndex() +
                CsvUtils.SEPARATOR +
                wekaClassifier.getTrainingPercent() +
                CsvUtils.SEPARATOR +
                wekaClassifier.getMethod().toString() +
                CsvUtils.SEPARATOR +
                wekaClassifier.getEvalMetrics().getPrecision() +
                CsvUtils.SEPARATOR +
                wekaClassifier.getEvalMetrics().getRecall() +
                CsvUtils.SEPARATOR +
                wekaClassifier.getEvalMetrics().getAuc() +
                CsvUtils.SEPARATOR +
                wekaClassifier.getEvalMetrics().getKappa() +
                CsvUtils.SEPARATOR +
                wekaClassifier.getResults().getTp() +
                CsvUtils.SEPARATOR +
                wekaClassifier.getResults().getFp() +
                CsvUtils.SEPARATOR +
                wekaClassifier.getResults().getTn() +
                CsvUtils.SEPARATOR +
                wekaClassifier.getResults().getFn() +
                CsvUtils.NEW_LINE;

        writer.append(evaluationString);
    }

}
