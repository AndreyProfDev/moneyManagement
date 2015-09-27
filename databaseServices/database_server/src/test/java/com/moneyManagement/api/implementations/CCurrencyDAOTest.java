package com.moneyManagement.api.implementations;

import org.junit.*;
import org.springframework.beans.factory.annotation.*;

import java.util.*;

import static org.junit.Assert.*;

public class CCurrencyDAOTest extends CAbstractDAOTest {

    @Autowired
    private CCurrencyDAO currencyDAO;

    @Test
    public void test(){
        currencyDAO.create("EUR");
        currencyDAO.create("RUB");
        currencyDAO.create("JPY");

        Set<String> currencies = currencyDAO.loadAll();


        assertTrue(currencies.contains("EUR"));
        assertTrue(currencies.contains("RUB"));
        assertTrue(currencies.contains("JPY"));

        currencyDAO.remove("EUR");
        currencies = currencyDAO.loadAll();

        assertEquals(2, currencies.size());
        assertTrue(currencies.contains("RUB"));
        assertTrue(currencies.contains("JPY"));

        currencyDAO.remove("RUB");
        currencyDAO.remove("JPY");

        assertEquals(0, currencyDAO.loadAll().size());
    }
}
