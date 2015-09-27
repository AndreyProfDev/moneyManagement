package com.moneyManagement.gui.table.cell;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public abstract class CTableButton extends AbstractCellEditor implements TableCellRenderer, TableCellEditor, ActionListener, MouseListener
{
    private Border originalBorder;
    private Border focusBorder;

    private JButton renderButton;
    private JButton editButton;
    private Object editorValue;
    private boolean isButtonColumnEditor;
    protected JTable table;

    public CTableButton(JTable table)
    {
        this.table = table;
        renderButton = new JButton();
        editButton = new JButton();
        editButton.setFocusPainted( false );
        editButton.addActionListener(this);
        originalBorder = editButton.getBorder();
        focusBorder = new LineBorder(Color.BLUE);
        editButton.setBorder(focusBorder);

        table.addMouseListener(this);
    }

    @Override
    public Component getTableCellEditorComponent(
            JTable table, Object value, boolean isSelected, int row, int column)
    {
        if (value == null || value instanceof Icon)
        {
            editButton.setText( "" );
            editButton.setIcon( (Icon)value );
        }

        this.editorValue = value;
        return editButton;
    }

    @Override
    public Object getCellEditorValue()
    {
        return editorValue;
    }

    public Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
    {
        if (isSelected)
        {
            renderButton.setForeground(table.getSelectionForeground());
            renderButton.setBackground(table.getSelectionBackground());
        }
        else
        {
            renderButton.setForeground(table.getForeground());
            renderButton.setBackground(UIManager.getColor("Button.background"));
        }

        if (hasFocus)
        {
            renderButton.setBorder( focusBorder );
        }
        else
        {
            renderButton.setBorder( originalBorder );
        }

        if (value == null || value instanceof Icon)
        {
            renderButton.setText( "" );
            renderButton.setIcon( (Icon)value );
        }

        return renderButton;
    }

    public void mousePressed(MouseEvent e)
    {
        if (table.isEditing() &&  table.getCellEditor() == this)
        {
            isButtonColumnEditor = true;
        }
    }

    public void mouseReleased(MouseEvent e)
    {
        if (isButtonColumnEditor &&  table.isEditing())
        {
            table.getCellEditor().stopCellEditing();
        }

        isButtonColumnEditor = false;
    }

    public void mouseClicked(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
}
