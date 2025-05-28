package com.newssearch.service.CSVservice;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class CsvDataProcessor{
    private final CsvFileManager fileManager;

    public CsvDataProcessor(CsvFileManager fileManager) {
        this.fileManager = fileManager;
    }


    public void process(List<NewsData> newsDataList) throws IOException {
        try {
            fileManager.openForAppend();

            String currentDateTime = LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));

            for (NewsData newsData : newsDataList) {
                fileManager.writeLine(String.format("%d,\"%s\",%d,\"%s\"",
                        newsData.getNumber(),
                        escapeCsv(newsData.getText()),
                        newsData.getTheme(),
                        currentDateTime));
            }
        } finally {
            fileManager.close();
        }
    }

    private String escapeCsv(String input) {
        return input.replace("\"", "\"\"");
    }
}
