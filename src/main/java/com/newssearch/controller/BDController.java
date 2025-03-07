package com.newssearch.controller;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.newssearch.model.MessageContainer;
import org.bson.Document;


public class BDController {
    public static void BDWrite(MessageContainer message) {
        String connectionString = "mongodb://kovalev:bF%3C8!Rac%3FfmQHYjg9G*k2%40@db.sciencepulse.ru:27017/?ssl=true&authSource=admin&authMechanism=SCRAM-SHA-1";  /* "mongodb://localhost:27017" */
        try (MongoClient client = MongoClients.create(connectionString)) {

            MongoDatabase database = client.getDatabase("priorities");

            String universityName = message.getUniversityName();
            MongoCollection<Document> universityCollection = database.getCollection(message.getGroup() + "_" + universityName);

            Document unNews = new Document("_id", getShortTitle(message.getTitle()))
                    .append("title", message.getTitle())
                    .append("datePublished", message.getDate())
                    .append("link", message.getLink())
                    .append("text", message.getText());

            synchronized (universityCollection) {
                if (isNotNewExists(universityCollection, unNews)) {
                    universityCollection.insertOne(unNews);
                    System.out.println("Добавлена новость для университета: " + universityName);
                } else {
                    System.out.println("Новость уже существует для университета: " + universityName);
                }
            }
        } catch (Exception e) {
            System.err.println("Ошибка: " + e.getMessage());
        }
    }

    private static boolean isNotNewExists(MongoCollection<Document> collection, Document news) {

        Document idQuery = new Document("_id", news.get("_id"));
        long idMatches = collection.countDocuments(idQuery);

        if (idMatches != 0) {
            Document linkQuery = new Document(idQuery)
                    .append("link", news.getString("link"));
            return !(collection.countDocuments(linkQuery) > 0);

        } else return true;
    }

    private static String getShortTitle(String title) {
        return title.length() > 50 ? title.substring(0, 45) : title;
    }

}