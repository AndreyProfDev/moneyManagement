package com.moneyManagement.gui.table.cell;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class CTableComboBox<T> extends DefaultCellEditor implements TableCellRenderer
{
    private JComboBox<T> comboBox;

    public CTableComboBox(T[] items)
    {
        this(new JComboBox<>(items));
    }

    private CTableComboBox(JComboBox comboBox)
    {
        super(comboBox);
        this.comboBox = comboBox;
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                   boolean hasFocus, int row, int column) {
        if (isSelected)
        {
            comboBox.setForeground(table.getSelectionForeground());
            comboBox.setBackground(table.getSelectionBackground());
        }
        else
        {
            comboBox.setForeground(table.getForeground());
            comboBox.setBackground(table.getBackground());
        }
        comboBox.setSelectedItem(value);

        if (table.getRowHeight(row) < 20)
        {
            table.setRowHeight(row, 20);
        }

        return comboBox;
    }
}