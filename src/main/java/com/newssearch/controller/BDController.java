package com.newssearch.controller;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.newssearch.model.MessageContainer;
import org.bson.Document;


public class BDController {
    public static void BDWrite(MessageContainer message) {

        try (MongoClient client = MongoClients.create("mongodb://localhost:27017")) {

            MongoDatabase database = client.getDatabase("UniversityNewsFeeds");

            String universityName = extractRootDomain(message.getLink());
            MongoCollection<Document> universityCollection = database.getCollection(message.getGroup() + "_" + universityName);

            Document fuNews = new Document("_id", getShortTitle(message.getTitle()))
                    .append("title", message.getTitle())
                    .append("datePublished", message.getDate())
                    .append("link", message.getLink())
                    .append("text", message.getText());

            if (isNotNewExists(universityCollection, fuNews)) {
                universityCollection.insertOne(fuNews);
                System.out.println("Добавлена новость для университета: " + universityName);
            } else {
                System.out.println("Новость уже существует для университета: " + universityName);
            }
        } catch (Exception e) {
            System.err.println("Ошибка: " + e.getMessage());
        }
    }

    private static boolean isNotNewExists(MongoCollection<Document> collection, Document news) {

        Document idQuery = new Document("_id", news.get("_id"));
        long idMatches = collection.countDocuments(idQuery);

        if (idMatches != 0) {
            Document linkQuery = new Document(idQuery) // Используем idQuery как основу
                    .append("link", news.getString("link"));
            return !(collection.countDocuments(linkQuery) > 0);

        } else return true;
    }

    private static String getShortTitle(String title) {
        return title.length() > 50 ? title.substring(0, 45) : title;
    }

    public static String extractRootDomain(String url) {

        int protocolIndex = url.indexOf("://");
        String domain = protocolIndex != -1 ? url.substring(protocolIndex + 3) : url;

        int pathIndex = domain.indexOf('/');
        if (pathIndex != -1) {
            domain = domain.substring(0, pathIndex);
        }

        // Разделяем домен по точкам
        String[] parts = domain.split("\\.");

        // Берём предпоследнюю часть
        if (parts.length >= 2) {
            return parts[parts.length - 2]; // Предпоследний элемент
        }

        return domain; // Если что-то пошло не так, возвращаем весь домен
    }
}
