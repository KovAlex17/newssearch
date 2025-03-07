package com.newssearch.model;

public class MessageContainer {
    private final String group;
    private final String title;
    private final String link;
    private final String date;
    private final String text;

    public MessageContainer(String group, String title, String link, String date, String text) {
        this.group = group;
        this.title = title;
        this.link = link;
        this.date = date;
        this.text = text;
    }

    public String getGroup() {
        return group;
    }

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    public String getDate() {
        return date;
    }

    public String getText() {
        return text;
    }
}
