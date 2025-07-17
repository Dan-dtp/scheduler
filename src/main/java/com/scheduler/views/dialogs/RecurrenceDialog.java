package com.scheduler.views.dialogs;

import com.scheduler.models.*;
import javax.swing.*;
import java.awt.*;
import java.time.DayOfWeek;
import java.util.EnumSet;

public class RecurrenceDialog extends JDialog {
    private RecurrencePattern recurrence;
    private boolean approved;

    public RecurrenceDialog(JFrame parent) {
        super(parent, "Set Recurrence", true);
        setSize(400, 300);
        setLocationRelativeTo(parent);
        initComponents();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Frequency selection
        JPanel frequencyPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        frequencyPanel.add(new JLabel("Repeat:"));
        JComboBox<RecurrencePattern.Frequency> frequencyCombo = new JComboBox<>(
                RecurrencePattern.Frequency.values());
        frequencyPanel.add(frequencyCombo);

        // Interval selection
        JPanel intervalPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        intervalPanel.add(new JLabel("Every:"));
        JSpinner intervalSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 365, 1));
        intervalPanel.add(intervalSpinner);
        intervalPanel.add(new JLabel("day(s)/week(s)/month(s)"));

        // Days of week (for weekly)
        JPanel daysPanel = new JPanel(new GridLayout(0, 2));
        EnumSet<DayOfWeek> daysOfWeek = EnumSet.noneOf(DayOfWeek.class);
        for (DayOfWeek day : DayOfWeek.values()) {
            JCheckBox checkBox = new JCheckBox(day.toString());
            checkBox.addActionListener(e -> {
                if (checkBox.isSelected()) daysOfWeek.add(day);
                else daysOfWeek.remove(day);
            });
            daysPanel.add(checkBox);
        }

        // End date
        JPanel endDatePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        endDatePanel.add(new JLabel("Ends:"));
        JRadioButton neverRadio = new JRadioButton("Never", true);
        JRadioButton onDateRadio = new JRadioButton("On:");
        JSpinner endDateSpinner = new JSpinner(new SpinnerDateModel());
        endDateSpinner.setEditor(new JSpinner.DateEditor(endDateSpinner, "MM/dd/yyyy"));

        ButtonGroup endGroup = new ButtonGroup();
        endGroup.add(neverRadio);
        endGroup.add(onDateRadio);

        endDatePanel.add(neverRadio);
        endDatePanel.add(onDateRadio);
        endDatePanel.add(endDateSpinner);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");

        okButton.addActionListener(e -> {
            recurrence = new RecurrencePattern();
            recurrence.setFrequency((RecurrencePattern.Frequency)frequencyCombo.getSelectedItem());
            recurrence.setInterval((Integer)intervalSpinner.getValue());
            recurrence.setDaysOfWeek(daysOfWeek);

            if (onDateRadio.isSelected()) {
                recurrence.setEndDate(((java.util.Date)endDateSpinner.getValue())
                        .toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate());
            }

            approved = true;
            dispose();
        });

        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        // Add components
        mainPanel.add(frequencyPanel, BorderLayout.NORTH);
        mainPanel.add(intervalPanel, BorderLayout.CENTER);
        mainPanel.add(daysPanel, BorderLayout.CENTER);
        mainPanel.add(endDatePanel, BorderLayout.SOUTH);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    public RecurrencePattern getRecurrence() {
        return approved ? recurrence : null;
    }
}