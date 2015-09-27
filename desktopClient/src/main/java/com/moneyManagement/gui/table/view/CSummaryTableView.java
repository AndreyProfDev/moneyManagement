package com.moneyManagement.gui.table.view;

import com.moneyManagement.data.CIncomeExpenseTransaction;
import com.moneyManagement.data.CTransaction;
import com.moneyManagement.data.CTransferTransaction;
import com.moneyManagement.data.CUser;
import com.moneyManagement.data.ECategoryType;
import com.moneyManagement.gui.table.cell.CTableButton;
import com.moneyManagement.gui.table.model.CCategoriesTableModel;
import com.moneyManagement.gui.table.model.CSummaryTableModel;

import javax.swing.table.*;
import java.awt.event.*;
import java.util.Collections;
import java.util.List;

public abstract class CSummaryTableView extends CTableView
{
    public CSummaryTableView(CUser user)
    {
        super(new CSummaryTableModel(Collections.<CTransaction>emptyList(), user), false);
        setAutoCreateColumnsFromModel(true);

        TableColumn dateColumn = getColumnByName(CSummaryTableModel.TRANSACTION_DATE_COLUMN);
        dateColumn.setMaxWidth(130);
        dateColumn.setMinWidth(130);

        TableColumn typeColumn = getColumnByName(CSummaryTableModel.TRANSACTION_TYPE_COLUMN);
        typeColumn.setMaxWidth(150);
        typeColumn.setMinWidth(150);

        CTableButton editRowTableButton = new CTableButton(this)
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                int rowIndex = table.convertRowIndexToModel(table.getEditingRow());
                fireEditingStopped();

                CTransaction transaction = getModel().getRow(rowIndex);

                if (transaction instanceof CIncomeExpenseTransaction &&
                    ECategoryType.INCOME.equals(((CIncomeExpenseTransaction) transaction).getCategory()))
                {
                    displayIncomeTransactionProfile((CIncomeExpenseTransaction) transaction);
                }
                else if (transaction instanceof CIncomeExpenseTransaction &&
                        ECategoryType.EXPENSE.equals(((CIncomeExpenseTransaction) transaction).getCategory()))
                {
                    displayExpenseTransactionProfile((CIncomeExpenseTransaction) transaction);
                }
                else if (transaction instanceof CTransferTransaction)
                {
                    displayTransferTransactionProfile((CTransferTransaction) transaction);
                }

                repaint();
            }
        };

        TableColumn editColumn = getColumnByName(CCategoriesTableModel.EDIT_COLUMN);
        editColumn.setCellRenderer(editRowTableButton);
        editColumn.setCellEditor(editRowTableButton);
    }

    public void setRows(List<CTransaction> rows)
    {
        getModel().setRows(rows);
    }

    @Override
    public CSummaryTableModel getModel()
    {
        return (CSummaryTableModel) super.getModel();
    }

    @Override
    protected String getTableName()
    {
        return "transactions_summary";
    }

    public abstract void displayIncomeTransactionProfile(CIncomeExpenseTransaction transaction);

    public abstract void displayExpenseTransactionProfile(CIncomeExpenseTransaction transaction);

    public abstract void displayTransferTransactionProfile(CTransferTransaction transaction);
}
