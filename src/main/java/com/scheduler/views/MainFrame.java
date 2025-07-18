package com.scheduler.views;

import com.scheduler.models.*;
import com.scheduler.utils.*;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class MainFrame extends JFrame {
    private TaskManager taskManager;
    private JTabbedPane tabbedPane;
    private TaskPanel taskPanel;
    private CalendarPanel calendarPanel;
    private SchedulePanel schedulePanel;
    private CompletedTasksPanel completedTasksPanel;

    public MainFrame() {
        initModels();
        initUI();
        initListeners();
        Theme.updateUIManager();
    }

    private void initModels() {
        taskManager = new TaskManager();
        try {
            StorageManager.loadData(taskManager);

            // Add default categories if none exist
            if (taskManager.getCategories().isEmpty()) {
                taskManager.getCategories().add(new Category("Work", Color.RED));
                taskManager.getCategories().add(new Category("Personal", Color.BLUE));
                taskManager.getCategories().add(new Category("Family", Color.GREEN));
                taskManager.getCategories().add(new Category("Education", Color.ORANGE));
                taskManager.getCategories().add(new Category("Other", Color.GRAY));

                // Save the default categories
                StorageManager.saveData(taskManager);
            }
        } catch (IOException e) {
            showError("Error loading data", e);
        }
    }

    private void initUI() {
        setTitle("Smart Scheduler Pro");
        setSize(1280, 800);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);


        // Initialize panels
        taskPanel = new TaskPanel(taskManager, this::refreshAllViews);
        calendarPanel = new CalendarPanel(taskManager);
        schedulePanel = new SchedulePanel(taskManager);
        completedTasksPanel = new CompletedTasksPanel(taskManager);

        // Create tabbed pane with modern styling
        tabbedPane = new JTabbedPane();
        tabbedPane.putClientProperty("JTabbedPane.tabType", "card");
        tabbedPane.putClientProperty("JTabbedPane.tabInsets", new Insets(10, 15, 10, 15));

        // Add tabs (using placeholder icons)
        tabbedPane.addTab("Tasks", new ImageIcon(), taskPanel);
        tabbedPane.addTab("Calendar", new ImageIcon(), calendarPanel);
        tabbedPane.addTab("Schedule", new ImageIcon(), schedulePanel);
        tabbedPane.addTab("Completed", new ImageIcon(), completedTasksPanel);

        // Set colorful tabbed pane
        tabbedPane.setBackground(new Color(230, 240, 255));
        tabbedPane.setForeground(new Color(70, 130, 180));

        // Main panel with padding
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        add(mainPanel);
    }

    public void refreshAllViews() {
        SwingUtilities.invokeLater(() -> {
            taskPanel.revalidate();
            taskPanel.repaint();
            calendarPanel.refresh();
            schedulePanel.refresh();
            completedTasksPanel.refresh();
        });
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // File menu
        JMenu fileMenu = new JMenu("File");
        JMenuItem saveItem = new JMenuItem("Save");
        saveItem.addActionListener(e -> {
            try {
                StorageManager.saveData(taskManager);
            } catch (IOException ex) {
                showError("Error saving data", ex);
            }
        });
        fileMenu.add(saveItem);

        // Theme menu
        JMenu themeMenu = new JMenu("Theme");
        menuBar.add(fileMenu);
        menuBar.add(themeMenu);

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