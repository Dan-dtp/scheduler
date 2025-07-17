package com.scheduler;

import com.formdev.flatlaf.FlatLightLaf;
import com.scheduler.views.MainFrame;
import javax.swing.*;

public class AppMain {
    public static void main(String[] args) {
        // Set modern look and feel
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ex) {
            System.err.println("Failed to initialize LaF");
        }

        // Create and show main frame
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}