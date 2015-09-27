package com.moneyManagement.api.implementations;

import com.moneyManagement.api.implementations.*;
import com.moneyManagement.data.*;
import org.junit.*;
import org.springframework.beans.factory.annotation.*;

import java.util.*;


import static junit.framework.Assert.*;


public class CAccountDAOTest extends CAbstractDAOTest
{
    @Autowired
    private CUserDAO userDAO;

    @Autowired
    private CAccountDAO accountDAO;

    @Autowired
    private CExchangeRateDAO exchangeRateDAO;

    private CUser user;
    private CUser user1;
    private CAccount account;
    private CAccount account1;
    private CAccount account2;
    private CExchangeRate exchangeRate;

    @Before
    public void before(){
        user = userDAO.create(new CUser.CBuilder()
                .setLogin(UUID.randomUUID().toString())
                .setPassword("Password1^")
                .setBaseCurrency("DOL")
                .build());

        user1 = userDAO.create(new CUser.CBuilder()
                .setLogin(UUID.randomUUID().toString())
                .setPassword("Password1^")
                .setBaseCurrency("DOL")
                .build());


        account = new CAccount.CBuilder()
                .setBalance(100.0)
                .setName(UUID.randomUUID().toString())
                .setType(EAccountType.CASH)
                .setIncludeInPlanning(false)
                .setCurrency("EUR")
                .setUserId(user.getId())
                .build();

        try
        {
            account = accountDAO.create(account);
            fail("Exchange rate check failed");
        }
        catch (Exception ex)
        {
            assertEquals(ex.getLocalizedMessage(), "Account " + account.getName() +" could not be created. " +
                    "There are no exchange rate (DOL, EUR)");
        }

        exchangeRate = exchangeRateDAO.create(new CExchangeRate.CBuilder()
                .setExchangeRate(1.5)
                .setFirstCurrency("EUR")
                .setSecondCurrency("DOL")
                .setEffectiveDate(new Date())
                .setEnterDate(new Date())
                .build());

        account = accountDAO.create(account);

        account1 = accountDAO.create(new CAccount.CBuilder()
                                        .setBalance(100.0)
                                        .setName(UUID.randomUUID().toString())
                                        .setType(EAccountType.CASH)
                                        .setIncludeInPlanning(false)
                                        .setCurrency("DOL")
                                        .setUserId(user1.getId())
                                        .build());

        account2 = accountDAO.create(new CAccount.CBuilder()
                                        .setBalance(100.0)
                                        .setName(UUID.randomUUID().toString())
                                        .setType(EAccountType.BANK_CARD)
                                        .setIncludeInPlanning(true)
                                        .setCurrency("DOL")
                                        .setUserId(user1.getId())
                                        .build());
    }

    @Test
    public void test() {
        /**
         * Check that loading accounts works fine
         */
        CAccount account3 = accountDAO.loadAccount(account.getId());
        assertEquals(account, account3);
        assertEquals(account.getId(), account3.getId());

        List<CAccount> accounts = accountDAO.loadAccounts(user);
        assertEquals(1, accounts.size());
        assertEquals(account, accounts.get(0));
        assertEquals(account.getId(), accounts.get(0).getId());

        accounts = accountDAO.loadAccounts(user1, true);
        assertEquals(1, accounts.size());
        assertEquals(account2, accounts.get(0));
        assertEquals(account2.getId(), accounts.get(0).getId());

        accounts = accountDAO.loadAccounts(user1, EAccountType.BANK_CARD);
        assertEquals(1, accounts.size());
        assertEquals(account2, accounts.get(0));
        assertEquals(account2.getId(), accounts.get(0).getId());

        assertTrue(accountDAO.checkAccountsExists(user));

        CAccount account4 = new CAccount.CBuilder(account)
                .setBalance(101.0)
                .setCurrency("EUR")
                .setIncludeInPlanning(true)
                .setName(UUID.randomUUID().toString())
                .setType(EAccountType.BANK_CARD)
                .setUserId(null)
                .build();

        accountDAO.update(account4);

        CAccount account5 = accountDAO.loadAccount(account.getId());
        assertNull(account5.getUserId());
        assertEquals(account4.getName(), account5.getName());
        assertEquals(EAccountType.CASH, account5.getType());
        assertEquals("EUR", account5.getCurrency());
        assertEquals(101.0, account5.getBalance());
        assertTrue(account5.isIncludeInPlanning());
        assertEquals(account4.getId(), account5.getId());


        CAccount account6 = new CAccount.CBuilder(account)
                .setName(UUID.randomUUID().toString())
                .build();
        accountDAO.update(account6);

        CAccount account7 = accountDAO.loadAccount(account.getId());
        assertEquals(account6, account7);
        assertEquals(account6.getId(), account7.getId());

        accountDAO.remove(account7);

        assertFalse(accountDAO.checkAccountsExists(user));
    }

    @After
    public void after(){
        userDAO.remove(user);
        userDAO.remove(user1);
        accountDAO.remove(account);
        accountDAO.remove(account1);
        accountDAO.remove(account2);
        exchangeRateDAO.remove(exchangeRate);
    }
}
