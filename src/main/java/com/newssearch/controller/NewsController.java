package com.newssearch.controller;

import com.newssearch.model.HtmlSelector;
import com.newssearch.model.MessageContainer;
import com.newssearch.service.InputTxtParser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.UnknownHostException;
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
                ExecutorService executorService = Executors.newFixedThreadPool(5);

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
        String url = "";
        boolean BFUlinkDetector = false;
        try {
            url = selector.getMainUrlSelector() + "/" + selector.getUrlSelector();
            Document doc = Jsoup.connect(url).get();
            Elements items = doc.select(selector.getItemSelector());

            for (Element newsItem : items) {

                MessageContainer message = getMessageInfo(selector, newsItem, BFUlinkDetector);
                BDController.BDWrite(message);
            }

        System.out.println("Новости успешно добавлены для университета " + url);

        } catch (UnknownHostException e){
            System.err.println("Skipping invalid link (UnknownHostException): " + url);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private MessageContainer getMessageInfo(HtmlSelector selector, Element newsItem, Boolean BFUlinkDetector){
        String title = newsItem.select(selector.getTitleSelector()).text();
        String link = "";
        Elements links = newsItem.select("a");
        for (Element a : links) {
            String href = a.attr(selector.getLinkSelector());
            if (!href.contains("?")  ) {
                link = selector.getMainUrlSelector() + href;
            }
            if (href.contains("//")){
                link = href;
                BFUlinkDetector = true;
            }
        }
        //link = selector.getMainUrlSelector() + newsItem.select("a").attr(selector.getLinkSelector());
        String date = newsItem.select(selector.getDateSelector()).text();
        String text;
        if (!BFUlinkDetector) {
            text = extractText(selector.getTextSelector(), link);
        } else {
            text = "Чтение статей из внешних источников не реализовано".toUpperCase();
            BFUlinkDetector = false;
        }
        return new MessageContainer(selector.getGroup(), title, link, date, text);
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
