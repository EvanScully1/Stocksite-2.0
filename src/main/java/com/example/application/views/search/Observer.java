package com.example.application.views.search;

public interface Observer {
    void update(String tickerVal, String rangeWidth, String minDate, String maxDate);
}
