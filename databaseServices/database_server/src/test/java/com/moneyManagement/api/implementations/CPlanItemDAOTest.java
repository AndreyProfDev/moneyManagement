package com.moneyManagement.api.implementations;

import com.moneyManagement.api.implementations.*;
import com.moneyManagement.data.*;
import org.junit.*;
import org.springframework.beans.factory.annotation.*;

import java.util.*;

import static org.junit.Assert.*;

public class CPlanItemDAOTest extends CAbstractDAOTest
{
    @Autowired
    private CUserDAO userDAO;

    @Autowired
    private CPlanItemDAO planItemDAO;

    @Autowired
    private CAccountDAO accountDAO;

    @Autowired
    private CCategoryDAO categoryDAO;

    @Autowired
    private CIncomeExpenseTransactionDAO incomeExpenseTransactionDAO;

    private Date dayBeforeYesterday;
    private Date yesterday;
    private Date today;
    private Date tomorrow;
    private CUser user1;
    private CUser user2;
    private CPlanItem plan1;
    private CPlanItem plan2;
    private CIncomeExpenseTransaction incomeTr1;
    private CIncomeExpenseTransaction incomeTr2;
    private CIncomeExpenseTransaction incomeTr3;
    private CIncomeExpenseTransaction expenseTr1;
    private CAccount account1;
    private CAccount account2;
    private CCategory incomeCategory;
    private CCategory expenseCategory;

    @Before
    public void before()
    {
        today = new Date(new Date().getTime());

        Calendar calendar = new GregorianCalendar();
        calendar.setTime(today);
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        yesterday = new Date(calendar.getTime().getTime());

        calendar.add(Calendar.DAY_OF_YEAR, -1);
        dayBeforeYesterday = new Date(calendar.getTime().getTime());

        calendar.add(Calendar.DAY_OF_YEAR, 3);
        tomorrow = new Date(calendar.getTime().getTime());

        user1 = userDAO.create(new CUser.CBuilder()
                .setLogin(UUID.randomUUID().toString())
                .setBaseCurrency("DOL")
                .setPassword("Password1^")
                .build());

        user2 = userDAO.create(new CUser.CBuilder()
                .setLogin(UUID.randomUUID().toString())
                .setPassword("Password2^")
                .setBaseCurrency("DOL")
                .build());

        plan1 = planItemDAO.create(new CPlanItem.CBuilder()
                .setUserId(user1.getId())
                .setDate(tomorrow)
                .setAmount(200.0)
                .build());

        plan2 = planItemDAO.create(new CPlanItem.CBuilder()
                .setUserId(user2.getId())
                .setDate(yesterday)
                .setAmount(400.0)
                .build());

        account1 = accountDAO.create(new CAccount.CBuilder()
                .setBalance(0.0)
                .setCurrency("DOL")
                .setIncludeInPlanning(true)
                .setName(UUID.randomUUID().toString())
                .setType(EAccountType.BANK_CARD)
                .setUserId(user2.getId())
                .build());

        account2 = accountDAO.create(new CAccount.CBuilder()
                .setBalance(0.0)
                .setCurrency("DOL")
                .setIncludeInPlanning(false)
                .setName(UUID.randomUUID().toString())
                .setType(EAccountType.BANK_CARD)
                .setUserId(user2.getId())
                .build());

        incomeCategory = categoryDAO.create(new CCategory.CBuilder()
                .setName(UUID.randomUUID().toString())
                .setType(ECategoryType.INCOME)
                .setUserId(user2.getId())
                .build());

        expenseCategory = categoryDAO.create(new CCategory.CBuilder()
                .setName(UUID.randomUUID().toString())
                .setType(ECategoryType.EXPENSE)
                .setUserId(user2.getId())
                .build());

        incomeTr1 = incomeExpenseTransactionDAO.create(new CIncomeExpenseTransaction.CBuilder()
                .setDate(today)
                .setAccountId(account1.getId())
                .setUserId(user2.getId())
                .setCategory(ECategoryType.INCOME)
                .addItem(new CIncomeExpenseTransactionItem.CBuilder()
                        .setAmount(100.0)
                        .setCategory(incomeCategory)
                        .build())
                .addItem(new CIncomeExpenseTransactionItem.CBuilder()
                        .setAmount(222.0)
                        .setCategory(incomeCategory)
                        .build())
                .build());

        incomeTr2 = incomeExpenseTransactionDAO.create(new CIncomeExpenseTransaction.CBuilder()
                .setDate(dayBeforeYesterday)
                .setAccountId(account1.getId())
                .setUserId(user2.getId())
                .setCategory(ECategoryType.INCOME)
                .addItem(new CIncomeExpenseTransactionItem.CBuilder()
                        .setAmount(10.0)
                        .setCategory(incomeCategory)
                        .build())
                .addItem(new CIncomeExpenseTransactionItem.CBuilder()
                        .setAmount(22.0)
                        .setCategory(expenseCategory)
                        .build())
                .build());

        incomeTr3 = incomeExpenseTransactionDAO.create(new CIncomeExpenseTransaction.CBuilder()
                .setDate(dayBeforeYesterday)
                .setAccountId(account2.getId())
                .setUserId(user2.getId())
                .setCategory(ECategoryType.INCOME)
                .addItem(new CIncomeExpenseTransactionItem.CBuilder()
                        .setAmount(10.0)
                        .setCategory(incomeCategory)
                        .build())
                .addItem(new CIncomeExpenseTransactionItem.CBuilder()
                        .setAmount(22.0)
                        .setCategory(expenseCategory)
                        .build())
                .build());

        expenseTr1 = incomeExpenseTransactionDAO.create(new CIncomeExpenseTransaction.CBuilder()
                .setDate(dayBeforeYesterday)
                .setAccountId(account1.getId())
                .setUserId(user2.getId())
                .setCategory(ECategoryType.EXPENSE)
                .addItem(new CIncomeExpenseTransactionItem.CBuilder()
                        .setAmount(1.0)
                        .setComment("reason")
                        .setCategory(incomeCategory)
                        .build())
                .addItem(new CIncomeExpenseTransactionItem.CBuilder()
                        .setAmount(2.0)
                        .setComment("reason")
                        .setCategory(expenseCategory)
                        .build())
                .build());
    }

