package com.moneyManagement.api.interfaces;

import com.moneyManagement.data.*;

import java.util.*;

public interface IIncomeExpenseTransactionItemDAO
{
    CIncomeExpenseTransactionItem create(final CIncomeExpenseTransactionItem item, final CIncomeExpenseTransaction parentTransaction);

    void update(CIncomeExpenseTransactionItem item, CIncomeExpenseTransaction parentTransaction);

    void remove(CIncomeExpenseTransactionItem item);

    List<CIncomeExpenseTransactionItem> loadTransactionItems(int transactionId);

    int getTransactionsCount(CCategory category);
}
