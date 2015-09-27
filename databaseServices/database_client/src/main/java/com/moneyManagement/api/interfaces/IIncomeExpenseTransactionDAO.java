package com.moneyManagement.api.interfaces;

import com.moneyManagement.data.*;

import java.util.*;

public interface IIncomeExpenseTransactionDAO
{
    CIncomeExpenseTransaction create(final CIncomeExpenseTransaction newTransaction);

    void update(CIncomeExpenseTransaction newTransaction);

    void remove(CIncomeExpenseTransaction transaction);

    List<CIncomeExpenseTransaction> loadAll(CUser user, Date startDate, Date endDate);

    List<CIncomeExpenseTransaction> loadAll(CUser user, Date startDate, Date endDate, ECategoryType categoryType);

    List<CIncomeExpenseTransaction> loadAll(CUser user, CAccount acc, Date startDate, Date endDate);

    int getTransactionsCount(CAccount account);

    CIncomeExpenseTransaction loadById(final int transactionId);
}
