package com.example.application.views.search;

import java.util.ArrayList;

public interface Observer {
//    void update(String ticker, ArrayList<Double> openPrices, ArrayList<Double> highPrices, ArrayList<Double> lowPrices);
    void update(String ticker, String minYear, String maxYear);
}
