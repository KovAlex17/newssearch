package com.newssearch.controller;

import com.newssearch.model.HtmlSelector;
import com.newssearch.model.MessageContainer;
import com.newssearch.service.*;
import com.newssearch.service.CSVservice.JSONtoCSVService;
import com.newssearch.service.ExcelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;


public class NewsController {
    private final InputNewsInfoTxtParser inputNewsInfoTxtParser;
    private final NewsFeedProcessor newsFeedProcessor;
    private final DatabaseManager databaseManager;

    private final MessageToJSONtempClass messageToJSONtempClass;
    private final JSONtoCSVService jsoNtoCSVService;
    private final ExcelService excelService;

    private final ConcurrentHashMap<String, ConcurrentHashMap<String, MessageContainer>> allFeedsGroupedMessagesByWeek;

    public NewsController() {
        this.inputNewsInfoTxtParser = new InputNewsInfoTxtParser();
        this.newsFeedProcessor = new NewsFeedProcessor();
        this.databaseManager = new DatabaseManager();
        this.messageToJSONtempClass = new MessageToJSONtempClass();
        this.jsoNtoCSVService = new JSONtoCSVService();
        this.excelService = new ExcelService();
        this.allFeedsGroupedMessagesByWeek = new ConcurrentHashMap<>();
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


//                for (Map.Entry<String, ConcurrentHashMap<String, MessageContainer>> weekEntry : allFeedsGroupedMessagesByWeek.entrySet()) {
//                    String weekKey = weekEntry.getKey();
//                    ConcurrentHashMap<String, MessageContainer> messagesMap = weekEntry.getValue();
//
//                    System.out.println("Неделя: " + weekKey);
//                    for (Map.Entry<String, MessageContainer> msgEntry : messagesMap.entrySet()) {
//                        String link = msgEntry.getKey();
//                        MessageContainer msg = msgEntry.getValue();
//
//                        // Пример вывода основных полей
//                        System.out.println("  Ссылка: " + link);
//                        System.out.println("  Заголовок: " + msg.getTitle());
//                        System.out.println("  Дата: " + msg.getDate());
//                        System.out.println("  Текст: " + msg.getText());
//                        System.out.println("  ---");
//                    }
//                }


                excelService.updateWeeklySheets(allFeedsGroupedMessagesByWeek);


//                for (Map.Entry<String, List<MessageContainer>> entry : groupedMessagesByWeek.entrySet()) {
//                    System.out.println("Неделя " + entry.getKey() + ": " + entry.getValue().size() + " сообщений");
//                    for (MessageContainer message : entry.getValue()) {
//                        System.out.println("  - " + message.getTitle());
//                    }
//                }

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


                for (MessageContainer message : messages) {
                    //String jsonString = messageToJSONtempClass.convertToJson(message);
                    //jsoNtoCSVService.addJsonObject(jsonString);
                    String sheetName = excelService.parseSheetName(message.getDate());

                    allFeedsGroupedMessagesByWeek
                            .computeIfAbsent(sheetName, k -> new ConcurrentHashMap<String, MessageContainer>())
                            .put(message.getLink(), message);


                    excelService.addWeek(sheetName);
                }


                //jsoNtoCSVService.writeAllToCsv("messages.csv");


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
}