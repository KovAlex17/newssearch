package com.newssearch.service.CSVservice;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CsvFileManager {
    private final String filePath;
    private BufferedWriter writer;
    private static final String CSV_HEADER = "number,text,theme,created_at\n";

    public CsvFileManager(String filePath) {
        this.filePath = filePath;
    }

    public void openForAppend() throws IOException {
        Path path = Paths.get(filePath);
        boolean fileExists = Files.exists(path);
        boolean isEmpty = fileExists && Files.size(path) == 0;

        this.writer = new BufferedWriter(new FileWriter(filePath, true)); // append=true - режим дозаписи

        if (!fileExists || isEmpty) {
            writer.write(CSV_HEADER);
            writer.flush();
        }
    }

    public void writeLine(String line) throws IOException {
        if (writer != null) {
            writer.write(line);
            writer.newLine();
        }
    }

    public void close() throws IOException {
        if (writer != null) {
            writer.close();
        }
    }

    public String getFilePath() {
        return filePath;
    }
}
