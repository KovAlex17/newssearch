package com.newssearch;

import com.newssearch.controller.NewsController;

public class Main {
    public static void main(String[] args) {

            NewsController newsController = new NewsController();
            newsController.startProcessing();

            //NewsCombinerToCommonFile.combineNews();
            //gptFilterService.gptFiltering();

    }
}