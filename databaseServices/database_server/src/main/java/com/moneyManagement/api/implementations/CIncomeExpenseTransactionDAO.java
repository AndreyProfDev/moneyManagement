package com.moneyManagement.api.implementations;

import com.google.common.base.*;
import com.google.common.collect.*;
import com.moneyManagement.api.interfaces.*;
import com.moneyManagement.data.*;
import com.moneyManagement.exceptions.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.support.*;
import org.springframework.transaction.annotation.*;

import javax.sql.*;
import java.sql.*;
import java.util.*;
import java.util.Date;

//@Component
//@Transactional(propagation= Propagation.REQUIRED, readOnly=true)
public class CIncomeExpenseTransactionDAO implements IIncomeExpenseTransactionDAO
{
    public static final String TABLE_NAME = "INCOME_EXPENSE_TRANSACTION";

    @Autowired
    private CAccountDAO accountDAO;

    @Autowired
    private CIncomeExpenseTransactionItemDAO incomeExpenseTransactionItemDAO;

    private JdbcTemplate jdbcTemplate;


    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Transactional(propagation= Propagation.REQUIRED, readOnly=false)
    @Override
    public CIncomeExpenseTransaction create(final CIncomeExpenseTransaction newTransaction)
    {
        CAccount account = accountDAO.loadAccount(newTransaction.getAccountId());
        if (newTransaction.getCategory().equals(ECategoryType.EXPENSE) && newTransaction.getSum() > account.getBalance())
        {
            throw new CValidationException("Your balance is insufficient for this operation.");
        }

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement("INSERT INTO " + TABLE_NAME + " " +
                        "(tr_date, user_id, account_id, category_type) " +
                        "VALUES(?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
                ps.setTimestamp(1, new Timestamp(newTransaction.getDate().getTime()));
                ps.setInt(2, newTransaction.getUserId());
                ps.setInt(3, newTransaction.getAccountId());
                ps.setString(4, newTransaction.getCategory().toString());
                return ps;
            }
        }, keyHolder);
        newTransaction.setId(keyHolder.getKey().intValue());

        for (CIncomeExpenseTransactionItem item : newTransaction.getItems())
        {
            CIncomeExpenseTransactionItem newItem = incomeExpenseTransactionItemDAO.create(item, newTransaction);
            item.setId(newItem.getId());
            item.setTransactionId(newTransaction.getId());
        }

        /**
         * Update related entity
         */
        if (ECategoryType.EXPENSE.equals(newTransaction.getCategory())){
            accountDAO.update(new CAccount.CBuilder(account)
                    .setBalance(account.getBalance() - newTransaction.getSum())
                    .build());
        }
        else{
            accountDAO.update(new CAccount.CBuilder(account)
                    .setBalance(account.getBalance() + newTransaction.getSum())
                    .build());
        }

        return newTransaction;
    }

    @Transactional(propagation= Propagation.REQUIRED, readOnly=false)
    @Override
    public void update(CIncomeExpenseTransaction newTransaction)
    {
        /**
         * Check constraints
         */
        int transactionTypeMultiplier = newTransaction.getCategory().equals(ECategoryType.EXPENSE) ? -1 : 1;


        CIncomeExpenseTransaction sourceTransaction = loadById(newTransaction.getId());
        double sourceBalanceChange =  transactionTypeMultiplier * sourceTransaction.getSum();
        List<Integer> sourceItemIds = Lists.newArrayList(Iterables.filter(Lists.transform(sourceTransaction.getItems(), new Function<CIncomeExpenseTransactionItem, Integer>() {
            @Override
            public Integer apply(CIncomeExpenseTransactionItem incomeExpenseTransactionItem) {
                return incomeExpenseTransactionItem.getId();
            }
        }), Predicates.notNull()));

        List<Integer> resultItemIds = Lists.newArrayList(Iterables.filter(Lists.transform(newTransaction.getItems(), new Function<CIncomeExpenseTransactionItem, Integer>() {
            @Override
            public Integer apply(CIncomeExpenseTransactionItem incomeExpenseTransactionItem) {
                return incomeExpenseTransactionItem.getId();
            }
        }), Predicates.notNull()));

        List<CIncomeExpenseTransactionItem> createdItems = new ArrayList<>();
        List<CIncomeExpenseTransactionItem> updatedItems = new ArrayList<>();
        List<CIncomeExpenseTransactionItem> removedItems= new ArrayList<>();

        for (CIncomeExpenseTransactionItem item : newTransaction.getItems()) {
            if (item.getId() == null) {
                createdItems.add(item);
            }
            else if (sourceItemIds.contains(item.getId())){
                updatedItems.add(item);
            }
        }

        for (CIncomeExpenseTransactionItem item : sourceTransaction.getItems()){
            if (!resultItemIds.contains(item.getId())){
                removedItems.add(item);
            }
        }

        double currentBalanceChange = 0;
        for (CIncomeExpenseTransactionItem item : createdItems)
        {
                currentBalanceChange += item.getAmount();
        }

        for (CIncomeExpenseTransactionItem item : updatedItems)
        {
            currentBalanceChange += item.getAmount();
        }

        currentBalanceChange *= transactionTypeMultiplier;

        if (newTransaction.getAccountId() != sourceTransaction.getAccountId())
        {
            CAccount sourceAccount = accountDAO.loadAccount(sourceTransaction.getAccountId());
            double newSourceAccountBalance = sourceAccount.getBalance() - sourceBalanceChange;
            if (newSourceAccountBalance < 0)
            {
                throw new CValidationException("Balance of account '" + sourceAccount.getName() + "' is insufficient for this operation.");
            }

            CAccount targetAccount = accountDAO.loadAccount(newTransaction.getAccountId());
            double newTargetAccountBalance = targetAccount.getBalance() + currentBalanceChange;
            if (newTargetAccountBalance < 0)
            {
                throw new CValidationException("Balance of account '" + targetAccount.getName() + "' is insufficient for this operation.");
            }

            jdbcTemplate.update("UPDATE " + TABLE_NAME + " SET tr_date = ?, account_id = ? WHERE id = ?",
                    new Timestamp(newTransaction.getDate().getTime()), newTransaction.getAccountId(), newTransaction.getId());

            for (CIncomeExpenseTransactionItem item : createdItems){
                incomeExpenseTransactionItemDAO.create(item, newTransaction);
            }

            for (CIncomeExpenseTransactionItem item : updatedItems){
                incomeExpenseTransactionItemDAO.update(item, newTransaction);
            }

            for (CIncomeExpenseTransactionItem item : removedItems){
                incomeExpenseTransactionItemDAO.remove(item);
            }

            /**
             * Update related entity
             */
            accountDAO.update(new CAccount.CBuilder(targetAccount)
                    .setBalance(newTargetAccountBalance)
                    .build());
            accountDAO.update(new CAccount.CBuilder(sourceAccount)
                    .setBalance(newSourceAccountBalance)
                    .build());
        }
        else
        {
            CAccount account = accountDAO.loadAccount(sourceTransaction.getAccountId());
            double newAccountBalance = account.getBalance() - sourceBalanceChange + currentBalanceChange;

            if (newAccountBalance < 0)
            {
                throw new CValidationException("Balance of account '" + account.getName() + "' is insufficient for this operation.");
            }

            jdbcTemplate.update("UPDATE " + TABLE_NAME + " SET tr_date = ?, account_id = ? WHERE id = ?",
                    new Timestamp(newTransaction.getDate().getTime()), newTransaction.getAccountId(), newTransaction.getId());

            for (CIncomeExpenseTransactionItem item : createdItems){
                incomeExpenseTransactionItemDAO.create(item, newTransaction);
            }

            for (CIncomeExpenseTransactionItem item : updatedItems){
                incomeExpenseTransactionItemDAO.update(item, newTransaction);
            }

            for (CIncomeExpenseTransactionItem item : removedItems){
                incomeExpenseTransactionItemDAO.remove(item);
            }

            /**
             * Update related entity
             */
            accountDAO.update(new CAccount.CBuilder(account)
                    .setBalance(newAccountBalance)
                    .build());
        }
    }

    @Transactional(propagation= Propagation.REQUIRED, readOnly=false)
    @Override
    public void remove(CIncomeExpenseTransaction transaction)
    {
        CIncomeExpenseTransaction sourceTransaction = loadById(transaction.getId());
        double sourceChange = sourceTransaction.getCategory().equals(ECategoryType.EXPENSE) ? -1 * sourceTransaction.getSum() : sourceTransaction.getSum();

        CAccount account = accountDAO.loadAccount(sourceTransaction.getAccountId());
        double newAccountBalance = account.getBalance() - sourceChange;

        /**
         * Remove entity
         */
        jdbcTemplate.update("DELETE FROM " + TABLE_NAME + " WHERE id = ?", transaction.getId());
        /**
         * Remove items
         */
        jdbcTemplate.update("DELETE FROM " + CIncomeExpenseTransactionItemDAO.TABLE_NAME + " WHERE transaction_id = ?", transaction.getId());

        /**
         * Update related account
         */
        accountDAO.update(new CAccount.CBuilder(account)
                                .setBalance(newAccountBalance)
                                .build());
    }

    @Override
    public List<CIncomeExpenseTransaction> loadAll(CUser user, java.util.Date startDate, java.util.Date endDate)
    {
        List<CIncomeExpenseTransaction> result = new ArrayList<>();

        List<Map<String, Object>> rows = jdbcTemplate.queryForList("SELECT id, tr_date, account_id, category_type " +
                        "FROM " + TABLE_NAME + " WHERE tr_date >= ? AND tr_date <= ? AND user_id = ?",
                new Timestamp(startDate.getTime()), new Timestamp(endDate.getTime()), user.getId());

        for (Map<String, Object> map : rows)
        {
            int id = (int) map.get("id");
            List<CIncomeExpenseTransactionItem> items = incomeExpenseTransactionItemDAO.loadTransactionItems(id);

            result.add(new CIncomeExpenseTransaction.CBuilder()
                    .setUserId(user.getId())
                    .setAccountId((int) map.get("account_id"))
                    .setDate(new java.util.Date(((Timestamp) map.get("tr_date")).getTime()))
                    .setItems(items)
                    .setId(id)
                    .setCategory(ECategoryType.fromString((String) map.get("category_type")))
                    .build());
        }

        return result;
    }

    @Override
    public List<CIncomeExpenseTransaction> loadAll(CUser user, Date startDate, Date endDate, ECategoryType categoryType){
        List<CIncomeExpenseTransaction> result = new ArrayList<>();

        List<Map<String, Object>> rows = jdbcTemplate.queryForList("SELECT id, tr_date, account_id, category_type " +
                        "FROM " + TABLE_NAME + " WHERE tr_date >= ? AND tr_date <= ? AND user_id = ? AND category_type = ?",
                new Timestamp(startDate.getTime()), new Timestamp(endDate.getTime()), user.getId(), categoryType.toString());

        for (Map<String, Object> map : rows)
        {
            int id = (int) map.get("id");
            List<CIncomeExpenseTransactionItem> items = incomeExpenseTransactionItemDAO.loadTransactionItems(id);

            result.add(new CIncomeExpenseTransaction.CBuilder()
                    .setUserId(user.getId())
                    .setAccountId((int) map.get("account_id"))
                    .setDate(new java.util.Date(((Timestamp) map.get("tr_date")).getTime()))
                    .setItems(items)
                    .setId(id)
                    .setCategory(ECategoryType.fromString((String) map.get("category_type")))
                    .build());
        }

        return result;
    }

    @Override
    public List<CIncomeExpenseTransaction> loadAll(CUser user, CAccount acc, java.util.Date startDate, java.util.Date endDate)
    {
        List<CIncomeExpenseTransaction> result = new ArrayList<>();

        List<Map<String, Object>> rows = jdbcTemplate.queryForList("SELECT id, tr_date, account_id, category_type " +
                        "FROM " + TABLE_NAME + " WHERE tr_date >= ? AND tr_date <= ? AND user_id = ? AND account_id = ?",
                new Timestamp(startDate.getTime()), new Timestamp(endDate.getTime()), user.getId(), acc.getId());

        for (Map<String, Object> map : rows)
        {
            int id = (int) map.get("id");
            List<CIncomeExpenseTransactionItem> items = incomeExpenseTransactionItemDAO.loadTransactionItems(id);

            result.add(new CIncomeExpenseTransaction.CBuilder()
                    .setUserId(user.getId())
                    .setAccountId((int) map.get("account_id"))
                    .setDate(new java.util.Date(((Timestamp) map.get("tr_date")).getTime()))
                    .setItems(items)
                    .setId(id)
                    .setCategory(ECategoryType.fromString((String) map.get("category_type")))
                    .build());
        }

        return result;
    }

    @Override
    public int getTransactionsCount(CAccount account)
    {
        return jdbcTemplate.queryForObject("select count(*) from " + TABLE_NAME + " WHERE account_id = ?", Integer.class, account.getId());
    }

    @Override
    public CIncomeExpenseTransaction loadById(final int transactionId)
    {
        return jdbcTemplate.queryForObject("SELECT tr_date, user_id, account_id, category_type FROM " + TABLE_NAME + " WHERE id = ?",
                new RowMapper<CIncomeExpenseTransaction>() {
                    public CIncomeExpenseTransaction mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return new CIncomeExpenseTransaction.CBuilder()
                                .setUserId(rs.getInt("user_id"))
                                .setAccountId(rs.getInt("account_id"))
                                .setDate(new java.util.Date(rs.getTimestamp("tr_date").getTime()))
                                .setItems(incomeExpenseTransactionItemDAO.loadTransactionItems(transactionId))
                                .setId(transactionId)
                                .setCategory(ECategoryType.fromString(rs.getString("category_type")))
                                .build();
                    }
                }, transactionId);
    }
}
