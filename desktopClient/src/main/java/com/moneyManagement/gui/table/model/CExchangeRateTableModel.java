package com.moneyManagement.gui.table.model;

import com.moneyManagement.data.CExchangeRate;
import com.moneyManagement.data.CUser;

import java.util.Arrays;
import java.util.Date;

public class CExchangeRateTableModel extends CTableModel<CExchangeRate>
{
    public final static String CURRENCY_NAME_COLUMN = "CURRENCY_NAME_COLUMN";
    public final static String FIRST_CURRENCY_COLUMN = "FIRST_CURRENCY_COLUMN";
    public final static String SECOND_CURRENCY_COLUMN = "SECOND_CURRENCY_COLUMN";

    public CExchangeRateTableModel(CExchangeRate exchangeRate, CUser user)
    {
        super(Arrays.asList(new CExchangeRate.CBuilder(exchangeRate)
                            .build(),
                            new CExchangeRate.CBuilder(exchangeRate)
                            .setFirstCurrency(exchangeRate.getSecondCurrency())
                            .setSecondCurrency(exchangeRate.getFirstCurrency())
                            .setExchangeRate(1 / exchangeRate.getExchangeRate())
                            .build()),
                            user,
                            Arrays.asList(CURRENCY_NAME_COLUMN, FIRST_CURRENCY_COLUMN, SECOND_CURRENCY_COLUMN));
    }

    public CExchangeRate getExchangeRate(){
        return getRows().get(0);
    }

    @Override
    public Object getValueAt(int row, String columnName)
    {
        CExchangeRate exchangeRate = rows.get(row).getValue();
        switch (columnName)
        {
            case CURRENCY_NAME_COLUMN:
            {
                return exchangeRate.getFirstCurrency();
            }
            case FIRST_CURRENCY_COLUMN:
            {
                switch (row)
                {
                    case 0:
                    {
                        return 1;
                    }
                    case 1:
                    {
                        return exchangeRate.getExchangeRate();
                    }
                }
            }
            case SECOND_CURRENCY_COLUMN:
            {
                switch (row)
                {
                    case 0:
                    {
                        return exchangeRate.getExchangeRate();
                    }
                    case 1:
                    {
                        return 1;
                    }
                }
            }
        }
        return super.getValueAt(row, columnName);
    }

    @Override
    public void setValueAt(Object aValue, int rowIdx, String columnName)
    {
        switch (columnName)
        {
            case FIRST_CURRENCY_COLUMN:
            {
                if (rowIdx == 1 && ((Double) aValue) > 0)
                {
                    setExchangeRate(new CExchangeRate.CBuilder(getExchangeRate())
                            .setExchangeRate(1 / (Double) aValue)
                            .build());

                    fireTableDataChanged();
                }
            }
            case SECOND_CURRENCY_COLUMN:
            {
                if (rowIdx == 0 && ((Double) aValue) > 0)
                {
                    setExchangeRate(new CExchangeRate.CBuilder(getExchangeRate())
                            .setExchangeRate((Double) aValue)
                            .build());

                    fireTableDataChanged();
                }
            }
        }
    }

    private void setExchangeRate(CExchangeRate exchangeRate){
        rows.get(0).setValue(new CExchangeRate.CBuilder(exchangeRate)
                .build());
        rows.get(1).setValue(new CExchangeRate.CBuilder(exchangeRate)
                .setFirstCurrency(exchangeRate.getSecondCurrency())
                .setSecondCurrency(exchangeRate.getFirstCurrency())
                .setExchangeRate(1 / exchangeRate.getExchangeRate())
                .build());
    }

    @Override
    public void addRow() {

    }

    @Override
    public String getColumnName(String columnName)
    {
        switch (columnName)
        {
            case CURRENCY_NAME_COLUMN:
            {
                return "";
            }
            case FIRST_CURRENCY_COLUMN:
            {
                return rows.get(0).getValue().getFirstCurrency();
            }
            case SECOND_CURRENCY_COLUMN:
            {
                return rows.get(1).getValue().getFirstCurrency();
            }
        }

        return super.getColumnName(columnName);
    }

    @Override
    public boolean isCellEditable(int rowIdx, String columnName)
    {
        switch (columnName)
        {
            case FIRST_CURRENCY_COLUMN:
            {
                return rowIdx == 1;
            }
            case SECOND_CURRENCY_COLUMN:
            {
                return rowIdx == 0;
            }
        }

        return false;
    }

    @Override
    public Class<?> getColumnClass(String columnName)
    {
        switch (columnName)
        {
            case CURRENCY_NAME_COLUMN:
            {
                return String.class;
            }
            case FIRST_CURRENCY_COLUMN:
            {
                return Double.class;
            }
            case SECOND_CURRENCY_COLUMN:
            {
                return Double.class;
            }
        }
        return super.getColumnClass(columnName);
    }

    public void setFirstCurrencyName(String newCurrency)
    {
        setExchangeRate(new CExchangeRate.CBuilder(getExchangeRate())
                .setFirstCurrency(newCurrency)
                .build());
        fireTableStructureChanged();
    }

    public void setSecondCurrencyName(String newCurrency)
    {
        setExchangeRate(new CExchangeRate.CBuilder(getExchangeRate())
                .setSecondCurrency(newCurrency)
                .build());
        fireTableStructureChanged();
    }

    public void setEffectiveDate(Date newEffectiveDate)
    {
        setExchangeRate(new CExchangeRate.CBuilder(getExchangeRate())
                .setEffectiveDate(newEffectiveDate)
                .build());
    }
}
