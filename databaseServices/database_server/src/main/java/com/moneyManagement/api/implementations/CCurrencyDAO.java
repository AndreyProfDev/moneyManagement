package com.moneyManagement.api.implementations;


import com.google.common.collect.*;
import com.moneyManagement.api.interfaces.*;
import com.moneyManagement.data.*;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.support.*;
import org.springframework.transaction.annotation.*;

import javax.sql.*;
import java.sql.*;
import java.util.*;

public class CCurrencyDAO implements ICurrencyDAO{

    public static final String TABLE_NAME = "CURRENCY";

    private JdbcTemplate template;

    public void setDataSource(DataSource dataSource) {
        template = new JdbcTemplate(dataSource);
    }

    @Transactional(propagation= Propagation.REQUIRED, readOnly=false)
    @Override
    public String create(final String currency){
        KeyHolder keyHolder = new GeneratedKeyHolder();
        template.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement("INSERT INTO " + TABLE_NAME + " " +
                        "(currency_name) " +
                        "VALUES(?)", Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, currency);
                return ps;
            }
        }, keyHolder);
//        newCategory.setId(keyHolder.getKey().intValue());

        return currency;
    }

    @Override
    public Set<String> loadAll(){
        Set<String> result = new HashSet<>();

        List<Map<String, Object>> rows = template.queryForList("SELECT currency_name FROM " + TABLE_NAME);

        for (Map<String, Object> map : rows)
        {
            result.add((String) map.get("currency_name"));
        }

        return Sets.newHashSet(result);
    }

    @Transactional(propagation= Propagation.REQUIRED, readOnly=false)
    @Override
    public void remove(String currency){
        template.update("DELETE FROM " + TABLE_NAME + " WHERE currency_name = ?", currency);
    }
}
