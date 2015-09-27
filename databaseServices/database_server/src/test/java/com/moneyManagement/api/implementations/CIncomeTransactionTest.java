package com.moneyManagement.api.implementations;

import com.moneyManagement.api.implementations.*;
import com.moneyManagement.data.*;
import org.junit.*;
import org.springframework.beans.factory.annotation.*;

import java.util.*;

import static junit.framework.Assert.*;

public class CIncomeTransactionTest extends CAbstractDAOTest
{
    @Autowired
    private CUserDAO userDAO;

    @Autowired
    private CAccountDAO accountDAO;

    @Autowired
    private CCategoryDAO categoryDAO;

    @Autowired
    private CIncomeExpenseTransactionDAO incomeExpenseTransactionDAO;

    private CUser user;
    private CAccount account;
    private CAccount account1;
    private CAccount sharedAccount;
    private CCategory category1;
    private CCategory category2;
    private CCategory category3;
    private Date today;
    private Date futureDate;

    private CIncomeExpenseTransactionItem item1;
    private CIncomeExpenseTransactionItem item2;
    private CIncomeExpenseTransactionItem item3;
    private CIncomeExpenseTransaction incomeTransaction;

    private CIncomeExpenseTransactionItem item4;
    private CIncomeExpenseTransactionItem item5;
    private CIncomeExpenseTransactionItem item6;
    private CIncomeExpenseTransaction incomeTransaction1;

    @Before
    public void before()
    {
        today = new Date();

        Calendar calendar = new GregorianCalendar();
        calendar.setTime(today);
        calendar.add(Calendar.DAY_OF_YEAR, 3);
        futureDate = new Date(calendar.getTime().getTime());

        /**
         * Create user
         */
        user = userDAO.create(new CUser.CBuilder()
                .setLogin(UUID.randomUUID().toString())
                .setPassword("MyPassword1^")
                .setBaseCurrency("DOL")
                .build());

        /**
         * Create accounts
         */
        account = accountDAO.create(new CAccount.CBuilder()
                .setBalance(1000)
                .setCurrency("DOL")
                .setIncludeInPlanning(false)
                .setName(UUID.randomUUID().toString())
                .setType(EAccountType.CASH)
                .setUserId(user.getId())
                .build());

        account1 = accountDAO.create(new CAccount.CBuilder()
                .setBalance(10000)
                .setCurrency("DOL")
                .setIncludeInPlanning(false)
                .setName(UUID.randomUUID().toString())
                .setType(EAccountType.CASH)
                .setUserId(user.getId())
                .build());

        sharedAccount = accountDAO.create(new CAccount.CBuilder()
                .setBalance(500)
                .setCurrency("EUR")
                .setIncludeInPlanning(false)
                .setName(UUID.randomUUID().toString())
                .setType(EAccountType.CASH)
                .build());

        /**
         * Create categories
         */
        category1 = categoryDAO.create(new CCategory.CBuilder()
                .setName(UUID.randomUUID().toString())
                .setType(ECategoryType.EXPENSE)
                .setUserId(user.getId())
                .build());

        category2 = categoryDAO.create(new CCategory.CBuilder()
                .setName(UUID.randomUUID().toString())
                .setType(ECategoryType.EXPENSE)
                .setUserId(user.getId())
                .build());

        category3 = categoryDAO.create(new CCategory.CBuilder()
                .setName(UUID.randomUUID().toString())
                .setType(ECategoryType.EXPENSE)
                .setUserId(user.getId())
                .build());

        /**
         * Create items for account Single
         */
        item1 = new CIncomeExpenseTransactionItem.CBuilder()
                .setAmount(444.0)
                .setCategory(category1)
                .build();

        item2 = new CIncomeExpenseTransactionItem.CBuilder()
                .setAmount(111.0)
                .setCategory(category2)
                .build();

        item3 = new CIncomeExpenseTransactionItem.CBuilder()
                .setAmount(222.0)
                .setCategory(category3)
                .build();

        incomeTransaction = new CIncomeExpenseTransaction.CBuilder()
                .setAccountId(account.getId())
                .setCategory(ECategoryType.INCOME)
                .setDate(today)
                .setUserId(user.getId())
                .addItem(item1)
                .addItem(item2)
                .addItem(item3)
                .build();
        incomeExpenseTransactionDAO.create(incomeTransaction);

        /**
         * Create items for account Shared
         */
        item4 = new CIncomeExpenseTransactionItem.CBuilder()
                .setAmount(10.0)
                .setCategory(category1)
                .build();

        item5 = new CIncomeExpenseTransactionItem.CBuilder()
                .setAmount(20.0)
                .setCategory(category2)
                .build();

        item6 = new CIncomeExpenseTransactionItem.CBuilder()
                .setAmount(30.0)
                .setCategory(category3)
                .build();

        incomeTransaction1 = new CIncomeExpenseTransaction.CBuilder()
                .setAccountId(sharedAccount.getId())
                .setCategory(ECategoryType.INCOME)
                .setDate(futureDate)
                .setUserId(user.getId())
                .addItem(item4)
                .addItem(item5)
                .addItem(item6)
                .build();
        incomeExpenseTransactionDAO.create(incomeTransaction1);
    }

