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
    private JCheckBox completedCheckbox;

    // Modified constructor to accept refresh callback
    public TaskDialog(JFrame parent, TaskManager taskManager, Task task, Runnable refreshCallback) {
        super(parent, task == null ? "Add Task" : "Edit Task", true);
        this.taskManager = taskManager;
        this.task = task;
        this.refreshCallback = refreshCallback;

        setSize(500, 400);
        setLocationRelativeTo(parent);
        setResizable(false);

        initComponents();
        pack();
    }


    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Form panel
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 5, 5));

        // Title
        formPanel.add(new JLabel("Title:"));
        titleField = new JTextField();
        formPanel.add(titleField);

        // Description
        formPanel.add(new JLabel("Description:"));
        descriptionArea = new JTextArea(3, 20);
        formPanel.add(new JScrollPane(descriptionArea));

        // Priority
        formPanel.add(new JLabel("Priority:"));
        priorityCombo = new JComboBox<>(Priority.values());
        formPanel.add(priorityCombo);

        // Category
        formPanel.add(new JLabel("Category:"));
        categoryCombo = new JComboBox<Category>() {
            @Override
            public void setSelectedItem(Object anObject) {
                if (anObject instanceof String) {
                    // Handle string selection by finding matching category
                    for (int i = 0; i < getItemCount(); i++) {
                        Category cat = getItemAt(i);
                        if (cat.getName().equals(anObject)) {
                            super.setSelectedItem(cat);
                            return;
                        }
                    }
                    super.setSelectedItem(null);
                } else {
                    super.setSelectedItem(anObject);
                }
            }
        };

        // Custom renderer for category display
        categoryCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Category) {
                    Category cat = (Category) value;
                    setText(cat.getName());
                    setBackground(cat.getColor());
                    setForeground(getContrastColor(cat.getColor()));
                }
                return this;
            }

            private Color getContrastColor(Color color) {
                // Calculate contrasting text color
                double luminance = (0.299 * color.getRed() + 0.587 * color.getGreen() + 0.114 * color.getBlue()) / 255;
                return luminance > 0.5 ? Color.BLACK : Color.WHITE;
            }
        });

        refreshCategories();

        JButton addCategoryBtn = new JButton("+");
        addCategoryBtn.addActionListener(e -> showAddCategoryDialog());

        JPanel categoryPanel = new JPanel(new BorderLayout());
        categoryPanel.add(categoryCombo, BorderLayout.CENTER);
        categoryPanel.add(addCategoryBtn, BorderLayout.EAST);
        formPanel.add(categoryPanel);

        // Due Date
        formPanel.add(new JLabel("Due Date:"));
        dueDateSpinner = new JSpinner(new SpinnerDateModel());
        dueDateSpinner.setEditor(new JSpinner.DateEditor(dueDateSpinner, "MM/dd/yyyy"));
        formPanel.add(dueDateSpinner);

        // Completed
        formPanel.add(new JLabel("Completed:"));
        completedCheckbox = new JCheckBox();
        formPanel.add(completedCheckbox);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> saveTask());
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        // Load task data if editing
        if (task != null) {
            titleField.setText(task.getTitle());
            descriptionArea.setText(task.getDescription());
            priorityCombo.setSelectedItem(task.getPriority());
            categoryCombo.setSelectedItem(task.getCategory());
            if (task.getDueDate() != null) {
                dueDateSpinner.setValue(Date.from(task.getDueDate().atZone(ZoneId.systemDefault()).toInstant()));
            }
            completedCheckbox.setSelected(task.isCompleted());
        }

        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel);
    }

    private void refreshCategories() {
        DefaultComboBoxModel<Category> model = new DefaultComboBoxModel<>();
        for (Category category : taskManager.getCategories()) {
            model.addElement(category);
        }
        categoryCombo.setModel(model);
    }

    private void showAddCategoryDialog() {
        String name = JOptionPane.showInputDialog(this, "Enter category name:");
        if (name != null && !name.trim().isEmpty()) {
            Color color = JColorChooser.showDialog(this, "Choose Category Color", Color.BLUE);
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
            // Validate inputs
            if (titleField.getText().trim().isEmpty()) {
                throw new IllegalArgumentException("Title cannot be empty");
            }

            Object selectedCategory = categoryCombo.getSelectedItem();
            if (!(selectedCategory instanceof Category)) {
                throw new IllegalArgumentException("Please select a valid category");
            }

            // Create or update task
            if (task == null) {
                task = new Task();
                taskManager.addTask(task);  // Add to manager before setting properties
            }

            // Set task properties
            task.setTitle(titleField.getText().trim());
            task.setDescription(descriptionArea.getText().trim());
            task.setPriority((Priority) priorityCombo.getSelectedItem());
            task.setCategory((Category) selectedCategory);

            // Handle date
            Date date = (Date) dueDateSpinner.getValue();
            Instant instant = date.toInstant();
            LocalDateTime dueDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
            task.setDueDate(dueDateTime);

            task.setCompleted(completedCheckbox.isSelected());

            // Save data
            StorageManager.saveData(taskManager);

            saved = true;

            // Refresh the main view
            if (refreshCallback != null) {
                refreshCallback.run();
            }

            dispose();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error saving task: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    public boolean isSaved() {
        return saved;
    }

    public Task getTask() {
        return task;
    }
}