package com.moneyManagement.api.interfaces;

import com.moneyManagement.data.*;

import java.util.*;

public interface IExchangeRateDAO
{
    CExchangeRate create(final CExchangeRate exchangeRate);

    void update(CExchangeRate exchangeRate);

    void remove(CExchangeRate exchangeRate);

    List<CExchangeRate> loadAll(String currency);

    CExchangeRate loadLast(String firstCurrency, String secondCurrency);
//
//    CExchangeRate loadLast(String currency);

    boolean checkExists(String firstCurrency, String secondCurrency);
}
