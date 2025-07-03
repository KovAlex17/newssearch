package com.newssearch.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigManager {
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = ConfigManager.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new RuntimeException("Unable to find config.properties");
            }
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Error loading configuration", e);
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    // Методы для конкретных свойств
    public static String getMongoUri() {
        return getProperty("mongo.uri");
    }

    public static String getOpenRouterApiKey() {
        return getProperty("openrouter.api.key2");
    }

}