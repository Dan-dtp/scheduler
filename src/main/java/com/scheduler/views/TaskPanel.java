package com.scheduler.views;

import com.scheduler.models.*;
import com.scheduler.utils.*;
import com.scheduler.views.components.CategoryRenderer;
import com.scheduler.views.components.PriorityRenderer;
import com.scheduler.views.dialogs.TaskDialog;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.time.*;
import java.util.*;
import java.util.List;

public class TaskPanel extends JPanel {
    private TaskManager taskManager;
    private final Runnable refreshCallback;
    private JTable taskTable;
    private TaskTableModel tableModel;

    public TaskPanel(TaskManager taskManager, Runnable refreshCallback) {
        this.taskManager = taskManager;
        this.refreshCallback = refreshCallback;
        setLayout(new BorderLayout());
        initComponents();
        setupTheme();
    }

    private void setupTheme() {
        setBackground(Theme.BACKGROUND);
        updateComponentTheme(this);
    }

    public void refresh() {
        SwingUtilities.invokeLater(() -> {
            tableModel.refreshData();
            tableModel.fireTableDataChanged();
            revalidate();
            repaint();
        });
    }

    private void updateComponentTheme(Container container) {
        for (Component comp : container.getComponents()) {
            comp.setBackground(Theme.BACKGROUND);
            comp.setForeground(Theme.TEXT_PRIMARY);

            if (comp instanceof JLabel) {
                ((JLabel)comp).setForeground(Theme.TEXT_PRIMARY);
            }
            else if (comp instanceof AbstractButton) {
                if (comp instanceof JButton) {
                    Theme.applyButtonStyle((JButton)comp);
                } else {
                    AbstractButton button = (AbstractButton)comp;
                    button.setBackground(Theme.PRIMARY);
                    button.setForeground(Color.WHITE);
                }
            }
            else if (comp instanceof JTextComponent) {
                comp.setBackground(Theme.CARD_BACKGROUND);
            }
            else if (comp instanceof JScrollPane) {
                JScrollPane scrollPane = (JScrollPane)comp;
                scrollPane.getViewport().setBackground(Theme.CARD_BACKGROUND);
                scrollPane.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
            }

            if (comp instanceof Container) {
                updateComponentTheme((Container)comp);
            }
        }
    }

