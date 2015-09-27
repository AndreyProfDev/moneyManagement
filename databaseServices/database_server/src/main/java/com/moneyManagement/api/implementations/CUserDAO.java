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
//@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
public class CUserDAO implements IUserDAO
{
    public static final String TABLE_NAME = "USER";

    private JdbcTemplate template;

    @Autowired
    private IAccountDAO accountDAO;

    public void setDataSource(DataSource dataSource) {
        template = new JdbcTemplate(dataSource);
    }

    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    @Override
    public CUser create(final CUser user)
    {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        template.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement psmt = connection.prepareStatement("INSERT INTO " + TABLE_NAME + " " +
                        "(user_name, user_password, user_currency) " +
                        "VALUES(?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
                psmt.setString(1, user.getLogin());
                psmt.setString(2, user.getPassword());
                psmt.setString(3, user.getBaseCurrency());
                return psmt;
            }
        }, keyHolder);

        user.setId(keyHolder.getKey().intValue());

        return user;
    }

    @Override
    public CUser loadByLogin(String login)
    {
        CUser result =  template.queryForObject("SELECT id, user_name, user_password, user_currency FROM " + TABLE_NAME + " WHERE user_name = ?",
                new RowMapper<CUser>() {
                    @Override
                    public CUser mapRow(ResultSet resultSet, int i) throws SQLException {
                        return new CUser.CBuilder()
                                .setId(resultSet.getInt("id"))
                                .setLogin(resultSet.getString("user_name"))
                                .setPassword(resultSet.getString("user_password"))
                                .setBaseCurrency(resultSet.getString("user_currency"))
                                .build();
                    }
                }, login);

        return result;
    }

    @Override
    public List<CUser> loadAll()
    {
        return loadAll(Collections.<CUser>emptyList());
    }

    @Override
    public List<CUser> loadAll(List<CUser> excludeUsers)
    {
        List<CUser> result = new ArrayList<>();
        List<Map<String, Object>> rows;

        if (excludeUsers.isEmpty())
        {
            rows = template.queryForList("SELECT id, user_name, user_password, user_currency FROM " + TABLE_NAME);
        }
        else
        {
            StringBuilder idsBuilder = new StringBuilder("(");
            idsBuilder.append(excludeUsers.get(0).getId());

            for (int userIdx = 1; userIdx < excludeUsers.size(); userIdx++)
            {
                idsBuilder.append(", ").append(excludeUsers.get(userIdx));
            }

            idsBuilder.append(")");

            rows = template.queryForList("SELECT id, user_name, user_password, user_currency FROM " + TABLE_NAME + " WHERE id not in " + idsBuilder.toString());
        }


        for (Map<String, Object> row : rows)
        {
            result.add(new CUser.CBuilder()
                    .setId((Integer) row.get("id"))
                    .setLogin((String) row.get("user_name"))
                    .setPassword((String) row.get("user_password"))
                    .setBaseCurrency((String) row.get("user_currency"))
                    .build());
        }

        return result;
    }

    @Override
    public CUser loadById(int userId)
    {
        return template.queryForObject("SELECT id, user_name, user_password, user_currency FROM " + TABLE_NAME + " WHERE id = ?",
                new RowMapper<CUser>() {
                    @Override
                    public CUser mapRow(ResultSet resultSet, int i) throws SQLException {
                        return new CUser.CBuilder()
                                .setId(resultSet.getInt("id"))
                                .setLogin(resultSet.getString("user_name"))
                                .setPassword(resultSet.getString("user_password"))
                                .setBaseCurrency(resultSet.getString("user_currency"))
                                .build();
                    }
                }, userId);
    }

    @Override
    public void update(CUser user)
    {
        template.update("UPDATE " + TABLE_NAME + " SET user_name = ?, user_password = ?, user_currency = ?" +
                "WHERE id = ?", user.getLogin(), user.getPassword(), user.getBaseCurrency(), user.getId());
    }

    @Override
    public void remove(CUser user)
    {
        template.update("DELETE FROM " + TABLE_NAME + " WHERE id = ?", user.getId());
    }

    @Override
    public boolean checkExists(String login, String password){
        return template.queryForObject("select count(*) from " + TABLE_NAME + " WHERE user_name = ? and user_password = ?", Integer.class,
                                        login, password) > 0;
    }
}