    @Test
    public void test()
    {
        List<CPlanItem> plans = planItemDAO.loadAll();
        assertTrue(plans.contains(plan1));
        assertEquals(plans.get(plans.indexOf(plan1)).getId(), plan1.getId());

        assertTrue(plans.contains(plan2));

        /**
         * Check that loading works
         */
        plans = planItemDAO.loadPlans(user1);
        assertEquals(1, plans.size());
        assertEquals(plans.get(0), plan1);
        assertEquals(plans.get(0).getId(), plan1.getId());

        plans = planItemDAO.loadPlans(user2);
        assertEquals(1, plans.size());
        assertEquals(plans.get(0), plan2);
        assertEquals(plans.get(0).getId(), plan2.getId());

        plans = planItemDAO.loadPlans(user2, dayBeforeYesterday, dayBeforeYesterday);
        assertEquals(0, plans.size());

        plans = planItemDAO.loadPlans(user2, yesterday, yesterday);
        assertEquals(1, plans.size());
        assertEquals(plans.get(0), plan2);
        assertEquals(plans.get(0).getId(), plan2.getId());

        List<CPlanItemResult> planResults = planItemDAO.loadPlanResults(user2, dayBeforeYesterday, dayBeforeYesterday);
        assertEquals(0, planResults.size());

        planResults = planItemDAO.loadPlanResults(user2, yesterday, yesterday);
        assertEquals(1, planResults.size());
        assertEquals(new CPlanItemResult.CBuilder()
                .setSource(plan2)
                .setPlanResult(29.0)
                .build(), planResults.get(0));
        assertEquals(plan2.getId(), planResults.get(0).getId());

        /**
         * Check that update works
         */
        plan2 = new CPlanItem.CBuilder(plan2)
                .setAmount(20.0)
                .setDate(dayBeforeYesterday)
                .build();

        planItemDAO.update(plan2);

        plans = planItemDAO.loadPlans(user2);
        assertTrue(plans.contains(plan2));

        plan2 = new CPlanItem.CBuilder(plan2)
                .setAmount(20.0)
                .setDate(today)
                .build();

        planItemDAO.update(plan2);

        plans = planItemDAO.loadPlans(user2);
        assertTrue(plans.contains(plan2));

        /**
         * Check that remove works
         */
        planItemDAO.remove(plan2);
        assertEquals(0, planItemDAO.loadPlans(user2).size());
    }

    @After
    public void after(){
        incomeExpenseTransactionDAO.remove(expenseTr1);
        incomeExpenseTransactionDAO.remove(incomeTr3);
        incomeExpenseTransactionDAO.remove(incomeTr2);
        incomeExpenseTransactionDAO.remove(incomeTr1);

        categoryDAO.remove(incomeCategory);
        categoryDAO.remove(expenseCategory);

        accountDAO.remove(account1);
        accountDAO.remove(account2);

        planItemDAO.remove(plan1);
        planItemDAO.remove(plan2);

        userDAO.remove(user1);
        userDAO.remove(user2);
    }
}
