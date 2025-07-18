package com.scheduler.views.dialogs;

import com.scheduler.models.*;
import com.scheduler.utils.StorageManager;
import com.scheduler.views.Theme;
import javax.swing.*;
import javax.swing.text.JTextComponent;
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
    private JButton okButton;
    private JButton cancelButton;

    public TaskDialog(JFrame parent, TaskManager taskManager, Task task, Runnable refreshCallback) {
        super(parent, task == null ? "Add Task" : "Edit Task", true);
        this.taskManager = taskManager;
        this.task = task;
        this.refreshCallback = refreshCallback;

        setSize(500, 450);
        setLocationRelativeTo(parent);
        initComponents();
        updateTheme();
    }

    private void updateTheme() {
        Color bg = Theme.BACKGROUND;
        Color fg = Theme.TEXT_PRIMARY;

        getContentPane().setBackground(bg);
        updateComponentTheme(getContentPane(), bg, fg);

        revalidate();
        repaint();
    }

    private void updateComponentTheme(Container container, Color bg, Color fg) {
        for (Component comp : container.getComponents()) {
            if (!comp.isVisible()) continue;

            // Set basic colors for all components
            if (comp instanceof JComponent) {
                ((JComponent)comp).setOpaque(true);
            }
            comp.setBackground(Theme.CARD_BACKGROUND);
            comp.setForeground(fg);

            // Handle specific component types
            if (comp instanceof JLabel) {
                // Labels don't need background
                comp.setBackground(container.getBackground());
                ((JLabel)comp).setForeground(fg);
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
            else if (comp instanceof JViewport) {
                // Viewports should match their container
                comp.setBackground(container.getBackground());
            }

            // Recursive update for containers
            if (comp instanceof Container) {
                updateComponentTheme((Container)comp, bg, fg);
            }
        }
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        Theme.applyModernPanelStyle(mainPanel);

        // Form panel with improved layout
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        Theme.applyModernPanelStyle(formPanel);

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
        Theme.applyButtonStyle(addCategoryBtn);
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
        Theme.applyModernPanelStyle(buttonPanel);

        okButton = new JButton("Save");
        cancelButton = new JButton("Cancel");

        okButton.addActionListener(e -> saveTask());
        cancelButton.addActionListener(e -> dispose());

        Theme.applyButtonStyle(okButton);
        Theme.applyButtonStyle(cancelButton);

        buttonPanel.add(cancelButton);
        buttonPanel.add(okButton);

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
        JLabel jLabel = new JLabel(label);
        jLabel.setForeground(Theme.TEXT_PRIMARY);
        panel.add(jLabel, BorderLayout.WEST);
        panel.add(component, BorderLayout.CENTER);
        return panel;
    }

    private void addFormRow(JPanel panel, String label, Component component) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        JLabel jLabel = new JLabel(label);
        jLabel.setForeground(Theme.TEXT_PRIMARY);
        row.add(jLabel, BorderLayout.WEST);
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

    private void updateFormComponents() {
        if (titleField != null) {
            titleField.setBackground(Theme.CARD_BACKGROUND);
        }
        if (descriptionArea != null) {
            descriptionArea.setBackground(Theme.CARD_BACKGROUND);
        }
        if (priorityCombo != null) {
            priorityCombo.setBackground(Theme.CARD_BACKGROUND);
        }
        if (categoryCombo != null) {
            categoryCombo.setBackground(Theme.CARD_BACKGROUND);
        }
        if (dueDateSpinner != null) {
            dueDateSpinner.setBackground(Theme.CARD_BACKGROUND);
        }
        if (timeSpinner != null) {
            timeSpinner.setBackground(Theme.CARD_BACKGROUND);
        }
    }

    private void saveTask() {
        try {
            if (titleField.getText().trim().isEmpty()) {
                throw new IllegalArgumentException("Title cannot be empty");
            }

            if (task == null) {
                task = new Task();
            }

            task.setTitle(titleField.getText().trim());
            task.setDescription(descriptionArea.getText().trim());
            task.setPriority((Priority) priorityCombo.getSelectedItem());
            task.setCategory((Category) categoryCombo.getSelectedItem());
            task.setCompleted(completedCheckbox.isSelected());

            // Combine date and time
            Date date = (Date) dueDateSpinner.getValue();
            Date time = (Date) timeSpinner.getValue();
            LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalTime localTime = time.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
            task.setDueDate(LocalDateTime.of(localDate, localTime));

            taskManager.addTask(task);
            StorageManager.saveData(taskManager);

            saved = true;
            refreshCallback.run();
            dispose();  // Close the dialog
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error saving task: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSaved() {
        return saved;
    }
}