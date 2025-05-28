package com.newssearch.service.CSVservice;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class NewsDataParser {
    public List<NewsData> parse(String jsonResponse) {
        JSONObject jsonObject = new JSONObject(jsonResponse);
        JSONArray numbers = jsonObject.getJSONArray("news_numbers");
        JSONArray texts = jsonObject.getJSONArray("news_texts");
        JSONArray themes = jsonObject.getJSONArray("themes");

        return IntStream.range(0, numbers.length())
                .mapToObj(i -> new NewsData(
                        numbers.getInt(i),
                        texts.getString(i),
                        themes.getInt(i)))
                .collect(Collectors.toList());
    }
}
