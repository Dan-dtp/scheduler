package com.scheduler.views.components;

import com.scheduler.models.Category;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class CategoryRenderer extends DefaultTableCellRenderer {
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus, int row, int column) {
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if (value instanceof Category) {
            Category c = (Category)value;
            setText(c.getName());
            setBackground(c.getColor());
        }
        return this;
    }
}