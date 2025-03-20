package com.newssearch.controller;

import com.newssearch.model.HtmlSelector;
import com.newssearch.model.MessageContainer;
import com.newssearch.service.DatabaseManager;
import com.newssearch.service.NewsFeedProcessor;
import com.newssearch.service.NewsFeedReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NewsController {
    private final NewsFeedReader newsFeedReader;
    private final NewsFeedProcessor newsFeedProcessor;
    private final DatabaseManager databaseManager;

    public NewsController() {
        this.newsFeedReader = new NewsFeedReader();
        this.newsFeedProcessor = new NewsFeedProcessor();
        this.databaseManager = new DatabaseManager();
    }

    private static final Logger logger = LoggerFactory.getLogger(NewsController.class);

    /**
     * Запускает процесс обработки новостных лент.
     */
    public void startProcessing() {
        try {
            List<HtmlSelector> newsFeeds = newsFeedReader.readNewsFeeds("src/main/resources/news.txt");
            if (!newsFeeds.isEmpty()) {
                ExecutorService executorService = Executors.newFixedThreadPool(1);

                for (HtmlSelector newsFeed : newsFeeds) {
                    CompletableFuture.runAsync(() -> processNewsFeed(newsFeed), executorService)
                            .exceptionally(ex -> {
                                logger.error("Error handling news feeds: {}", ex.getMessage(), ex);
                                return null;
                            });
                }
                executorService.shutdown();
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

                databaseManager.writeMessages(messages, collectionName, universityName);

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