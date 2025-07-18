package com.scheduler.views;

import com.scheduler.models.Category;
import com.scheduler.models.Priority;
import com.scheduler.models.Task;
import com.scheduler.models.TaskManager;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class CompletedTasksPanel extends JPanel {
    private final TaskManager taskManager;
    private JTable completedTable;

    public CompletedTasksPanel(TaskManager taskManager) {
        this.taskManager = taskManager;
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 255));
        initComponents();
    }

    private void initComponents() {
        completedTable = new JTable(new CompletedTasksTableModel());
        completedTable.setBackground(new Color(255, 253, 245));
        completedTable.setGridColor(new Color(220, 230, 240));
        completedTable.setSelectionBackground(new Color(255, 200, 150));

        JScrollPane scrollPane = new JScrollPane(completedTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);
    }

    public void refresh() {
        ((CompletedTasksTableModel)completedTable.getModel()).refresh();
    }

    private class CompletedTasksTableModel extends AbstractTableModel {
        private List<Task> completedTasks;
        private final String[] columnNames = {"Title", "Description", "Priority", "Completed Date", "Category"};

        public CompletedTasksTableModel() {
            this.completedTasks = new ArrayList<>(taskManager.getCompletedTasks());
        }

        public void refresh() {
            this.completedTasks = new ArrayList<>(taskManager.getCompletedTasks());
            fireTableDataChanged();
        }

        @Override
        public int getRowCount() {
            return completedTasks.size();
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            switch (columnIndex) {
                case 0: return String.class;
                case 1: return String.class;
                case 2: return Priority.class;
                case 3: return LocalDateTime.class;
                case 4: return Category.class;
                default: return Object.class;
            }
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Task task = completedTasks.get(rowIndex);
            switch (columnIndex) {
                case 0: return task.getTitle();
                case 1: return task.getDescription();
                case 2: return task.getPriority();
                case 3: return task.getDueDate();
                case 4: return task.getCategory();
                default: return null;
            }
        }
    }
}

