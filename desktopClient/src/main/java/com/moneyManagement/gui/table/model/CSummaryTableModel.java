package com.moneyManagement.gui.table.model;

import com.moneyManagement.data.CIncomeExpenseTransaction;
import com.moneyManagement.data.CTransaction;
import com.moneyManagement.data.CTransferTransaction;
import com.moneyManagement.data.CUser;
import com.moneyManagement.gui.table.cell.CTableItem;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class CSummaryTableModel extends CTableModel<CTransaction>
{
    public static final String TRANSACTION_DATE_COLUMN = "TRANSACTION_DATE_COLUMN";
    public static final String TRANSACTION_TYPE_COLUMN = "TRANSACTION_TYPE_COLUMN";
    public static final String TRANSACTION_SUMMARY_COLUMN = "TRANSACTION_SUMMARY_COLUMN";

    public CSummaryTableModel(List<CTransaction> alreadyExistedTrs, CUser user)
    {
        super(alreadyExistedTrs, user, Arrays.asList(TRANSACTION_DATE_COLUMN,
                                                    TRANSACTION_TYPE_COLUMN,
                                                    TRANSACTION_SUMMARY_COLUMN,
                                                    EDIT_COLUMN));
    }

    public CTransaction getRow(int index)
    {
        return rows.get(index).getValue();
    }

    @Override
    public Object getValueAt(int row, String column)
    {
        if (row >= getRowCount())
        {
            return super.getValueAt(row, column);
        }

        CTransaction transaction = rows.get(row).getValue();

        switch (column)
        {
            case TRANSACTION_DATE_COLUMN:
            {
                return transaction.getDate();
            }
            case TRANSACTION_TYPE_COLUMN:
            {
                return transaction.getTransactionName();
            }
            case TRANSACTION_SUMMARY_COLUMN:
            {
                if (transaction instanceof CIncomeExpenseTransaction)
                {
                    return ((CIncomeExpenseTransaction) transaction).getSum();
                }
                else if (transaction instanceof CTransferTransaction)
                {
                    return ((CTransferTransaction) transaction).getTransferAmount();
                }
                else
                {
                    return null;
                }
            }
        }

        return super.getValueAt(row, column);
    }

    @Override
    public String getColumnName(String columnName)
    {
        switch (columnName)
        {
            case TRANSACTION_DATE_COLUMN:
            {
                return "Transaction date";
            }
            case TRANSACTION_TYPE_COLUMN:
            {
                return "Transaction type";
            }
            case TRANSACTION_SUMMARY_COLUMN:
            {
                return "Transaction summary";
            }
        }

        return super.getColumnName(columnName);
    }

    @Override
    public boolean isCellEditable(int row, String columnName)
    {
        switch (columnName){
            case TRANSACTION_DATE_COLUMN:
            case TRANSACTION_TYPE_COLUMN:
            case TRANSACTION_SUMMARY_COLUMN:{
                return false;
            }
//            case EDIT_COLUMN:{
//                return true;
//            }
        }
        return super.isCellEditable(row, columnName);
    }

    @Override
    public Class<?> getColumnClass(String columnName)
    {
        switch (columnName)
        {
            case TRANSACTION_DATE_COLUMN:
            {
                return Date.class;
            }
            case TRANSACTION_TYPE_COLUMN:
            {
                return String.class;
            }
            case TRANSACTION_SUMMARY_COLUMN:
            {
                return Double.class;
            }
        }
        return super.getColumnClass(columnName);
    }

    @Override
    public void addRow() {

    }
}
