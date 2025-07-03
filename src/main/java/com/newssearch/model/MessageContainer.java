package com.newssearch.model;

public class MessageContainer {
    private final String title;
    private final String link;
    private final String date;
    private final String text;

    private int numOfPriority;

    public MessageContainer(String title, String link, String date, String text) {
        this.title = title;
        this.link = link;
        this.date = date;
        this.text = text;
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

    public int getNumOfPriority() {
        return numOfPriority;
    }

    public void setNumOfPriority(int numOfPriority) {
        this.numOfPriority = numOfPriority;
    }

}
