package com.newssearch.service;

import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.newssearch.controller.ConfigManager;
import org.bson.Document;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class NewsCombinerToCommonFile {
    public static void combineNews() {

        try (MongoClient mongoClient = MongoClients.create(ConfigManager.getMongoUri())) {
            MongoDatabase database = mongoClient.getDatabase("priorities");
            MongoCollection<Document> collection = database.getCollection("NationalUniversities_nsu");

            // Собираем все тексты новостей
            StringBuilder allTexts = new StringBuilder();

            int counter = 1;
            for (Document doc : collection.find()) {
                String name = "Новость номер_" + counter++;
                String text = doc.getString("text");
                if (text != null && !text.isEmpty()) {
                    allTexts.append(name).append("\n\n").append(text).append("\n\n");
                }
            }

            // Сохраняем в файл
            String outputPath = "news_texts.txt";
            try {
                Files.write(Paths.get(outputPath), allTexts.toString().getBytes());
                System.out.println("Файл успешно сохранён: " + outputPath);
            } catch (IOException e) {
                System.err.println("Ошибка при сохранении файла: " + e.getMessage());
            }

        } catch (Exception e) {
            System.err.println("Ошибка при работе с MongoDB: " + e.getMessage());
        }
    }
}