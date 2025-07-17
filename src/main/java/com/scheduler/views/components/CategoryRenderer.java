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
                                                   boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(table, value,
                isSelected, hasFocus, row, column);

        if (value instanceof Category) {
            Category priority = (Category) value;
            c.setBackground(priority.getColor());
            c.setForeground(Color.WHITE);
        } else {
            c.setBackground(Theme.isDarkMode() ?
                    Theme.CARD_BACKGROUND_DARK : Theme.CARD_BACKGROUND_LIGHT);
            c.setForeground(Theme.isDarkMode() ?
                    Theme.TEXT_PRIMARY_DARK : Theme.TEXT_PRIMARY_LIGHT);
        }

        return c;
    }
}