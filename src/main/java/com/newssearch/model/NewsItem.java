package com.newssearch.model;

import java.util.Date;

public class NewsItem {
    private String title;
    private String link;
    private Date publishedDate;

    public NewsItem(String title, String link, Date publishedDate) {
        this.title = title;
        this.link = link;
        this.publishedDate = publishedDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Date getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(Date publishedDate) {
        this.publishedDate = publishedDate;
    }

    @Override
    public String toString() {
        return "NewsItem{" +
                "title='" + title + '\'' +
                ", link='" + link + '\'' +
                ", publishedDate=" + publishedDate +
                '}';
    }
}
