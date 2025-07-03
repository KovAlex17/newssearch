package com.newssearch.service;


import com.newssearch.model.MessageContainer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class CombinerNewsToOneArray {

    public static boolean exportToFile(
            ConcurrentHashMap<String,ConcurrentHashMap<String, MessageContainer>> groupedMessagesByWeek,
            String outputPath) throws IOException {

        boolean hasAnyNews = groupedMessagesByWeek.values().stream()
                .anyMatch(innerMap -> !innerMap.isEmpty());

        if(!hasAnyNews){
            return false;
        } else {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath))) {
                for (Map.Entry<String, ConcurrentHashMap<String, MessageContainer>> entry : groupedMessagesByWeek.entrySet()) {
                    ConcurrentHashMap<String, MessageContainer> messages = entry.getValue();

                    for (MessageContainer msg : messages.values()) {

                        String infoToLlm = "\"link\":\"" + msg.getLink() +
                                "\",\"text\":\"" + msg.getText() + "\"";

                        writer.write(infoToLlm);
                        writer.newLine();
                    }

                }
                writer.flush();
            }
        }

        return true;
    }

}
