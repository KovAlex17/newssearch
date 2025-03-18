package com.newssearch.service;

import com.newssearch.model.HtmlSelector;
import com.newssearch.model.MessageContainer;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class NewsFeedProcessor {

    /**
     * Обрабатывает новостную ленту и возвращает список сообщений.
     *
     * @param selector Селектор для обработки новостной ленты.
     * @return Список сообщений.
     * @throws IOException Если произошла ошибка при подключении к сайту.
     */
    public List<MessageContainer> processNewsFeed(HtmlSelector selector) throws IOException {
        String url = selector.getMainUrlSelector() + "/" + selector.getUrlSelector();
        List<MessageContainer> messages = new ArrayList<>();

        try {
            Document doc = Jsoup.connect(url).get();
            Elements items = doc.select(selector.getItemSelector());

            for (Element newsItem : items) {
                MessageContainer message = getMessageInfo(selector, newsItem);
                if (message != null) {
                    messages.add(message);
                    System.out.println("Title: " + message.getTitle());
                    System.out.println("Link: " + message.getLink());
                    System.out.println("Date: " + message.getDate());
                    System.out.println("Text: " + message.getText());
                    System.out.println(" ");
                }
            }
        } catch (UnknownHostException e) {
            System.err.println("Skipping invalid link (UnknownHostException): " + url);
        }

        return messages;
    }

    /**
     * Извлекает информацию о сообщении из элемента новости.
     *
     * @param selector Селектор для извлечения данных.
     * @param newsItem Элемент новости.
     * @return Объект MessageContainer с данными о новости.
     */
    private MessageContainer getMessageInfo(HtmlSelector selector, Element newsItem) {
        String href = newsItem.select(selector.getLink1Selector()).attr(selector.getLink2Selector());
        String link = href.startsWith(selector.getMainUrlSelector()) ? href : selector.getMainUrlSelector() + href;

        if (href.contains("//")) {
            return null;
        }

        String title = newsItem.select(selector.getTitleSelector()).text();
        String date = newsItem.select(selector.getDateSelector()).text();
        String text = extractText(selector.getTextSelector(), link);

        return new MessageContainer(title, link, date, text);
    }

    /**
     * Извлекает текст статьи по указанному селектору.
     *
     * @param textSelector Селектор для извлечения текста.
     * @param link         Ссылка на статью.
     * @return Текст статьи.
     */
    private String extractText(String textSelector, String link) {
        try {
            Document textDoc = Jsoup.connect(link).get();
            Element articleContent = textDoc.selectFirst(textSelector);

            if (articleContent != null) {
                return articleContent.text();
            } else {
                System.err.println("Article content not found on page: " + link);
                return "";
            }
        } catch (IOException e) {
            System.err.println("Failed to fetch article: " + link);
            return "";
        }
    }
}