package com.newssearch.model;

public class HtmlSelector {
    private String urlSelector;
    private String itemSelector;
    private String titleSelector;
    private String linkSelector;
    private String dateSelector;

    public HtmlSelector(String urlSelector, String itemSelector, String titleSelector, String linkSelector, String dateSelector) {
        this.urlSelector = urlSelector;
        this.itemSelector = itemSelector;
        this.titleSelector = titleSelector;
        this.linkSelector = linkSelector;
        this.dateSelector = dateSelector;
    }

    public String getUrlSelector() {
        return urlSelector;
    }

    public void setUrlSelector(String urlSelector) {
        this.urlSelector = urlSelector;
    }

    public String getItemSelector() {
        return itemSelector;
    }

    public void setItemSelector(String itemSelector) {
        this.itemSelector = itemSelector;
    }

    public String getTitleSelector() {
        return titleSelector;
    }

    public void setTitleSelector(String titleSelector) {
        this.titleSelector = titleSelector;
    }

    public String getLinkSelector() {
        return linkSelector;
    }

    public void setLinkSelector(String linkSelector) {
        this.linkSelector = linkSelector;
    }

    public String getDateSelector() {
        return dateSelector;
    }

    public void setDateSelector(String dateSelector) {
        this.dateSelector = dateSelector;
    }
}
