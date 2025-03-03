package com.newssearch.service;

import com.newssearch.model.HtmlSelector;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InputTxtParser {

    public static List<HtmlSelector> readNewsFromFile(String filePath) throws IOException {
        List<HtmlSelector> newsItems = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(filePath));

        String line;
        while ((line = reader.readLine()) != null) {

            String[] parts = line.split(" ; ");

            if (parts.length == 7) {
                String mainUrlSelector = parts[0].trim();
                String urlSelector = parts[1].trim();
                String itemSelector = parts[2].trim();
                String titleSelector = parts[3].trim();
                String linkSelector = parts[4].trim();
                String dateSelector = parts[5].trim();
                String textSelector = parts[6].trim();

                newsItems.add(new HtmlSelector(mainUrlSelector, urlSelector, itemSelector, titleSelector, linkSelector, dateSelector, textSelector));
            } else {
                System.err.println("Invalid line format: " + line);
            }
        }

        reader.close();
        return newsItems;
    }
}
