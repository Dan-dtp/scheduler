package com.scheduler.views;
import com.scheduler.models.Task;

import com.scheduler.models.Category;
import com.scheduler.models.Priority;
import com.scheduler.models.TaskManager;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.stream.*;


public class StatsPanel extends JPanel {
    private TaskManager taskManager;

    public StatsPanel(TaskManager taskManager) {
        this.taskManager = taskManager;
        setLayout(new BorderLayout());
        initComponents();
    }

    private void initComponents() {
        JTabbedPane tabbedPane = new JTabbedPane();

        // Completion Rate Chart
        JFreeChart completionChart = createCompletionChart();
        tabbedPane.addTab("Completion", new ChartPanel(completionChart));

        // Priority Distribution Chart
        JFreeChart priorityChart = createPriorityChart();
        tabbedPane.addTab("Priority", new ChartPanel(priorityChart));

        // Category Distribution Chart
        JFreeChart categoryChart = createCategoryChart();
        tabbedPane.addTab("Categories", new ChartPanel(categoryChart));

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JFreeChart createCompletionChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        long completed = taskManager.getTasks().stream()
                .filter(Task::isCompleted)
                .count();

        long pending = taskManager.getTasks().size() - completed;

        dataset.addValue(completed, "Tasks", "Completed");
        dataset.addValue(pending, "Tasks", "Pending");

        return ChartFactory.createBarChart(
                "Task Completion", "Status", "Count", dataset,
                PlotOrientation.VERTICAL, true, true, false);
    }

    private JFreeChart createPriorityChart() {
        DefaultPieDataset dataset = new DefaultPieDataset();

        Map<Priority, Long> counts = taskManager.getTasks().stream()
                .collect(Collectors.groupingBy(Task::getPriority, Collectors.counting()));

        counts.forEach((priority, count) ->
                dataset.setValue(priority.getDisplayName(), count));

        return ChartFactory.createPieChart(
                "Task Priority Distribution", dataset, true, true, false);
    }

    private JFreeChart createCategoryChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        Map<Category, Long> counts = taskManager.getTasks().stream()
                .collect(Collectors.groupingBy(Task::getCategory, Collectors.counting()));

        counts.forEach((category, count) ->
                dataset.addValue(count, "Tasks", category.getName()));

        return ChartFactory.createBarChart(
                "Task Categories", "Category", "Count", dataset,
                PlotOrientation.VERTICAL, true, true, false);
    }
}