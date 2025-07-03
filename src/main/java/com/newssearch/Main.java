package com.newssearch;

import com.newssearch.controller.NewsController;

import com.newssearch.service.GptFilterService;
import com.newssearch.service.NewsCombinerToCommonFile;


public class Main {
    public static void main(String[] args) {

            NewsController newsController = new NewsController();
            newsController.startProcessing();

            //NewsCombinerToCommonFile.combineNews();



    }
}