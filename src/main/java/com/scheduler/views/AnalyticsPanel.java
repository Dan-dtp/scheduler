package com.scheduler.views;

import com.scheduler.models.*;
import org.jfree.chart.*;
import org.jfree.chart.plot.*;
import org.jfree.data.category.*;
import org.jfree.data.general.*;
import javax.swing.*;
import java.awt.*;
import java.time.*;
import java.time.temporal.*;
import java.util.*;
import java.util.stream.*;

public class AnalyticsPanel extends JPanel {
    private final TaskManager taskManager;
    private ChartPanel priorityChartPanel;
    private ChartPanel completionChartPanel;
    private JPanel heatmapPanel;

    public AnalyticsPanel(TaskManager taskManager) {
        this.taskManager = taskManager;
        setLayout(new GridLayout(1, 2, 15, 15));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(new Color(245, 245, 245));
        initComponents();
    }

    private void initComponents() {
        // Priority Distribution Chart
        priorityChartPanel = new ChartPanel(createPriorityChart());
        priorityChartPanel.setBorder(BorderFactory.createTitledBorder("Priority Distribution"));

        // Completion Rate Chart
        completionChartPanel = new ChartPanel(createCompletionChart());
        completionChartPanel.setBorder(BorderFactory.createTitledBorder("Completion Rate"));

        // Heatmap Panel (placeholder implementation)
        heatmapPanel = createHeatmapPanel();
        heatmapPanel.setBorder(BorderFactory.createTitledBorder("Weekly Productivity"));

        add(priorityChartPanel);
        add(completionChartPanel);
        // add(heatmapPanel); // Uncomment when implemented
    }

    public void refresh() {
        // Update priority chart
        priorityChartPanel.setChart(createPriorityChart());

        // Update completion chart
        completionChartPanel.setChart(createCompletionChart());

        // Update heatmap (if implemented)
        // refreshHeatmap();
    }

    private JFreeChart createPriorityChart() {
        DefaultPieDataset<String> dataset = new DefaultPieDataset<>();

        taskManager.getTasks().stream()
                .collect(Collectors.groupingBy(
                        Task::getPriority,
                        Collectors.counting()
                ))
                .forEach((priority, count) ->
                        dataset.setValue(priority.getDisplayName(), count)
                );

        JFreeChart chart = ChartFactory.createPieChart(
                null, // no title
                dataset,
                true, // include legend
                true, // tooltips
                false // no URLs
        );

        // Styling
        chart.setBackgroundPaint(new Color(245, 245, 245));
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setBackgroundPaint(null);
        plot.setOutlineVisible(false);
        plot.setLabelGenerator(null); // Hide labels on slices

        return chart;
    }

    private JFreeChart createCompletionChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        long completed = taskManager.getTasks().stream()
                .filter(Task::isCompleted)
                .count();
        long pending = taskManager.getTasks().size() - completed;

        dataset.addValue(completed, "Tasks", "Completed");
        dataset.addValue(pending, "Tasks", "Pending");

        JFreeChart chart = ChartFactory.createBarChart(
                null, // no title
                "Status",
                "Count",
                dataset,
                PlotOrientation.VERTICAL,
                false, // no legend
                true, // tooltips
                false // no URLs
        );

        // Styling
        chart.setBackgroundPaint(new Color(245, 245, 245));
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(null);
        plot.setOutlineVisible(false);
        plot.getRenderer().setSeriesPaint(0, new Color(70, 130, 180));

        return chart;
    }

    private JPanel createHeatmapPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        // Placeholder implementation
        JLabel placeholder = new JLabel("Weekly Productivity Heatmap", SwingConstants.CENTER);
        placeholder.setForeground(new Color(150, 150, 150));
        panel.add(placeholder, BorderLayout.CENTER);

        return panel;
    }

    private void refreshHeatmap() {
        // Implementation would update the heatmap visualization
        // based on current task data
    }
}