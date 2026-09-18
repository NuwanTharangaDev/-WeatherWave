package weatherdashboard.ui;

import weatherdashboard.config.UITheme;

import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * Custom Swing JPanel subclass providing glassmorphism card styling with anti-aliased borders and gradient fills.
 */
public class ModernCard extends JPanel {

    private final int cornerRadius;

    public ModernCard(int x, int y, int width, int height) {
        this(x, y, width, height, 15);
    }

    public ModernCard(int x, int y, int width, int height, int cornerRadius) {
        this.cornerRadius = cornerRadius;
        setBounds(x, y, width, height);
        setLayout(null);
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Card background with subtle gradient
        GradientPaint gradient = new GradientPaint(0, 0, UITheme.CARD_BACKGROUND, 0, getHeight(), UITheme.LIGHT_BLACK);
        g2d.setPaint(gradient);
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);

        // Subtle border
        g2d.setColor(UITheme.BORDER_COLOR);
        g2d.setStroke(new BasicStroke(1));
        g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);
    }
}
