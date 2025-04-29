package com.newssearch.service;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.newssearch.controller.ConfigManager;
import com.newssearch.model.MessageContainer;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String CONNECTION_STRING = ConfigManager.getMongoUri();

    /**
     * Записывает сообщения в коллекцию MongoDB.
     *
     * @param messages       Список сообщений для записи.
     * @param collectionName Имя коллекции, в которую будут записаны данные.
     * @param universityName Название университета (для логирования).
     */
    public void writeMessages(List<MessageContainer> messages, String collectionName, String universityName) {
        try (MongoClient client = MongoClients.create(CONNECTION_STRING)) {
            MongoDatabase database = client.getDatabase("priorities");
            MongoCollection<Document> collection = database.getCollection(collectionName);

            List<Document> newNews = new ArrayList<>();

            for (MessageContainer message : messages) {
                String newsId = extractPathAndQuery(message.getLink());
                Document existingNews = collection.find(new Document("_id", newsId)).first();

                if (existingNews == null) {
                    newNews.add(new Document("_id", newsId)
                            .append("title", message.getTitle())
                            .append("datePublished", message.getDate())
                            .append("link", message.getLink())
                            .append("text", message.getText()));
                }
            }

            if (!newNews.isEmpty()) {
                collection.insertMany(newNews);
                System.out.println("Добавлено " + newNews.size() + " новостей для университета: " + universityName);
            } else {
                System.out.println("Добавлено 0 новостей для университета: " + universityName);
            }
        } catch (Exception e) {
            System.err.println("Ошибка при записи в базу данных: " + e.getMessage());
        }
    }

    /**
     * Извлекает путь и параметры из URL для использования в качестве _id.
     *
     * @param url URL новости.
     * @return Путь и параметры URL без корневого домена.
     */
    private String extractPathAndQuery(String url) {

        /* Убираем протокол (если есть)*/
        int protocolIndex = url.indexOf("://");
        String domainAndPath = protocolIndex != -1 ? url.substring(protocolIndex + 3) : url;

        /* Находим начало пути (первый слэш после домена) */
        int pathIndex = domainAndPath.indexOf('/');
        if (pathIndex == -1) {
            return "";  /* Если путь отсутствует, возвращаем пустую строку */
        }

        /* Возвращаем всё, что после домена */
        return domainAndPath.substring(pathIndex);
    }
}