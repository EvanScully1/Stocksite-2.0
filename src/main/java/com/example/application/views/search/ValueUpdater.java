package com.example.application.views.search;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.*;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.json.JSONArray;
import org.json.JSONObject;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;


public class ValueUpdater implements Observer {

    final String API_KEY = "CI7FYHBhYMeJSFiRsCHP0yNLcIb3Bqhw";
    public String ticker;
    private HashMap<String, Object> tickerInfoMap = new HashMap<>();
    public String minYear;
    public String maxYear;
    public ArrayList<Double> openPricesList = new ArrayList<>();
    public ArrayList<Double> highPricesList = new ArrayList<>();
    public ArrayList<Double> lowPricesList = new ArrayList<>();
    public ArrayList<Long> timestampList = new ArrayList<>();
    private JSONArray results;
    LocalDate today = LocalDate.now();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private String currentDate = today.format(formatter);

    @Override
    public void update(String ticker, String minYear, String maxYear) {
        this.ticker = ticker.toUpperCase();
        this.minYear = minYear;
        this.maxYear = maxYear;

        System.out.println("Hello World!" + this.ticker);
        // goal of this is to add the open, high, and low data for the ticker to it's corresponding ArrayList.
        try {
//            String apiUrl = "https://api.polygon.io/v2/aggs/ticker/"+ticker+"/range/1/day/2022-01-09/2023-01-09?adjusted=true&sort=asc&limit=5000&apiKey="+API_KEY;
//            String apiUrl = "https://api.polygon.io/v2/aggs/ticker/"+ticker+"/range/1/day/2019-01-09/2009-01-10?adjusted=true&sort=asc&limit=5000&apiKey=CI7FYHBhYMeJSFiRsCHP0yNLcIb3Bqhw
            String apiUrl;
            if(minYear.equals("2024")) {
                apiUrl = "https://api.polygon.io/v2/aggs/ticker/" + this.ticker + "/range/1/day/" + maxYear + "-01-01/"+currentDate+"?adjusted=true&sort=asc&limit=5000&apiKey=" + API_KEY;
            } else {
                apiUrl = "https://api.polygon.io/v2/aggs/ticker/" + this.ticker + "/range/1/day/" + maxYear + "-01-01/" + minYear + "-12-31?adjusted=true&sort=asc&limit=5000&apiKey=" + API_KEY;
            }

            URL url = new URL(apiUrl);
            System.out.println(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            // Set the request method
            connection.setRequestMethod("GET");
            // Get the response code
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder response = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            // Close the reader and connection
            reader.close();
            connection.disconnect();

            // Initialize data and print the response
            String data = response.toString();
            System.out.println("Response: " + data);

            // checks if the response has nothing (not a stock in the API)
            if(data.isEmpty()) {
                System.out.println("That is not a stock!");
            // if there is data, convert to a JSONArray, and add data one of the three lists for open, high and low.
            } else {
                JSONObject jsonObject = new JSONObject(data);
                results = jsonObject.getJSONArray("results");

                for (int i = 0; i < results.length(); i++) {
                    JSONObject result = results.getJSONObject(i);
                    // access each 'o' value in results, add to corres. list
                    double openPrice = result.getDouble("o");
                    this.openPricesList.add(openPrice);
                    // access each 'h' value in results, add to corres. list
                    double highPrice = result.getDouble("h");
                    this.highPricesList.add(highPrice);
                    // access each 'l' value in results, add to corres. list
                    double lowPrice = result.getDouble("l");
                    this.lowPricesList.add(lowPrice);
                    // access each 't' value in results, add to corres. list
                    long timestamp = result.getLong("t");
                    this.timestampList.add(timestamp);

                    setHashMap();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void setHashMap() {
        this.tickerInfoMap.put("ticker", this.ticker);
        this.tickerInfoMap.put("minYear", this.minYear);
        this.tickerInfoMap.put("maxYear", this.maxYear);
//        this.tickerInfoMap.put("timestampList", this.timestampList);
        this.tickerInfoMap.put("openPricesList", this.openPricesList);
        this.tickerInfoMap.put("closePricesList", this.highPricesList);
        this.tickerInfoMap.put("highPricesList", this.highPricesList);
        this.tickerInfoMap.put("lowPricesList", this.lowPricesList);
    }

    public HashMap<String, Object> getMapData() {
        return this.tickerInfoMap;
    }
/*
    // methods to get the data from the open/high/low ArrayList<Double> 's for the current ticker symbol.
    public ArrayList<Double> getOpenPricesList() {
        return this.openPricesList;
    }
    public ArrayList<Double> getHighPricesList() {
        return this.highPricesList;
    }
    public ArrayList<Double> getLowPricesList() {
        return this.lowPricesList;
    }

    public JSONArray getResponseList() {
        return this.results;
    }*/
    public ArrayList<Long> getTimestampList() {
        return this.timestampList;
    }

    private String minDateCalculator(String range, int maxYear, int maxMonth, int maxDay) {
        if(range.equals("1 day")) {
//            return 2024-02-27 - 1 day;
            maxDay -= 1;
            return maxYear+"-"+maxMonth+"-"+maxDay;
        } else if(range.equals("5 days")) {
            maxDay -= 5;
            return maxYear+"-"+maxMonth+"-"+maxDay;
        } else if(range.equals("1 month")) {
            maxMonth -= 1;
            return maxYear+"-"+maxMonth+"-"+maxDay;
        } else if (range.equals("6 months")) {
            maxMonth -= 6;
            return maxYear+"-"+maxMonth+"-"+maxDay;
        } else if (range.equals("1 year")) {
            maxYear -= 1;
            return maxYear+"-"+maxMonth+"-"+maxDay;
        } else if (range.equals("5 years")) {
            maxYear -= 5;
            return maxYear+"-"+maxMonth+"-"+maxDay;
        } else {
            maxDay -= 1;
            return maxYear+"-"+maxMonth+"-"+maxDay;
        }
    }
}


