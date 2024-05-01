package com.example.application.views.search;

import java.util.ArrayList;

public interface Subject {
    void addObserver(Observer observer);
    void removeObserver(Observer observer);
//    void notifyObservers(String ticker, ArrayList<Double> openPrices, ArrayList<Double> highPrices, ArrayList<Double> lowPrices);
    void notifyObservers(String ticker, String minYear, String maxYear);

}