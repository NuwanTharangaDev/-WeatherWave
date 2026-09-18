package weatherdashboard.ui.components;

import weatherdashboard.config.UITheme;
import weatherdashboard.ui.ModernCard;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.Cursor;

/**
 * UI component providing city search text field and action button.
 */
public class SearchPanel {

    private final JTextField searchField;
    private final JButton searchButton;
    private final JButton locationButton;

    public interface SearchListener {
        void onSearchRequested(String city);
        void onLocationRequested();
    }

    public SearchPanel(JPanel parent, String initialCity, SearchListener listener) {
        JPanel searchContainer = new ModernCard(520, 25, 380, 50);

        searchField = new JTextField(initialCity);
        searchField.setBounds(15, 10, 250, 30);
        searchField.setBackground(UITheme.LIGHT_BLACK);
        searchField.setForeground(UITheme.PURE_WHITE);
        searchField.setFont(UITheme.FONT_LABEL_PLAIN);
        searchField.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        searchField.setCaretColor(UITheme.ACCENT_ORANGE);

        locationButton = new JButton("📍");
        locationButton.setBounds(275, 10, 40, 30);
        locationButton.setBackground(UITheme.CARD_BG_HOVER);
        locationButton.setForeground(UITheme.PURE_WHITE);
        locationButton.setFont(UITheme.FONT_LABEL_BOLD);
        locationButton.setBorder(null);
        locationButton.setFocusPainted(false);
        locationButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        locationButton.setToolTipText("Use Current Location / Default City");

        searchButton = new JButton("🔍");
        searchButton.setBounds(325, 10, 40, 30);
        searchButton.setBackground(UITheme.ACCENT_ORANGE);
        searchButton.setForeground(UITheme.PURE_WHITE);
        searchButton.setFont(UITheme.FONT_LABEL_BOLD);
        searchButton.setBorder(null);
        searchButton.setFocusPainted(false);
        searchButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        Runnable performSearchAction = () -> {
            String city = searchField.getText().trim();
            if (!city.isEmpty() && listener != null) {
                listener.onSearchRequested(city);
            }
        };

        searchField.addActionListener(e -> performSearchAction.run());
        searchButton.addActionListener(e -> performSearchAction.run());
        locationButton.addActionListener(e -> {
            if (listener != null) listener.onLocationRequested();
        });

        searchButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                searchButton.setBackground(UITheme.SOFT_ORANGE);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                searchButton.setBackground(UITheme.ACCENT_ORANGE);
            }
        });

        searchContainer.add(searchField);
        searchContainer.add(locationButton);
        searchContainer.add(searchButton);
        parent.add(searchContainer);
    }

    public void setCityText(String city) {
        searchField.setText(city);
    }

    public void setEnabled(boolean enabled) {
        searchButton.setEnabled(enabled);
        locationButton.setEnabled(enabled);
        searchField.setEnabled(enabled);
    }
}