    @Test
    public void testCreateTransaction()
    {
        /**
         * Check, that system is stable
         */
        account = accountDAO.loadAccount(account.getId());
        assertEquals(1777.0, account.getBalance());

        sharedAccount = accountDAO.loadAccount(sharedAccount.getId());
        assertEquals(560.0, sharedAccount.getBalance());

        assertEquals(incomeExpenseTransactionDAO.getTransactionsCount(account), 1);
        assertEquals(incomeExpenseTransactionDAO.getTransactionsCount(account1), 0);
        assertEquals(incomeExpenseTransactionDAO.getTransactionsCount(sharedAccount), 1);

        /**
         * Check, that transactions are loaded successfully
         */
        CIncomeExpenseTransaction incomeTransaction2 = incomeExpenseTransactionDAO.loadById(incomeTransaction.getId());
        assertEquals(incomeTransaction, incomeTransaction2);
        assertEquals(incomeTransaction.getId(), incomeTransaction2.getId());

        List<CIncomeExpenseTransaction> incomeTrs = incomeExpenseTransactionDAO.loadAll(user, today, today);
        assertEquals(1, incomeTrs.size());
        assertEquals(incomeTransaction, incomeTrs.get(0));
        assertEquals(incomeTransaction.getId(), incomeTrs.get(0).getId());

        incomeTrs = incomeExpenseTransactionDAO.loadAll(user, today, today, ECategoryType.INCOME);
        assertEquals(1, incomeTrs.size());
        assertEquals(incomeTransaction, incomeTrs.get(0));
        assertEquals(incomeTransaction.getId(), incomeTrs.get(0).getId());

        incomeTrs = incomeExpenseTransactionDAO.loadAll(user, account, today, today);
        assertEquals(1, incomeTrs.size());
        assertEquals(incomeTransaction, incomeTrs.get(0));
        assertEquals(incomeTransaction.getId(), incomeTrs.get(0).getId());

        incomeTrs = incomeExpenseTransactionDAO.loadAll(user, account, futureDate, futureDate);
        assertEquals(0, incomeTrs.size());

        /**
         * Check, that transaction update works if account is changed
         */
        CIncomeExpenseTransaction incomeTransaction3 = new CIncomeExpenseTransaction.CBuilder(incomeTransaction)
                .setDate(futureDate)
                .setAccountId(account1.getId())
                .build();

        incomeTransaction.getItems().remove(incomeTransaction.getItems().indexOf(item1));
        incomeExpenseTransactionDAO.update(incomeTransaction3);

        incomeTransaction = incomeExpenseTransactionDAO.loadById(incomeTransaction.getId());
        assertEquals(incomeTransaction.getItems().size(), 2);
        assertFalse(incomeTransaction.getItems().contains(item1));
        assertTrue(incomeTransaction.getItems().contains(item2));
        assertTrue(incomeTransaction.getItems().contains(item3));
        assertEquals(incomeTransaction.getDate(), futureDate);
        assertEquals(incomeTransaction.getAccountId(), account1.getId().intValue());

        account1 = accountDAO.loadAccount(account1.getId());
        assertEquals(10333.0, account1.getBalance());

        account = accountDAO.loadAccount(account.getId());
        assertEquals(1000.0, account.getBalance());

        assertEquals(0, incomeExpenseTransactionDAO.getTransactionsCount(account));
        assertEquals(1, incomeExpenseTransactionDAO.getTransactionsCount(account1));

        /**
         * Check, that transaction update works if account is not changed
         */
        item1.setId(null);
        CIncomeExpenseTransaction incomeTransaction4 = new CIncomeExpenseTransaction.CBuilder(incomeTransaction)
                .setDate(futureDate)
                .setAccountId(account1.getId())
                .addItem(item1)
                .build();
        incomeExpenseTransactionDAO.update(incomeTransaction4);

        incomeTransaction = incomeExpenseTransactionDAO.loadById(incomeTransaction.getId());
        assertEquals(3, incomeTransaction.getItems().size());
        assertTrue(incomeTransaction.getItems().contains(item1));
        assertTrue(incomeTransaction.getItems().contains(item2));
        assertTrue(incomeTransaction.getItems().contains(item3));
        assertEquals(incomeTransaction.getDate(), futureDate);
        assertEquals(incomeTransaction.getAccountId(), account1.getId().intValue());

        account1 = accountDAO.loadAccount(account1.getId());
        assertEquals(10777.0, account1.getBalance());

        incomeExpenseTransactionDAO.remove(incomeTransaction);

        account1 = accountDAO.loadAccount(account1.getId());
        assertEquals(10000.0, account1.getBalance());

        assertEquals(0 , incomeExpenseTransactionDAO.getTransactionsCount(account1));
    }

    @After
    public void after(){
        incomeExpenseTransactionDAO.remove(incomeTransaction1);

        categoryDAO.remove(category1);
        categoryDAO.remove(category2);
        categoryDAO.remove(category3);

        accountDAO.remove(account);
        accountDAO.remove(account1);
        accountDAO.remove(sharedAccount);

        userDAO.remove(user);
    }
}
