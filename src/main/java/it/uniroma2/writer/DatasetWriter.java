package it.uniroma2.writer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import it.uniroma2.enums.CsvType;
import it.uniroma2.model.javaclass.JavaClass;
import it.uniroma2.model.releases.Release;
import it.uniroma2.model.releases.Releases;
import it.uniroma2.utils.CsvUtils;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;

public class DatasetWriter {

    private static final String[] HEADER = new String[] { "VERSION", "FILE", "LOC", "N_FIX",
            "N_AUTHORS", "AVG_LOC_ADD", "CHURN", "AVG_CHURN", "AGE", "AVG_FIX_CHURN", "FANOUT", "NUM_REVISIONS",
            "IS_BUGGY" };

    private String projName;

    public DatasetWriter(String projName) throws IOException {
        this.projName = projName;
        Files.createDirectories(PathBuilder.buildTrainingSetPath(projName));
        Files.createDirectories(PathBuilder.buildTestingSetPath(projName));
        Files.createDirectories(PathBuilder.buildWekaTrainDirectories(projName));
        Files.createDirectories(PathBuilder.buildWekaTestDirectories(projName));
    }

    public void writeSet(int wolkForewardStep, Releases releases, CsvType type) throws IOException {
        Path outDir = type == CsvType.TRAINING ? PathBuilder.buildTrainingSetFile(projName)
                : PathBuilder.buildTestingSetFile(projName);

        if (type == CsvType.TRAINING) {
            writeTraining(wolkForewardStep, outDir.toString(), releases);
        } else {
            writeTesting(outDir.toString(), releases);
        }
    }

    private void writeTraining(int step, String outDir, Releases releases) throws IOException {
        File trainFile = new File(outDir + step + ".csv");
        try (FileWriter fileWriter = new FileWriter(trainFile)) {
            CsvUtils.writeHeader(HEADER, fileWriter);

            for (Release rel : releases.getReleases()) {
                for (JavaClass clazz : rel.getClasses()) {
                    writeInstance(fileWriter, rel.getId().toString(), clazz);
                }
            }

        }

        convertCsvToArff(step, trainFile, true);
    }

    private void writeTesting(String outDir, Releases releases) throws IOException {

        for (int i = 0; i < releases.getReleases().size(); i++) {
            File testFile = new File(outDir + (i + 1) + ".csv");
            try (FileWriter fileWriter = new FileWriter(testFile)) {
                CsvUtils.writeHeader(HEADER, fileWriter);

                for (JavaClass clazz : releases.get(i).getClasses()) {
                    writeInstance(fileWriter, releases.get(i).getId().toString(), clazz);
                }

            }
            convertCsvToArff(i + 1, testFile, false);
        }
    }

    private static void writeInstance(FileWriter fileWriter, String relId, JavaClass clazz)
            throws IOException {

        fileWriter.append(
                relId +
                        CsvUtils.SEPARATOR +
                        clazz.getPathName() +
                        CsvUtils.SEPARATOR +
                        clazz.getSize() +
                        CsvUtils.SEPARATOR +
                        clazz.getnFix() +
                        CsvUtils.SEPARATOR +
                        clazz.getnAuthors() +
                        CsvUtils.SEPARATOR +
                        clazz.getAvgLocAdded() +
                        CsvUtils.SEPARATOR +
                        clazz.getChurn() +
                        CsvUtils.SEPARATOR +
                        clazz.getAvgChurn() +
                        CsvUtils.SEPARATOR +
                        clazz.getAge() +
                        CsvUtils.SEPARATOR +
                        clazz.getAvgFixChurn() +
                        CsvUtils.SEPARATOR +
                        clazz.getFanOut() +
                        CsvUtils.SEPARATOR +
                        clazz.getNumOfRevisions() +
                        CsvUtils.SEPARATOR +
                        clazz.isBuggy() +
                        CsvUtils.NEW_LINE);
    }

    private void convertCsvToArff(int step, File file, boolean isTraining) throws IOException {
        // load CSV file
        CSVLoader loader = new CSVLoader();
        loader.setSource(file);
        Instances data = loader.getDataSet();

        // save ARFF file
        String output = isTraining ? PathBuilder.buildWekaTrainFile(projName).toString() + step + ".arff"
                : PathBuilder.buildWekaTestFile(projName).toString() + step + ".arff";

        ArffSaver saver = new ArffSaver();
        saver.setInstances(data);
        saver.setFile(new File(output));
        saver.writeBatch();
    }
}
