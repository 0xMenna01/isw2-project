package it.uniroma2.utils;

import java.io.FileWriter;
import java.io.IOException;

public class CsvUtils {

    public static final String SEPARATOR = ",";
    public static final String NEW_LINE = "\n";

    private CsvUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static void writeHeader(String[] header, FileWriter writer) throws IOException {
        for (int i = 0; i < header.length; i++) {
            writer.append(header[i]);
            if (i < header.length - 1) {
                writer.append(SEPARATOR);
            }
        }
        writer.append(NEW_LINE);

    }

}
