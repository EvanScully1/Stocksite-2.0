package com.example.application.views.search;


import java.util.ArrayList;
import java.util.List;

public class TextfieldSubject implements Subject {
    private List<Observer> observers = new ArrayList<>();
    private String tickerVal;
    private String rangeW;
    private String minDate;
    private String maxDate;

    public void setValue(String tickerVal, String rangeW, String minDate, String maxDate) {
        this.tickerVal = tickerVal;
        this.rangeW = rangeW;
        this.minDate = minDate;
        this.maxDate = maxDate;

        notifyObservers(tickerVal, rangeW, minDate, maxDate);
    }

    @Override
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(String tickerVal, String rangeW, String minDate, String maxDate) {
        for (Observer observer : observers) {
            observer.update(tickerVal, rangeW, minDate, maxDate);
        }
    }
}

