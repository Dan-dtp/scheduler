package com.scheduler.views;

import com.scheduler.models.Task;
import com.scheduler.models.TaskManager;

import javax.swing.*;
import java.awt.*;
import java.time.*;
import java.time.format.*;
import java.util.List;

public class SchedulePanel extends JPanel {
    private TaskManager taskManager;
    private JList<DaySchedule> scheduleList;

    public SchedulePanel(TaskManager taskManager) {
        this.taskManager = taskManager;
        setLayout(new BorderLayout());
        initComponents();
        refreshSchedule();
    }

    private void initComponents() {
        scheduleList = new JList<>();
        scheduleList.setCellRenderer(new ScheduleCellRenderer());
        scheduleList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(scheduleList);
        add(scrollPane, BorderLayout.CENTER);

        JToolBar toolBar = new JToolBar();
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refreshSchedule());

        toolBar.add(refreshButton);
        add(toolBar, BorderLayout.NORTH);
    }

    private void refreshSchedule() {
        DefaultListModel<DaySchedule> model = new DefaultListModel<>();

        LocalDate today = LocalDate.now();
        for (int i = 0; i < 7; i++) {
            LocalDate date = today.plusDays(i);
            List<Task> tasks = taskManager.getTasksForDate(date);
            model.addElement(new DaySchedule(date, tasks));
        }

        scheduleList.setModel(model);
    }

    private static class DaySchedule {
        private final LocalDate date;
        private final List<Task> tasks;

        public DaySchedule(LocalDate date, List<Task> tasks) {
            this.date = date;
            this.tasks = tasks;
        }

        public String toString() {
            return date.format(DateTimeFormatter.ofPattern("EEEE, MMMM d")) +
                    " (" + tasks.size() + " tasks)";
        }
    }

    private static class ScheduleCellRenderer extends JPanel implements ListCellRenderer<DaySchedule> {
        private JLabel dateLabel;
        private JPanel tasksPanel;

        public ScheduleCellRenderer() {
            setLayout(new BorderLayout());
            dateLabel = new JLabel();
            dateLabel.setFont(dateLabel.getFont().deriveFont(Font.BOLD, 14));
            tasksPanel = new JPanel();
            tasksPanel.setLayout(new BoxLayout(tasksPanel, BoxLayout.Y_AXIS));

            add(dateLabel, BorderLayout.NORTH);
            add(new JScrollPane(tasksPanel), BorderLayout.CENTER);
            setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends DaySchedule> list,
                                                      DaySchedule value, int index, boolean isSelected, boolean cellHasFocus) {
            dateLabel.setText(value.toString());

            tasksPanel.removeAll();
            for (Task task : value.tasks) {
                JLabel taskLabel = new JLabel("• " + task.getTitle());
                taskLabel.setForeground(task.getPriority().getColor());
                tasksPanel.add(taskLabel);
            }

            if (isSelected) {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            } else {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }

            return this;
        }
    }
}