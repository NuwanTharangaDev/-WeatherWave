package weatherdashboard.model;

/**
 * Strongly-typed Java POJO encapsulating real-time current weather metrics.
 */
public class WeatherData {
    private final String cityName;
    private final String country;
    private final double temperature;
    private final double feelsLike;
    private final int humidity;
    private final double pressure;
    private final int visibility;
    private final double windSpeed;
    private final String description;
    private final String iconCode;
    private final double latitude;
    private final double longitude;
    private final long sunriseTime;
    private final long sunsetTime;

    public WeatherData(String cityName, String country, double temperature, double feelsLike,
                       int humidity, double pressure, int visibility, double windSpeed,
                       String description, String iconCode, double latitude, double longitude,
                       long sunriseTime, long sunsetTime) {
        this.cityName = cityName;
        this.country = country;
        this.temperature = temperature;
        this.feelsLike = feelsLike;
        this.humidity = humidity;
        this.pressure = pressure;
        this.visibility = visibility;
        this.windSpeed = windSpeed;
        this.description = description;
        this.iconCode = iconCode;
        this.latitude = latitude;
        this.longitude = longitude;
        this.sunriseTime = sunriseTime;
        this.sunsetTime = sunsetTime;
    }

    public String getCityName() { return cityName; }
    public String getCountry() { return country; }
    public double getTemperature() { return temperature; }
    public double getFeelsLike() { return feelsLike; }
    public int getHumidity() { return humidity; }
    public double getPressure() { return pressure; }
    public int getVisibility() { return visibility; }
    public double getWindSpeed() { return windSpeed; }
    public String getDescription() { return description; }
    public String getIconCode() { return iconCode; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public long getSunriseTime() { return sunriseTime; }
    public long getSunsetTime() { return sunsetTime; }
}
