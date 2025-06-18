package com.newssearch.controller;

import com.newssearch.model.HtmlSelector;
import com.newssearch.model.MessageContainer;
import com.newssearch.service.*;
import com.newssearch.service.CSVservice.JSONtoCSVService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

public class NewsController {
    private final InputNewsInfoTxtParser inputNewsInfoTxtParser;
    private final NewsFeedProcessor newsFeedProcessor;
    private final DatabaseManager databaseManager;

    private final MessageToJSONtempClass messageToJSONtempClass;
    private final JSONtoCSVService jsoNtoCSVService;

    private final Set<String> allWeeks = ConcurrentHashMap.newKeySet();

    public NewsController() {
        this.inputNewsInfoTxtParser = new InputNewsInfoTxtParser();
        this.newsFeedProcessor = new NewsFeedProcessor();
        this.databaseManager = new DatabaseManager();
        this.messageToJSONtempClass = new MessageToJSONtempClass();
        this.jsoNtoCSVService = new JSONtoCSVService();
    }

    private static final Logger logger = LoggerFactory.getLogger(NewsController.class);

    /**
     * Запускает процесс обработки новостных лент.
     */
    public void startProcessing() {
        try {
            List<HtmlSelector> newsFeeds = inputNewsInfoTxtParser.readNewsFromFile("src/main/resources/news.txt");
            if (!newsFeeds.isEmpty()) {
                ExecutorService executorService = Executors.newFixedThreadPool(1);
                List<CompletableFuture<Void>> futures = new ArrayList<>();

                for (HtmlSelector newsFeed : newsFeeds) {
                    CompletableFuture<Void> future = CompletableFuture
                            .runAsync(() -> processNewsFeed(newsFeed), executorService)
                            .exceptionally(ex -> {
                                logger.error("Error handling news feeds: {}", ex.getMessage(), ex);
                                return null;
                            });
                    futures.add(future);
                }
                CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
                executorService.shutdown();

                //System.out.println(allWeeks);

            } else {
                logger.info("No news feeds found in the file.");
            }
        } catch (IOException e) {
            logger.error("Failed to read news feeds from file", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Обрабатывает новостную ленту и записывает данные в базу данных.
     *
     * @param selector Селектор для обработки новостной ленты.
     */
    private void processNewsFeed(HtmlSelector selector) {
        try {
            List<MessageContainer> messages = newsFeedProcessor.processNewsFeed(selector);
            if (!messages.isEmpty()) {
                String universityName = extractRootDomain(selector.getMainUrlSelector());
                String collectionName = selector.getGroup() + "_" + universityName;

                //databaseManager.writeMessages(messages, collectionName, universityName);

//                for (MessageContainer message : messages) {
//                    messageToJSONtempClass.convertToJson(message);
//                    System.out.println(messageToJSONtempClass.convertToJson(message));
//                }


                for (MessageContainer message : messages) {
                    String jsonString = messageToJSONtempClass.convertToJson(message);
                    jsoNtoCSVService.addJsonObject(jsonString);
                    addWeek(message.getDate());
                }
                jsoNtoCSVService.printJsonArray();


                jsoNtoCSVService.writeAllToCsv("messages.csv");


                logger.info("Обработана лента сайта {}", selector.getMainUrlSelector());
            }
        } catch (IOException e) {
            logger.error("Ошибка при обработке новостной ленты", e);
        }
    }

    /**
     * Извлекает корневой домен из URL.
     *
     * @param url URL для обработки.
     * @return Корневой домен.
     */
    private String extractRootDomain(String url) {
        int protocolIndex = url.indexOf("://");
        String domain = protocolIndex != -1 ? url.substring(protocolIndex + 3) : url;

        int pathIndex = domain.indexOf('/');
        if (pathIndex != -1) {
            domain = domain.substring(0, pathIndex);
        }

        String[] parts = domain.split("\\.");
        if (parts.length >= 2) {
            return parts[parts.length - 2];
        }

        return domain;
    }

    private void addWeek(String dateStr) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDate date = LocalDate.parse(dateStr, formatter);

        // Определяем год и номер недели по ISO-8601
        WeekFields wf     = WeekFields.ISO;
        int yearBased   = date.get(wf.weekBasedYear());
        int weekOfYear  = date.get(wf.weekOfWeekBasedYear());
        String weekKey  = String.format("%d-W%02d", yearBased, weekOfYear);

        allWeeks.add(weekKey);
    }
}