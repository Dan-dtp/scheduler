package com.scheduler.views;

import com.scheduler.models.*;
import com.scheduler.utils.*;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class MainFrame extends JFrame {
    private TaskManager taskManager;
    private NotificationManager notificationManager;

    public MainFrame() {
        initModels();
        initUI();
        initListeners();
    }

    private void initModels() {
        taskManager = new TaskManager();
        try {
            StorageManager.loadData(taskManager);
        } catch (IOException e) {
            showError("Error loading data", e);
        }
        notificationManager = new NotificationManager(taskManager);
    }

    private void initUI() {
        setTitle("Smart Scheduler Pro");
        setSize(1200, 800);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Tasks", new TaskPanel(taskManager));
        tabbedPane.addTab("Calendar", new CalendarPanel(taskManager));
        tabbedPane.addTab("Schedule", new SchedulePanel(taskManager));
        tabbedPane.addTab("Statistics", new StatsPanel(taskManager));

        setJMenuBar(createMenuBar());
        add(tabbedPane);
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // File Menu
        JMenu fileMenu = new JMenu("File");
        JMenuItem exportItem = new JMenuItem("Export to CSV");
        JMenuItem importItem = new JMenuItem("Import from CSV");
        JMenuItem exitItem = new JMenuItem("Exit");

        fileMenu.add(importItem);
        fileMenu.add(exportItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        // View Menu
        JMenu viewMenu = new JMenu("View");
        JCheckBoxMenuItem darkModeItem = new JCheckBoxMenuItem("Dark Mode");
        viewMenu.add(darkModeItem);

        menuBar.add(fileMenu);
        menuBar.add(viewMenu);

        return menuBar;
    }

    private void initListeners() {
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                try {
                    StorageManager.saveData(taskManager);
                } catch (IOException e) {
                    showError("Error saving data", e);
                }
            }
        });
    }

    private void showError(String message, Exception e) {
        JOptionPane.showMessageDialog(this,
                message + ": " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
    }
}