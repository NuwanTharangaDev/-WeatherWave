package weatherdashboard.service;

import weatherdashboard.config.ApiConfig;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.net.URI;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CompletableFuture;

/**
 * Service class handling asynchronous download, smooth scaling, and memory caching of weather icon images.
 */
public class IconLoaderService {

    private final Map<String, ImageIcon> iconCache = new ConcurrentHashMap<>();

    public void loadWeatherIcon(String iconCode, JLabel targetLabel, int size) {
        if (iconCode == null || iconCode.isEmpty() || targetLabel == null) return;

        String cacheKey = iconCode + "_" + size;
        if (iconCache.containsKey(cacheKey)) {
            targetLabel.setIcon(iconCache.get(cacheKey));
            return;
        }

        CompletableFuture.runAsync(() -> {
            try {
                String iconUrlStr = ApiConfig.ICON_BASE_URL + iconCode + "@2x.png";
                URL url = URI.create(iconUrlStr).toURL();
                BufferedImage image = ImageIO.read(url);

                if (image != null) {
                    Image scaledImage = image.getScaledInstance(size, size, Image.SCALE_SMOOTH);
                    ImageIcon icon = new ImageIcon(scaledImage);
                    iconCache.put(cacheKey, icon);

                    SwingUtilities.invokeLater(() -> targetLabel.setIcon(icon));
                }
            } catch (Exception e) {
                System.err.println("IconLoaderService error loading " + iconCode + ": " + e.getMessage());
            }
        });
    }
}
