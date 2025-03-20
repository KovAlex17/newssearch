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
        String group = "NuN University";
        while ((line = reader.readLine()) != null) {

            String[] parts = line.split(" ; ");

            if(parts.length == 1 && !parts[0].trim().isEmpty()){
                switch (parts[0].trim()){
                    case "FederalUniversities":
                        group = "FederalUniversities";
                        break;
                    case "NationalUniversities":
                        group = "NationalUniversities";
                        break;
                    case "FlagshipUniversities":
                        group = "FlagshipUniversities";
                        break;
                    default: System.out.println("Incorrect University Group");
                }
            } else if (parts.length == 8) {
                String mainUrlSelector = parts[0].trim();
                String urlSelector = parts[1].trim();
                String itemSelector = parts[2];
                String titleSelector = parts[3].trim();
                String link1Selector = parts[4].trim();
                String link2Selector = parts[5].trim();
                String dateSelector = parts[6].trim();
                String textSelector = parts[7].trim();

                newsItems.add(new HtmlSelector(group, mainUrlSelector, urlSelector, itemSelector, titleSelector, link1Selector, link2Selector, dateSelector, textSelector));
            } else {
                System.err.println("Invalid line format: " + line);
            }
        }

        reader.close();
        return newsItems;
    }
}