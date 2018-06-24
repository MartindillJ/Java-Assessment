/**
 * Student No: 1768263
 * Module Code: CMT205
 * Module Title: Object-Oriented Development with Java
 * Coursework Title: Simple Weather Data Viewer using JavaFx
 * Lecturer: Dr Y Lai and Dr M Morgan
 * Hours Spent on this Exercise: ~40
 * ----------------------------------------
 * WeatherStationMain class is JavaFX application that read weathers from file,
 * displays weather details and statistics on a grid.
 * And allows users to view weather on the Chart
 */
import javafx.application.Application;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class WeatherStationMain extends Application {

    /**
     * stage
     */
    private Stage window;

    /**
     * data folder
     */
    private static final String DATA_FOLDER = "CMT205CWDATA";

    /**
     * list of station
     */
    private static List<String> stations = new ArrayList<>();

    /**
     * map with entry {station, list of weather information}
     */
    private static HashMap<String, List<Weather>> weathersMap = new HashMap<>();

    /**
     * station combo box
     */
    private static ComboBox<String> stationComboBox;

    /**
     * weather grid panel
     */
    private static  GridPane weatherGrid;

    /**
     * statistics grid panel
     */
    private static  GridPane statisticGrid;

    /**
     * year combo box
     */
    private static ComboBox<Integer> yearComboBox;

    /**
     * main scene
     */
    private static Scene mainScene;

    /**
     * array of months
     */
    private static final String[] MONTHS = {
            "", "JAN", "FEB","MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC"
    };

    /**
     * main method
     * @param args
     */
    public static void main(String[] args) {
        //load weather from files
        loadWeathers();
        launch(args);
    }

    /**
     * load weathers from file
     */
    private static void loadWeathers() {
        //data folder
        File folder = new File(DATA_FOLDER);

        File[] fileNames = folder.listFiles();
        for(File file : fileNames){

            //file name that contains station
            String filename = file.getName();

            //station
            String station = filename.substring(0, filename.indexOf("."));

            //weathers of station
            List<Weather> weathers = new ArrayList<>();

            //read file
            try (Scanner input = new Scanner(file)) {

                //read line by line
                while (input.hasNextLine()){

                    String[] data = input.nextLine().split(",");

                    //extra information for the weather
                    int year = Integer.parseInt(data[0]);
                    int month = Integer.parseInt(data[1]);
                    double maxTemperature = Double.parseDouble(data[2]);
                    double minTemperature = Double.parseDouble(data[3]);
                    int airForstDays = Integer.parseInt(data[4]);
                    double totalRainfall = Double.parseDouble(data[5]);

                    //add weathers
                    weathers.add(new Weather(year, month, maxTemperature, minTemperature, airForstDays, totalRainfall));
                }

                //add station
                stations.add(station);

                //add to map
                weathersMap.put(station, weathers);

            }catch(Exception e){
                //ignore
            }
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        window = primaryStage;
        window.setTitle("Weather Station Statistics Application");

        //create main scene
        createMainScene();

        //set scene
        window.setScene(mainScene);

        //show window
        window.show();

        //Disable resizing
        window.setResizable(false);
    }

    /**
     * data series of total rainfall
     */
    private XYChart.Series  totalRainfallDataSeries;

    //create main scene
    private void createMainScene(){

        //a box work as container of upper, center, and bottom box
        VBox mainBox = new VBox();

        //upper box that contains years and stations
        HBox upperBox = new HBox();
        upperBox.setSpacing(10);
        upperBox.setPadding(new Insets(10, 10, 10, 10));
        upperBox.getChildren().add(new Label("Meteorological Station: "));

        ObservableList<String> stationsOptions = FXCollections.observableArrayList(stations);
        stationComboBox = new ComboBox(stationsOptions);
        upperBox.getChildren().add(stationComboBox);

        yearComboBox = new ComboBox();

        upperBox.getChildren().add(new Label("Year: "));
        upperBox.getChildren().add(yearComboBox);

        //GridPane for display weather
        weatherGrid = new GridPane();
        weatherGrid.setPadding(new Insets(10, 10, 10, 10));
        weatherGrid.setVgap(8);
        weatherGrid.setHgap(28);

        //GridPane for display statistics
        statisticGrid = new GridPane();
        statisticGrid.setPadding(new Insets(10, 10, 10, 10));
        statisticGrid.setVgap(8);
        statisticGrid.setHgap(10);

        //button panel
        HBox buttonBox = new HBox();
        buttonBox.setPadding(new Insets(10, 10, 10, 10));
        buttonBox.setSpacing(10);

        Button viewChart = new Button("View Chart");
        Button createReport = new Button("Generate Report");
        Button exitApp = new Button("Quit Application");

        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(viewChart, createReport, exitApp);

        //create report
        createReport.setOnAction(e -> {
            doReport();
        });

        //set exit
        exitApp.setOnAction(e -> {
            System.exit(0);
        });

        //set exit
        viewChart.setOnAction(e -> {
            doViewChart();
        });

        //add to main box
        mainBox.getChildren().add(upperBox);
        mainBox.getChildren().add(weatherGrid);
        mainBox.getChildren().add(statisticGrid);
        mainBox.getChildren().add(buttonBox);

        mainScene = new Scene(mainBox, 505, 645);

        //set default
        stationComboBox.getSelectionModel().selectFirst();

        //load year for specific station
        int first = loadYear();

        //load specific weather
        loadSpecificWeather(first);

        //change station
        stationComboBox.valueProperty().addListener(new javafx.beans.value.ChangeListener<String>() {

            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

                //load year and specific weather
                int firstYear = loadYear();
                yearComboBox.getSelectionModel().selectLast();
                loadSpecificWeather(firstYear);
            }
        });

        //change year
        yearComboBox.valueProperty().addListener(new javafx.beans.value.ChangeListener<Integer>() {

            @Override
            public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {

                //load specific weather
                if (yearComboBox.getSelectionModel().getSelectedItem() != null){
                    loadSpecificWeather(yearComboBox.getSelectionModel().getSelectedItem());
                }
            }
        });

        yearComboBox.getSelectionModel().selectLast();

        //set border for grid
        weatherGrid.setStyle("-fx-padding: 20;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 1;" +
                "-fx-border-insets: 1;" +
                "-fx-border-radius: 0;" +
                "-fx-border-color: grey;");
    }

    //create chart scene
    private Scene createChartScene(){

        //x axis
        CategoryAxis xAxis    = new CategoryAxis();
        xAxis.setLabel("Month");

        //y axis
        NumberAxis yAxis = new NumberAxis();

        //bar chart
        BarChart barChart = new BarChart(xAxis, yAxis);
        barChart.setPrefHeight(1080);

        XYChart.Series maxTempDataSeries = new XYChart.Series();
        maxTempDataSeries.setName("Tmax (\u00B0C) ");
        XYChart.Series minTempDataSeries = new XYChart.Series();
        minTempDataSeries.setName("Tmin (\u00B0C) ");
        XYChart.Series airFrostDaysDataSeries = new XYChart.Series();
        airFrostDaysDataSeries.setName("Air Frost");

        totalRainfallDataSeries  = new XYChart.Series();
        totalRainfallDataSeries.setName("Rainfall (cm)");

        String station = stationComboBox.getSelectionModel().getSelectedItem();
        int year = yearComboBox.getSelectionModel().getSelectedItem();

        for (Weather weather: weathersMap.get(station)){
            if (weather.getYear() == year){
                maxTempDataSeries.getData().add(new XYChart.Data(MONTHS[weather.getMonth()] + "", weather.getMaxTemperature()));
                minTempDataSeries.getData().add(new XYChart.Data(MONTHS[weather.getMonth()] + "", weather.getMinTemperature()));
                airFrostDaysDataSeries.getData().add(new XYChart.Data(MONTHS[weather.getMonth()] + "", weather.getAirFrostDays()));
                totalRainfallDataSeries.getData().add(new XYChart.Data(MONTHS[weather.getMonth()] + "", weather.getTotalRainfall()/10));
            }
        }

        barChart.getData().add(maxTempDataSeries);
        barChart.getData().add(minTempDataSeries);
        barChart.getData().add(airFrostDaysDataSeries);

        //bar chart box
        VBox barChartBox = new VBox(barChart);

        //button box
        HBox buttonBox = new HBox();
        buttonBox.setPadding(new Insets(5, 10, 10, 10));
        buttonBox.setSpacing(50);
        buttonBox.setAlignment(Pos.CENTER);

        Button closeScene = new Button("Back");
        CheckBox rainfallCheckBox = new CheckBox("View rainfall");
        rainfallCheckBox.selectedProperty().addListener(new javafx.beans.value.ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if (newValue){
                    barChart.getData().add(totalRainfallDataSeries);
                } else {
                    barChart.getData().remove(totalRainfallDataSeries);
                }
            }
        });

        buttonBox.getChildren().addAll(rainfallCheckBox, closeScene);

        //set exit
        closeScene.setOnAction(e -> {
            window.setScene(mainScene);
        });

        VBox mainBox = new VBox();
        mainBox.getChildren().addAll(barChartBox, buttonBox);

        return new Scene(mainBox, 960, 645);
    }

    /**
     * write to file
     *
     * Number: <sequence number>
     * Station: <station name>
     * Highest: <month/year with the highest Tmax>
     * Lowest: <month/year with the lowest Tmin>
     * Average annual af: <average days of air frost per year>
     * Average annual rainfall: <average annual rainfall>
     */
    private void doReport(){

        //choose file
        final FileChooser fileChooser = new FileChooser();

        //add extension
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);

        File file = fileChooser.showSaveDialog(window);
        if (file != null) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(file.getAbsoluteFile()))) {
                //bw.write("Your report " + System.lineSeparator());
                //bw.write(System.lineSeparator());

                //for each station
                for (int seq = 0; seq < stations.size(); seq++){

                    bw.write("Number: " + (seq  + 1) + System.lineSeparator());
                    bw.write("Station: " + stations.get(seq) + System.lineSeparator());

                    //Highest: <month/year with the highest Tmax>
                    Weather highestMaxTemp = null;
                    //Lowest: <month/year with the lowest Tmin>
                    Weather lowestMinTempWeather = null;

                    //Average annual af: <average days of air frost per year>
                    //Average annual rainfall: <average annual rainfall>
                    int totalAirForstDays = 0;
                    double totalRainfall = 0;

                    List<Weather> weathers = weathersMap.get(stations.get(seq));
                    for (Weather weather: weathers){
                        if (highestMaxTemp == null || highestMaxTemp.getMaxTemperature() < weather.getMaxTemperature()){
                            highestMaxTemp = weather;
                        }
                        if (lowestMinTempWeather == null || lowestMinTempWeather.getMinTemperature() > weather.getMinTemperature()){
                            lowestMinTempWeather = weather;
                        }
                        totalAirForstDays += weather.getAirFrostDays();
                        totalRainfall += weather.getTotalRainfall();
                    }

                    bw.write("Highest: " + highestMaxTemp.getMonth() + "/" + highestMaxTemp.getYear() + System.lineSeparator());
                    bw.write("Lowest: " + lowestMinTempWeather.getMonth() + "/" + lowestMinTempWeather.getYear() + System.lineSeparator());
                    bw.write("Average annual af: " + String.format("%.2f", totalAirForstDays / (double)weathers.size()) + System.lineSeparator());
                    bw.write("Average annual rainfall: " + String.format("%.2f", totalRainfall / (double)weathers.size()) + System.lineSeparator());

                    bw.write(System.lineSeparator());
                }

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Weather station");
                alert.setHeaderText(null);
                alert.setContentText("Report has been created successfully");
                alert.showAndWait();

            } catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Weather station");
                alert.setHeaderText(null);
                alert.setContentText("Could not write to file");
                alert.showAndWait();
            }
        }
    }

    /**
     * view chart
     */
    private void doViewChart(){
        window.setScene(createChartScene());
    }
    /**
     * load year for specific station
     * @return first year
     */
    private int loadYear(){

        int firstYear = 0;

        //retrieve weather of the specific station
        List<Weather> weathers = weathersMap.get(stationComboBox.getSelectionModel().getSelectedItem());

        List<Integer> years = new ArrayList<>();

        //iterate the list and extract year
        for (Weather w: weathers){

            if (!years.contains(w.getYear())){
                years.add(w.getYear());

                if (firstYear == 0){
                    firstYear = w.getYear();
                }
            }
        }

        //add item to combo box
        ObservableList<Integer> yearOptions =
                FXCollections.observableArrayList(years);
        yearComboBox.setItems(yearOptions);

        return firstYear;
    }

    /**
     * load weather information for a station in one year
     */
    private void loadSpecificWeather(int year) {

        //remove grid content
        weatherGrid.getChildren().clear();

        String station = stationComboBox.getSelectionModel().getSelectedItem();

        //add header
        //Name Label - constrains use (child, column, row)
        Label monthLabel = new Label("Month");
        GridPane.setConstraints(monthLabel, 0, 0);
        Label maxTemperatureLabel = new Label("Tmax (\u00B0C)");
        GridPane.setConstraints(maxTemperatureLabel, 1, 0);
        Label minTemperatureLabel = new Label("Tmin (\u00B0C)");
        GridPane.setConstraints(minTemperatureLabel, 2, 0);
        Label airFrostDaysLabel  = new Label("Air frost days");
        GridPane.setConstraints(airFrostDaysLabel, 3, 0);
        Label totalRainfallLabel  = new Label("Rainfall (mm)");
        GridPane.setConstraints(totalRainfallLabel, 4, 0);

        //add as column header of grid
        weatherGrid.getChildren().addAll(monthLabel, maxTemperatureLabel, minTemperatureLabel, airFrostDaysLabel, totalRainfallLabel);

        //for statistics
        //Highest monthly mean maximum temperature (Tmax)
        //Lowest monthly mean minimum temperature (Tmin)
        //Total air frost days
        //Total rainfall

        double highestMaxTemp = Double.MIN_NORMAL;
        double lowestMinTemp = Double.MAX_VALUE;
        int totalAirForstDays = 0;
        double totalRainfall = 0;

        int row = 1;
        for (Weather weather: weathersMap.get(station)){
            if (weather.getYear() == year){
                //month
                Label monthValueLabel = new Label("" + MONTHS[weather.getMonth()]);
                GridPane.setConstraints(monthValueLabel, 0, row);

                //max temperature
                Label maxTempValueLabel = new Label("" + weather.getMaxTemperature());
                GridPane.setConstraints(maxTempValueLabel, 1, row);

                //min temperature
                Label minTempValueLabel = new Label("" + weather.getMinTemperature());
                GridPane.setConstraints(minTempValueLabel, 2, row);

                //air forst days
                Label airFrostDaysValueLabel = new Label("" + weather.getAirFrostDays());
                GridPane.setConstraints(airFrostDaysValueLabel, 3, row);

                //total rainfall
                Label totalRainfallValueLabel = new Label("" + weather.getTotalRainfall());
                GridPane.setConstraints(totalRainfallValueLabel, 4, row);

                weatherGrid.getChildren().addAll(monthValueLabel, maxTempValueLabel, minTempValueLabel, airFrostDaysValueLabel, totalRainfallValueLabel);

                row++; //next row

                if (highestMaxTemp < weather.getMaxTemperature()){
                    highestMaxTemp = weather.getMaxTemperature();
                }
                if (lowestMinTemp > weather.getMinTemperature()){
                    lowestMinTemp = weather.getMinTemperature();
                }
                totalAirForstDays += weather.getAirFrostDays();
                totalRainfall += weather.getTotalRainfall();
            }
        }

        /**
         * Display the followings
         * Highest monthly mean maximum temperature (Tmax)
         * Lowest monthly mean minimum temperature (Tmin)
         * Total air frost days
         * Total rainfall
         */
        statisticGrid.getChildren().clear();

        // Highest monthly mean maximum temperature (Tmax)
        Label highestMaxTempLabel = new Label("Highest monthly mean maximum temperature (\u00B0C):");
        GridPane.setConstraints(highestMaxTempLabel, 0, 0);
        Label highestMaxTempValueLabel = new Label("" + highestMaxTemp);
        GridPane.setConstraints(highestMaxTempValueLabel, 1, 0);

        // Lowest monthly mean minimum temperature (Tmin)
        Label lowestMinTempLabel = new Label("Lowest monthly mean minimum temperature (\u00B0C):");
        GridPane.setConstraints(lowestMinTempLabel, 0, 1);
        Label lowestMinTempValueLabel = new Label("" + lowestMinTemp);
        GridPane.setConstraints(lowestMinTempValueLabel, 1, 1);

        // Total air frost days
        Label totalAirForstDaysLabel = new Label("Total air frost days:");
        GridPane.setConstraints(totalAirForstDaysLabel, 0, 2);
        Label totalAirForstDaysValueLabel = new Label("" + totalAirForstDays);
        GridPane.setConstraints(totalAirForstDaysValueLabel, 1, 2);

        //  Total rainfall
        Label totalRainFallLabel = new Label("Total annual rainfall (mm):");
        GridPane.setConstraints(totalRainFallLabel, 0, 3);
        Label totalRainFallValueLabel = new Label(String.format("%.1f", totalRainfall));
        GridPane.setConstraints(totalRainFallValueLabel, 1, 3);

        statisticGrid.getChildren().addAll(highestMaxTempLabel, highestMaxTempValueLabel,
                lowestMinTempLabel, lowestMinTempValueLabel,
                totalAirForstDaysLabel, totalAirForstDaysValueLabel,
                totalRainFallLabel, totalRainFallValueLabel);
    }
}