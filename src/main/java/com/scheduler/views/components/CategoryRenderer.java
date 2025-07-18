package com.scheduler.views.components;

import com.scheduler.models.Category;
import com.scheduler.views.Theme;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class CategoryRenderer extends DefaultTableCellRenderer {
    // In your PriorityRenderer class
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value,
                isSelected, hasFocus, row, column);

        if (value instanceof Category) {
            Category category = (Category) value;
            c.setBackground(category.getColor());
            c.setForeground(Color.WHITE);
        } else {
            c.setBackground(Theme.CARD_BACKGROUND);
            c.setForeground(Theme.TEXT_PRIMARY);
        }

        return c;
    }
}