    private void initComponents() {
        // Apply theme to main panel
        Theme.applyModernPanelStyle(this);

        // Create table model
        tableModel = new TaskTableModel(taskManager);
        taskTable = new JTable(tableModel);
        Theme.applyTableStyle(taskTable);

        // Custom renderers
        taskTable.setDefaultRenderer(Priority.class, new PriorityRenderer());
        taskTable.setDefaultRenderer(Category.class, new CategoryRenderer());
        taskTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        taskTable.getColumnModel().getColumn(5).setCellRenderer(new CheckBoxRenderer());
        taskTable.getColumnModel().getColumn(5).setCellEditor(new CheckBoxEditor(new JCheckBox()));

        // Main toolbar
        JToolBar mainToolBar = new JToolBar();
        mainToolBar.setFloatable(false);
        mainToolBar.setBackground(Theme.BACKGROUND);

        // Action buttons
        JButton addButton = new JButton("Add Task");
        JButton editButton = new JButton("Edit");
        JButton deleteButton = new JButton("Delete");

        Theme.applyButtonStyle(addButton);
        Theme.applyButtonStyle(editButton);
        Theme.applyButtonStyle(deleteButton);

        addButton.addActionListener(e -> showTaskDialog(null));
        editButton.addActionListener(e -> editSelectedTask());
        deleteButton.addActionListener(e -> deleteSelectedTask());

        // Sort buttons
        JButton sortByDateBtn = new JButton("Sort by Date");
        JButton sortByPriorityBtn = new JButton("Sort by Priority");

        Theme.applyButtonStyle(sortByDateBtn);
        Theme.applyButtonStyle(sortByPriorityBtn);

        sortByDateBtn.addActionListener(e -> sortTasks(Comparator.comparing(Task::getDueDate)));
        sortByPriorityBtn.addActionListener(e -> sortTasks(Comparator.comparing(Task::getPriority)));

        // Add buttons to toolbar
        mainToolBar.add(addButton);
        mainToolBar.add(editButton);
        mainToolBar.add(deleteButton);
        mainToolBar.add(Box.createHorizontalStrut(20));
        mainToolBar.add(sortByDateBtn);
        mainToolBar.add(sortByPriorityBtn);

        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        Theme.applyModernPanelStyle(filterPanel);

        JTextField searchField = new JTextField(20);
        JComboBox<Priority> priorityFilter = new JComboBox<>(Priority.values());
        priorityFilter.insertItemAt(null, 0);
        priorityFilter.setSelectedIndex(0);

        searchField.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            protected void changed() {
                String searchText = searchField.getText().trim();
                Priority priority = (Priority)priorityFilter.getSelectedItem();
                filterTasks(searchText, priority);
            }
        });

        priorityFilter.addActionListener(e -> {
            String searchText = searchField.getText().trim();
            Priority priority = (Priority)priorityFilter.getSelectedItem();
            filterTasks(searchText, priority);
        });

        filterPanel.add(new JLabel("Search:"));
        filterPanel.add(searchField);
        filterPanel.add(new JLabel("Priority:"));
        filterPanel.add(priorityFilter);

        // Button panel
        JPanel buttonPanel = new JPanel(new BorderLayout());
        Theme.applyModernPanelStyle(buttonPanel);

        // Add components
        add(filterPanel, BorderLayout.NORTH);
        add(new JScrollPane(taskTable), BorderLayout.CENTER);

        // Add toolbar to button panel
        buttonPanel.add(mainToolBar, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private static class CheckBoxRenderer extends DefaultTableCellRenderer {
        private final JCheckBox checkBox = new JCheckBox();

        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            checkBox.setSelected(value != null && (Boolean) value);
            checkBox.setHorizontalAlignment(JLabel.CENTER);
            checkBox.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
            return checkBox;
        }
    }

    private static class CheckBoxEditor extends DefaultCellEditor {
        public CheckBoxEditor(JCheckBox checkBox) {
            super(checkBox);
        }
    }

    private void sortTasks(Comparator<Task> comparator) {
        tableModel.sortTasks(comparator);
    }

    private void filterTasks(String searchText, Priority priorityFilter) {
        tableModel.filter(task -> {
            boolean matches = true;

            // Search text filter (case insensitive)
            if (!searchText.isEmpty()) {
                String searchLower = searchText.toLowerCase();
                matches = task.getTitle().toLowerCase().contains(searchLower) ||
                        task.getDescription().toLowerCase().contains(searchLower);
            }

            // Priority filter
            if (priorityFilter != null) {
                matches = matches && (task.getPriority() == priorityFilter);
            }

            return matches;
        });
    }

    private void showTaskDialog(Task task) {
        TaskDialog dialog = new TaskDialog(
                (JFrame)SwingUtilities.getWindowAncestor(this),
                taskManager,
                task,
                () -> {
                    refreshImmediately();
                }
        );
        dialog.setVisible(true);
    }

    private void refreshImmediately() {
        tableModel.refreshData();
        SwingUtilities.invokeLater(() -> {
            tableModel.fireTableDataChanged();
            revalidate();
            repaint();
        });

        Window window = SwingUtilities.getWindowAncestor(this);
        if (window != null) {
            window.repaint();
        }
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
            return column == 5;
        }

        public void sortTasks(Comparator<Task> comparator) {
            filteredTasks.sort(comparator);
            fireTableDataChanged();
        }


        @Override
        public void setValueAt(Object value, int row, int column) {
            Task task = filteredTasks.get(row);
            if (column == 5) { // Completed column
                boolean completed = (Boolean)value;
                task.setCompleted(completed);
                if (completed) {
                    taskManager.completeTask(task);
                    filteredTasks.remove(row);
                    fireTableRowsDeleted(row, row);
                    if (refreshCallback != null) {
                        refreshCallback.run();
                    }
                }
            }
        }

        public void refreshData() {
            filteredTasks = new ArrayList<>(taskManager.getTasks());
            Collections.sort(filteredTasks);
        }

        public void fireTableDataChanged() {
            filteredTasks = new ArrayList<>(taskManager.getTasks());
            super.fireTableDataChanged();
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

    private abstract class DocumentAdapter implements javax.swing.event.DocumentListener {
        @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { changed(); }
        @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { changed(); }
        @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { changed(); }
        protected abstract void changed();
    }
}