package weatherdashboard.model;

/**
 * Model class encapsulating a single 3-hour forecast slot.
 */
public class HourlyForecast {
    private final String timeLabel;
    private final double temperature;
    private final String iconCode;

    public HourlyForecast(String timeLabel, double temperature, String iconCode) {
        this.timeLabel = timeLabel;
        this.temperature = temperature;
        this.iconCode = iconCode;
    }

    public String getTimeLabel() { return timeLabel; }
    public double getTemperature() { return temperature; }
    public String getIconCode() { return iconCode; }
}
