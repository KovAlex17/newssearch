package com.newssearch.service.CSVservice;

public class NewsData {
    private final int number;
    private final String text;
    private final int theme;

    public NewsData(int number, String text, int theme) {
        this.number = number;
        this.text = text;
        this.theme = theme;
    }

    public int getNumber() {
        return number;
    }

    public String getText() {
        return text;
    }

    public int getTheme() {
        return theme;
    }
}
