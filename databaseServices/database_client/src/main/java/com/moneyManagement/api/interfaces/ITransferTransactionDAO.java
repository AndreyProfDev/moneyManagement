package com.moneyManagement.api.interfaces;

import com.moneyManagement.data.*;

import java.util.*;

public interface ITransferTransactionDAO
{
    CTransferTransaction create(final CTransferTransaction transaction);

    void update(CTransferTransaction newTransaction);

    void remove(CTransferTransaction transaction);

    List<CTransferTransaction> loadAll(CUser user, Date startDate, Date endDate);

    List<CTransferTransaction> loadAllFrom(CUser user, CAccount fromAcc, Date startDate, Date endDate);

    List<CTransferTransaction> loadAllTo(CUser user, CAccount toAcc, Date startDate, Date endDate);

    int getTransactionsCount(CAccount account);

    CTransferTransaction loadById(final int transactionId);
}
