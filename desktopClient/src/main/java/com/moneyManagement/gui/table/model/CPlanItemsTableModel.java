package com.moneyManagement.gui.table.model;

import com.moneyManagement.data.CPlanItem;
import com.moneyManagement.data.CUser;
import com.moneyManagement.gui.table.cell.CTableItem;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class CPlanItemsTableModel extends CTableModel<CPlanItem>
{
    public static final String PLAN_DATE_COLUMN = "PLAN_DATE_COLUMN";
    public static final String PLAN_AMOUNT_COLUMN = "PLAN_AMOUNT_COLUMN";

    public CPlanItemsTableModel(List<CPlanItem> alreadyExistedPlans, CUser user)
    {
        super(alreadyExistedPlans, user, Arrays.asList(STATUS_COLUMN, PLAN_DATE_COLUMN,
                                                        PLAN_AMOUNT_COLUMN, EDIT_COLUMN, ADD_REMOVE_COLUMN));
    }

    @Override
    public Object getValueAt(int rowIdx, String columnName)
    {
        if (rowIdx >= rows.size())
        {
            return super.getValueAt(rowIdx, columnName);
        }

        CPlanItem plan = rows.get(rowIdx).getValue();

        switch (columnName)
        {
            case PLAN_DATE_COLUMN:
            {
                return plan.getDate();
            }
            case PLAN_AMOUNT_COLUMN:
            {
                return plan.getAmount();
            }
        }

        return super.getValueAt(rowIdx, columnName);
    }

    @Override
    public void setValueAt(Object aValue, int rowIdx, String columnName)
    {
        if (rowIdx >= rows.size())
        {
            return;
        }

        CPlanItem.CBuilder planBuilder = new CPlanItem.CBuilder(rows.get(rowIdx).getValue());

        switch (columnName)
        {
            case PLAN_DATE_COLUMN:
            {
                planBuilder.setDate((Date) aValue);
                break;
            }
            case PLAN_AMOUNT_COLUMN:
            {
                if (aValue instanceof Double && ((Double) aValue) < 0)
                {
                    return;
                }

                planBuilder.setAmount((Double) aValue);
                break;
            }
        }

        rows.get(rowIdx).setValue(planBuilder.build());

        super.setValueAt(aValue, rowIdx, columnName);
    }

    @Override
    public String getColumnName(String columnName)
    {
        switch (columnName)
        {
            case PLAN_DATE_COLUMN:
            {
                return "Date";
            }
            case PLAN_AMOUNT_COLUMN:
            {
                return "Planning assets";
            }
        }

        return super.getColumnName(columnName);
    }

    @Override
    public boolean isCellEditable(int rowIdx, String columnName)
    {
        switch (columnName){
            case PLAN_DATE_COLUMN:
            case PLAN_AMOUNT_COLUMN:{
                return rowIdx < getRowCount() &&
                        (CTableItem.EDataState.NEW.equals(rows.get(rowIdx).getDataState()) ||
                                CTableItem.EDataState.UPDATED.equals(rows.get(rowIdx).getDataState()));
            }
        }
        return super.isCellEditable(rowIdx, columnName);
    }

    @Override
    public Class<?> getColumnClass(String columnName)
    {
        switch (columnName)
        {
            case PLAN_DATE_COLUMN:
            {
                return Date.class;
            }
            case PLAN_AMOUNT_COLUMN:
            {
                return Double.class;
            }
        }
        return super.getColumnClass(columnName);
    }

    public void addRow()
    {
        addRow(true, new CPlanItem.CBuilder()
                .setAmount(0.0)
                .setDate(new Date())
                .setUserId(user.getId())
                .build());
    }
}
