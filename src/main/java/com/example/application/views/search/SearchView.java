package com.example.application.views.search;


import com.example.application.views.MainLayout;
import com.example.application.views.favorites.Client;
import com.example.application.views.favorites.FavoritesView;
import com.example.application.views.search.ServiceHealth.Status;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.charts.model.OhlcItem;
import com.vaadin.flow.component.board.Board;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.*;
import com.vaadin.flow.component.charts.model.style.SolidColor;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.Autocapitalize;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
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
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.router.Route;
import javax.swing.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.*;

@PageTitle("Search")
@Route(value = "dashboard", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@AnonymousAllowed
public class SearchView extends Main {

    private String ticker;
    private final ArrayList<HashMap<String, Object>> listOfMapData = new ArrayList<>();
    private final ArrayList listOfCharts = new ArrayList();
    private ArrayList<String> timeStampList;
    private String minYear;
    private String maxYear;
    double lastOpenPrice;
    double maxHighPrice;
    double maxLowPrice;
//    private Checkbox favoriteCheckBox;
    private Button favoriteButton;

    public SearchView() {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);

        // set up Board for layout, and add ticker search box and button
        Board board = new Board();

        TextField stockTickerTF = new TextField();
        stockTickerTF.getElement().setAttribute("title", "example: AAPL is Apple's ticker");
        stockTickerTF.setClearButtonVisible(true);
        stockTickerTF.setMinLength(1);
        stockTickerTF.setMaxLength(5);
        stockTickerTF.setPattern("^[A-Z]+");
        stockTickerTF.setErrorMessage("Not a ticker symbol.");
        stockTickerTF.setWidth("min-content");
        stockTickerTF.setPlaceholder("enter");
        stockTickerTF.setWidth("35%");
        stockTickerTF.setLabel("Ticker Symbol");

        Button searchButton = new Button("Search");
        searchButton.getElement().setAttribute("title", "Search");
        Button clearButton = new Button("Clear");
        clearButton.getElement().setAttribute("title", "Clear");

        // max year selection
        Select maxYear = new Select();
        maxYear.setItems("2022", "2023", "2024");
        maxYear.setValue("Max Year");
        maxYear.setLabel("Max Year");
        maxYear.setWidth("35%");
        // min year selection
        Select minYear = new Select();
        minYear.setItems("2022", "2023", "2024");
        minYear.setValue("Min Year");
        minYear.setLabel("Min Year");
        minYear.setWidth("35%");

        // check if minYear's seelction is greater than maxYears' and visa-versa to throw error messge.

        // Create a HorizontalLayout for text field and select tools
        HorizontalLayout textFieldLayout = new HorizontalLayout();
        textFieldLayout.add(stockTickerTF, maxYear, minYear);
        textFieldLayout.setSpacing(true);

        // Create a HorizontalLayout for text field and select tools
        HorizontalLayout textFieldLayout2 = new HorizontalLayout();
        textFieldLayout2.add(searchButton, clearButton);
        textFieldLayout2.setSpacing(true);

        verticalLayout.add(board, textFieldLayout, textFieldLayout2);
        add(verticalLayout);

        // when button is pressed, run this Lambda function
        searchButton.addClickListener(e -> {

            TextfieldSubject searchSubject = new TextfieldSubject();
            ValueUpdater valUpdater = new ValueUpdater();
            searchSubject.addObserver(valUpdater);
            this.minYear = minYear.getValue().toString();
            this.maxYear = maxYear.getValue().toString();

            // setValue in TextfieldSubject calls notifyObservers which then calls update in ValueUpdater.
            // The update method in ValueUpdater receives an API response based on the text-field input.
            searchSubject.setValue(stockTickerTF.getValue(), this.minYear, this.maxYear);

            // get API data from update in Value Updater and add it to a list in SearchView
            listOfMapData.add(valUpdater.getMapData());

            // get the max/min value from the data
            this.maxHighPrice = ((ArrayList<Double>)listOfMapData.get(0).get("highPricesList")).stream().mapToDouble(Double::doubleValue).max().orElse(Double.NaN);
            this.maxLowPrice = ((ArrayList<Double>)listOfMapData.get(0).get("lowPricesList")).stream().mapToDouble(Double::doubleValue).min().orElse(Double.NaN);
            this.lastOpenPrice = ((ArrayList<Double>)listOfMapData.get(0).get("openPricesList")).get(((ArrayList<Double>)listOfMapData.get(0).get("openPricesList")).size() - 1);

            ArrayList<Long> tempTimeList = valUpdater.getTimestampList();
            timeStampList = new ArrayList<>();
            for (Long timeStamp : tempTimeList) {
                // tempTimeList.get(i) is of type Long
                // unixConverter(tempTimeList.get(i)) returns a String
                this.timeStampList.add(unixConverter(timeStamp));
            }
            // initialize df for formatting -> max two decimals
            DecimalFormat df = new DecimalFormat("#.##");
            board.addRow(
                    createHighlight("Ticker Symbol", String.valueOf(listOfMapData.get(0).get("ticker"))),
                    // get the most recent open price by finding the last value (size()-1) in the list
                    createHighlight("Last Open", String.valueOf(((ArrayList<Double>)listOfMapData.get(0).get("openPricesList")).get(((ArrayList<Double>)listOfMapData.get(0).get("openPricesList")).size() - 1)),
                            Double.parseDouble(df.format((((ArrayList<Double>)listOfMapData.get(0).get("openPricesList")).get(((ArrayList<Double>)listOfMapData.get(0).get("openPricesList")).size() - 1)) - (((ArrayList<Double>)listOfMapData.get(0).get("openPricesList")).get(((ArrayList<Double>)listOfMapData.get(0).get("openPricesList")).size() - 2))))),
                    // get the max value in valUpdater.getHighPricesList()
                    createHighlight("High", String.valueOf(maxHighPrice)),
                    // get the min value in valUpdater.getLowPricesList()
                    createHighlight("Low", String.valueOf(maxLowPrice))
            );
            // create favorite check box when chart is created and ticker is searched.
//            favoriteCheckBox = new Checkbox("Favorite");
            favoriteButton = new Button("Favorite");

            VerticalLayout checkBoxLayout = new VerticalLayout(favoriteButton);
            checkBoxLayout.setDefaultHorizontalComponentAlignment(FlexComponent.Alignment.CENTER);
            addToFavorites();

            board.addRow(checkBoxLayout);
            // board.addRow(createViewEvents(valUpdater.getOpenPricesList(), valUpdater.getResponseList()));
            board.addRow(createViewEvents((ArrayList<Double>) listOfMapData.get(0).get("openPricesList")));
            // board.addRow(createServiceHealth(), createResponseTimes());

        });
        add(board);
        // when clear button is hit, clear the current chart and data info on the screen.
        clearButton.addClickListener(e -> {
            // if there is something in listOfMapData, remove it. If not, show a message on the vaadin web app to show that you can't do that.
            if(!listOfMapData.isEmpty()) {
                listOfMapData.remove(0);
            } else {
                Notification n = new Notification("Nothing to Clear!");
                n.open();
            }
//            if(chartComponent )
            System.out.println("LIST OF MAP DATA:" + listOfMapData);
        });

        // NEXT STEP: REMOVE OBSERVER FROM LIST OF OBSERVERS TO GET RID OF CURRENT CHART AND CREATE NEW ONE.
        // HOW? IF THE OBSERVER LIST IS NOT EMPTY, REMOVE WHAT EVER IS IN THERE VIA searchSubject.removeObserver(valUpdater);
        // AND THEN GO FORTH IN ADDING A NEW ONE. THIS CONDITIONAL MAY HAVE TO NEST THE CURRENT CLICK LISTENER LAMBDA FUNCTION.

    }

    private void addToFavorites() {
        favoriteButton.addClickListener(e -> {
//            Client newFavorite = new Client(this.ticker, this.minYear, this.maxYear, String.valueOf(this.lastOpenPrice), String.valueOf(this.maxHighPrice), String.valueOf(this.maxLowPrice));
            FavoritesView f = new FavoritesView();
        });
    }

    private Component createHighlight(String title, String value, Double percentage) {
        VaadinIcon icon = VaadinIcon.ARROW_UP;
        String prefix = "";
        String theme = "badge";

        if (percentage == 0) {
            prefix = "± $";
        } else if (percentage > 0) {
            prefix = "+ $";
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

        Span badge = new Span(i, new Span(prefix + percentage));
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

    private Component createViewEvents(ArrayList<Double> openPricesList) {
//    private Component createViewEvents(ArrayList<Double> openPricesList, JSONArray responseList) {

        HorizontalLayout header = createHeader("Chart","");

        // Chart
        Chart chart = new Chart(ChartType.AREASPLINE);
//        Chart chart = new Chart(ChartType.CANDLESTICK);
        Configuration conf = chart.getConfiguration();

        conf.getChart().setStyledMode(true);

        XAxis xAxis = new XAxis();
        xAxis.setCategories("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec");
        conf.addxAxis(xAxis);

        conf.getyAxis().setTitle("Price (USD)");

        PlotOptionsAreaspline plotOptions = new PlotOptionsAreaspline();
        plotOptions.setPointPlacement(PointPlacement.ON);
        plotOptions.setMarker(new Marker(false));
        conf.addPlotOptions(plotOptions);


        // add a new stock to the list series that will appear as a chart and a little icon below chart on web app.
        // How to add a basic one: conf.addSeries(new ListSeries("test", 189.1, 191.1, 291.4, 396, 501, 403, 609, 712, 729, 942, 1044, 1247));
        ListSeries tempChart = new ListSeries(ticker);
        // Customize the background color of the chart
        conf.getChart().setBackgroundColor(new SolidColor("#223348"));
        /* -- for the candlestick chart delete if don't want candlestick chart
        DataSeries dataSeries = new DataSeries();
        PlotOptionsCandlestick plotOptionsCandlestick = new PlotOptionsCandlestick();
        DataGrouping grouping = new DataGrouping();
        grouping.addUnit(new TimeUnitMultiples(TimeUnit.WEEK, 1));
        grouping.addUnit(new TimeUnitMultiples(TimeUnit.MONTH, 1, 2, 3, 4, 6));
        plotOptionsCandlestick.setDataGrouping(grouping);
        dataSeries.setPlotOptions(plotOptionsCandlestick);


        // for (data in openPricesList) in java
        OhlcItem item;
        for(int i = 0; i < responseList.length(); i++) {
            item = new OhlcItem();
            // set each instance of "l" in responseList to Low in the chart
            item.setLow(responseList.getJSONObject(i).getDouble("l"));
            item.setHigh(responseList.getJSONObject(i).getDouble("h"));
            item.setOpen(responseList.getJSONObject(i).getDouble("o"));
            item.setClose(responseList.getJSONObject(i).getDouble("c"));
*/
        for(int i = 0; i < openPricesList.size(); i++) {
            tempChart.addData(openPricesList.get(i));
        }
        /*
        conf.setSeries(dataSeries);
        RangeSelector rangeSelector = new RangeSelector();
        rangeSelector.setSelected(4);
        conf.setRangeSelector(rangeSelector);

        chart.setTimeline(true);
        add(chart);*/

        ArrayList<ListSeries> stockChartList = new ArrayList<ListSeries>();
        stockChartList.add(tempChart);
        for(int i = 0; i< stockChartList.size(); i++) {
            conf.addSeries(stockChartList.get(i));
        }
        System.out.println(stockChartList.get(0));

        // Add it all together
        VerticalLayout viewEvents = new VerticalLayout(header, chart);
        viewEvents.addClassName(Padding.LARGE);
        viewEvents.setPadding(false);
        viewEvents.setSpacing(false);
        viewEvents.getElement().getThemeList().add("spacing-l");
        return viewEvents;
    }

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

    public static String unixConverter(Long unixTimestampStr) {
        // Convert Unix timestamp String to long
        long unixTimestamp = unixTimestampStr;

        // Convert Unix timestamp to Date object
        Date date = new Date(unixTimestamp);

        // Create a TimeZone object for EST (Eastern Standard Time)
        TimeZone estTimeZone = TimeZone.getTimeZone("America/New_York");

        // Set the TimeZone of the Date object to EST
        date.setTime(date.getTime() + estTimeZone.getRawOffset());

        // Format the Date object to a string in the desired format
        // You can use SimpleDateFormat or DateTimeFormatter for custom formatting
        String formattedDate = date.toString();

        // Return the formatted date
        return formattedDate;
    }
}
