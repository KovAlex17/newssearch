package com.newssearch.service;

import com.newssearch.model.HtmlFeed;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InputTxtParser {
    public static List<HtmlFeed> readNewsFromFile(String filePath) throws IOException {
        List<HtmlFeed> newsItems = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(filePath));

        String line;
        while ((line = reader.readLine()) != null) {

            String[] parts = line.split(" ; ");

            if (parts.length == 5) {
                String urlSelector = parts[0].trim();
                String itemSelector = parts[1].trim();
                String titleSelector = parts[2].trim();
                String linkSelector = parts[3].trim();
                String dateSelector = parts[4].trim();

                newsItems.add(new HtmlFeed(urlSelector, itemSelector, titleSelector, linkSelector, dateSelector));
            } else {
                System.err.println("Invalid line format: " + line);
            }
        }

        reader.close();
        return newsItems;
    }
}
