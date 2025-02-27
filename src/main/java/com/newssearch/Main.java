package com.newssearch;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        try {
            // Укажите URL RSS-канала
            String url = "https://media.kpfu.ru/news-rss";
            URL feedUrl = new URL(url);

            // Сохраните содержимое RSS-канала в файл
            try (InputStream inputStream = feedUrl.openStream();
                 FileOutputStream outputStream = new FileOutputStream("feed.xml")) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                System.out.println("RSS-канал сохранен в файл feed.xml");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}