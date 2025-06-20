package com.newssearch.service;


import com.newssearch.model.MessageContainer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class CombinerNewsToOneArray {

    public static void exportToFile(
            ConcurrentHashMap<String, List<MessageContainer>> groupedMessagesByWeek,
            String outputPath) throws IOException {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath))) {
            for (Map.Entry<String, List<MessageContainer>> entry : groupedMessagesByWeek.entrySet()) {
                List<MessageContainer> messages = entry.getValue();

                for (MessageContainer msg : messages) {

                    String infoToLlm = "\"link\":\"" + msg.getLink() +
                            "\",\"text\":\"" + msg.getText() + "\"";

                    writer.write(infoToLlm);
                    writer.newLine();
                }

            }
            writer.flush();
        }
    }

}
