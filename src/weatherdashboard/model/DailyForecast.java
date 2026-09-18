package weatherdashboard.model;

/**
 * Model class encapsulating daily weather predictions for the 5-day forecast.
 */
public class DailyForecast {
    private final String dayName;
    private final double maxTemp;
    private final double minTemp;
    private final String description;
    private final String iconCode;

    public DailyForecast(String dayName, double maxTemp, double minTemp, String description, String iconCode) {
        this.dayName = dayName;
        this.maxTemp = maxTemp;
        this.minTemp = minTemp;
        this.description = description;
        this.iconCode = iconCode;
    }

    public String getDayName() { return dayName; }
    public double getMaxTemp() { return maxTemp; }
    public double getMinTemp() { return minTemp; }
    public String getDescription() { return description; }
    public String getIconCode() { return iconCode; }
}
