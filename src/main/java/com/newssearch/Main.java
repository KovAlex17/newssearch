package com.newssearch;

import com.newssearch.controller.NewsController;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        //try {
            NewsController newsController = new NewsController();

            /*Document doc = Jsoup.connect("https://media.kpfu.ru/news?kn%5B0%5D=%D0%9D%D0%BE%D0%B2%D0%BE%D1%81%D1%82%D0%B8%20%D0%BD%D0%B0%D1%83%D0%BA%D0%B8&created").get();

            Elements newsItems = doc.select(".newsItem-content");

            for (Element newsItem : newsItems) {
                String title = newsItem.select(".newsItem-top a.boldLink").text();
                String link = newsItem.select("a").attr("href");
                String date = newsItem.select(".newsItem-date").text();

                System.out.println("Title: " + title);
                System.out.println("Link: " + link);
                System.out.println("Date: " + date);
                System.out.println();
            }*/
        //} catch (IOException e) {
        //    e.printStackTrace();
        //}
    }
}