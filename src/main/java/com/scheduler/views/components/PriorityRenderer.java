package com.scheduler.views.components;

import com.scheduler.models.Priority;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class PriorityRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (value instanceof Priority) {
            Priority p = (Priority)value;
            setText(p.getDisplayName());
            setBackground(p.getColor());
        }
        return this;
    }
}