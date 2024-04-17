package com.example.application.views.search;

public interface Subject {
    void addObserver(Observer observer);
    void removeObserver(Observer observer);
    void notifyObservers(String ticker, String rangeW, String minDate, String maxDate);
}