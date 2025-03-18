package com.newssearch.service;

import com.newssearch.model.HtmlSelector;

import java.io.IOException;
import java.util.List;


/**
 * Этот класс отвечает за чтение новостных лент, информация о которых содержится в .txt файле
 */
public class NewsFeedReader {

    public List<HtmlSelector> readNewsFeeds(String filePath) throws IOException {
        return InputTxtParser.readNewsFromFile(filePath);
    }

}
