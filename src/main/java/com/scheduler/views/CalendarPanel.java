package com.scheduler.views;

import com.scheduler.models.*;
import javax.swing.*;
import java.awt.*;
import java.time.*;
import java.time.format.*;
import java.time.temporal.*;
import java.util.*;
import java.util.List;
import java.util.stream.*;

public class CalendarPanel extends JPanel {
    private final TaskManager taskManager;
    private YearMonth currentYearMonth;
    private JLabel monthLabel;
    private JPanel calendarGrid;

    public CalendarPanel(TaskManager taskManager) {
        this.taskManager = taskManager;
        this.currentYearMonth = YearMonth.now();
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        initComponents();
    }

    private void initComponents() {
        // Navigation panel
        JPanel navPanel = new JPanel(new BorderLayout());
        navPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        calendarGrid = new JPanel(new GridLayout(0, 7, 5, 5)); // Initialize here
        add(new JScrollPane(calendarGrid), BorderLayout.CENTER);

        JButton prevBtn = new JButton("←");
        JButton nextBtn = new JButton("→");
        monthLabel = new JLabel("", SwingConstants.CENTER);
        monthLabel.setFont(monthLabel.getFont().deriveFont(Font.BOLD, 16));

        JButton todayBtn = new JButton("Refresh");

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        leftPanel.add(prevBtn);
        leftPanel.add(todayBtn);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        rightPanel.add(nextBtn);

        navPanel.add(leftPanel, BorderLayout.WEST);
        navPanel.add(monthLabel, BorderLayout.CENTER);
        navPanel.add(rightPanel, BorderLayout.EAST);

        // Calendar grid
        JPanel calendarGrid = new JPanel(new GridLayout(0, 7, 5, 5));
        calendarGrid.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        // Add components
        add(navPanel, BorderLayout.NORTH);
        add(new JScrollPane(calendarGrid), BorderLayout.CENTER);

        // Event listeners
        prevBtn.addActionListener(e -> {
            currentYearMonth = currentYearMonth.minusMonths(1);
            refreshCalendar(calendarGrid);
        });

        nextBtn.addActionListener(e -> {
            currentYearMonth = currentYearMonth.plusMonths(1);
            refreshCalendar(calendarGrid);
        });

        todayBtn.addActionListener(e -> {
            currentYearMonth = YearMonth.now();
            refreshCalendar(calendarGrid);
        });

        refreshCalendar(calendarGrid);
    }

    public void refresh() {
        refreshCalendar(calendarGrid);
    }

    private void refreshCalendar(JPanel calendarGrid) {
        calendarGrid.removeAll();
        Theme.applyModernPanelStyle(calendarGrid);

        monthLabel.setText(currentYearMonth.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault())
                + " " + currentYearMonth.getYear());
        monthLabel.setForeground(Theme.isDarkMode() ? Theme.TEXT_PRIMARY_DARK : Theme.TEXT_PRIMARY_LIGHT);

        // Day headers
        String[] dayNames = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (String day : dayNames) {
            JLabel label = new JLabel(day, SwingConstants.CENTER);
            label.setFont(label.getFont().deriveFont(Font.BOLD));
            label.setForeground(Theme.isDarkMode() ? Theme.TEXT_PRIMARY_DARK : Theme.TEXT_PRIMARY_LIGHT);
            calendarGrid.add(label);
        }

        LocalDate firstOfMonth = currentYearMonth.atDay(1);
        int daysInMonth = currentYearMonth.lengthOfMonth();

        // Empty cells before first day
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue() % 7;
        for (int i = 0; i < dayOfWeek; i++) {
            calendarGrid.add(new JPanel());
        }

        // Days of month
        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = currentYearMonth.atDay(day);
            List<Task> tasks = taskManager.getTasksForDate(date);

            JPanel dayPanel = new JPanel(new BorderLayout());
            Theme.applyCardStyle(dayPanel);

            // Day number
            JLabel dayLabel = new JLabel(String.valueOf(day), SwingConstants.CENTER);
            dayLabel.setFont(dayLabel.getFont().deriveFont(Font.BOLD));
            dayLabel.setForeground(Theme.isDarkMode() ? Theme.TEXT_PRIMARY_DARK : Theme.TEXT_PRIMARY_LIGHT);
            dayPanel.add(dayLabel, BorderLayout.NORTH);

            // Tasks list
            JTextArea tasksArea = new JTextArea();
            tasksArea.setEditable(false);
            tasksArea.setBackground(Theme.isDarkMode() ? Theme.CARD_BACKGROUND_DARK : Theme.CARD_BACKGROUND_LIGHT);
            tasksArea.setForeground(Theme.isDarkMode() ? Theme.TEXT_PRIMARY_DARK : Theme.TEXT_PRIMARY_LIGHT);
            tasksArea.setFont(tasksArea.getFont().deriveFont(12f));

            if (!tasks.isEmpty()) {
                for (Task task : tasks) {
                    tasksArea.append("• " + task.getTitle() + "\n");
                }
            } else {
                tasksArea.setText("No tasks");
                tasksArea.setForeground(Theme.isDarkMode() ? Theme.TEXT_SECONDARY_DARK : Theme.TEXT_SECONDARY_LIGHT);
            }

            dayPanel.add(new JScrollPane(tasksArea), BorderLayout.CENTER);
            calendarGrid.add(dayPanel);
        }

        calendarGrid.revalidate();
        calendarGrid.repaint();
    }
}