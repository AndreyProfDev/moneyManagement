package com.moneyManagement.gui.table.cell;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

public class CTableHeaderRenderer implements TableCellRenderer
{
    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
    {
        String strValue = ((String) value).replace("\n", "<br>");
        strValue = "<html>" + strValue + "</html>";
        return new JLabel(strValue);
    }
}
