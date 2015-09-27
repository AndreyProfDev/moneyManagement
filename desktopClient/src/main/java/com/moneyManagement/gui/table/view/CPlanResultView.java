package com.moneyManagement.gui.table.view;

import com.moneyManagement.gui.table.model.CPlanResultTableModel;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

public class CPlanResultView extends CTableView
{
    public CPlanResultView(CPlanResultTableModel dataModel)
    {
        super(dataModel, false);

        setAutoCreateColumnsFromModel(true);

        getColumnByName(CPlanResultTableModel.PLAN_RESULT_COLUMN)
                .setCellRenderer(new CPlanRenderer());

        repaint();
    }

    @Override
    public CPlanResultTableModel getModel()
    {
        return (CPlanResultTableModel) super.getModel();
    }

    @Override
    protected String getTableName()
    {
        return "plan_result";
    }

    private class CPlanRenderer extends DefaultTableCellRenderer
    {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
        {
            Component defaultComponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            Double planAssets = (Double) getModel().getValueAt(row, 1);
            Double actualAssets = (Double) getModel().getValueAt(row, 2);

            defaultComponent.setForeground(planAssets > actualAssets ? Color.RED : Color.GREEN);

            return defaultComponent;
        }
    }
}
