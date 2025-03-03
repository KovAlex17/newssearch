package com.newssearch.controller;

import com.newssearch.model.HtmlSelector;
import com.newssearch.service.InputTxtParser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.List;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NewsController {
    List<HtmlSelector> newsFeeds;

    public NewsController() {
        try {
            newsFeeds = InputTxtParser.readNewsFromFile("src/main/resources/news.txt");
            if (!newsFeeds.isEmpty()) {
                ExecutorService executorService = Executors.newFixedThreadPool(3);

                for (HtmlSelector newsFeed : newsFeeds) {
                    CompletableFuture.runAsync(() -> handlingNewsFeed(newsFeed), executorService)
                            .exceptionally(ex -> {
                                System.err.println("Error handling news feeds: " + ex.getMessage());
                                return null;
                            });
                }
                executorService.shutdown();
            } else {
                System.out.println("No news feeds found in the file.");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void handlingNewsFeed(HtmlSelector selector){
        try {
            Document doc = Jsoup.connect(selector.getMainUrlSelector() + "/" + selector.getUrlSelector()).get();
            Elements items = doc.select(selector.getItemSelector());

            for (Element newsItem : items) {
                String title = newsItem.select(selector.getTitleSelector()).text();
                String link = selector.getMainUrlSelector() + newsItem.select("a").attr(selector.getLinkSelector());
                String date = newsItem.select(selector.getDateSelector()).text();
                String text = extractText(selector.getTextSelector(), link);

                synchronized (System.out) {
                    System.out.println("Title: " + title);
                    System.out.println("Link: " + link);
                    System.out.println("Date: " + date);
                    System.out.println("Text: " + text);
                    System.out.println();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private String extractText(String textSelector, String link){
        try {
            Document textDoc = Jsoup.connect(link).get();
            Element articleContent = textDoc.selectFirst(textSelector);

            if (articleContent != null) {
                return articleContent.text();
            } else {
                System.err.println("Article content not found on page: " + link);
                return "";
            }
        } catch (IOException e) {
            System.err.println("Failed to fetch article: " + link);
            e.printStackTrace();
            return "";
        }
    }
}
