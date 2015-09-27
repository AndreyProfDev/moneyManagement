package com.moneyManagement.api.interfaces;

import com.moneyManagement.data.*;

import java.util.*;

public interface IUserDAO
{
    CUser create(final CUser user);

    CUser loadByLogin(String login);

    List<CUser> loadAll();

    List<CUser> loadAll(List<CUser> excludeUsers);

    CUser loadById(int userId);

    void update(CUser user);

    void remove(CUser user);

    boolean checkExists(String login, String password);
}
