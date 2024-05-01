package com.example.application.views.search;


import java.util.ArrayList;
import java.util.List;

public class TextfieldSubject implements Subject {
    private List<Observer> observers = new ArrayList<>();
    protected String tickerVal;
    protected String minYear;
    protected String maxYear;
//    private String rangeW;
//    private String minDate;
//    private String maxDate;

    public void setValue(String tickerVal, String minYear, String maxYear) {
        this.tickerVal = tickerVal;
        this.minYear = minYear;
        this.maxYear = maxYear;
//        this.rangeW = rangeW;

        notifyObservers(this.tickerVal, this.minYear, this.maxYear);
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
    public void notifyObservers(String tickerVal, String minYear, String maxYear) {
        for (Observer observer : observers) {
            observer.update(tickerVal, minYear, maxYear);
        }
    }
}

