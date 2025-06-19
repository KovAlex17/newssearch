package com.newssearch.service.CSVservice;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class JSONtoCSVService {
    private List<String> jsonObjects = new ArrayList<>();

    public void addJsonObject(String jsonString) {
        jsonObjects.add(jsonString);
    }

    public void printJsonArray(){
        System.out.println(jsonObjects);
    }

    private void writeHeaderTableEmpty(String fileName, JsonNode firstObject) throws IOException {
        File file = new File(fileName);
        boolean fileExists = file.exists();
        boolean isEmpty = !fileExists || file.length() == 0;

        if (isEmpty) {
            try (BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(fileName, true), StandardCharsets.UTF_8))) {
                // Пишем BOM для Excel, если файл только что создан
                if (!fileExists) {
                    writer.write("\uFEFF");
                }
                // Заголовок
                Iterator<String> fieldNames = firstObject.fieldNames();
                StringBuilder header = new StringBuilder();
                while (fieldNames.hasNext()) {
                    header.append(fieldNames.next()).append(";");
                }
                if (header.length() > 0) header.setLength(header.length() - 1); // удалить последнюю ;
                writer.write(header.toString());
                writer.newLine();
            }
        }
    }

    public void writeAllToCsv(String fileName) throws IOException {
        if (jsonObjects.isEmpty()) {
            return;
        }

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode firstObject = objectMapper.readTree(jsonObjects.get(0));

        writeHeaderTableEmpty(fileName, firstObject);

        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(fileName, true), StandardCharsets.UTF_8))) { // append = true (режим дозаписи)
            for (String jsonString : jsonObjects) {
                JsonNode obj = objectMapper.readTree(jsonString);
                StringBuilder sb = new StringBuilder();
                Iterator<String> fieldNames = obj.fieldNames();
                while (fieldNames.hasNext()) {
                    String field = fieldNames.next();
                    String value = obj.get(field).asText().replace(";", ","); // чтобы не ломать CSV
                    sb.append(value).append(";");
                }
                if (sb.length() > 0) sb.setLength(sb.length() - 1); // удалить последнюю ;
                writer.write(sb.toString());
                writer.newLine();
            }
        }

        jsonObjects.clear();
    }
}

