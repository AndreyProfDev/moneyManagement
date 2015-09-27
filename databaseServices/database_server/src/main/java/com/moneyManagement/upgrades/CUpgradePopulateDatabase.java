package com.moneyManagement.upgrades;

import com.moneyManagement.api.implementations.*;
import com.moneyManagement.data.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.jdbc.core.*;

import javax.sql.*;
import java.util.*;

public class CUpgradePopulateDatabase implements IUpgrade{

    @Autowired
    private CAccountDAO accountDao;

    @Autowired
    private CUserDAO userDao;

    @Autowired
    private CCategoryDAO categoryDAO;

    @Autowired
    private CExchangeRateDAO exchangeRateDAO;

    @Autowired
    private CIncomeExpenseTransactionDAO incomeExpenseTransactionDAO;

    @Autowired
    private CTransferTransactionDAO transferTransaction;

    public void setDataSource(DataSource dataSource) {
    }

    public void apply() {

        CUser user = userDao.create(new CUser.CBuilder()
                .setLogin("Andrey")
                .setPassword("Qw1@qwer")
                .setBaseCurrency("EUR")
                .build());

        exchangeRateDAO.create(new CExchangeRate.CBuilder()
                .setEnterDate(new Date())
                .setEffectiveDate(new Date())
                .setFirstCurrency("EUR")
                .setSecondCurrency("RUB")
                .setExchangeRate(70.0)
                .build());

        CAccount bankAccount = accountDao.create(new CAccount.CBuilder()
                .setBalance(0)
                .setCurrency("RUB")
                .setIncludeInPlanning(false)
                .setName("Bank account")
                .setType(EAccountType.BANK_CARD)
                .setUserId(user.getId())
                .build());

        exchangeRateDAO.create(new CExchangeRate.CBuilder()
                .setEnterDate(new Date())
                .setEffectiveDate(new Date())
                .setFirstCurrency("EUR")
                .setSecondCurrency("DOL")
                .setExchangeRate(1.2)
                .build());

        CAccount cashAccount = accountDao.create(new CAccount.CBuilder()
                .setBalance(0)
                .setCurrency("DOL")
                .setIncludeInPlanning(false)
                .setName("Cash account")
                .setType(EAccountType.CASH)
                .setUserId(user.getId())
                .build());

        CCategory dinnerCategory = categoryDAO.create(new CCategory.CBuilder()
                .setName("Dinner")
                .setType(ECategoryType.EXPENSE)
                .setUserId(user.getId())
                .build());

        CCategory transportCategory = categoryDAO.create(new CCategory.CBuilder()
                .setName("Transport")
                .setType(ECategoryType.EXPENSE)
                .setUserId(user.getId())
                .build());

        CCategory salaryCategory = categoryDAO.create(new CCategory.CBuilder()
                .setName("Salary")
                .setType(ECategoryType.INCOME)
                .setUserId(user.getId())
                .build());

        incomeExpenseTransactionDAO.create(new CIncomeExpenseTransaction.CBuilder()
                .setAccountId(bankAccount.getId())
                .setDate(new Date())
                .setUserId(user.getId())
                .setCategory(ECategoryType.INCOME)
                .addItem(new CIncomeExpenseTransactionItem.CBuilder()
                        .setAmount(20.0)
                        .setCategory(dinnerCategory)
                        .setComment("Salary bank")
                        .build())
                .addItem(new CIncomeExpenseTransactionItem.CBuilder()
                        .setAmount(15.0)
                        .setCategory(transportCategory)
                        .setComment("Salary bank")
                        .build())
                .build());

        incomeExpenseTransactionDAO.create(new CIncomeExpenseTransaction.CBuilder()
                .setAccountId(bankAccount.getId())
                .setDate(new Date())
                .setUserId(user.getId())
                .setCategory(ECategoryType.EXPENSE)
                .addItem(new CIncomeExpenseTransactionItem.CBuilder()
                        .setAmount(10.0)
                        .setCategory(dinnerCategory)
                        .setComment("Dinner bank")
                        .build())
                .addItem(new CIncomeExpenseTransactionItem.CBuilder()
                        .setAmount(15.0)
                        .setCategory(transportCategory)
                        .setComment("Transport bank")
                        .build())
                .build());

        incomeExpenseTransactionDAO.create(new CIncomeExpenseTransaction.CBuilder()
                .setAccountId(cashAccount.getId())
                .setDate(new Date())
                .setUserId(user.getId())
                .setCategory(ECategoryType.INCOME)
                .addItem(new CIncomeExpenseTransactionItem.CBuilder()
                        .setAmount(10.0)
                        .setCategory(dinnerCategory)
                        .setComment("Salary cash")
                        .build())
                .addItem(new CIncomeExpenseTransactionItem.CBuilder()
                        .setAmount(15.0)
                        .setCategory(transportCategory)
                        .setComment("Salary cash")
                        .build())
                .build());

        incomeExpenseTransactionDAO.create(new CIncomeExpenseTransaction.CBuilder()
                .setAccountId(cashAccount.getId())
                .setDate(new Date())
                .setUserId(user.getId())
                .setCategory(ECategoryType.EXPENSE)
                .addItem(new CIncomeExpenseTransactionItem.CBuilder()
                        .setAmount(10.0)
                        .setCategory(dinnerCategory)
                        .setComment("Dinner cash")
                        .build())
                .addItem(new CIncomeExpenseTransactionItem.CBuilder()
                        .setAmount(15.0)
                        .setCategory(transportCategory)
                        .setComment("Transport cash")
                        .build())
                .build());

        transferTransaction.create(new CTransferTransaction.CBuilder()
                .setFromAccountId(bankAccount.getId())
                .setFromUserId(user.getId())
                .setToUserId(user.getId())
                .setToAccountId(cashAccount.getId())
                .setTransferAmount(5.0)
                .setTransferReason("Transfer")
                .setTrDate(new Date())
                .build());
    }
}
