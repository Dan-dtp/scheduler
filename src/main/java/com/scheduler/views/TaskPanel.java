package com.scheduler.views;

import com.scheduler.models.*;
import com.scheduler.utils.*;
import com.scheduler.views.components.CategoryRenderer;
import com.scheduler.views.components.PriorityRenderer;
import com.scheduler.views.dialogs.TaskDialog;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.time.*;
import java.util.*;
import java.util.List;

public class TaskPanel extends JPanel {
    private TaskManager taskManager;
    private final Runnable refreshCallback; // Add this field
    private JTable taskTable;
    private TaskTableModel tableModel;

    public TaskPanel(TaskManager taskManager, Runnable refreshCallback) {
        this.taskManager = taskManager;
        this.refreshCallback = refreshCallback;
        setLayout(new BorderLayout());
        initComponents();
    }

    private void initComponents() {
        // Create table model
        tableModel = new TaskTableModel(taskManager);
        taskTable = new JTable(tableModel);

        // Custom renderers
        taskTable.setDefaultRenderer(Priority.class, new PriorityRenderer());
        taskTable.setDefaultRenderer(Category.class, new CategoryRenderer());

        // Row height and selection mode
        taskTable.setRowHeight(30);
        taskTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Toolbar
        JToolBar toolBar = new JToolBar();
        JButton addButton = new JButton("Add Task");
        JButton editButton = new JButton("Edit");
        JButton deleteButton = new JButton("Delete");

        addButton.addActionListener(e -> showTaskDialog(null));
        editButton.addActionListener(e -> editSelectedTask());
        deleteButton.addActionListener(e -> deleteSelectedTask());

        toolBar.add(addButton);
        toolBar.add(editButton);
        toolBar.add(deleteButton);

        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField searchField = new JTextField(20);
        JComboBox<Priority> priorityFilter = new JComboBox<>(Priority.values());
        priorityFilter.insertItemAt(null, 0); // Add "All" option
        priorityFilter.setSelectedIndex(0);

        searchField.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void changed() {
                filterTasks(searchField.getText(), (Priority)priorityFilter.getSelectedItem());
            }
        });

        priorityFilter.addActionListener(e ->
                filterTasks(searchField.getText(), (Priority)priorityFilter.getSelectedItem()));

        filterPanel.add(new JLabel("Search:"));
        filterPanel.add(searchField);
        filterPanel.add(new JLabel("Priority:"));
        filterPanel.add(priorityFilter);

        // Add components
        add(filterPanel, BorderLayout.NORTH);
        add(toolBar, BorderLayout.SOUTH);
        add(new JScrollPane(taskTable), BorderLayout.CENTER);
    }

    private void filterTasks(String searchText, Priority priorityFilter) {
        tableModel.filter(task -> {
            boolean matches = true;

            if (searchText != null && !searchText.isEmpty()) {
                matches &= task.getTitle().toLowerCase().contains(searchText.toLowerCase()) ||
                        task.getDescription().toLowerCase().contains(searchText.toLowerCase());
            }

            if (priorityFilter != null) {
                matches &= task.getPriority() == priorityFilter;
            }

            return matches;
        });
    }

    private void showTaskDialog(Task task) {
        TaskDialog dialog = new TaskDialog(
                (JFrame)SwingUtilities.getWindowAncestor(this),
                taskManager,
                task,
                refreshCallback // Use the field here
        );
        dialog.setVisible(true);
    }

    private void editSelectedTask() {
        int row = taskTable.getSelectedRow();
        if (row >= 0) {
            Task task = taskManager.getTasks().get(row);
            showTaskDialog(task);
        }
    }

    private void deleteSelectedTask() {
        int row = taskTable.getSelectedRow();
        if (row >= 0) {
            Task task = taskManager.getTasks().get(row);
            taskManager.deleteTask(task);
            tableModel.fireTableDataChanged();
        }
    }

    // Custom table model with filtering support
    private class TaskTableModel extends AbstractTableModel {
        private final TaskManager taskManager;
        private List<Task> filteredTasks;
        private final String[] columnNames = {"Title", "Description", "Priority", "Due Date", "Category", "Completed"};

        public TaskTableModel(TaskManager taskManager) {
            this.taskManager = taskManager;
            this.filteredTasks = new ArrayList<>(taskManager.getTasks());
        }
        @Override
        public boolean isCellEditable(int row, int column) {
            return column == 5; // Only make "Completed" column editable
        }

        @Override
        public void setValueAt(Object value, int row, int column) {
            Task task = filteredTasks.get(row);
            if (column == 5) { // Completed column
                task.setCompleted((Boolean) value);
                fireTableCellUpdated(row, column);
                try {
                    StorageManager.saveData(taskManager);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }

        public void filter(java.util.function.Predicate<Task> predicate) {
            filteredTasks.clear();
            for (Task task : taskManager.getTasks()) {
                if (predicate.test(task)) {
                    filteredTasks.add(task);
                }
            }
            fireTableDataChanged();
        }

        @Override
        public int getRowCount() {
            return filteredTasks.size();
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
                case 5: return Boolean.class;
                default: return Object.class;
            }
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Task task = filteredTasks.get(rowIndex);
            switch (columnIndex) {
                case 0: return task.getTitle();
                case 1: return task.getDescription();
                case 2: return task.getPriority();
                case 3: return task.getDueDate();
                case 4: return task.getCategory();
                case 5: return task.isCompleted();
                default: return null;
            }
        }
    }

    public void refresh() {
        tableModel.filter(task -> true); // Reset any filters
        tableModel.fireTableDataChanged();
    }

    // DocumentAdapter for text field changes
    private abstract class DocumentAdapter implements javax.swing.event.DocumentListener {
        @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { changed(); }
        @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { changed(); }
        @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { changed(); }
        protected abstract void changed();
    }
}