package com.moneyManagement.gui.table.view;

import com.moneyManagement.data.CCategory;
import com.moneyManagement.data.CIncomeExpenseTransactionItem;
import com.moneyManagement.data.CUser;
import com.moneyManagement.gui.table.cell.CTableComboBox;
import com.moneyManagement.gui.table.model.CIncomeTableModel;

import java.util.Collections;
import java.util.List;

public class CIncomeTableView extends CTableView
{
    private CCategory defaultCategory;

    public CIncomeTableView(CUser user, List<CCategory> categories)
    {
        this(Collections.<CIncomeExpenseTransactionItem>emptyList(), user, categories);
    }

    public CIncomeTableView(List<CIncomeExpenseTransactionItem> items, CUser user, List<CCategory> categories)
    {
        super(new CIncomeTableModel(items, user, categories.get(0)));

        this.defaultCategory = categories.get(0);
        setAutoCreateColumnsFromModel(true);

        getColumnByName(CIncomeTableModel.CATEGORY_COLUMN)
                .setCellEditor(new CTableComboBox<>(categories.toArray(new CCategory[categories.size()])));
    }

    @Override
    protected String getTableName()
    {
        return "income_items";
    }

    @Override
    public CIncomeTableModel getModel()
    {
        return (CIncomeTableModel) super.getModel();
    }
}
