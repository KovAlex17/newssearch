package com.newssearch.controller;


import com.mongodb.client.MongoCollection;
import com.newssearch.model.MessageContainer;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;


public class BDController {
    public static void BDWrite(List<MessageContainer> messages, MongoCollection<Document> universityCollection, String universityName) {
        try{

            List<Document> newNews = new ArrayList<>();

            for(MessageContainer message : messages){
                String newsId = extractPathAndQuery(message.getLink());
                Document existingNews = universityCollection.find(new Document("_id", newsId)).first();

                if (existingNews == null) {
                    newNews.add(new Document("_id", newsId)
                            .append("title", message.getTitle())
                            .append("datePublished", message.getDate())
                            .append("link", message.getLink())
                            .append("text", message.getText()));
                }
            }

            if (!newNews.isEmpty()) {
                universityCollection.insertMany(newNews);
                System.out.println("Добавлено " + newNews.size() + " новостей для университета: " + universityName);
            } else { System.out.println("Добавлено 0 новостей для университета: " + universityName);}

        } catch (Exception e) {
            System.err.println("Ошибка: " + e.getMessage());
        }
    }

    public static String extractPathAndQuery(String url) {
        // Убираем протокол (если есть)
        int protocolIndex = url.indexOf("://");
        String domainAndPath = protocolIndex != -1 ? url.substring(protocolIndex + 3) : url;

        // Находим начало пути (первый слэш после домена)
        int pathIndex = domainAndPath.indexOf('/');
        if (pathIndex == -1) {
            // Если путь отсутствует, возвращаем пустую строку
            return "";
        }

        // Возвращаем всё, что после домена
        return domainAndPath.substring(pathIndex);
    }

}