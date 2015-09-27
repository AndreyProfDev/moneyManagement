package com.moneyManagement.api.implementations;

import com.moneyManagement.api.interfaces.*;
import com.moneyManagement.data.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.support.*;
import org.springframework.transaction.annotation.*;

import javax.sql.*;
import java.sql.*;
import java.util.*;

//@Component
//@Transactional(propagation= Propagation.REQUIRED, readOnly=true)
public class CIncomeExpenseTransactionItemDAO implements IIncomeExpenseTransactionItemDAO
{
    public static final String TABLE_NAME = "INCOME_EXPENSE_TRANSACTION_ITEM";

    @Autowired
    private CCategoryDAO categoryDAO;


    private JdbcTemplate jdbcTemplate;


    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Transactional(propagation= Propagation.REQUIRED, readOnly=false)
    @Override
    public CIncomeExpenseTransactionItem create(final CIncomeExpenseTransactionItem item, final CIncomeExpenseTransaction parentTransaction)
    {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement("INSERT INTO " + TABLE_NAME + " " +
                        "(reason, amount, category_id, transaction_id) " +
                        "VALUES(?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, item.getComment());
                ps.setDouble(2, item.getAmount());
                ps.setInt(3, item.getCategory().getId());
                ps.setInt(4, parentTransaction.getId());
                return ps;
            }
        }, keyHolder);
        item.setId(keyHolder.getKey().intValue());

        return item;
    }

    @Transactional(propagation= Propagation.REQUIRED, readOnly=false)
    @Override
    public void update(CIncomeExpenseTransactionItem item, CIncomeExpenseTransaction parentTransaction)
    {
        jdbcTemplate.update("UPDATE " + TABLE_NAME + " SET reason = ?, amount = ?, category_id = ?, transaction_id = ? WHERE id = ?",
                item.getComment(), item.getAmount(), item.getCategory().getId(), parentTransaction.getId(), item.getId());
    }

    @Transactional(propagation= Propagation.REQUIRED, readOnly=false)
    @Override
    public void remove(CIncomeExpenseTransactionItem item)
    {
        jdbcTemplate.update("DELETE FROM " + TABLE_NAME + " WHERE id = ?", item.getId());
    }

    @Override
    public List<CIncomeExpenseTransactionItem> loadTransactionItems(int transactionId)
    {
        List<CIncomeExpenseTransactionItem> result = new ArrayList<>();

        List<Map<String, Object>> rows = jdbcTemplate.queryForList("SELECT  id, reason, amount, category_id " +
                "FROM " + TABLE_NAME + " WHERE transaction_id = ?", transactionId);

        for (Map<String, Object> map : rows)
        {
            CCategory category = categoryDAO.loadById((int) map.get("category_id"));

            result.add(new CIncomeExpenseTransactionItem.CBuilder()
                    .setCategory(category)
                    .setComment((String) map.get("reason"))
                    .setAmount((Double) map.get("amount"))
                    .setId((int) map.get("id"))
                    .setTransactionId(transactionId)
                    .build());
        }

        return result;
    }

    @Override
    public int getTransactionsCount(CCategory category)
    {
        return jdbcTemplate.queryForObject("select count(*) from " + TABLE_NAME + " WHERE category_id = ?", Integer.class, category.getId());
    }
}