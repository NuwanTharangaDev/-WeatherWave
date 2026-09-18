package weatherdashboard.ui.components;

import weatherdashboard.config.UITheme;
import weatherdashboard.model.WeatherData;
import weatherdashboard.ui.ModernCard;
import weatherdashboard.util.DateTimeUtil;
import weatherdashboard.util.WeatherMathUtil;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.Font;

/**
 * UI Component displaying air condition metrics (Feels like, Wind speed, Humidity, Visibility, UV Index, Pressure, Dew Point, Sunrise/Sunset).
 */
public class AirConditionsPanel {

    private final JLabel feelsLikeLabel;
    private final JLabel windLabel;
    private final JLabel visibilityLabel;
    private final JLabel humidityLabel;
    private final JLabel uvIndexLabel;
    private final JLabel pressureLabel;
    private final JLabel dewPointLabel;
    private final JLabel sunriseLabel;
    private final JLabel sunsetLabel;

    public AirConditionsPanel(JPanel parent) {
        // Air Conditions Card
        JPanel airCard = new ModernCard(40, 580, 250, 160);
        addCardTitle(airCard, "AIR CONDITIONS");

        feelsLikeLabel = createConditionLabel("Feels Like: --°C");
        feelsLikeLabel.setBounds(20, 35, 210, 25);

        windLabel = createConditionLabel("Wind Speed: -- km/h");
        windLabel.setBounds(20, 60, 210, 25);

        visibilityLabel = createConditionLabel("Visibility: -- km");
        visibilityLabel.setBounds(20, 85, 210, 25);

        humidityLabel = createConditionLabel("Humidity: --%");
        humidityLabel.setBounds(20, 110, 210, 25);

        uvIndexLabel = createConditionLabel("UV Index: --");
        uvIndexLabel.setBounds(20, 135, 210, 25);

        airCard.add(feelsLikeLabel);
        airCard.add(windLabel);
        airCard.add(visibilityLabel);
        airCard.add(humidityLabel);
        airCard.add(uvIndexLabel);
        parent.add(airCard);

        // Pressure Card
        JPanel pressureCard = new ModernCard(310, 580, 100, 75);
        addCardTitle(pressureCard, "PRESSURE");

        pressureLabel = new JLabel("-- hPa", SwingConstants.CENTER);
        pressureLabel.setForeground(UITheme.ACCENT_ORANGE);
        pressureLabel.setFont(UITheme.FONT_LABEL_BOLD);
        pressureLabel.setBounds(0, 30, 100, 30);
        pressureCard.add(pressureLabel);
        parent.add(pressureCard);

        // Dew Point Card
        JPanel dewCard = new ModernCard(310, 665, 100, 75);
        addCardTitle(dewCard, "DEW POINT");

        dewPointLabel = new JLabel("--°C", SwingConstants.CENTER);
        dewPointLabel.setForeground(UITheme.ACCENT_ORANGE);
        dewPointLabel.setFont(UITheme.FONT_LABEL_BOLD);
        dewPointLabel.setBounds(0, 30, 100, 30);
        dewCard.add(dewPointLabel);
        parent.add(dewCard);

        // Sun & Moon Card
        JPanel sunMoonCard = new ModernCard(430, 580, 200, 160);
        addCardTitle(sunMoonCard, "SUN & MOON");

        JLabel sunIcon = new JLabel("🌅", SwingConstants.CENTER);
        sunIcon.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        sunIcon.setBounds(20, 40, 40, 30);

        sunriseLabel = new JLabel("<html>SUNRISE<br/>--:-- AM</html>");
        sunriseLabel.setForeground(UITheme.PURE_WHITE);
        sunriseLabel.setFont(UITheme.FONT_SMALL_PLAIN);
        sunriseLabel.setBounds(70, 40, 120, 35);

        JLabel moonIcon = new JLabel("🌇", SwingConstants.CENTER);
        moonIcon.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        moonIcon.setBounds(20, 90, 40, 30);

        sunsetLabel = new JLabel("<html>SUNSET<br/>--:-- PM</html>");
        sunsetLabel.setForeground(UITheme.PURE_WHITE);
        sunsetLabel.setFont(UITheme.FONT_SMALL_PLAIN);
        sunsetLabel.setBounds(70, 90, 120, 35);

        sunMoonCard.add(sunIcon);
        sunMoonCard.add(sunriseLabel);
        sunMoonCard.add(moonIcon);
        sunMoonCard.add(sunsetLabel);
        parent.add(sunMoonCard);
    }

    private void addCardTitle(JPanel card, String title) {
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(UITheme.FONT_SMALL_BOLD);
        titleLabel.setForeground(UITheme.LIGHT_GRAY);
        titleLabel.setBounds(15, 8, 200, 15);
        card.add(titleLabel);
    }

    private JLabel createConditionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(UITheme.PURE_WHITE);
        label.setFont(UITheme.FONT_SMALL_PLAIN);
        return label;
    }

    public void updateWeatherData(WeatherData data) {
        updateWeatherData(data, "metric");
    }

    public void updateWeatherData(WeatherData data, String unit) {
        String tempSymbol = unit.equals("imperial") ? "°F" : "°C";
        String speedUnit = unit.equals("imperial") ? "mph" : "km/h";
        double windSpeedVal = unit.equals("imperial") ? data.getWindSpeed() : data.getWindSpeed() * 3.6;

        feelsLikeLabel.setText("Feels Like: " + Math.round(data.getFeelsLike()) + tempSymbol);
        windLabel.setText("Wind Speed: " + Math.round(windSpeedVal) + " " + speedUnit);
        humidityLabel.setText("Humidity: " + data.getHumidity() + "%");

        if (data.getVisibility() > 0) {
            visibilityLabel.setText(String.format("Visibility: %.1f km", data.getVisibility() / 1000.0));
        } else {
            visibilityLabel.setText("Visibility: N/A");
        }

        double dewPoint = WeatherMathUtil.calculateDewPoint(data.getTemperature(), data.getHumidity());
        dewPointLabel.setText(String.format("%.1f%s", dewPoint, tempSymbol));
        pressureLabel.setText(String.format("%.0f hPa", data.getPressure()));

        String sunriseStr = DateTimeUtil.formatTime12Hour(data.getSunriseTime());
        String sunsetStr = DateTimeUtil.formatTime12Hour(data.getSunsetTime());

        sunriseLabel.setText("<html><center><span style='color: white; font-weight: bold;'>SUNRISE</span><br/>" +
                "<span style='color: #FFDD44;'>" + sunriseStr + "</span></center></html>");
        sunsetLabel.setText("<html><center><span style='color: white; font-weight: bold;'>SUNSET</span><br/>" +
                "<span style='color: #FF8844;'>" + sunsetStr + "</span></center></html>");
    }

    public void updateUVIndex(double uvIndex) {
        uvIndexLabel.setText(String.format("UV Index: %.1f", uvIndex));
    }
}
