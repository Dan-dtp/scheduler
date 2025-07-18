package com.scheduler.views;

import com.scheduler.models.*;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.time.*;
import java.time.format.*;
import java.util.*;
import java.util.List;

public class SchedulePanel extends JPanel {
    private final TaskManager taskManager;
    private JTable scheduleTable;
    private JSpinner weeksSpinner;

    public SchedulePanel(TaskManager taskManager) {
        this.taskManager = taskManager;
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        initComponents();
    }

    private void initComponents() {
        Theme.applyModernPanelStyle(this);

        // Control panel
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        Theme.applyModernPanelStyle(controlPanel);

        JLabel weeksLabel = new JLabel("Weeks to show:");
        weeksLabel.setForeground(Theme.TEXT_PRIMARY);

        weeksSpinner = new JSpinner(new SpinnerNumberModel(2, 1, 12, 1));

        JButton refreshBtn = new JButton("Refresh");
        Theme.applyButtonStyle(refreshBtn);
        refreshBtn.addActionListener(e -> refreshSchedule());

        controlPanel.add(weeksLabel);
        controlPanel.add(weeksSpinner);
        controlPanel.add(refreshBtn);

        // Table setup
        scheduleTable = new JTable(new ScheduleTableModel());
        Theme.applyTableStyle(scheduleTable);
        scheduleTable.setDefaultRenderer(Object.class, new ScheduleTableCellRenderer());

        // Add components
        add(controlPanel, BorderLayout.NORTH);
        add(new JScrollPane(scheduleTable), BorderLayout.CENTER);

        refreshSchedule();
    }

    public void refresh() {
        refreshSchedule();
    }

    private void refreshSchedule() {
        ((ScheduleTableModel)scheduleTable.getModel()).refresh();
    }

    private class ScheduleTableModel extends AbstractTableModel {
        private List<DaySchedule> schedule = new ArrayList<>();
        private final String[] columnNames = {"Date", "Day", "Tasks"};

        public ScheduleTableModel() {
            refresh();
        }

        public void refresh() {
            schedule.clear();
            LocalDate today = LocalDate.now();
            int weeks = (Integer)weeksSpinner.getValue();

            for (int i = 0; i < 7 * weeks; i++) {
                LocalDate date = today.plusDays(i);
                schedule.add(new DaySchedule(date, taskManager.getTasksForDate(date)));
            }

            fireTableDataChanged();
        }

        @Override public int getRowCount() { return schedule.size(); }
        @Override public int getColumnCount() { return columnNames.length; }
        @Override public String getColumnName(int col) { return columnNames[col]; }

        @Override
        public Object getValueAt(int row, int col) {
            DaySchedule day = schedule.get(row);
            return switch (col) {
                case 0 -> day.date.format(DateTimeFormatter.ofPattern("MMM d"));
                case 1 -> day.date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.getDefault());
                case 2 -> day.tasks;
                default -> null;
            };
        }
    }

    private class ScheduleTableCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value,
                    isSelected, hasFocus, row, column);

            DaySchedule day = ((ScheduleTableModel)table.getModel()).schedule.get(row);

            if (column == 2) { // Tasks column
                JPanel panel = new JPanel(new BorderLayout());
                panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                panel.setBackground(isSelected ? table.getSelectionBackground() : Theme.CARD_BACKGROUND);

                JTextArea textArea = new JTextArea();
                textArea.setEditable(false);
                textArea.setBackground(panel.getBackground());
                textArea.setForeground(Theme.TEXT_PRIMARY);
                textArea.setFont(table.getFont());

                if (day.tasks.isEmpty()) {
                    textArea.setText("No tasks scheduled");
                    textArea.setForeground(Theme.TEXT_SECONDARY);
                } else {
                    for (Task task : day.tasks) {
                        textArea.append("• " + task.getTitle() +
                                " (" + task.getDueDate().format(DateTimeFormatter.ofPattern("h:mm a")) + ")\n");
                    }
                }

                panel.add(textArea, BorderLayout.CENTER);
                return panel;
            }

            c.setForeground(Theme.TEXT_PRIMARY);
            c.setBackground(Theme.BACKGROUND);
            return c;
        }
    }

    private static class DaySchedule {
        final LocalDate date;
        final List<Task> tasks;

        DaySchedule(LocalDate date, List<Task> tasks) {
            this.date = date;
            this.tasks = tasks;
        }
    }
}