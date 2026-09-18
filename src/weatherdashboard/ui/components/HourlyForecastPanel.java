package weatherdashboard.ui.components;

import weatherdashboard.config.UITheme;
import weatherdashboard.model.HourlyForecast;
import weatherdashboard.service.IconLoaderService;
import weatherdashboard.ui.ModernCard;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.util.List;

/**
 * UI component rendering today's 3-hour forecast timeline cards.
 */
public class HourlyForecastPanel {

    private final JLabel[] hourlyTimes = new JLabel[6];
    private final JLabel[] hourlyIcons = new JLabel[6];
    private final JLabel[] hourlyTemps = new JLabel[6];
    private final IconLoaderService iconService;

    public HourlyForecastPanel(JPanel parent, IconLoaderService iconService) {
        this.iconService = iconService;

        JLabel hourlyTitle = new JLabel("TODAY'S FORECAST");
        hourlyTitle.setFont(UITheme.FONT_SUBHEADER);
        hourlyTitle.setForeground(UITheme.PURE_WHITE);
        hourlyTitle.setBounds(40, 410, 200, 20);
        parent.add(hourlyTitle);

        for (int i = 0; i < 6; i++) {
            JPanel hourlyCard = new ModernCard(40 + i * 85, 440, 75, 120);

            hourlyTimes[i] = new JLabel("--:--", SwingConstants.CENTER);
            hourlyTimes[i].setForeground(UITheme.LIGHT_GRAY);
            hourlyTimes[i].setFont(UITheme.FONT_SMALL_PLAIN);
            hourlyTimes[i].setBounds(0, 15, 75, 15);

            hourlyIcons[i] = new JLabel("", SwingConstants.CENTER);
            hourlyIcons[i].setBounds(20, 35, 35, 35);

            hourlyTemps[i] = new JLabel("--°", SwingConstants.CENTER);
            hourlyTemps[i].setForeground(UITheme.ACCENT_ORANGE);
            hourlyTemps[i].setFont(UITheme.FONT_SUBHEADER);
            hourlyTemps[i].setBounds(0, 80, 75, 20);

            hourlyCard.add(hourlyTimes[i]);
            hourlyCard.add(hourlyIcons[i]);
            hourlyCard.add(hourlyTemps[i]);
            parent.add(hourlyCard);
        }
    }

    public void updateData(List<HourlyForecast> forecastList) {
        for (int i = 0; i < Math.min(6, forecastList.size()); i++) {
            HourlyForecast forecast = forecastList.get(i);
            hourlyTimes[i].setText(forecast.getTimeLabel());
            hourlyTemps[i].setText(Math.round(forecast.getTemperature()) + "°");
            iconService.loadWeatherIcon(forecast.getIconCode(), hourlyIcons[i], 32);
        }
    }
}
