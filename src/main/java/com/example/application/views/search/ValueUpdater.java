package com.example.application.views.search;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;


public class ValueUpdater implements Observer {
    @Override
    public void update(String ticker, String rangeWidth, String minDate, String maxDate) {
        // polygon.io data reader based on update from searchview where user inputs: ticker symbol, dates, and range
        try {
            String apiKey = "CI7FYHBhYMeJSFiRsCHP0yNLcIb3Bqhw";
            maxDate = "2024-03-27";
            String newMinDate = minDateCalculator(rangeWidth, Integer.parseInt(maxDate.substring(0,4)), Integer.parseInt(maxDate.substring(5,7)), Integer.parseInt(maxDate.substring(8,10)));
            System.out.println(newMinDate);
            //            String apiUrl = "https://api.polygon.io/v2/aggs/ticker/"+ticker+"/range/5/year/"+minDate+"/"+maxDate+"?adjusted=true&sort=asc&limit=120&apiKey="+ apiKey;
//            String apiUrl = "https://api.polygon.io/v2/aggs/ticker/" + ticker + "/range/1/day?apiKey=" + apiKey + "&from=2022-05-20&to=2022-11-11";
            String apiUrl = "https://api.polygon.io/v2/aggs/ticker/"+ticker+"/range/1/day/"+minDate+"/2024-03-27?adjusted=true&sort=asc&limit=120&apiKey="+ apiKey;
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            // Set the request method
            connection.setRequestMethod("GET");
            // Get the response code
            //int responseCode = connection.getResponseCode();
            //System.out.println("Response Code: " + responseCode);
            // Read the response
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder response = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            // Close the reader and connection
            reader.close();
            connection.disconnect();

            // Print the response(s)
            String data = response.toString();
            System.out.println("Response: " + data);

            JSONObject jsonObject = new JSONObject(data);
            int resultsCount = jsonObject.getInt("resultsCount");
            if (resultsCount > 0) {
                double high = jsonObject.getJSONArray("results").getJSONObject(0).getDouble("h");
                double low = jsonObject.getJSONArray("results").getJSONObject(0).getDouble("l");

                System.out.println(ticker);
                System.out.println("High: " + high);
                System.out.println("Low: " + low);

            } else {
                System.out.println("That is not a stock!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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


