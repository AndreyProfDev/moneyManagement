package com.moneyManagement.api.implementations;


import com.moneyManagement.api.implementations.*;
import com.moneyManagement.data.*;
import org.junit.*;
import org.springframework.beans.factory.annotation.*;

import java.util.*;

import static junit.framework.Assert.*;

public class CExpenseTransactionTest extends CAbstractDAOTest
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
    private CIncomeExpenseTransaction expenseTransaction;

    private CIncomeExpenseTransactionItem item4;
    private CIncomeExpenseTransactionItem item5;
    private CIncomeExpenseTransactionItem item6;
    private CIncomeExpenseTransaction expenseTransaction1;

    @Before
    public void before()
    {
        today = new Date();

        Calendar calendar = new GregorianCalendar();
        calendar.setTime(today);
        calendar.add(Calendar.DAY_OF_YEAR, 3);
        futureDate = calendar.getTime();

        /**
         * Create user
         */
        user = new CUser.CBuilder()
                .setLogin("MyUser")
                .setPassword("MyPassword1^")
                .setBaseCurrency("DOL")
                .build();

        userDAO.create(user);

        /**
         * Create accounts
         */
        account = new CAccount.CBuilder()
                .setBalance(1000)
                .setCurrency("DOL")
                .setIncludeInPlanning(false)
                .setName("Single")
                .setType(EAccountType.CASH)
                .setUserId(user.getId())
                .build();
        accountDAO.create(account);

        account1 = new CAccount.CBuilder()
                .setBalance(10000)
                .setCurrency("DOL")
                .setIncludeInPlanning(false)
                .setName("Single1")
                .setType(EAccountType.CASH)
                .setUserId(user.getId())
                .build();
        accountDAO.create(account1);

        sharedAccount = new CAccount.CBuilder()
                .setBalance(500)
                .setCurrency("EUR")
                .setIncludeInPlanning(false)
                .setName("Shared")
                .setType(EAccountType.CASH)
                .build();
        accountDAO.create(sharedAccount);

        /**
         * Create categories
         */
        category1 = new CCategory.CBuilder()
                .setName("category 1")
                .setType(ECategoryType.EXPENSE)
                .setUserId(user.getId())
                .build();
        categoryDAO.create(category1);

        category2 = new CCategory.CBuilder()
                .setName("category 2")
                .setType(ECategoryType.EXPENSE)
                .setUserId(user.getId())
                .build();
        categoryDAO.create(category2);

        category3 = new CCategory.CBuilder()
                .setName("category 3")
                .setType(ECategoryType.EXPENSE)
                .setUserId(user.getId())
                .build();
        categoryDAO.create(category3);

        /**
         * Create items for account Single
         */
        item1 = new CIncomeExpenseTransactionItem.CBuilder()
                .setAmount(444.0)
                .setCategory(category1)
                .setComment("reason")
                .build();

        item2 = new CIncomeExpenseTransactionItem.CBuilder()
                .setAmount(111.0)
                .setCategory(category2)
                .setComment("reason")
                .build();

        item3 = new CIncomeExpenseTransactionItem.CBuilder()
                .setAmount(222.0)
                .setCategory(category3)
                .setComment("reason")
                .build();

        expenseTransaction = new CIncomeExpenseTransaction.CBuilder()
                .setAccountId(account.getId())
                .setCategory(ECategoryType.EXPENSE)
                .setDate(today)
                .setUserId(user.getId())
                .addItem(item1)
                .addItem(item2)
                .addItem(item3)
                .build();
        incomeExpenseTransactionDAO.create(expenseTransaction);

        /**
         * Create items for account Shared
         */
        item4 = new CIncomeExpenseTransactionItem.CBuilder()
                .setAmount(10.0)
                .setCategory(category1)
                .setComment("reason")
                .build();

        item5 = new CIncomeExpenseTransactionItem.CBuilder()
                .setAmount(20.0)
                .setCategory(category2)
                .setComment("reason")
                .build();

        item6 = new CIncomeExpenseTransactionItem.CBuilder()
                .setAmount(30.0)
                .setCategory(category3)
                .setComment("reason")
                .build();

        expenseTransaction1 = new CIncomeExpenseTransaction.CBuilder()
                .setAccountId(sharedAccount.getId())
                .setCategory(ECategoryType.EXPENSE)
                .setDate(futureDate)
                .setUserId(user.getId())
                .addItem(item4)
                .addItem(item5)
                .addItem(item6)
                .build();
        incomeExpenseTransactionDAO.create(expenseTransaction1);
    }

    @Test
    public void testCreateTransaction()
    {
        /**
         * Check, that system is stable
         */
        account = accountDAO.loadAccount(account.getId());
        assertEquals(223.0, account.getBalance());

        sharedAccount = accountDAO.loadAccount(sharedAccount.getId());
        assertEquals(440.0, sharedAccount.getBalance());

        assertEquals(incomeExpenseTransactionDAO.getTransactionsCount(account), 1);
        assertEquals(incomeExpenseTransactionDAO.getTransactionsCount(account1), 0);
        assertEquals(incomeExpenseTransactionDAO.getTransactionsCount(sharedAccount), 1);

        /**
         * Check, that transactions are loaded successfully
         */
        CIncomeExpenseTransaction expenseTransaction2 = incomeExpenseTransactionDAO.loadById(expenseTransaction.getId());
        assertEquals(expenseTransaction, expenseTransaction2);
        assertEquals(expenseTransaction.getId(), expenseTransaction2.getId());

        List<CIncomeExpenseTransaction> expenseTrs = incomeExpenseTransactionDAO.loadAll(user, today, today);
        assertEquals(1, expenseTrs.size());
        assertEquals(expenseTransaction, expenseTrs.get(0));
        assertEquals(expenseTransaction.getId(), expenseTrs.get(0).getId());

        expenseTrs = incomeExpenseTransactionDAO.loadAll(user, today, today, ECategoryType.EXPENSE);
        assertEquals(1, expenseTrs.size());
        assertEquals(expenseTransaction, expenseTrs.get(0));
        assertEquals(expenseTransaction.getId(), expenseTrs.get(0).getId());

        expenseTrs = incomeExpenseTransactionDAO.loadAll(user, account, today, today);
        assertEquals(1, expenseTrs.size());
        assertEquals(expenseTransaction, expenseTrs.get(0));
        assertEquals(expenseTransaction.getId(), expenseTrs.get(0).getId());

        expenseTrs = incomeExpenseTransactionDAO.loadAll(user, account, futureDate, futureDate);
        assertEquals(0, expenseTrs.size());

        /**
         * Check, that transaction update works if account is changed
         */
        CIncomeExpenseTransaction expenseTransaction3 = new CIncomeExpenseTransaction.CBuilder(expenseTransaction)
                .setDate(futureDate)
                .setAccountId(account1.getId())
                .build();

        expenseTransaction.getItems().remove(expenseTransaction.getItems().indexOf(item1));
        incomeExpenseTransactionDAO.update(expenseTransaction3);

        expenseTransaction = incomeExpenseTransactionDAO.loadById(expenseTransaction.getId());
        assertEquals(2, expenseTransaction.getItems().size());
        assertFalse(expenseTransaction.getItems().contains(item1));
        assertTrue(expenseTransaction.getItems().contains(item2));
        assertTrue(expenseTransaction.getItems().contains(item3));
        assertEquals(expenseTransaction.getDate(), futureDate);
        assertEquals(expenseTransaction.getAccountId(), account1.getId().intValue());

        account1 = accountDAO.loadAccount(account1.getId());
        assertEquals(account1.getBalance(), 9667.0);

        account = accountDAO.loadAccount(account.getId());
        assertEquals(account.getBalance(), 1000.0);

        assertEquals(incomeExpenseTransactionDAO.getTransactionsCount(account), 0);
        assertEquals(incomeExpenseTransactionDAO.getTransactionsCount(account1), 1);

        /**
         * Check, that transaction update works if account is not changed
         */
        item1 = new CIncomeExpenseTransactionItem.CBuilder()
                .setAmount(444.0)
                .setCategory(category1)
                .setComment("reason")
                .build();

        CIncomeExpenseTransaction expenseTransaction4 = new CIncomeExpenseTransaction.CBuilder(expenseTransaction)
                .setDate(futureDate)
                .setAccountId(account1.getId())
                .addItem(item1)
                .build();
        incomeExpenseTransactionDAO.update(expenseTransaction4);

        expenseTransaction = incomeExpenseTransactionDAO.loadById(expenseTransaction.getId());
        assertEquals(expenseTransaction.getItems().size(), 3);
        assertTrue(expenseTransaction.getItems().contains(item1));
        assertTrue(expenseTransaction.getItems().contains(item2));
        assertTrue(expenseTransaction.getItems().contains(item3));
        assertEquals(expenseTransaction.getDate(), futureDate);
        assertEquals(expenseTransaction.getAccountId(), account1.getId().intValue());

        account1 = accountDAO.loadAccount(account1.getId());
        assertEquals(account1.getBalance(), 9223.0);

//        incomeExpenseTransactionDAO.remove(expenseTransaction);
//
//        account1 = accountDAO.loadAccount(account1.getId());
//        assertEquals(account1.getBalance(), 10000.0);
//
//        assertEquals(incomeExpenseTransactionDAO.getTransactionsCount(account1), 0);
    }

    @After
    public void after(){
        incomeExpenseTransactionDAO.remove(expenseTransaction);
        incomeExpenseTransactionDAO.remove(expenseTransaction1);
        accountDAO.remove(account);
        accountDAO.remove(account1);
        accountDAO.remove(sharedAccount);
        categoryDAO.remove(category1);
        categoryDAO.remove(category2);
        categoryDAO.remove(category3);
        userDAO.remove(user);
    }
}
