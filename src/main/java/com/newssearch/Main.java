package com.newssearch;

import com.newssearch.controller.NewsController;
import com.newssearch.service.CSVservice.CsvDataProcessor;
import com.newssearch.service.CSVservice.CsvFileManager;
import com.newssearch.service.CSVservice.NewsDataParser;
import com.newssearch.service.CSVservice.NewsDataService;
import com.newssearch.service.GptFilterService;
import com.newssearch.service.NewsCombinerToCommonFile;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {

            NewsController newsController = new NewsController();
            newsController.startProcessing();

            //NewsCombinerToCommonFile.combineNews();

            //GptFilterService.gptFiltering();



        // 5. Сохранение в csv файл
//
//        CsvFileManager csvFileManager = new CsvFileManager("news_data.csv");
//        CsvDataProcessor csvDataProcessor = new CsvDataProcessor(csvFileManager);
//        NewsDataParser parser = new NewsDataParser();
//        NewsDataService service = new NewsDataService(csvDataProcessor, parser);
//
//        try {
//            service.processSaveJsonToCsv("llm_answer.json");
//            System.out.println("Данные успешно сохранены в " + "news_data.csv");
//        } catch (IOException e) {
//            System.err.println("Ошибка: " + e.getMessage());
//        }

    }
}