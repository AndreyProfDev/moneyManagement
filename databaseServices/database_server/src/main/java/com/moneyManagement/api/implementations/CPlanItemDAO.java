package com.moneyManagement.api.implementations;

import com.moneyManagement.api.interfaces.*;
import com.moneyManagement.data.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.core.support.*;
import org.springframework.jdbc.support.*;
import org.springframework.transaction.annotation.*;

import javax.sql.*;
import java.sql.*;
import java.util.*;
import java.util.Date;

//@Component
//@Transactional(propagation= Propagation.REQUIRED, readOnly=true)
public class CPlanItemDAO extends JdbcDaoSupport implements IPlanItemDAO
{
    public static final String TABLE_NAME = "PLAN_ITEM";

    @Autowired
    private CIncomeExpenseTransactionDAO incomeTransactionDAO;

//    @Autowired
//    private CTransferTransactionInternalDAO transferTransactionInternalDAO;
//
//    @Autowired
//    private CTransferTransactionExternalDAO transferTransactionExternalDAO;

    @Autowired
    private CAccountDAO accountDAO;


    @Autowired
    public CPlanItemDAO(DataSource dataSource)
    {
        super();
        setDataSource(dataSource);
    }

    @Transactional(propagation= Propagation.REQUIRED, readOnly=false)
    @Override
    public CPlanItem create(final CPlanItem planToCreate)
    {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        getJdbcTemplate().update(new PreparedStatementCreator()
        {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException
            {
                PreparedStatement psmt = connection.prepareStatement("INSERT INTO " + TABLE_NAME + " " +
                        "(plan_item_date, plan_item_amount, user_id) " +
                        "VALUES(?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
                psmt.setTimestamp(1, new Timestamp(planToCreate.getDate().getTime()));
                psmt.setDouble(2, planToCreate.getAmount());
                psmt.setInt(3, planToCreate.getUserId());

                return psmt;
            }
        }, keyHolder);
        planToCreate.setId(keyHolder.getKey().intValue());

        return planToCreate;
    }

    @Transactional(propagation= Propagation.REQUIRED, readOnly=false)
    @Override
    public void update(CPlanItem plan)
    {
        getJdbcTemplate().update("UPDATE " + TABLE_NAME + " SET plan_item_date = ?, plan_item_amount = ? WHERE id = ?",
                new Timestamp(plan.getDate().getTime()), plan.getAmount(), plan.getId());
    }

    @Transactional(propagation= Propagation.REQUIRED, readOnly=false)
    @Override
    public void remove(CPlanItem plan)
    {
        getJdbcTemplate().update("DELETE FROM " + TABLE_NAME + " WHERE id = ?", plan.getId());
    }

    @Override
    public List<CPlanItem> loadPlans(CUser user)
    {
        List<CPlanItem> result = new ArrayList<>();

        List<Map<String, Object>> rows = getJdbcTemplate().queryForList("SELECT id, plan_item_date, plan_item_amount, user_id " +
                "FROM " + TABLE_NAME + " WHERE user_id = ? ORDER BY plan_item_date", user.getId());

        for (Map<String, Object> row : rows)
        {
            result.add(new CPlanItem.CBuilder()
                    .setId((Integer) row.get("id"))
                    .setDate(new Date(((Timestamp) row.get("plan_item_date")).getTime()))
                    .setAmount((Double) row.get("plan_item_amount"))
                    .setUserId(user.getId())
                    .build());
        }

        return result;
    }

    @Override
    public List<CPlanItem> loadPlans(CUser user, Date startDate, Date endDate)
    {
        List<CPlanItem> result = new ArrayList<>();

        List<Map<String, Object>> rows = getJdbcTemplate().queryForList("SELECT id, plan_item_date, plan_item_amount, user_id " +
                        "FROM " + TABLE_NAME + " WHERE user_id = ? AND plan_item_date >= ? AND plan_item_date <= ? ORDER BY plan_item_date",
                user.getId(), new Timestamp(startDate.getTime()), new Timestamp(endDate.getTime()));

        for (Map<String, Object> row : rows)
        {
            result.add(new CPlanItem.CBuilder()
                        .setId((Integer) row.get("id"))
                        .setDate(new Date(((Timestamp) row.get("plan_item_date")).getTime()))
                        .setAmount((Double) row.get("plan_item_amount"))
                        .setUserId(user.getId())
                        .build());
        }

        return result;
    }

    @Override
    public List<CPlanItemResult> loadPlanResults(CUser user, Date startDate, Date endDate)
    {
        List<CPlanItem> planItems = loadPlans(user, startDate, endDate);

        List<CAccount> accountsToCheck = accountDAO.loadAccounts(user, true);

        List<CPlanItemResult> result = new ArrayList<>();

        for (CPlanItem planItem : planItems)
        {
            double planItemBalance = 0;

            for (CAccount acc : accountsToCheck)
            {
                double accountBalanseChange = 0;

                if (!planItem.getDate().equals(new Date()))
                {
                    List<CIncomeExpenseTransaction> trs = incomeTransactionDAO.loadAll(user, acc, planItem.getDate(), new Date());

                    for (CIncomeExpenseTransaction tr : trs)
                    {
                        accountBalanseChange -= tr.getCategory().equals(ECategoryType.INCOME) ? tr.getSum() : (-1) * tr.getSum();
                    }

//                    List<CTransferTransactionExternal> externalTrs = transferTransactionExternalDAO.loadAll(user, acc, planItem.getDate(), new Date());
//
//                    for (CTransferTransactionExternal externalTr : externalTrs)
//                    {
//                        accountBalanseChange += externalTr.getTransferAmount();
//                    }
//
//                    List<CTransferTransactionInternal> internalTrsFrom = transferTransactionInternalDAO.loadAllFrom(user, acc, planItem.getDate(), new Date());
//
//                    for (CTransferTransactionInternal internalTrFrom : internalTrsFrom)
//                    {
//                        accountBalanseChange += internalTrFrom.getTransferAmount();
//                    }
//
//                    List<CTransferTransactionInternal> internalTrsTo = transferTransactionInternalDAO.loadAllTo(user, acc, planItem.getDate(), new Date());
//
//                    for (CTransferTransactionInternal internalTrTo : internalTrsTo)
//                    {
//                        accountBalanseChange -= internalTrTo.getTransferAmount();
//                    }
                }

                double accountBalanse = acc.getBalance() + accountBalanseChange;
                planItemBalance += accountBalanse;
            }

            result.add(new CPlanItemResult.CBuilder()
                    .setSource(planItem)
                    .setPlanResult(planItemBalance)
                    .build());
        }

        return result;
    }

    public List<CPlanItem> loadAll()
    {
        List<CPlanItem> result = new ArrayList<>();

        List<Map<String, Object>> rows = getJdbcTemplate().queryForList("SELECT id, user_id, plan_item_date, plan_item_amount, user_id FROM " + TABLE_NAME);

        for (Map<String, Object> row : rows)
        {
            result.add(new CPlanItem.CBuilder()
                    .setId((Integer) row.get("id"))
                    .setDate(new Date(((Timestamp) row.get("plan_item_date")).getTime()))
                    .setAmount((Double) row.get("plan_item_amount"))
                    .setUserId((Integer) row.get("user_id"))
                    .build());
        }

        return result;
    }
}
