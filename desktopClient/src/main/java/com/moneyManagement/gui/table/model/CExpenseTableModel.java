package com.moneyManagement.gui.table.model;

import com.moneyManagement.data.CCategory;
import com.moneyManagement.data.CIncomeExpenseTransactionItem;
import com.moneyManagement.data.CUser;
import com.moneyManagement.gui.table.cell.CTableItem;

import java.util.Arrays;
import java.util.List;

public class CExpenseTableModel extends CTableModel<CIncomeExpenseTransactionItem>
{
    public static final String CATEGORY_COLUMN = "CATEGORY_COLUMN";
    public static final String EXPENSE_REASON_COLUMN = "EXPENSE_REASON_COLUMN";
    public static final String SIZE_COLUMN = "SIZE_COLUMN";

    private CCategory defaultCategory;

    public CExpenseTableModel(List<CIncomeExpenseTransactionItem> alreadyExistedEntities, CUser user, CCategory defaultCategory)
    {
        super(alreadyExistedEntities, user, Arrays.asList(STATUS_COLUMN,
                CATEGORY_COLUMN,
                EXPENSE_REASON_COLUMN,
                SIZE_COLUMN,
                EDIT_COLUMN,
                ADD_REMOVE_COLUMN));

        this.defaultCategory = defaultCategory;
    }

    @Override
    public String getColumnName(String columnName)
    {
        switch (columnName)
        {
            case CATEGORY_COLUMN:
            {
                return "Category";
            }
            case EXPENSE_REASON_COLUMN:
            {
                return "Expense reason";
            }
            case SIZE_COLUMN:
            {
                return "Size";
            }
        }

        return super.getColumnName(columnName);
    }

    @Override
    public Class<?> getColumnClass(String columnName)
    {
        switch (columnName)
        {
            case CATEGORY_COLUMN:
            {
                return CCategory.class;
            }
            case EXPENSE_REASON_COLUMN:
            {
                return String.class;
            }
            case SIZE_COLUMN:
            {
                return Double.class;
            }
        }
        return super.getColumnClass(columnName);
    }

    @Override
    public Object getValueAt(int row, String columnName)
    {
        if (SIZE_COLUMN.equals(columnName) && row == rows.size())
        {
            double sum = 0;

            for (int rowIndex = 0; rowIndex < getRowCount(); rowIndex++)
            {
                sum += (Double) getValueAt(rowIndex, columnName);
            }

            return sum;
        }
        else if (row >= rows.size())
        {
            return super.getValueAt(row, columnName);
        }

        CIncomeExpenseTransactionItem expenseItem = rows.get(row).getValue();

        switch (columnName)
        {
            case CATEGORY_COLUMN:
            {
                return expenseItem.getCategory();
            }
            case EXPENSE_REASON_COLUMN:
            {
                return expenseItem.getComment();
            }
            case SIZE_COLUMN:
            {
                return expenseItem.getAmount();
            }
        }

        return super.getValueAt(row, columnName);
    }

    @Override
    public void setValueAt(Object aValue, int rowIdx, String columnName)
    {
        CTableItem<CIncomeExpenseTransactionItem> row = rows.get(rowIdx);
        CIncomeExpenseTransactionItem.CBuilder expenseItemBuilder = new CIncomeExpenseTransactionItem.CBuilder(row.getValue());

        switch (columnName)
        {
            case CATEGORY_COLUMN:
            {
                expenseItemBuilder.setCategory((CCategory) aValue);
                break;
            }
            case EXPENSE_REASON_COLUMN:
            {
                if (aValue instanceof String && ((String) aValue).isEmpty())
                {
                    return;
                }

                expenseItemBuilder.setComment((String) aValue);
                break;
            }
            case SIZE_COLUMN:
            {
                if (aValue instanceof Double && ((Double) aValue) < 1)
                {
                    return;
                }

                expenseItemBuilder.setAmount((Double) aValue);
                break;
            }
        }

        rows.get(rowIdx).setValue(expenseItemBuilder.build());

        super.setValueAt(aValue, rowIdx, columnName);
    }

    @Override
    protected boolean isCellEditable(int row, String column) {
        switch (column){
            case CATEGORY_COLUMN:
            case EXPENSE_REASON_COLUMN:
            case SIZE_COLUMN:{
                return row < getRowCount() &&
                        (CTableItem.EDataState.NEW.equals(rows.get(row).getDataState()) ||
                                CTableItem.EDataState.UPDATED.equals(rows.get(row).getDataState()));
            }
        }
        return super.isCellEditable(row, column);
    }

    @Override
    public void addRow()
    {
        addRow(true, new CIncomeExpenseTransactionItem.CBuilder()
                .setCategory(defaultCategory)
                .setComment("expense")
                .setAmount(1.0)
                .build());
    }
}
