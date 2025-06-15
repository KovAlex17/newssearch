package com.newssearch.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.newssearch.model.MessageContainer;

public class MessageToJSONtempClass {
    private ObjectMapper objectMapper = new ObjectMapper();

    public String convertToJson(MessageContainer message) {
        try {
            return objectMapper.writeValueAsString(message);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "{}";
        }
    }
}
