package com.newssearch.controller;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.newssearch.model.HtmlSelector;
import com.newssearch.model.MessageContainer;
import com.newssearch.service.InputTxtParser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
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
                ExecutorService executorService = Executors.newFixedThreadPool(8);

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
    /**
     * Метод, обрабатывающий новостную новость. Принимает соответствующий селектор
     */
    private void handlingNewsFeed(HtmlSelector selector){

        String url = "";
        boolean BFUlinkDetector = false;
        String connectionString = "mongodb://kovalev:bF%3C8!Rac%3FfmQHYjg9G*k2%40@db.sciencepulse.ru:27017/?ssl=true&authSource=admin&authMechanism=SCRAM-SHA-1";  /* "mongodb://localhost:27017" */

        try (MongoClient client = MongoClients.create(connectionString)){

            url = selector.getMainUrlSelector() + "/" + selector.getUrlSelector();
            Document doc = Jsoup.connect(url).get();
            Elements items = doc.select(selector.getItemSelector());

            MongoDatabase database = client.getDatabase("priorities");

            MongoCollection<org.bson.Document> universityCollection =
                    database.getCollection(selector.getGroup() + "_" + extractRootDomain(selector.getMainUrlSelector()));

            List<MessageContainer> messages = new ArrayList<>();
            for (Element newsItem : items) {
                MessageContainer message = getMessageInfo(selector, newsItem, BFUlinkDetector);
                if(message != null) {
                    messages.add(message);
                    //System.out.println(message.getLink());
                }
            }

            BDController.BDWrite(messages, universityCollection, extractRootDomain(selector.getMainUrlSelector()));

        System.out.println("Обработана лента сайта " + url);

        } catch (UnknownHostException e){
            System.err.println("Skipping invalid link (UnknownHostException): " + url);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private MessageContainer getMessageInfo(HtmlSelector selector, Element newsItem, Boolean BFUlinkDetector){

        String link = "";
        Elements links = newsItem.select("a");

        //link = selector.getMainUrlSelector() + newsItem.select("a").attr(selector.getLinkSelector());

        System.out.println(links.size());

        for (Element el : links) {

            String href = el.attr(selector.getLinkSelector());
            if (href.contains("//")){
                //link = href;
                //BFUlinkDetector = true;
                return null;
            }
            if (!href.contains("?")  ) {
                link = selector.getMainUrlSelector() + href;
            }

        }


        String title = newsItem.select(selector.getTitleSelector()).text();
        String date = newsItem.select(selector.getDateSelector()).text();
        String text;
        if (!BFUlinkDetector) {
            text = extractText(selector.getTextSelector(), link);
        } else {
            text = "Чтение статей из внешних источников не реализовано".toUpperCase();
            BFUlinkDetector = false;
        }
        return new MessageContainer(title, link, date, text);
    }

    public static String extractRootDomain(String url) {

        int protocolIndex = url.indexOf("://");
        String domain = protocolIndex != -1 ? url.substring(protocolIndex + 3) : url;

        int pathIndex = domain.indexOf('/');
        if (pathIndex != -1) {
            domain = domain.substring(0, pathIndex);
        }

        // Разделяем домен по точкам
        String[] parts = domain.split("\\.");

        // Берём предпоследнюю часть
        if (parts.length >= 2) {
            return parts[parts.length - 2]; // Предпоследний элемент
        }

        return domain; // Если что-то пошло не так, возвращаем весь домен
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
