package com.newssearch.controller;

import com.newssearch.model.HtmlSelector;
import com.newssearch.service.InputTxtParser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.List;


public class NewsController {
    List<HtmlSelector> newsItems;

    public NewsController() {
        try {
            newsItems = InputTxtParser.readNewsFromFile("src/main/resources/news.txt");

            if (!newsItems.isEmpty()) {
                Document doc = Jsoup.connect(newsItems.get(0).getMainUrlSelector() + "/" + newsItems.get(0).getUrlSelector()).get();
                Elements items = doc.select(newsItems.get(0).getItemSelector());
                //System.out.println(items.isEmpty());
                //System.out.println(items);

                for (Element newsItem : items) {
                    String title = newsItem.select(newsItems.get(0).getTitleSelector()).text();

                    String preLink = newsItem.select("a").attr(newsItems.get(0).getLinkSelector());
                    String link = newsItems.get(0).getMainUrlSelector() + preLink;

                    String date = newsItem.select(newsItems.get(0).getDateSelector()).text();

                    System.out.println("Title: " + title);
                    System.out.println("Link: " + link);
                    System.out.println("Date: " + date);
                    System.out.println();
                }


            } else {
                System.out.println("No news items found in the file.");
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


}
