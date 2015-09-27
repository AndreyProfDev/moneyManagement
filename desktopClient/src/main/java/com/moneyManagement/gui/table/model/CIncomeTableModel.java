package com.moneyManagement.gui.table.model;

import com.moneyManagement.data.CCategory;
import com.moneyManagement.data.CIncomeExpenseTransactionItem;
import com.moneyManagement.data.CUser;
import com.moneyManagement.gui.table.cell.CTableItem;

import java.util.Arrays;
import java.util.List;

public class CIncomeTableModel extends CTableModel<CIncomeExpenseTransactionItem>
{
    public static final String CATEGORY_COLUMN = "CATEGORY_COLUMN";
    public static final String SIZE_COLUMN = "SIZE_COLUMN";
    private CCategory defaultCategory;

    public CIncomeTableModel(List<CIncomeExpenseTransactionItem> alreadyExistedEntities, CUser user, CCategory defaultCategory)
    {
        super(alreadyExistedEntities, user, Arrays.asList(STATUS_COLUMN,
                                                            CATEGORY_COLUMN,
                                                            SIZE_COLUMN,
                                                            EDIT_COLUMN,
                                                            ADD_REMOVE_COLUMN));

        this.defaultCategory = defaultCategory;
    }

    @Override
    protected boolean isCellEditable(int row, String column) {
        switch (column){
            case CATEGORY_COLUMN:
            case SIZE_COLUMN:{
                return row < getRowCount() &&
                        (CTableItem.EDataState.NEW.equals(rows.get(row).getDataState()) ||
                        CTableItem.EDataState.UPDATED.equals(rows.get(row).getDataState()));
            }
        }
        return super.isCellEditable(row, column);
    }

    @Override
    public String getColumnName(String columnNAme)
    {
        switch (columnNAme)
        {
            case CATEGORY_COLUMN:
            {
                return "Category";
            }
            case SIZE_COLUMN:
            {
                return "Size";
            }
        }

        return super.getColumnName(columnNAme);
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

        CIncomeExpenseTransactionItem incomeItem = rows.get(row).getValue();

        switch (columnName)
        {
            case CATEGORY_COLUMN:
            {
                return incomeItem.getCategory();
            }
            case SIZE_COLUMN:
            {
                return incomeItem.getAmount();
            }
        }

        return super.getValueAt(row, columnName);
    }

    @Override
    public void setValueAt(Object aValue, int rowIdx, String columnname)
    {
        CTableItem<CIncomeExpenseTransactionItem> row = rows.get(rowIdx);
        CIncomeExpenseTransactionItem.CBuilder incomeItemBuilder = new CIncomeExpenseTransactionItem.CBuilder(row.getValue());

        switch (columnname)
        {
            case CATEGORY_COLUMN:
            {
                incomeItemBuilder.setCategory((CCategory) aValue);
                break;
            }
            case SIZE_COLUMN:
            {
                if (aValue instanceof Double && ((Double) aValue) < 1)
                {
                    return;
                }

                incomeItemBuilder.setAmount((Double) aValue);
                break;
            }
        }

        rows.get(rowIdx).setValue(incomeItemBuilder.build());

        super.setValueAt(aValue, rowIdx, columnname);
    }

    public void addRow()
    {
        addRow(true, new CIncomeExpenseTransactionItem.CBuilder()
                .setAmount(1.0)
                .setCategory(defaultCategory)
                .build());
    }
}
