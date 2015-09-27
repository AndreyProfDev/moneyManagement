package com.moneyManagement.upgrades;

import org.springframework.jdbc.core.*;

import javax.sql.*;

public class CUpgradeResetDatabase implements IUpgrade{

    private JdbcTemplate jdbcTemplate;

    @Override
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public void apply() {
        jdbcTemplate.update("DROP ALL OBJECTS");
    }
}
