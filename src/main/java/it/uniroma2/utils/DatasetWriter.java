package it.uniroma2.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import it.uniroma2.enums.CsvType;
import it.uniroma2.model.Release;
import it.uniroma2.model.Releases;
import it.uniroma2.model.javaclass.JavaClass;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;

public class DatasetWriter {

    private static final String TRAINING = "outputs/trainingset/";
    private static final String TESTING = "outputs/testingset/";

    private DatasetWriter() {
        throw new IllegalStateException("Utility class");
    }

    public static void writeSet(String setName, Releases releases, CsvType type) throws IOException {
        String outDir = type == CsvType.TRAINING ? TRAINING : TESTING;

        if (type == CsvType.TRAINING) {
            writeTraining(setName, outDir, releases);
        } else {
            writeTesting(setName, outDir, releases);
        }
    }

    private static void writeTraining(String setName, String outDir, Releases releases) throws IOException {
        try (FileWriter fileWriter = new FileWriter(outDir + setName + ".csv")) {
            writeHeader(fileWriter);

            for (Release rel : releases.getReleases()) {
                for (JavaClass clazz : rel.getClasses()) {
                    writeInstance(fileWriter, rel.getId().toString(), clazz);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        convertCsvToArff(outDir + setName);
    }

    private static void writeTesting(String setName, String outDir, Releases releases) throws IOException {

        for (int i = 0; i < releases.getReleases().size(); i++) {
            try (FileWriter fileWriter = new FileWriter(outDir + setName + (i + 1) + ".csv")) {
                writeHeader(fileWriter);

                for (JavaClass clazz : releases.get(i).getClasses()) {
                    writeInstance(fileWriter, releases.get(i).getId().toString(), clazz);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            convertCsvToArff(outDir + setName + (i + 1));
        }
    }

    private static void writeInstance(FileWriter fileWriter, String relId, JavaClass clazz) throws IOException {
        fileWriter.append(relId);
        fileWriter.append(",");
        fileWriter.append(clazz.getPathName());
        fileWriter.append(",");
        fileWriter.append(String.valueOf(clazz.getSize()));
        fileWriter.append(",");
        fileWriter.append(String.valueOf(clazz.getnFix()));
        fileWriter.append(",");
        fileWriter.append(String.valueOf(clazz.getnAuthors()));
        fileWriter.append(",");
        fileWriter.append(String.valueOf(clazz.getAvgLocAdded()));
        fileWriter.append(",");
        fileWriter.append(String.valueOf(clazz.getChurn()));
        fileWriter.append(",");
        fileWriter.append(String.valueOf(clazz.getAvgChurn()));
        fileWriter.append(",");
        fileWriter.append(String.valueOf(clazz.getAge()));
        fileWriter.append(",");
        fileWriter.append(String.valueOf(clazz.getAvgFixChurn()));
        fileWriter.append(",");
        fileWriter.append(String.valueOf(clazz.getFanOut()));
        fileWriter.append(",");
        fileWriter.append(String.valueOf(clazz.getNumOfRevisions()));
        fileWriter.append(",");
        fileWriter.append(String.valueOf(clazz.isBuggy()));
        fileWriter.append("\n");
    }

    private static void writeHeader(FileWriter fileWriter) throws IOException {

        fileWriter.append(
                "Version,File Name,LOC,nFix,nAuthors,avgLocAdded,churn,avgChurn,age,avgFixChurn,fanOut,numRevisions, isBuggy");

        fileWriter.append("\n");
    }

    private static void convertCsvToArff(String fileName) throws IOException {
        // load CSV file
        CSVLoader loader = new CSVLoader();
        loader.setSource(new File(fileName + ".csv"));
        Instances data = loader.getDataSet();

        // save ARFF file
        int startIndex = fileName.indexOf("/") + 1;
        String output = "outputs/weka/" + fileName.substring(startIndex, fileName.length()) + ".arff";

        ArffSaver saver = new ArffSaver();
        saver.setInstances(data);
        saver.setFile(new File(output));
        saver.writeBatch();
    }
}
