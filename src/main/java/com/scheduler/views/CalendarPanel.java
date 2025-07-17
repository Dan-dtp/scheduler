package com.scheduler.views;

import com.scheduler.models.TaskManager;
import javax.swing.JPanel;

public class CalendarPanel extends JPanel {
    private TaskManager taskManager;

    public CalendarPanel(TaskManager taskManager) {
        this.taskManager = taskManager;
        // Initialize calendar UI components
    }
}