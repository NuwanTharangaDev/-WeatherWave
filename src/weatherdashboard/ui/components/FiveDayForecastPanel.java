package weatherdashboard.ui.components;

import weatherdashboard.config.UITheme;
import weatherdashboard.model.DailyForecast;
import weatherdashboard.service.IconLoaderService;
import weatherdashboard.ui.ModernCard;
import weatherdashboard.util.WeatherMathUtil;

import javax.swing.JLabel;
import javax.swing.JPanel;
import java.util.List;

/**
 * UI Component displaying 5-day weather forecast cards with min/max temperatures and icons.
 */
public class FiveDayForecastPanel {

    private final JLabel[] forecastDays = new JLabel[5];
    private final JLabel[] forecastIcons = new JLabel[5];
    private final JLabel[] forecastDescs = new JLabel[5];
    private final JLabel[] forecastTemps = new JLabel[5];
    private final IconLoaderService iconService;

    public FiveDayForecastPanel(JPanel parent, IconLoaderService iconService) {
        this.iconService = iconService;

        JLabel forecastTitle = new JLabel("5-DAY FORECAST");
        forecastTitle.setFont(UITheme.FONT_SUBHEADER);
        forecastTitle.setForeground(UITheme.PURE_WHITE);
        forecastTitle.setBounds(750, 110, 200, 20);
        parent.add(forecastTitle);

        for (int i = 0; i < 5; i++) {
            JPanel forecastCard = new ModernCard(750, 140 + i * 85, 400, 70);

            forecastDays[i] = new JLabel("---");
            forecastDays[i].setForeground(UITheme.PURE_WHITE);
            forecastDays[i].setFont(UITheme.FONT_LABEL_BOLD);
            forecastDays[i].setBounds(20, 10, 60, 25);

            forecastIcons[i] = new JLabel();
            forecastIcons[i].setBounds(20, 35, 40, 25);

            forecastDescs[i] = new JLabel("Loading...");
            forecastDescs[i].setForeground(UITheme.LIGHT_GRAY);
            forecastDescs[i].setFont(UITheme.FONT_SMALL_PLAIN);
            forecastDescs[i].setBounds(80, 25, 200, 20);

            forecastTemps[i] = new JLabel("--°/--°");
            forecastTemps[i].setForeground(UITheme.ACCENT_ORANGE);
            forecastTemps[i].setFont(UITheme.FONT_LABEL_BOLD);
            forecastTemps[i].setBounds(300, 25, 80, 20);

            forecastCard.add(forecastDays[i]);
            forecastCard.add(forecastIcons[i]);
            forecastCard.add(forecastDescs[i]);
            forecastCard.add(forecastTemps[i]);
            parent.add(forecastCard);
        }
    }

    public void updateData(List<DailyForecast> dailyList) {
        for (int i = 0; i < Math.min(5, dailyList.size()); i++) {
            DailyForecast forecast = dailyList.get(i);
            forecastDays[i].setText(forecast.getDayName());
            forecastTemps[i].setText(Math.round(forecast.getMaxTemp()) + "°/" + Math.round(forecast.getMinTemp()) + "°");
            forecastDescs[i].setText(WeatherMathUtil.capitalizeWords(forecast.getDescription()));
            iconService.loadWeatherIcon(forecast.getIconCode(), forecastIcons[i], 32);
        }
    }
}
