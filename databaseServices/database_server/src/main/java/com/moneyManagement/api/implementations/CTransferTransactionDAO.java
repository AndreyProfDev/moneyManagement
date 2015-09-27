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

//@Component
//@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
public class CTransferTransactionDAO implements ITransferTransactionDAO
{
    public static final String TABLE_NAME = "TRANSFER_TRANSACTION";

    @Autowired
    private CAccountDAO accountDAO;

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public CTransferTransactionDAO(DataSource dataSource)
    {
        super();
        setDataSource(dataSource);
    }

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    @Override
    public CTransferTransaction create(final CTransferTransaction transaction)
    {
        CAccount fromAccount = accountDAO.loadAccount(transaction.getFromAccountId());

        if (fromAccount.getBalance() < transaction.getTransferAmount())
        {
            throw new CValidationException("Account " + fromAccount.getName() + " does not contain enough assets");
        }

        if (transaction.getFromAccountId().equals(transaction.getToAccountId())){
            throw new CValidationException("Accounts should differ");
        }

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement psmt = connection.prepareStatement("INSERT INTO " + TABLE_NAME + " (account_from_id, account_to_id, " +
                        "transfer_reason, transfer_amount, tr_date, user_from_id, user_to_id) " +
                        "VALUES(?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
                psmt.setInt(1, transaction.getFromAccountId());
                psmt.setInt(2, transaction.getToAccountId());
                psmt.setString(3, transaction.getTransferReason());
                psmt.setDouble(4, transaction.getTransferAmount());
                psmt.setTimestamp(5, new Timestamp(transaction.getTrDate().getTime()));
                psmt.setInt(6, transaction.getFromUserId());
                psmt.setInt(7, transaction.getToUserId());
                return psmt;
            }
        }, keyHolder);
        transaction.setId(keyHolder.getKey().intValue());

        /**
         * Update related entity
         */
        CAccount account = accountDAO.loadAccount(transaction.getFromAccountId());
        accountDAO.update(new CAccount.CBuilder(account)
                                .setBalance(account.getBalance() - transaction.getTransferAmount())
                                .build());

        if (transaction.getToAccountId() != null){
            account = accountDAO.loadAccount(transaction.getToAccountId());
            accountDAO.update(new CAccount.CBuilder(account)
                    .setBalance(account.getBalance() + transaction.getTransferAmount())
                    .build());
        }

        return transaction;
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    @Override
    public void update(CTransferTransaction newTransaction)
    {
        CTransferTransaction sourceTransaction = loadById(newTransaction.getId());

        int sourceFromAccountId = sourceTransaction.getFromAccountId();
        int sourceToAccountId = sourceTransaction.getToAccountId();
        double sourceTransferAmount = sourceTransaction.getTransferAmount();

        int newFromAccountId = newTransaction.getFromAccountId();
        int newToAccountId = newTransaction.getToAccountId();
        double newTransferAmount = newTransaction.getTransferAmount();

        /**
         * Load accounts, update their balance and check constraints
         */
        CAccount sourceFromAccount = accountDAO.loadAccount(sourceFromAccountId);
        sourceFromAccount = new CAccount.CBuilder(sourceFromAccount)
                .setBalance(sourceFromAccount.getBalance() + sourceTransferAmount)
                .build();

        CAccount sourceToAccount = accountDAO.loadAccount(sourceToAccountId);
        double sourceToAccountBalance = sourceToAccount.getBalance() - sourceTransferAmount;
        if (sourceToAccountBalance < 0)
        {
            throw new CValidationException("Account " + sourceToAccount.getName() + " does not contain enough assets");
        }
        sourceToAccount = new CAccount.CBuilder(sourceToAccount)
                .setBalance(sourceToAccountBalance)
                .build();

        CAccount newFromAccount = sourceFromAccountId == newFromAccountId ? sourceFromAccount : accountDAO.loadAccount(newFromAccountId);
        double newFromAccountBalance = newFromAccount.getBalance() - newTransferAmount;
        if (newFromAccountBalance < 0)
        {
            throw new CValidationException("Account " + newFromAccount.getName() + " does not contain enough assets");
        }
        newFromAccount = new CAccount.CBuilder(newFromAccount)
                .setBalance(newFromAccountBalance)
                .build();

        CAccount newToAccount = sourceToAccountId == newToAccountId ? sourceToAccount : accountDAO.loadAccount(newToAccountId);
        newToAccount = new CAccount.CBuilder(newToAccount)
                .setBalance(newToAccount.getBalance() + newTransferAmount)
                .build();

        /**
         * Update transaction
         */
        jdbcTemplate.update("UPDATE " + TABLE_NAME + " SET account_from_id = ?, account_to_id = ?, " +
                        "transfer_reason = ?, transfer_amount = ?, tr_date = ? WHERE id = ?",
                newTransaction.getFromAccountId(), newTransaction.getToAccountId(),
                newTransaction.getTransferReason(), newTransaction.getTransferAmount(),
                new Timestamp(newTransaction.getTrDate().getTime()), newTransaction.getId());

        /**
         * Update related accounts
         */
        accountDAO.update(sourceFromAccount);

        if (sourceFromAccountId != newFromAccountId)
        {
            accountDAO.update(newFromAccount);
        }

        accountDAO.update(sourceToAccount);

        if (sourceToAccountId != newToAccountId)
        {
            accountDAO.update(newToAccount);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    @Override
    public void remove(CTransferTransaction transaction)
    {
        CTransferTransaction sourceTransaction = loadById(transaction.getId());

        double currentTransferAmount = sourceTransaction.getTransferAmount();

        CAccount toAccount = null;

        if (sourceTransaction.getToAccountId() != null){
            toAccount = accountDAO.loadAccount(sourceTransaction.getToAccountId());
            if (toAccount.getBalance() - currentTransferAmount < 0)
            {
                throw new CValidationException("Balance of account '" + toAccount.getName() + "' is unsufficient for this operation.");
            }
            toAccount = new CAccount.CBuilder(toAccount)
                    .setBalance(toAccount.getBalance() - currentTransferAmount)
                    .build();

            accountDAO.update(toAccount);
        }


        CAccount currentFromAccount = accountDAO.loadAccount(sourceTransaction.getFromAccountId());
        currentFromAccount = new CAccount.CBuilder(currentFromAccount)
                .setBalance(currentFromAccount.getBalance() + currentTransferAmount)
                .build();
        accountDAO.update(currentFromAccount);

        /**
         * Remove entity
         */
        jdbcTemplate.update("DELETE FROM " + TABLE_NAME + " WHERE id = ?", transaction.getId());



    }

    @Override
    public List<CTransferTransaction> loadAll(CUser userFrom, java.util.Date startDate, java.util.Date endDate)
    {
        List<CTransferTransaction> result = new ArrayList<>();

        List<Map<String, Object>> rows = jdbcTemplate.queryForList("SELECT id, account_from_id, account_to_id, transfer_reason, transfer_amount, tr_date, user_to_id " +
                        "FROM " + TABLE_NAME + " WHERE tr_date >= ? AND tr_date <= ? AND user_from_id = ?",
                new Timestamp(startDate.getTime()), new Timestamp(endDate.getTime()), userFrom.getId());

        for (Map<String, Object> row : rows)
        {
            result.add(new CTransferTransaction.CBuilder()
                    .setId((Integer) row.get("id"))
                    .setFromAccountId((Integer) row.get("account_from_id"))
                    .setToAccountId((Integer) row.get("account_to_id"))
                    .setTransferReason((String) row.get("transfer_reason"))
                    .setTransferAmount((Double) row.get("transfer_amount"))
                    .setTrDate(new java.util.Date(((Timestamp) row.get("tr_date")).getTime()))
                    .setFromUserId(userFrom.getId())
                    .setToUserId((Integer) row.get("user_to_id"))
                    .build());
        }

        return result;
    }

    @Override
    public List<CTransferTransaction> loadAllFrom(CUser fromUser, CAccount fromAcc, java.util.Date startDate, java.util.Date endDate)
    {
        List<CTransferTransaction> result = new ArrayList<>();

        List<Map<String, Object>> rows = jdbcTemplate.queryForList("SELECT id, account_to_id, transfer_reason, transfer_amount, tr_date, user_to_id " +
                        "FROM " + TABLE_NAME + " WHERE tr_date >= ? AND tr_date <= ? AND user_from_id = ? AND account_from_id = ?",
                new Timestamp(startDate.getTime()), new Timestamp(endDate.getTime()), fromUser.getId(), fromAcc.getId());

        for (Map<String, Object> row : rows)
        {
            result.add(new CTransferTransaction.CBuilder()
                    .setId((Integer) row.get("id"))
                    .setFromAccountId(fromAcc.getId())
                    .setToAccountId((Integer) row.get("account_to_id"))
                    .setTransferReason((String) row.get("transfer_reason"))
                    .setTransferAmount((Double) row.get("transfer_amount"))
                    .setTrDate(new java.util.Date(((Timestamp) row.get("tr_date")).getTime()))
                    .setFromUserId(fromUser.getId())
                    .setToUserId((Integer) row.get("user_to_id"))
                    .build());
        }

        return result;
    }

    @Override
    public List<CTransferTransaction> loadAllTo(CUser fromUser, CAccount toAcc, java.util.Date startDate, java.util.Date endDate)
    {
        List<CTransferTransaction> result = new ArrayList<>();

        List<Map<String, Object>> rows = jdbcTemplate.queryForList("SELECT id, account_from_id, transfer_reason, transfer_amount, tr_date, user_to_id " +
                        "FROM " + TABLE_NAME + " WHERE tr_date >= ? AND tr_date <= ? AND user_from_id = ? AND account_to_id = ?",
                new Timestamp(startDate.getTime()), new Timestamp(endDate.getTime()), fromUser.getId(), toAcc.getId());

        for (Map<String, Object> row : rows)
        {
            result.add(new CTransferTransaction.CBuilder()
                    .setId((Integer) row.get("id"))
                    .setFromAccountId((Integer) row.get("account_from_id"))
                    .setToAccountId(toAcc.getId())
                    .setTransferReason((String) row.get("transfer_reason"))
                    .setTransferAmount((Double) row.get("transfer_amount"))
                    .setTrDate(new java.util.Date(((Timestamp) row.get("tr_date")).getTime()))
                    .setFromUserId(fromUser.getId())
                    .setToUserId((Integer) row.get("user_to_id"))
                    .build());
        }

        return result;
    }

    @Override
    public int getTransactionsCount(CAccount account)
    {
        return jdbcTemplate.queryForObject("SELECT COUNT(id) FROM " + TABLE_NAME + " WHERE account_from_id = ? OR account_to_id = ?", Integer.class, account.getId(), account.getId());
    }

    @Override
    public CTransferTransaction loadById(final int transactionId)
    {
        return jdbcTemplate.queryForObject("SELECT account_from_id, account_to_id, transfer_reason, transfer_amount, tr_date, user_from_id, user_to_id " +
                        "FROM " + TABLE_NAME + " WHERE id = ?",
                new RowMapper<CTransferTransaction>() {
                    @Override
                    public CTransferTransaction mapRow(ResultSet resultSet, int i) throws SQLException {
                        return new CTransferTransaction.CBuilder()
                                .setId(transactionId)
                                .setFromAccountId(resultSet.getInt("account_from_id"))
                                .setToAccountId(resultSet.getInt("account_to_id"))
                                .setTransferReason(resultSet.getString("transfer_reason"))
                                .setTransferAmount(resultSet.getDouble("transfer_amount"))
                                .setTrDate(new java.util.Date((resultSet.getTimestamp("tr_date")).getTime()))
                                .setFromUserId(resultSet.getInt("user_from_id"))
                                .setToUserId(resultSet.getInt("user_to_id"))
                                .build();
                    }
                },
                transactionId);
    }
}
