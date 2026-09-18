package weatherdashboard.config;

/**
 * Configuration class holding OpenWeatherMap API credentials,
 * base endpoint URLs, and HTTP connection timeout settings.
 */
public final class ApiConfig {

    private ApiConfig() {
        // Restrict instantiation
    }

    // OpenWeatherMap API Key
    public static final String API_KEY = "Your_API_Key";

    // Endpoints
    public static final String BASE_URL = "http://api.openweathermap.org/data/2.5/weather";
    public static final String FORECAST_URL = "http://api.openweathermap.org/data/2.5/forecast";
    public static final String UV_URL = "http://api.openweathermap.org/data/2.5/uvi";
    public static final String ICON_BASE_URL = "https://openweathermap.org/img/wn/";

    // Connection Timeouts (ms)
    public static final int CONNECT_TIMEOUT_MS = 10000;
    public static final int READ_TIMEOUT_MS = 10000;
    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) WeatherWave/1.0";
}
