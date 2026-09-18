package weatherdashboard.ui.components;

import weatherdashboard.config.UITheme;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * UI Component displaying application branding title with gradient text and live status indicator.
 */
public class HeaderPanel {

    private final JLabel statusLabel;
    private final JButton celsiusBtn;
    private final JButton fahrenheitBtn;
    private String activeUnit = "metric";

    public interface UnitToggleListener {
        void onUnitChanged(String unit);
    }

    public HeaderPanel(JPanel parent, UnitToggleListener unitListener) {
        JLabel titleLabel = new JLabel("WeatherWave") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gradient = new GradientPaint(0, 0, UITheme.ACCENT_ORANGE, getWidth(), 0, UITheme.SOFT_ORANGE);
                g2d.setPaint(gradient);
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int y = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2d.drawString(getText(), 0, y);
            }
        };
        titleLabel.setFont(UITheme.FONT_TITLE);
        titleLabel.setBounds(40, 20, 300, 40);
        parent.add(titleLabel);

        statusLabel = new JLabel("● Live Weather Data");
        statusLabel.setFont(UITheme.FONT_LABEL_PLAIN);
        statusLabel.setForeground(UITheme.ACCENT_ORANGE);
        statusLabel.setBounds(40, 65, 500, 20);
        parent.add(statusLabel);

        // Unit Toggle Buttons Container Card
        JPanel unitToggleCard = new weatherdashboard.ui.ModernCard(920, 25, 110, 50);
        unitToggleCard.setLayout(null);

        celsiusBtn = createUnitButton("°C", true);
        celsiusBtn.setBounds(8, 8, 45, 34);

        fahrenheitBtn = createUnitButton("°F", false);
        fahrenheitBtn.setBounds(57, 8, 45, 34);

        celsiusBtn.addActionListener(e -> {
            if (!activeUnit.equals("metric")) {
                activeUnit = "metric";
                updateButtonStates();
                if (unitListener != null) unitListener.onUnitChanged("metric");
            }
        });

        fahrenheitBtn.addActionListener(e -> {
            if (!activeUnit.equals("imperial")) {
                activeUnit = "imperial";
                updateButtonStates();
                if (unitListener != null) unitListener.onUnitChanged("imperial");
            }
        });

        unitToggleCard.add(celsiusBtn);
        unitToggleCard.add(fahrenheitBtn);
        parent.add(unitToggleCard);
    }

    private JButton createUnitButton(String text, boolean active) {
        JButton btn = new JButton(text);
        btn.setFont(UITheme.FONT_LABEL_BOLD);
        btn.setFocusPainted(false);
        btn.setBorder(null);
        btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        applyButtonStyle(btn, active);
        return btn;
    }

    private void applyButtonStyle(JButton btn, boolean active) {
        if (active) {
            btn.setBackground(UITheme.ACCENT_ORANGE);
            btn.setForeground(UITheme.PURE_WHITE);
        } else {
            btn.setBackground(UITheme.LIGHT_BLACK);
            btn.setForeground(UITheme.LIGHT_GRAY);
        }
    }

    private void updateButtonStates() {
        applyButtonStyle(celsiusBtn, activeUnit.equals("metric"));
        applyButtonStyle(fahrenheitBtn, activeUnit.equals("imperial"));
    }

    public void setStatus(String message, boolean isError) {
        statusLabel.setText("● " + message);
        statusLabel.setForeground(isError ? java.awt.Color.RED : UITheme.ACCENT_ORANGE);
    }
}
