package weatherdashboard.model;

/**
 * Model class encapsulating air quality, pressure, dew point, and UV index.
 */
public class AirConditions {
    private final double uvIndex;
    private final double dewPoint;

    public AirConditions(double uvIndex, double dewPoint) {
        this.uvIndex = uvIndex;
        this.dewPoint = dewPoint;
    }

    public double getUvIndex() { return uvIndex; }
    public double getDewPoint() { return dewPoint; }
}
