package weatherdashboard.config;

import java.awt.Color;
import java.awt.Font;

/**
 * Design system tokens defining modern dark mode colors, accent gradients,
 * typography rules, and border styling constants.
 */
public final class UITheme {

    private UITheme() {
        // Restrict instantiation
    }

    // Color Palette
    public static final Color DARK_BLACK = new Color(15, 15, 15);
    public static final Color LIGHT_BLACK = new Color(25, 25, 25);
    public static final Color CARD_BACKGROUND = new Color(35, 35, 35);
    public static final Color CARD_BG_HOVER = new Color(50, 50, 50);
    public static final Color BORDER_COLOR = new Color(60, 60, 60);

    public static final Color ACCENT_ORANGE = new Color(255, 165, 0);
    public static final Color SOFT_ORANGE = new Color(255, 140, 0);

    public static final Color PURE_WHITE = Color.WHITE;
    public static final Color LIGHT_GRAY = new Color(180, 180, 180);
    public static final Color MEDIUM_GRAY = new Color(120, 120, 120);

    // Typography
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 32);
    public static final Font FONT_CITY = new Font("Segoe UI", Font.BOLD, 28);
    public static final Font FONT_TEMP_LARGE = new Font("Segoe UI", Font.BOLD, 72);
    public static final Font FONT_SUBHEADER = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_LABEL_BOLD = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_LABEL_PLAIN = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_SMALL_BOLD = new Font("Segoe UI", Font.BOLD, 11);
    public static final Font FONT_SMALL_PLAIN = new Font("Segoe UI", Font.PLAIN, 12);
}
