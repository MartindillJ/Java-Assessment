/**
 * Weather represents a weather information
 */
public class Weather {

    /**
     * year
     */
    private int year;

    /**
     * month
     */
    private int month;

    /**
     * max temperature
     */
    private double maxTemperature;

    /**
     * min temperature
     */
    private double minTemperature;

    /**
     * air frost days
     */
    private int airFrostDays;

    /**
     * total rainfall
     */
    private double totalRainfall;

    /**
     * constructor
     *
     * @param year
     * @param month
     * @param maxTemperature
     * @param minTemperature
     * @param airFrostDays
     * @param totalRainfall
     */
    public Weather(int year, int month, double maxTemperature, double minTemperature, int airFrostDays, double totalRainfall) {
        this.year = year;
        this.month = month;
        this.maxTemperature = maxTemperature;
        this.minTemperature = minTemperature;
        this.airFrostDays = airFrostDays;
        this.totalRainfall = totalRainfall;
    }

    /**
     * year
     */
    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    /**
     * month
     */
    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    /**
     * max temperature
     */
    public double getMaxTemperature() {
        return maxTemperature;
    }

    public void setMaxTemperature(double maxTemperature) {
        this.maxTemperature = maxTemperature;
    }

    /**
     * min temperature
     */
    public double getMinTemperature() {
        return minTemperature;
    }

    public void setMinTemperature(double minTemperature) {
        this.minTemperature = minTemperature;
    }

    /**
     *  air frost days in that month
     */
    public int getAirFrostDays() {
        return airFrostDays;
    }

    public void setAirFrostDays(int airFrostDays) {
        this.airFrostDays = airFrostDays;
    }

    /**
     * total rainfall.
     */
    public double getTotalRainfall() {
        return totalRainfall;
    }

    public void setTotalRainfall(double totalRainfall) {
        this.totalRainfall = totalRainfall;
    }
}
