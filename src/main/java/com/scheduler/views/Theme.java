package com.scheduler.views;

import javax.swing.*;
import java.awt.*;

public class Theme {
    // Light Theme Colors
    public static final Color PRIMARY_LIGHT = new Color(70, 130, 180);
    public static final Color SECONDARY_LIGHT = new Color(255, 165, 0);
    public static final Color BACKGROUND_LIGHT = new Color(245, 245, 245);
    public static final Color CARD_BACKGROUND_LIGHT = Color.WHITE;
    public static final Color TEXT_PRIMARY_LIGHT = new Color(60, 60, 60);
    public static final Color TEXT_SECONDARY_LIGHT = new Color(120, 120, 120);
    public static final Color BORDER_LIGHT = new Color(220, 220, 220);

    // Dark Theme Colors
    public static final Color PRIMARY_DARK = new Color(100, 150, 200);
    public static final Color SECONDARY_DARK = new Color(255, 185, 50);
    public static final Color BACKGROUND_DARK = new Color(45, 45, 45);
    public static final Color CARD_BACKGROUND_DARK = new Color(60, 60, 60);
    public static final Color TEXT_PRIMARY_DARK = new Color(220, 220, 220);
    public static final Color TEXT_SECONDARY_DARK = new Color(180, 180, 180);
    public static final Color BORDER_DARK = new Color(80, 80, 80);

    // Fonts
    public static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font SUBHEADER_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font BODY_FONT = new Font("Segoe UI", Font.PLAIN, 12);

    private static boolean darkMode = false;

    public static boolean isDarkMode() {
        return darkMode;
    }

    public static void toggleDarkMode() {
        darkMode = !darkMode;
        updateUIManager();
    }

    public static void updateUIManager() {
        if (darkMode) {
            UIManager.put("Panel.background", BACKGROUND_DARK);
            UIManager.put("Table.background", CARD_BACKGROUND_DARK);
            UIManager.put("Table.foreground", TEXT_PRIMARY_DARK);
            UIManager.put("Table.gridColor", BORDER_DARK);
            UIManager.put("TableHeader.background", PRIMARY_DARK);
            UIManager.put("TableHeader.foreground", Color.WHITE);
            UIManager.put("TextField.background", CARD_BACKGROUND_DARK);
            UIManager.put("TextField.foreground", TEXT_PRIMARY_DARK);
            UIManager.put("TextArea.background", CARD_BACKGROUND_DARK);
            UIManager.put("TextArea.foreground", TEXT_PRIMARY_DARK);
            UIManager.put("ComboBox.background", CARD_BACKGROUND_DARK);
            UIManager.put("ComboBox.foreground", TEXT_PRIMARY_DARK);
        } else {
            UIManager.put("Panel.background", BACKGROUND_LIGHT);
            UIManager.put("Table.background", CARD_BACKGROUND_LIGHT);
            UIManager.put("Table.foreground", TEXT_PRIMARY_LIGHT);
            UIManager.put("Table.gridColor", BORDER_LIGHT);
            UIManager.put("TableHeader.background", PRIMARY_LIGHT);
            UIManager.put("TableHeader.foreground", Color.WHITE);
            UIManager.put("TextField.background", CARD_BACKGROUND_LIGHT);
            UIManager.put("TextField.foreground", TEXT_PRIMARY_LIGHT);
            UIManager.put("TextArea.background", CARD_BACKGROUND_LIGHT);
            UIManager.put("TextArea.foreground", TEXT_PRIMARY_LIGHT);
            UIManager.put("ComboBox.background", CARD_BACKGROUND_LIGHT);
            UIManager.put("ComboBox.foreground", TEXT_PRIMARY_LIGHT);
        }
    }

    public static void applyButtonStyle(JButton button) {
        button.setBackground(isDarkMode() ? PRIMARY_DARK : PRIMARY_LIGHT);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setFont(BODY_FONT);
    }

    public static void applyModernPanelStyle(JPanel panel) {
        panel.setBackground(isDarkMode() ? BACKGROUND_DARK : BACKGROUND_LIGHT);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
    }

    public static void applyCardStyle(JPanel card) {
        card.setBackground(isDarkMode() ? CARD_BACKGROUND_DARK : CARD_BACKGROUND_LIGHT);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(isDarkMode() ? BORDER_DARK : BORDER_LIGHT),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
    }

    public static void applyTableStyle(JTable table) {
        table.setBackground(isDarkMode() ? CARD_BACKGROUND_DARK : CARD_BACKGROUND_LIGHT);
        table.setForeground(isDarkMode() ? TEXT_PRIMARY_DARK : TEXT_PRIMARY_LIGHT);
        table.setGridColor(isDarkMode() ? BORDER_DARK : BORDER_LIGHT);
        table.setSelectionBackground(isDarkMode() ? PRIMARY_DARK : PRIMARY_LIGHT);
        table.setSelectionForeground(Color.WHITE);
        table.setFont(BODY_FONT);
        table.setRowHeight(30);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFillsViewportHeight(true);
    }
}