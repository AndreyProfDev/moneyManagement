package com.moneyManagement.gui.table.view;

import com.moneyManagement.data.ECategoryType;
import com.moneyManagement.gui.table.cell.CTableComboBox;
import com.moneyManagement.gui.table.model.CCategoriesTableModel;

public class CCategoriesTableView extends CTableView
{
    public CCategoriesTableView(CCategoriesTableModel model)
    {
        super(model);
        setAutoCreateColumnsFromModel(true);

        getColumnByName(CCategoriesTableModel.ACCOUNT_TYPE_COLUMN)
                .setCellEditor(new CTableComboBox<>(ECategoryType.values()));

        repaint();
    }

    @Override
    public CCategoriesTableModel getModel()
    {
        return (CCategoriesTableModel) super.getModel();
    }

    @Override
    protected String getTableName()
    {
        return "categories";
    }
}
