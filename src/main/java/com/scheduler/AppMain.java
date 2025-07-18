package com.scheduler;

import com.formdev.flatlaf.FlatLightLaf;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import com.scheduler.views.MainFrame;
import com.scheduler.views.Theme;
import javax.swing.*;
import java.awt.*;

public class AppMain {
    public static void main(String[] args) {
        // Set modern look and feel
        try {
            FlatMacLightLaf.setup();
            Theme.updateUIManager();

            // Additional UI styling
            UIManager.put("Button.arc", 10);
            UIManager.put("Component.arc", 10);
            UIManager.put("ProgressBar.arc", 5);
            UIManager.put("TextComponent.arc", 5);
            UIManager.put("ScrollBar.width", 12);

            // Set more colorful UI defaults
            UIManager.put("TabbedPane.selectedBackground", Theme.PRIMARY);
            UIManager.put("TabbedPane.selectedForeground", Color.WHITE);
            UIManager.put("TabbedPane.background", Theme.BACKGROUND);
            UIManager.put("TabbedPane.foreground", Theme.TEXT_PRIMARY);
        } catch (Exception ex) {
            System.err.println("Failed to initialize LaF");
        }

        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}