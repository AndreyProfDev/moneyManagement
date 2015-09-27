package com.moneyManagement.gui.table.view;

import com.moneyManagement.data.CExchangeRate;
import com.moneyManagement.data.CUser;
import com.moneyManagement.gui.table.model.CExchangeRateTableModel;

import java.util.Date;

public class CExchangeRateTableView extends CTableView
{

    public CExchangeRateTableView(CExchangeRate exchangeRate, CUser user)
    {
        super(new CExchangeRateTableModel(exchangeRate, user), false);
    }

    public void setFirstCurrencyName(String newCurrency)
    {
        getModel().setFirstCurrencyName(newCurrency);
    }

    public void setSecondCurrencyName(String newCurrency)
    {
        getModel().setSecondCurrencyName(newCurrency);
    }

    public void setEffectiveDate(Date newEffectiveDate)
    {
        getModel().setEffectiveDate(newEffectiveDate);
    }

    public CExchangeRate getExchangeRate()
    {
        return getModel().getExchangeRate();
    }

    @Override
    protected String getTableName()
    {
        return "exchangeRate";
    }

    @Override
    public CExchangeRateTableModel getModel()
    {
        return (CExchangeRateTableModel) super.getModel();
    }
}
