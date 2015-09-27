package com.moneyManagement.gui.table.model;

import com.moneyManagement.data.CPlanItemResult;
import com.moneyManagement.data.CUser;

import javax.swing.table.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class CPlanResultTableModel extends CTableModel<CPlanItemResult>
{
    public static final String PLAN_DATE_COLUMN = "PLAN_DATE_COLUMN";
    public static final String PLAN_AMOUNT_COLUMN = "PLAN_AMOUNT_COLUMN";
    public static final String PLAN_RESULT_COLUMN = "PLAN_RESULT_COLUMN";

    public CPlanResultTableModel(List<CPlanItemResult> alreadyExistedPlans, CUser user)
    {
        super(alreadyExistedPlans, user, Arrays.asList(PLAN_DATE_COLUMN, PLAN_AMOUNT_COLUMN, PLAN_RESULT_COLUMN));
    }

    @Override
    public Object getValueAt(int rowIdx, String columnName)
    {
        if (rowIdx >= rows.size())
        {
            return super.getValueAt(rowIdx, columnName);
        }

        CPlanItemResult plan = rows.get(rowIdx).getValue();

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
            case PLAN_RESULT_COLUMN:
            {
                return plan.getPlanResult();
            }
        }

        return super.getValueAt(rowIdx, columnName);
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
            case PLAN_RESULT_COLUMN:
            {
                return "Actual assets";
            }
        }

        return super.getColumnName(columnName);
    }

    @Override
    public boolean isCellEditable(int rowIdx, String columnName)
    {
        return false;
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
            case PLAN_RESULT_COLUMN:
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
