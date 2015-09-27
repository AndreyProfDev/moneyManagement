package com.moneyManagement.gui.table.model;

import com.moneyManagement.data.CExchangeRate;
import com.moneyManagement.data.CUser;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class CExchangeRateSummaryTableModel extends CTableModel<CExchangeRate>
{
    public static final String EFFECTIVE_DATE_COLUMN = "EFFECTIVE_DATE_COLUMN";
    public static final String FIRST_CURRENCY_COLUMN = "FIRST_CURRENCY_COLUMN";
    public static final String SECOND_CURRENCY_COLUMN = "SECOND_CURRENCY_COLUMN";
    public static final String EXCHANGE_RATE_COLUMN = "EXCHANGE_RATE_COLUMN";

    public CExchangeRateSummaryTableModel(List<CExchangeRate> exchangeRates, CUser user)
    {
        super(exchangeRates, user, Arrays.asList(EFFECTIVE_DATE_COLUMN,
                                                FIRST_CURRENCY_COLUMN,
                                                SECOND_CURRENCY_COLUMN,
                                                EXCHANGE_RATE_COLUMN,
                                                EDIT_COLUMN));
        //TODO implement removing
    }

    @Override
    public Object getValueAt(int rowIdx, String columnName)
    {
        if (rowIdx >= rows.size()){
            return super.getValueAt(rowIdx, columnName);
        }

        CExchangeRate exchangeRate = rows.get(rowIdx).getValue();

        switch (columnName)
        {
            case EFFECTIVE_DATE_COLUMN:
            {
                return exchangeRate.getEffectiveDate();
            }
            case FIRST_CURRENCY_COLUMN:
            {
                return exchangeRate.getFirstCurrency();
            }
            case SECOND_CURRENCY_COLUMN:
            {
                return exchangeRate.getSecondCurrency();
            }
            case EXCHANGE_RATE_COLUMN:
            {
                return exchangeRate.getExchangeRate();
            }
        }

        return super.getValueAt(rowIdx, columnName);
    }

    @Override
    public String getColumnName(String columnName)
    {
        switch (columnName)
        {
            case EFFECTIVE_DATE_COLUMN:
            {
                return "Effective date";
            }
            case FIRST_CURRENCY_COLUMN:
            {
                return "First currency";
            }
            case SECOND_CURRENCY_COLUMN:
            {
                return "Second currency";
            }
            case EXCHANGE_RATE_COLUMN:
            {
                return "Exchange rate";
            }
        }

        return null;
    }

    @Override
    public Class<?> getColumnClass(String columnName)
    {
        switch (columnName)
        {
            case EFFECTIVE_DATE_COLUMN:
            {
                return Date.class;
            }
            case FIRST_CURRENCY_COLUMN:
            {
                return String.class;
            }
            case SECOND_CURRENCY_COLUMN:
            {
                return String.class;
            }
            case EXCHANGE_RATE_COLUMN:
            {
                return Double.class;
            }
        }
        return null;
    }

    @Override
    public void addRow() {

    }
}
