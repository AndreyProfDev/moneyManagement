package com.moneyManagement.gui.table.model;

import com.moneyManagement.data.CCategory;
import com.moneyManagement.data.CUser;
import com.moneyManagement.data.EAccountType;
import com.moneyManagement.data.ECategoryType;
import com.moneyManagement.gui.table.cell.CTableItem;

import java.util.Arrays;
import java.util.List;

import static com.moneyManagement.gui.table.cell.CTableItem.*;

public class CCategoriesTableModel extends CTableModel<CCategory>
{
    public static final String ACCOUNT_TYPE_COLUMN = "ACCOUNT_TYPE_COLUMN";
    public static final String ACCOUNT_NAME_COLUMN = "ACCOUNT_NAME_COLUMN";

    public CCategoriesTableModel(List<CCategory> alreadyExistedCategories, CUser user)
    {
        super(alreadyExistedCategories, user, Arrays.asList(STATUS_COLUMN,
                                                            ACCOUNT_TYPE_COLUMN,
                                                            ACCOUNT_NAME_COLUMN,
                                                            EDIT_COLUMN,
                                                            ADD_REMOVE_COLUMN));
    }

    @Override
    public Object getValueAt(int rowIdx, String columnName)
    {
        if (rowIdx >= rows.size()){
            return super.getValueAt(rowIdx, columnName);
        }

        CCategory category = rows.get(rowIdx).getValue();

        switch (columnName)
        {
            case ACCOUNT_TYPE_COLUMN:
            {
                return category.getType();
            }
            case ACCOUNT_NAME_COLUMN:
            {
                return category.getName();
            }
        }
        return super.getValueAt(rowIdx, columnName);
    }

    @Override
    public void setValueAt(Object aValue, int rowIdx, String columnName)
    {
        CCategory.CBuilder categoryBuilder = new CCategory.CBuilder(rows.get(rowIdx).getValue());

        switch (columnName)
        {
            case ACCOUNT_TYPE_COLUMN:
            {
                categoryBuilder.setType((ECategoryType) aValue);
                break;
            }
            case ACCOUNT_NAME_COLUMN:
            {
                if (aValue instanceof String && ((String) aValue).isEmpty())
                {
                    return;
                }

                categoryBuilder.setName((String) aValue);
                break;
            }
        }

        rows.get(rowIdx).setValue(categoryBuilder.build());

        super.setValueAt(aValue, rowIdx, columnName);
    }

    @Override
    public String getColumnName(String columnName)
    {
        switch (columnName)
        {
            case ACCOUNT_TYPE_COLUMN:
            {
                return "Account Type";
            }
            case ACCOUNT_NAME_COLUMN:
            {
                return "Account name";
            }
        }

        return super.getColumnName(columnName);
    }

    @Override
    public boolean isCellEditable(int row, String columnName)
    {
        switch (columnName){
            case ACCOUNT_TYPE_COLUMN:{
                return row != getRowCount() &&
                        EDataState.NEW.equals(rows.get(row).getDataState());
            }
            case ACCOUNT_NAME_COLUMN:{
                return row != getRowCount() &&
                (EDataState.NEW.equals(rows.get(row).getDataState()) ||
                EDataState.UPDATED.equals(rows.get(row).getDataState()));
            }
        }

        return super.isCellEditable(row, columnName);
    }

    @Override
    public Class<?> getColumnClass(String columnName)
    {
        switch (columnName)
        {
            case ACCOUNT_TYPE_COLUMN:
            {
                return EAccountType.class;
            }
            case ACCOUNT_NAME_COLUMN:
            {
                return String.class;
            }
        }
        return super.getColumnClass(columnName);
    }

    @Override
    public void addRow()
    {
        CCategory newCategory = new CCategory.CBuilder()
                        .setName("new category")
                        .setType(ECategoryType.INCOME)
                        .setUserId(user.getId())
                        .build();

        addRow(true, newCategory);
    }
}
