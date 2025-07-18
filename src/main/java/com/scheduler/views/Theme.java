package com.scheduler.views;

import javax.swing.*;
import java.awt.*;

public class Theme {
    // Primary colors
    public static final Color PRIMARY = new Color(70, 130, 180);  // Steel blue
    public static final Color SECONDARY = new Color(255, 150, 100); // Coral
    public static final Color ACCENT = new Color(180, 220, 130); // Green

    // Background colors
    public static final Color BACKGROUND = new Color(245, 250, 255); // Very light blue
    public static final Color CARD_BACKGROUND = Color.WHITE;

    // Text colors
    public static final Color TEXT_PRIMARY = new Color(60, 60, 80);
    public static final Color TEXT_SECONDARY = new Color(120, 120, 140);

    // Other colors
    public static final Color BORDER = new Color(210, 225, 240);
    public static final Color COMPLETED = new Color(200, 230, 200); // Light green
    public static final Color HIGHLIGHT = new Color(255, 200, 100); // Yellow

    public static void applyButtonStyle(JButton button) {
        button.setBackground(PRIMARY);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PRIMARY.darker(), 1),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
    }

    public static void applyCompletedStyle(JComponent comp) {
        comp.setBackground(COMPLETED);
        comp.setBorder(BorderFactory.createLineBorder(COMPLETED.darker()));
    }

    public static void applyModernPanelStyle(JPanel panel) {
        panel.setBackground(BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }

    public static void applyCardStyle(JPanel panel) {
        panel.setBackground(CARD_BACKGROUND);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
    }

    public static void applyTableStyle(JTable table) {
        table.setBackground(CARD_BACKGROUND);
        table.setForeground(TEXT_PRIMARY);
        table.setGridColor(BORDER);
        table.setSelectionBackground(PRIMARY);
        table.setSelectionForeground(Color.WHITE);
        table.setFont(table.getFont().deriveFont(Font.PLAIN, 12));
        table.setRowHeight(30);
        table.setShowGrid(true);
    }

    public static void updateUIManager() {
        UIManager.put("Panel.background", BACKGROUND);
        UIManager.put("Label.foreground", TEXT_PRIMARY);
        UIManager.put("Table.background", CARD_BACKGROUND);
        UIManager.put("Table.foreground", TEXT_PRIMARY);
        UIManager.put("Table.gridColor", BORDER);
        UIManager.put("TableHeader.background", PRIMARY);
        UIManager.put("TableHeader.foreground", Color.WHITE);
    }
}