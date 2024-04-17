package com.example.application.views.search;


import com.example.application.views.MainLayout;
import com.example.application.views.search.ServiceHealth.Status;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.board.Board;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.*;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility.BoxSizing;
import com.vaadin.flow.theme.lumo.LumoUtility.FontSize;
import com.vaadin.flow.theme.lumo.LumoUtility.FontWeight;
import com.vaadin.flow.theme.lumo.LumoUtility.Margin;
import com.vaadin.flow.theme.lumo.LumoUtility.Padding;
import com.vaadin.flow.theme.lumo.LumoUtility.TextColor;
import com.vaadin.flow.component.charts.model.ChartType;
import com.vaadin.flow.component.charts.model.Configuration;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@PageTitle("Search")
@Route(value = "dashboard", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@AnonymousAllowed
public class SearchView extends Main {

    private String ticker = "AAPL";
    private ArrayList<Double> openPricesList;
    private ArrayList<Double> highPricesList;
    private ArrayList<Double> lowPricesList;
    private ArrayList<ListSeries> stockChartList;

    public SearchView() {
        Board board = new Board();

        TextField stockTickerTF = new TextField();
        stockTickerTF.getElement().setAttribute("title", "example: AAPL is Apple's ticker");
        stockTickerTF.setClearButtonVisible(true);
        stockTickerTF.setMinLength(1);
        stockTickerTF.setMaxLength(5);
        stockTickerTF.setPattern("^[A-Z]+");
        stockTickerTF.setErrorMessage("Not a ticker symbol.");
        stockTickerTF.setWidth("min-content");
        stockTickerTF.setPlaceholder("ticker symbol");
        stockTickerTF.setErrorMessage("Not a ticker symbol.");
        stockTickerTF.setWidth("min-content");

        add(stockTickerTF);
        Button searchButton = new Button();

        add(searchButton);

        // POLYGON.IO STOCK AGG PRICE API RESPONSE
        try {
            String apiKey = "CI7FYHBhYMeJSFiRsCHP0yNLcIb3Bqhw";
//            String apiUrl = "https://api.polygon.io/v2/aggs/ticker/AAPL/range/1/day/2023-01-09/2023-01-09?apiKey="+apiKey;
//            String apiUrl = "https://api.polygon.io/v3/reference/options/contracts?underlying_ticker="+ticker+"&apiKey="+apiKey;
            String apiUrl = "https://api.polygon.io/v2/aggs/ticker/"+ticker+"/range/1/day/2021-01-09/2023-01-09?adjusted=true&sort=asc&limit=5000&apiKey="+apiKey;

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
            JSONArray results = jsonObject.getJSONArray("results");
            //access each 'o' value in results
            openPricesList = new ArrayList<Double>();
            highPricesList = new ArrayList<Double>();
            lowPricesList = new ArrayList<Double>();
            for(int i = 0; i < results.length(); i++) {
                JSONObject result = results.getJSONObject(i);

                double openPrice = result.getDouble("o");
                openPricesList.add(openPrice);
                double highPrice = result.getDouble("h");
                highPricesList.add(highPrice);
                double lowPrice = result.getDouble("l");
                lowPricesList.add(lowPrice);

//                System.out.println(i + ", "+ openPrice); // for testing purposes
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        addClassName("search-view");

        board.addRow(createHighlight("Ticker Symbol", ticker),
                createHighlight("Current", openPricesList.get(openPricesList.size()-1).toString(), 33.7),
                createHighlight("High", highPricesList.get(highPricesList.size()-1).toString(), -112.45),
                createHighlight("Low", lowPricesList.get(lowPricesList.size()-1).toString(), 3.9));
        board.addRow(createViewEvents());
//        board.addRow(createServiceHealth(), createResponseTimes());
        add(board);



//        TextfieldSubject searchSubject = new TextfieldSubject();
//        ValueUpdater searchValUpdater = new ValueUpdater();
//        searchSubject.addObserver(searchValUpdater);
//        searchButton.addClickListener(e -> {
//            searchSubject.setValue(stockTickerTF.getValue(), rangeWidthTF.getValue(), minDateTF.getValue(), maxDateTF.getValue());
//            currChart.setVisible(true);
//            conf.setTitle(stockTickerTF.getValue());
//            conf.setSubTitle(rangeWidthTF.getValue());
//        });
    }
    private Component createHighlight(String title, String value, Double percentage) {
        VaadinIcon icon = VaadinIcon.ARROW_UP;
        String prefix = "";
        String theme = "badge";

        if (percentage == 0) {
            prefix = "±";
        } else if (percentage > 0) {
            prefix = "+";
            theme += " success";
        } else if (percentage < 0) {
            icon = VaadinIcon.ARROW_DOWN;
            theme += " error";
        }

        H2 h2 = new H2(title);
        h2.addClassNames(FontWeight.NORMAL, Margin.NONE, TextColor.SECONDARY, FontSize.XSMALL);

        Span span = new Span(value);
        span.addClassNames(FontWeight.SEMIBOLD, FontSize.XXXLARGE);

        Icon i = icon.create();
        i.addClassNames(BoxSizing.BORDER, Padding.XSMALL);

        Span badge = new Span(i, new Span(prefix + percentage.toString()));
        badge.getElement().getThemeList().add(theme);

        VerticalLayout layout = new VerticalLayout(h2, span, badge);
        layout.addClassName(Padding.LARGE);
        layout.setPadding(false);
        layout.setSpacing(false);
        return layout;
    }

    private Component createHighlight(String title, String subtitle) {
        VaadinIcon icon = VaadinIcon.ARROW_UP;
        String prefix = "";
        String theme = "badge";

        H2 h2 = new H2(title);
        h2.addClassNames(FontWeight.NORMAL, Margin.NONE, TextColor.SECONDARY, FontSize.XSMALL);

        Span span = new Span(subtitle);
        span.addClassNames(FontWeight.SEMIBOLD, FontSize.XXXLARGE);

        Icon i = icon.create();
        i.addClassNames(BoxSizing.BORDER, Padding.XSMALL);

        VerticalLayout layout = new VerticalLayout(h2, span);
        layout.addClassName(Padding.LARGE);
        layout.setPadding(false);
        layout.setSpacing(false);
        return layout;
    }

    private Component createViewEvents() {
        // Header
        Select year = new Select();
        year.setItems("2011", "2012", "2013", "2014", "2015", "2016", "2017", "2018", "2019", "2020", "2021");
        year.setValue("Max Year");
        year.setWidth("200px");

        HorizontalLayout header = createHeader("Chart","");
        header.add(year);

        // Chart
        Chart chart = new Chart(ChartType.AREASPLINE);
        Configuration conf = chart.getConfiguration();
        conf.getChart().setStyledMode(true);

        XAxis xAxis = new XAxis();
//        xAxis.setCategories("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec");
        conf.addxAxis(xAxis);

        conf.getyAxis().setTitle("Price (USD)");

        PlotOptionsAreaspline plotOptions = new PlotOptionsAreaspline();
        plotOptions.setPointPlacement(PointPlacement.ON);
        plotOptions.setMarker(new Marker(false));
        conf.addPlotOptions(plotOptions);


        // add a new stock to the list series that will appear as a chart and a little icon below chart on web app.
        // How to add a basic one: conf.addSeries(new ListSeries("test", 189.1, 191.1, 291.4, 396, 501, 403, 609, 712, 729, 942, 1044, 1247));
        ListSeries temp = new ListSeries(ticker);

        for(int i = 0; i<openPricesList.size(); i++) {
            temp.addData(openPricesList.get(i));
        }
        stockChartList = new ArrayList<ListSeries>();
        stockChartList.add(temp);
        for(int i=0; i<stockChartList.size(); i++) {
            conf.addSeries(stockChartList.get(i));
        }

        // Add it all together
        VerticalLayout viewEvents = new VerticalLayout(header, chart);
        viewEvents.addClassName(Padding.LARGE);
        viewEvents.setPadding(false);
        viewEvents.setSpacing(false);
        viewEvents.getElement().getThemeList().add("spacing-l");
        return viewEvents;
    }

    /*
    private Component createServiceHealth() {
        // Header
        HorizontalLayout header = createHeader("Service health", "Input / output");

        // Grid
        Grid<ServiceHealth> grid = new Grid();
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setAllRowsVisible(true);

        grid.addColumn(new ComponentRenderer<>(serviceHealth -> {
            Span status = new Span();
            String statusText = getStatusDisplayName(serviceHealth);
            status.getElement().setAttribute("aria-label", "Status: " + statusText);
            status.getElement().setAttribute("title", "Status: " + statusText);
            status.getElement().getThemeList().add(getStatusTheme(serviceHealth));
            return status;
        })).setHeader("").setFlexGrow(0).setAutoWidth(true);
        grid.addColumn(ServiceHealth::getCity).setHeader("City").setFlexGrow(1);
        grid.addColumn(ServiceHealth::getInput).setHeader("Input").setAutoWidth(true).setTextAlign(ColumnTextAlign.END);
        grid.addColumn(ServiceHealth::getOutput).setHeader("Output").setAutoWidth(true)
                .setTextAlign(ColumnTextAlign.END);

        grid.setItems(new ServiceHealth(Status.EXCELLENT, "Münster", 324, 1540),
                new ServiceHealth(Status.OK, "Cluj-Napoca", 311, 1320),
                new ServiceHealth(Status.FAILING, "Ciudad Victoria", 300, 1219));

        // Add it all together
        VerticalLayout serviceHealth = new VerticalLayout(header, grid);
        serviceHealth.addClassName(Padding.LARGE);
        serviceHealth.setPadding(false);
        serviceHealth.setSpacing(false);
        serviceHealth.getElement().getThemeList().add("spacing-l");
        return serviceHealth;
    }

    private Component createResponseTimes() {
        HorizontalLayout header = createHeader("Response times", "Average across all systems");

        // Chart
        Chart chart = new Chart(ChartType.PIE);
        Configuration conf = chart.getConfiguration();
        conf.getChart().setStyledMode(true);
        chart.setThemeName("gradient");

        DataSeries series = new DataSeries();
        series.add(new DataSeriesItem("System 1", 12.5));
        series.add(new DataSeriesItem("System 2", 12.5));
        series.add(new DataSeriesItem("System 3", 12.5));
        series.add(new DataSeriesItem("System 4", 12.5));
        series.add(new DataSeriesItem("System 5", 12.5));
        series.add(new DataSeriesItem("System 6", 12.5));
        conf.addSeries(series);

        // Add it all together
        VerticalLayout serviceHealth = new VerticalLayout(header, chart);
        serviceHealth.addClassName(Padding.LARGE);
        serviceHealth.setPadding(false);
        serviceHealth.setSpacing(false);
        serviceHealth.getElement().getThemeList().add("spacing-l");
        return serviceHealth;
    }
    */
    private HorizontalLayout createHeader(String title, String subtitle) {
        H2 h2 = new H2(title);
        h2.addClassNames(FontSize.XLARGE, Margin.NONE);

        Span span = new Span(subtitle);
        if(subtitle.equals("")) {
            span.addClassNames(TextColor.SECONDARY, FontSize.XXSMALL);
        } else {
            span.addClassNames(TextColor.SECONDARY, FontSize.SMALL);
        }
        VerticalLayout column = new VerticalLayout(h2, span);
        column.setPadding(false);
        column.setSpacing(false);

        HorizontalLayout header = new HorizontalLayout(column);
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.setSpacing(false);
        header.setWidthFull();
        return header;
    }

    private String getStatusDisplayName(ServiceHealth serviceHealth) {
        Status status = serviceHealth.getStatus();
        if (status == Status.OK) {
            return "Ok";
        } else if (status == Status.FAILING) {
            return "Failing";
        } else if (status == Status.EXCELLENT) {
            return "Excellent";
        } else {
            return status.toString();
        }
    }

    private String getStatusTheme(ServiceHealth serviceHealth) {
        Status status = serviceHealth.getStatus();
        String theme = "badge primary small";
        if (status == Status.EXCELLENT) {
            theme += " success";
        } else if (status == Status.FAILING) {
            theme += " error";
        }
        return theme;
    }

}
