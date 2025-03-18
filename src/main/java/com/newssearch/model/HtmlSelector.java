package com.newssearch.model;

public class HtmlSelector {
    private final String group;
    private final String mainUrlSelector;
    private final String urlSelector;
    private final String itemSelector;
    private final String titleSelector;
    private final String link1Selector;
    private final String link2Selector;
    private final String dateSelector;
    private final String textSelector;

    public HtmlSelector(String group, String mainUrlSelector, String urlSelector, String itemSelector, String titleSelector, String link1Selector, String link2Selector, String dateSelector, String textSelector) {
        this.group = group;
        this.mainUrlSelector = mainUrlSelector;
        this.urlSelector = urlSelector;
        this.itemSelector = itemSelector;
        this.titleSelector = titleSelector;
        this.link1Selector = link1Selector;
        this.link2Selector = link2Selector;
        this.dateSelector = dateSelector;
        this.textSelector = textSelector;
    }

    public String getGroup() { return group; }

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

    public String getLink1Selector() {
        return link1Selector;
    }

    public String getLink2Selector() {
        return link2Selector;
    }

    public String getDateSelector() {
        return dateSelector;
    }

    public String getTextSelector() {
        return textSelector;
    }

}
