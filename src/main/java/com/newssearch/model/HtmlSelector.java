package com.newssearch.model;

public class HtmlSelector {
    private String group;
    private String mainUrlSelector;
    private String urlSelector;
    private String itemSelector;
    private String titleSelector;
    private String linkSelector;
    private String dateSelector;
    private String textSelector;

    public HtmlSelector(String group, String mainUrlSelector, String urlSelector, String itemSelector, String titleSelector, String linkSelector, String dateSelector, String textSelector) {
        this.group = group;
        this.mainUrlSelector = mainUrlSelector;
        this.urlSelector = urlSelector;
        this.itemSelector = itemSelector;
        this.titleSelector = titleSelector;
        this.linkSelector = linkSelector;
        this.dateSelector = dateSelector;
        this.textSelector = textSelector;

    }

    public String getGroup() {
        return group;
    }

    public String getMainUrlSelector() {
        return mainUrlSelector;
    }

    public String getUrlSelector() {
        return urlSelector;
    }

    public String getItemSelector() {
        return itemSelector;
    }

    public String getTitleSelector() {
        return titleSelector;
    }

    public String getLinkSelector() {
        return linkSelector;
    }

    public String getDateSelector() {
        return dateSelector;
    }

    public String getTextSelector() {
        return textSelector;
    }

}
