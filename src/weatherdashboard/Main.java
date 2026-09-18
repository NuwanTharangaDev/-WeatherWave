package weatherdashboard;

import weatherdashboard.ui.WeatherDashboardFrame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {

    public static void main(String[] args) {
        // Enable anti-aliased font rendering for high-DPI desktop displays
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        SwingUtilities.invokeLater(() -> {
            try {
                // Set system cross-platform look and feel
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception e) {
                System.err.println("Note: System LookAndFeel fallback used - " + e.getMessage());
            }

            WeatherDashboardFrame frame = new WeatherDashboardFrame();
            frame.setVisible(true);
        });
    }
}
