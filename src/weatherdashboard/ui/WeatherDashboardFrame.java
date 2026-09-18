package weatherdashboard.ui;

import weatherdashboard.config.UITheme;
import weatherdashboard.model.WeatherData;
import weatherdashboard.service.IconLoaderService;
import weatherdashboard.service.WeatherApiService;
import weatherdashboard.ui.components.AirConditionsPanel;
import weatherdashboard.ui.components.CurrentWeatherPanel;
import weatherdashboard.ui.components.FiveDayForecastPanel;
import weatherdashboard.ui.components.HeaderPanel;
import weatherdashboard.ui.components.HourlyForecastPanel;
import weatherdashboard.ui.components.SearchPanel;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * Main Swing Window Frame coordinating UI component panels and binding weather services.
 */
public class WeatherDashboardFrame extends JFrame {

    private final WeatherApiService apiService;
    private final IconLoaderService iconService;

    private HeaderPanel headerPanel;
    private SearchPanel searchPanel;
    private CurrentWeatherPanel currentWeatherPanel;
    private HourlyForecastPanel hourlyForecastPanel;
    private AirConditionsPanel airConditionsPanel;
    private FiveDayForecastPanel fiveDayForecastPanel;

    private Timer clockTimer;
    private String currentCity = "Homagama";
    private String activeUnit = "metric";

    public WeatherDashboardFrame() {
        this.apiService = new WeatherApiService();
        this.iconService = new IconLoaderService();

        initializeWindow();
        fetchData(currentCity);
        startClockTimer();
    }

    private void initializeWindow() {
        setTitle("WeatherWave - Live Dashboard");
        setSize(1400, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        getContentPane().setBackground(UITheme.DARK_BLACK);
        setLayout(null);

        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gradient = new GradientPaint(0, 0, UITheme.DARK_BLACK, getWidth(), getHeight(), UITheme.LIGHT_BLACK);
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        backgroundPanel.setBounds(0, 0, 1400, 800);
        backgroundPanel.setLayout(null);
        add(backgroundPanel);

        // Instantiate modular component panels
        headerPanel = new HeaderPanel(backgroundPanel, this::onUnitChanged);
        searchPanel = new SearchPanel(backgroundPanel, currentCity, new SearchPanel.SearchListener() {
            @Override
            public void onSearchRequested(String city) {
                onSearchSubmitted(city);
            }

            @Override
            public void onLocationRequested() {
                onSearchSubmitted("Homagama");
            }
        });

        currentWeatherPanel = new CurrentWeatherPanel(backgroundPanel, currentCity, iconService);
        hourlyForecastPanel = new HourlyForecastPanel(backgroundPanel, iconService);
        airConditionsPanel = new AirConditionsPanel(backgroundPanel);
        fiveDayForecastPanel = new FiveDayForecastPanel(backgroundPanel, iconService);
    }

    private void onUnitChanged(String unit) {
        this.activeUnit = unit;
        headerPanel.setStatus("Switched units to " + (unit.equals("metric") ? "Celsius (°C)" : "Fahrenheit (°F)"), false);
        fetchData(currentCity);
    }

    private void onSearchSubmitted(String city) {
        this.currentCity = city;
        headerPanel.setStatus("Searching for " + city + "...", false);
        searchPanel.setEnabled(false);
        fetchData(city);
    }

    private void fetchData(String city) {
        apiService.fetchCurrentWeather(city, activeUnit)
                .thenAcceptAsync(weatherData -> {
                    currentWeatherPanel.updateData(weatherData);
                    airConditionsPanel.updateWeatherData(weatherData, activeUnit);

                    // Fetch UV index after current weather coordinates arrive
                    apiService.fetchUVIndex(weatherData.getLatitude(), weatherData.getLongitude())
                            .thenAcceptAsync(uv -> airConditionsPanel.updateUVIndex(uv), SwingUtilities::invokeLater);

                    headerPanel.setStatus("Weather data updated successfully for " + weatherData.getCityName(), false);
                    searchPanel.setEnabled(true);
                }, SwingUtilities::invokeLater)
                .exceptionally(ex -> {
                    SwingUtilities.invokeLater(() -> {
                        String err = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
                        headerPanel.setStatus("Error: " + err, true);
                        searchPanel.setEnabled(true);
                    });
                    return null;
                });

        apiService.fetchHourlyForecast(city, activeUnit)
                .thenAcceptAsync(hourlyList -> hourlyForecastPanel.updateData(hourlyList), SwingUtilities::invokeLater);

        apiService.fetch5DayForecast(city, activeUnit)
                .thenAcceptAsync(dailyList -> fiveDayForecastPanel.updateData(dailyList), SwingUtilities::invokeLater);
    }

    private void startClockTimer() {
        clockTimer = new Timer(60000, e -> currentWeatherPanel.updateDateTime());
        clockTimer.start();
    }

    @Override
    public void dispose() {
        if (clockTimer != null) {
            clockTimer.stop();
        }
        super.dispose();
    }
}
