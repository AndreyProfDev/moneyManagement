package com.moneyManagement.gui.table.view;

import com.moneyManagement.data.EAccountType;
import com.moneyManagement.gui.table.cell.CTableComboBox;
import com.moneyManagement.gui.table.model.CAccountsTableModel;

import java.util.Set;

public class CAccountsTableView extends CTableView
{
    public CAccountsTableView(CAccountsTableModel model, Set<String> currencies)
    {
        super(model);
        setAutoCreateColumnsFromModel(true);

        getColumnByName(CAccountsTableModel.ACCOUNT_TYPE_COLUMN)
                .setCellEditor(new CTableComboBox<>(EAccountType.values()));

        String[] currenciesArr = new String[currencies.size()];
        currencies.toArray(currenciesArr);
        getColumnByName(CAccountsTableModel.ACCOUNT_CURRENCY_COLUMN)
                .setCellEditor(new CTableComboBox<>(currenciesArr));

    }

    @Override
    public CAccountsTableModel getModel()
    {
        return (CAccountsTableModel) super.getModel();
    }

    @Override
    protected String getTableName()
    {
        return "accounts";
    }
}
