package com.moneyManagement.api.implementations;

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
public class CAccountDAO implements IAccountDAO
{
    public static final String TABLE_NAME = "ACCOUNT";

    @Autowired
    private CIncomeExpenseTransactionDAO incomeExpenseTransactionDAO;

    @Autowired
    private CTransferTransactionDAO transferTransactionDAO;

    @Autowired
    private CExchangeRateDAO exchangeRateDAO;

    @Autowired
    private CUserDAO userDAO;

    private JdbcTemplate template;

    public void setDataSource(DataSource dataSource) {
        template = new JdbcTemplate(dataSource);
    }

    @Transactional(propagation= Propagation.REQUIRED, readOnly=false)
    @Override
    public CAccount create(final CAccount account)
    {
        if (account.getUserId() != null){
            CUser accountUser = userDAO.loadById(account.getUserId());
            String baseCurrency = accountUser.getBaseCurrency();
            String accountCurrency = account.getCurrency();

            if (!account.getCurrency().equals(baseCurrency) &&
                    !exchangeRateDAO.checkExists(accountCurrency, baseCurrency)){
                throw new CValidationException("Account " + account + " could not be created. " +
                        "There are no exchange rate (" + baseCurrency +", " + accountCurrency + ")");
            }
        }


        KeyHolder keyHolder = new GeneratedKeyHolder();
        template.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement("INSERT INTO " + TABLE_NAME + " " +
                        "(account_name,  account_type,  balance,  participate_in_planning,  user_id, currency_type, creation_date) " +
                        "VALUES(?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, account.getName());
                ps.setString(2, account.getType().toString());
                ps.setDouble(3, account.getBalance());
                ps.setBoolean(4, account.isIncludeInPlanning());
                ps.setString(6, account.getCurrency());
                ps.setDate(7, new java.sql.Date(new Date().getTime()));

                if (account.getUserId() != null) {
                    ps.setInt(5, account.getUserId());
                } else {
                    ps.setNull(5, Types.INTEGER);
                }
                return ps;
            }
        }, keyHolder);
        account.setId(keyHolder.getKey().intValue());

        return account;
    }

    @Transactional(propagation= Propagation.REQUIRED, readOnly=false)
    @Override
    public void update(CAccount account)
    {
        template.update("UPDATE " + TABLE_NAME + " SET account_name = ?, balance = ?, participate_in_planning = ?, currency_type = ?, user_id = ? WHERE id = ?",
                account.getName(), account.getBalance(), account.isIncludeInPlanning(), account.getCurrency(), account.getUserId(), account.getId());
    }

    @Transactional(propagation= Propagation.REQUIRED, readOnly=false)
    @Override
    public void remove(CAccount account)
    {
        if (incomeExpenseTransactionDAO.getTransactionsCount(account) > 0 ||
                transferTransactionDAO.getTransactionsCount(account) > 0)
        {
            throw new CValidationException("Account " + account + " could not be removed. It have related entities.");
        }

        template.update("DELETE FROM " + TABLE_NAME + " WHERE id = ?", account.getId());

        //TODO remove all related transactions
    }

    @Override
    public List<CAccount> loadAccounts(CUser user, EAccountType accountType)
    {
        if (user != null)
        {
            return fillAccountsFromRows(template.queryForList("SELECT id, account_name, account_type, balance, participate_in_planning, user_id, currency_type " +
                    "FROM " + TABLE_NAME + " WHERE user_id = ? AND account_type = ?", user.getId(), accountType.toString()));
        }

        return fillAccountsFromRows(template.queryForList("SELECT id, account_name, account_type, balance, participate_in_planning, user_id, currency_type " +
                "FROM " + TABLE_NAME + " WHERE user_id IS NULL AND account_type = ?", accountType.toString()));
    }

    @Override
    public CAccount loadAccount(int id)
    {
        return template.queryForObject("SELECT id, account_name, account_type, balance, participate_in_planning, user_id, currency_type FROM " + TABLE_NAME + " WHERE id = ?",
                new RowMapper<CAccount>() {
                    public CAccount mapRow(ResultSet rs, int rowNum)
                            throws SQLException {
                        Integer userId = rs.getInt(6);
                        userId = rs.wasNull() ? null : userId;
                        return new CAccount.CBuilder()
                                .setId(rs.getInt(1))
                                .setName(rs.getString(2))
                                .setType(EAccountType.fromString(rs.getString(3)))
                                .setBalance(rs.getDouble(4))
                                .setIncludeInPlanning(rs.getBoolean(5))
                                .setUserId(userId)
                                .setCurrency(rs.getString(7))
                                .build();
                    }
                }, id);
    }

    @Override
    public List<CAccount> loadAccounts(CUser user)
    {
        if (user != null)
        {
            return fillAccountsFromRows(template.queryForList("SELECT id, account_name, account_type, balance, participate_in_planning, user_id, currency_type " +
                    "FROM " + TABLE_NAME + " WHERE user_id = ?", user.getId()));
        }

        return fillAccountsFromRows(template.queryForList("SELECT id, account_name, account_type, balance, participate_in_planning, user_id, currency_type " +
                "FROM " + TABLE_NAME + " WHERE user_id IS NULL"));
    }

    @Override
    public List<CAccount> loadAccounts(CUser user, boolean participateInPlanning)
    {
        if (user != null)
        {
            return fillAccountsFromRows(template.queryForList("SELECT id, account_name, account_type, balance, participate_in_planning, user_id, currency_type " +
                    "FROM " + TABLE_NAME + " WHERE user_id = ? AND participate_in_planning = ?", user.getId(), participateInPlanning));
        }

        return fillAccountsFromRows(template.queryForList("SELECT id, account_name, account_type, balance, participate_in_planning, user_id, currency_type " +
                "FROM " + TABLE_NAME + " WHERE user_id IS NULL AND participate_in_planning = ?", participateInPlanning));
    }

    @Override
    public List<CAccount> fillAccountsFromRows(List<Map<String, Object>> rows)
    {
        List<CAccount> result = new ArrayList<>();

        for (Map<String, Object> map : rows)
        {
            result.add(new CAccount.CBuilder()
                    .setId((Integer) map.get("id"))
                    .setName((String) map.get("account_name"))
                    .setType(EAccountType.fromString((String) map.get("account_type")))
                    .setBalance((Double) map.get("balance"))
                    .setIncludeInPlanning((Boolean) map.get("participate_in_planning"))
                    .setUserId((Integer) map.get("user_id"))
                    .setCurrency((String) map.get("currency_type"))
                    .build());
        }

        return result;
    }

    @Override
    public boolean checkAccountsExists(CUser user)
    {
        if (user != null)
        {
            return template.queryForObject("select count(*) from " + TABLE_NAME + " WHERE user_id = ?", Integer.class, user.getId()) > 0;
        }

        return template.queryForObject("SELECT count(*) FROM " + TABLE_NAME + " WHERE user_id IS NULL", Integer.class) > 0;
    }
}