package com.newssearch.service.CSVservice;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class NewsDataService {
    private final CsvDataProcessor csvDataProcessor;
    private final NewsDataParser parser;

    public NewsDataService(CsvDataProcessor csvDataProcessor, NewsDataParser parser) {
        this.csvDataProcessor = csvDataProcessor;
        this.parser = parser;
    }

    public void processSaveJsonToCsv(String jsonFilePath) throws IOException {

        String jsonContent = new String(Files.readAllBytes(Paths.get(jsonFilePath)));
        List<NewsData> newsDataList = parser.parse(jsonContent);

        csvDataProcessor.process(newsDataList);
    }
}
