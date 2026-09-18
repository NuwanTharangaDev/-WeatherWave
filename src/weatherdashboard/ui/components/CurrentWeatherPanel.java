package weatherdashboard.ui.components;

import weatherdashboard.config.UITheme;
import weatherdashboard.model.WeatherData;
import weatherdashboard.service.IconLoaderService;
import weatherdashboard.ui.ModernCard;
import weatherdashboard.util.DateTimeUtil;
import weatherdashboard.util.WeatherMathUtil;

import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Date;

/**
 * UI Component displaying location name, date/time, gradient temperature display, weather description, and main weather icon.
 */
public class CurrentWeatherPanel {

    private final JLabel cityLabel;
    private final JLabel dateTimeLabel;
    private final JLabel temperatureLabel;
    private final JLabel weatherDescLabel;
    private final JLabel weatherIconLabel;
    private final IconLoaderService iconService;

    public CurrentWeatherPanel(JPanel parent, String initialCity, IconLoaderService iconService) {
        this.iconService = iconService;

        JPanel mainCard = new ModernCard(40, 110, 480, 280);

        cityLabel = new JLabel(initialCity);
        cityLabel.setFont(UITheme.FONT_CITY);
        cityLabel.setForeground(UITheme.PURE_WHITE);
        cityLabel.setBounds(30, 20, 420, 35);

        dateTimeLabel = new JLabel(DateTimeUtil.formatHeaderDateTime(new Date()));
        dateTimeLabel.setFont(UITheme.FONT_LABEL_PLAIN);
        dateTimeLabel.setForeground(UITheme.LIGHT_GRAY);
        dateTimeLabel.setBounds(30, 55, 300, 20);

        temperatureLabel = new JLabel("--°") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gradient = new GradientPaint(0, 0, UITheme.ACCENT_ORANGE, getWidth(), getHeight(), UITheme.SOFT_ORANGE);
                g2d.setPaint(gradient);
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2d.drawString(getText(), 0, y);
            }
        };
        temperatureLabel.setFont(UITheme.FONT_TEMP_LARGE);
        temperatureLabel.setBounds(30, 90, 250, 80);

        weatherDescLabel = new JLabel("Loading...");
        weatherDescLabel.setFont(UITheme.FONT_LABEL_BOLD);
        weatherDescLabel.setForeground(UITheme.LIGHT_GRAY);
        weatherDescLabel.setBounds(30, 175, 300, 25);

        weatherIconLabel = new JLabel();
        weatherIconLabel.setBounds(350, 80, 100, 100);

        mainCard.add(cityLabel);
        mainCard.add(dateTimeLabel);
        mainCard.add(temperatureLabel);
        mainCard.add(weatherDescLabel);
        mainCard.add(weatherIconLabel);
        parent.add(mainCard);
    }

    public void updateData(WeatherData data) {
        cityLabel.setText(data.getCityName() + ", " + data.getCountry());
        temperatureLabel.setText(Math.round(data.getTemperature()) + "°");
        weatherDescLabel.setText(WeatherMathUtil.capitalizeWords(data.getDescription()));
        dateTimeLabel.setText(DateTimeUtil.formatHeaderDateTime(new Date()));

        iconService.loadWeatherIcon(data.getIconCode(), weatherIconLabel, 120);
    }

    public void updateDateTime() {
        dateTimeLabel.setText(DateTimeUtil.formatHeaderDateTime(new Date()));
    }
}
