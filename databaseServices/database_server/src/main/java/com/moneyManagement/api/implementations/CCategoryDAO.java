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

//@Transactional(propagation= Propagation.REQUIRED, readOnly=true)
public class CCategoryDAO implements ICategoryDAO
{
    public static final String TABLE_NAME = "CATEGORY";

    @Autowired
    private CIncomeExpenseTransactionItemDAO incomeExpenseTransactionItemDAO;

    private JdbcTemplate template;

    public void setDataSource(DataSource dataSource) {
        template = new JdbcTemplate(dataSource);
    }

    @Transactional(propagation= Propagation.REQUIRED, readOnly=false)
    @Override
    public CCategory create(final CCategory newCategory)
    {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        template.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement("INSERT INTO " + TABLE_NAME + " " +
                        "(category_name, category_type, user_id) " +
                        "VALUES(?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, newCategory.getName());
                ps.setString(2, newCategory.getType().toString());
                ps.setInt(3, newCategory.getUserId());
                return ps;
            }
        }, keyHolder);
        newCategory.setId(keyHolder.getKey().intValue());

        return newCategory;
    }

    @Transactional(propagation= Propagation.REQUIRED, readOnly=false)
    @Override
    public void update(CCategory category)
    {
        template.update("UPDATE " + TABLE_NAME + " SET category_name = ? WHERE id = ?",
                category.getName(), category.getId());
    }

    @Transactional(propagation= Propagation.REQUIRED, readOnly=false)
    @Override
    public void remove(CCategory category)
    {
        if (incomeExpenseTransactionItemDAO.getTransactionsCount(category) > 0)
        {
            throw new CValidationException("Category " + category + " could not be removed. It have related entities.");
        }

        template.update("DELETE FROM " + TABLE_NAME + " WHERE id = ?", category.getId());

        //TODO remove all related transactions
    }

    @Override
    public List<CCategory> loadCategories(ECategoryType catType, CUser user)
    {
        List<CCategory> result = new ArrayList<>();

        List<Map<String, Object>> rows = template.queryForList("SELECT id, category_name, category_type " +
                "FROM " + TABLE_NAME + " WHERE category_type = ? AND user_id = ?", catType.toString(), user.getId());

        for (Map<String, Object> map : rows)
        {
            result.add(new CCategory.CBuilder()
                    .setId((Integer) map.get("id"))
                    .setName((String) map.get("category_name"))
                    .setUserId(user.getId())
                    .setType(ECategoryType.fromString((String) map.get("category_type")))
                    .build());
        }

        return result;
    }

    @Override
    public List<CCategory> loadCategories(CUser user)
    {
        List<CCategory> result = new ArrayList<>();

        List<Map<String, Object>> rows = template.queryForList("SELECT id, category_name, category_type " +
                "FROM " + TABLE_NAME + " WHERE user_id = ?", user.getId());

        for (Map<String, Object> map : rows)
        {
            result.add(new CCategory.CBuilder()
                    .setId((Integer) map.get("id"))
                    .setName((String) map.get("category_name"))
                    .setUserId(user.getId())
                    .setType(ECategoryType.fromString((String) map.get("category_type")))
                    .build());
        }

        return result;
    }

    @Override
    public CCategory loadById(final int categoryId)
    {
        return template.queryForObject("SELECT category_name, category_type, user_id FROM " + TABLE_NAME + " WHERE id = ?",
                new RowMapper<CCategory>() {
                    public CCategory mapRow(ResultSet rs, int rowNum)
                            throws SQLException {
                        return new CCategory.CBuilder()
                                .setId(categoryId)
                                .setName(rs.getString("category_name"))
                                .setType(ECategoryType.fromString(rs.getString("category_type")))
                                .setUserId(rs.getInt("user_id"))
                                .build();
                    }
                }, categoryId);
    }

    @Override
    public boolean checkCategoriesExists(CUser user)
    {
        return template.queryForObject("select count(*) from " + TABLE_NAME + " WHERE user_id = ?", Integer.class, user.getId()) > 0;
    }
}
