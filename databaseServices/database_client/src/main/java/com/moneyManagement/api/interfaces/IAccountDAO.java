package com.moneyManagement.api.interfaces;

import com.moneyManagement.data.*;

import java.util.*;

public interface IAccountDAO
{
    CAccount create(final CAccount account);

    void update(CAccount account);

    void remove(CAccount account);

    List<CAccount> loadAccounts(CUser user, EAccountType accountType);

    CAccount loadAccount(int id);

    List<CAccount> loadAccounts(CUser user);

    List<CAccount> loadAccounts(CUser user, boolean participateInPlanning);

    List<CAccount> fillAccountsFromRows(List<Map<String, Object>> rows);

    boolean checkAccountsExists(CUser user);
}
