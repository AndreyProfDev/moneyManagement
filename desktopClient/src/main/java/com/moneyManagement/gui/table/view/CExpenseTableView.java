package com.moneyManagement.gui.table.view;

import com.moneyManagement.data.CCategory;
import com.moneyManagement.data.CIncomeExpenseTransactionItem;
import com.moneyManagement.data.CUser;
import com.moneyManagement.gui.table.cell.CTableComboBox;
import com.moneyManagement.gui.table.model.CExpenseTableModel;

import java.util.Collections;
import java.util.List;

public class CExpenseTableView extends CTableView
{
    public CExpenseTableView(CUser user, List<CCategory> categories)
    {
        this(Collections.<CIncomeExpenseTransactionItem>emptyList(), user, categories);
    }

    public CExpenseTableView(List<CIncomeExpenseTransactionItem> items, CUser user, List<CCategory> categories)
    {
        super(new CExpenseTableModel(items, user, categories.get(0)));
        setAutoCreateColumnsFromModel(true);

        getColumnByName(CExpenseTableModel.CATEGORY_COLUMN)
                .setCellEditor(new CTableComboBox<>(categories.toArray(new CCategory[categories.size()])));
    }

    @Override
    public CExpenseTableModel getModel()
    {
        return (CExpenseTableModel) super.getModel();
    }

    @Override
    protected String getTableName()
    {
        return "expense_items";
    }
}
