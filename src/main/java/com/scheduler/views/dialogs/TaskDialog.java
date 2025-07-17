package com.scheduler.views.dialogs;

import com.scheduler.models.*;
import com.scheduler.utils.StorageManager;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.time.*;
import java.util.Date;

public class TaskDialog extends JDialog {
    private final TaskManager taskManager;
    private Task task;
    private boolean saved = false;
    private final Runnable refreshCallback;

    // UI Components
    private JTextField titleField;
    private JTextArea descriptionArea;
    private JComboBox<Priority> priorityCombo;
    private JComboBox<Category> categoryCombo;
    private JSpinner dueDateSpinner;
    private JSpinner timeSpinner;
    private JCheckBox completedCheckbox;

    public TaskDialog(JFrame parent, TaskManager taskManager, Task task, Runnable refreshCallback) {
        super(parent, task == null ? "Add Task" : "Edit Task", true);
        this.taskManager = taskManager;
        this.task = task;
        this.refreshCallback = refreshCallback;

        setSize(500, 450);
        setLocationRelativeTo(parent);
        initComponents();
        pack();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Form panel with improved layout
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));

        // Title
        addFormRow(formPanel, "Title:", titleField = new JTextField());

        // Description
        descriptionArea = new JTextArea(4, 20);
        addFormRow(formPanel, "Description:", new JScrollPane(descriptionArea));

        // Priority and Category
        JPanel rowPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        priorityCombo = new JComboBox<>(Priority.values());
        rowPanel.add(createLabeledPanel("Priority:", priorityCombo));

        categoryCombo = new JComboBox<>();
        refreshCategories();
        JButton addCategoryBtn = new JButton("+");
        addCategoryBtn.addActionListener(e -> showAddCategoryDialog());
        JPanel categoryPanel = new JPanel(new BorderLayout());
        categoryPanel.add(categoryCombo, BorderLayout.CENTER);
        categoryPanel.add(addCategoryBtn, BorderLayout.EAST);
        rowPanel.add(createLabeledPanel("Category:", categoryPanel));

        formPanel.add(rowPanel);
        formPanel.add(Box.createVerticalStrut(10));

        // Date and Time
        rowPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        dueDateSpinner = new JSpinner(new SpinnerDateModel());
        dueDateSpinner.setEditor(new JSpinner.DateEditor(dueDateSpinner, "MM/dd/yyyy"));
        rowPanel.add(createLabeledPanel("Date:", dueDateSpinner));

        timeSpinner = new JSpinner(new SpinnerDateModel());
        timeSpinner.setEditor(new JSpinner.DateEditor(timeSpinner, "HH:mm"));
        rowPanel.add(createLabeledPanel("Time:", timeSpinner));

        formPanel.add(rowPanel);
        formPanel.add(Box.createVerticalStrut(15));

        // Completed Checkbox
        completedCheckbox = new JCheckBox("Mark as completed");
        formPanel.add(completedCheckbox);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> saveTask());
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(cancelButton);
        buttonPanel.add(saveButton);

        // Load task data if editing
        if (task != null) {
            titleField.setText(task.getTitle());
            descriptionArea.setText(task.getDescription());
            priorityCombo.setSelectedItem(task.getPriority());
            categoryCombo.setSelectedItem(task.getCategory());
            if (task.getDueDate() != null) {
                dueDateSpinner.setValue(Date.from(task.getDueDate().atZone(ZoneId.systemDefault()).toInstant()));
                timeSpinner.setValue(Date.from(task.getDueDate().atZone(ZoneId.systemDefault()).toInstant()));
            }
            completedCheckbox.setSelected(task.isCompleted());
        }

        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel);
    }

    private JPanel createLabeledPanel(String label, Component component) {
        JPanel panel = new JPanel(new BorderLayout(5, 0));
        panel.add(new JLabel(label), BorderLayout.WEST);
        panel.add(component, BorderLayout.CENTER);
        return panel;
    }

    private void addFormRow(JPanel panel, String label, Component component) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.add(new JLabel(label), BorderLayout.WEST);
        row.add(component, BorderLayout.CENTER);
        panel.add(row);
        panel.add(Box.createVerticalStrut(10));
    }

    private void refreshCategories() {
        DefaultComboBoxModel<Category> model = new DefaultComboBoxModel<>();
        taskManager.getCategories().forEach(model::addElement);
        categoryCombo.setModel(model);
    }

    private void showAddCategoryDialog() {
        String name = JOptionPane.showInputDialog(this, "New Category Name:");
        if (name != null && !name.trim().isEmpty()) {
            Color color = JColorChooser.showDialog(this, "Choose Color", Color.BLUE);
            if (color != null) {
                Category newCategory = new Category(name.trim(), color);
                taskManager.getCategories().add(newCategory);
                try {
                    StorageManager.saveData(taskManager);
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this,
                            "Error saving categories: " + ex.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
                refreshCategories();
                categoryCombo.setSelectedItem(newCategory);
            }
        }
    }

    private void saveTask() {
        try {
            if (titleField.getText().trim().isEmpty()) {
                throw new IllegalArgumentException("Title cannot be empty");
            }

            // Create new task or use existing one
            Task taskToSave = (task == null) ? new Task() : task;

            // Set all fields
            taskToSave.setTitle(titleField.getText().trim());
            taskToSave.setDescription(descriptionArea.getText().trim());
            taskToSave.setPriority((Priority) priorityCombo.getSelectedItem());
            Category selectedCategory = (Category) categoryCombo.getSelectedItem();
            if (selectedCategory == null) {
                throw new IllegalArgumentException("Please select a category");
            }
            taskToSave.setCategory(selectedCategory);
            // Combine date and time
            Date date = (Date) dueDateSpinner.getValue();
            Date time = (Date) timeSpinner.getValue();
            if (date != null && time != null) {
                LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                LocalTime localTime = time.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
                taskToSave.setDueDate(LocalDateTime.of(localDate, localTime));
            }

            taskToSave.setCompleted(completedCheckbox.isSelected());

            // Only add to manager if it's a new task
            if (task == null) {
                taskManager.addTask(taskToSave);
            }

            StorageManager.saveData(taskManager);
            saved = true;
            refreshCallback.run();
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error saving task: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }



    public boolean isSaved() {
        return saved;
    }
}