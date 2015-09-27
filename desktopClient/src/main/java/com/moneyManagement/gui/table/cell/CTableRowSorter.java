package com.moneyManagement.gui.table.cell;

import javax.swing.table.*;

public class CTableRowSorter extends TableRowSorter<TableModel>
{
    public CTableRowSorter(TableModel model)
    {
        super(model);
    }

    public int convertRowIndexToModel(int index)
    {
        int maxRow = super.getViewRowCount();
        if (index >= maxRow)
        {
            return index;
        }

        return super.convertRowIndexToModel(index);
    }

    public int convertRowIndexToView(int index)
    {
        int maxRow = super.getModelRowCount();
        if (index > maxRow)
        {
            return index;
        }

        return super.convertRowIndexToView(index);
    }

    public int getViewRowCount()
    {
        return super.getViewRowCount() + 1;
    }
}
