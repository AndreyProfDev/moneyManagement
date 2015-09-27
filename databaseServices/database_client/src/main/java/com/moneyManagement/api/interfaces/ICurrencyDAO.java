package com.moneyManagement.api.interfaces;

import java.util.*;

public interface ICurrencyDAO {
    String create(String currency);

    Set<String> loadAll();

    void remove(String currency);
}
