package com.moneyManagement.gui.table.view;

import com.moneyManagement.gui.table.cell.CDateEditor;
import com.moneyManagement.gui.table.cell.CTableButton;
import com.moneyManagement.gui.table.model.CPlanItemsTableModel;
import com.moneyManagement.gui.table.model.CTableModel;

import javax.swing.table.*;
import java.awt.event.*;

public class CPlanItemsView extends CTableView
{
    public CPlanItemsView(CPlanItemsTableModel model)
    {
        super(model);
        setAutoCreateColumnsFromModel(true);

        getColumnByName(CPlanItemsTableModel.PLAN_DATE_COLUMN)
                .setCellEditor(new CDateEditor());

        CTableButton editRowTableButton = new CTableButton(this)
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                int rowIndex = table.convertRowIndexToModel(table.getEditingRow());
                fireEditingStopped();

                getModel().markRowAsUpdated(rowIndex);

                repaint();
            }
        };

        TableColumn editColumn = getColumnByName(CTableModel.EDIT_COLUMN);
        editColumn.setCellRenderer(editRowTableButton);
        editColumn.setCellEditor(editRowTableButton);

        repaint();
    }

    @Override
    public CPlanItemsTableModel getModel()
    {
        return (CPlanItemsTableModel) super.getModel();
    }

    @Override
    protected String getTableName()
    {
        return "plan";
    }
}
