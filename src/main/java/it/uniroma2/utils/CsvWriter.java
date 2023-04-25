package it.uniroma2.utils;

import java.io.FileWriter;
import java.io.IOException;

import it.uniroma2.model.Release;
import it.uniroma2.model.Releases;
import it.uniroma2.model.javaclass.JavaClass;

public class CsvWriter {

    private CsvWriter() {
        throw new IllegalStateException("Utility class");
    }

    public static void writeCsv(String projName, Releases releases) {
        FileWriter fileWriter = null;
        try {
            fileWriter = null;
            String outname = "outputs/" + projName + "DataSet.csv";
            // Name of CSV for output
            fileWriter = new FileWriter(outname);
            fileWriter.append(
                    "Version,File Name,LOC,nFix,nAuthors,avgLocAdded,churn,avgChurn,age,avgTimeFix,fanOut,changeSetSize, isBuggy");
            fileWriter.append("\n");

            for (Release rel : releases.getReleases()) {
                for (JavaClass clazz : rel.getClasses()) {
                    fileWriter.append(rel.getId().toString());
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
                    fileWriter.append(String.valueOf(clazz.getAvgTimeFix()));
                    fileWriter.append(",");
                    fileWriter.append(String.valueOf(clazz.getFanOut()));
                    fileWriter.append(",");
                    fileWriter.append(String.valueOf(clazz.getChangeSetSize()));
                    fileWriter.append(",");
                    fileWriter.append(String.valueOf(clazz.isBuggy()));
                    fileWriter.append("\n");
                }
            }

        } catch (Exception e) {
            System.out.println("Error in csv writer");
            e.printStackTrace();
        } finally {
            try {
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                System.out.println("Error while flushing/closing fileWriter !!!");
                e.printStackTrace();
            }
        }
    }

}
