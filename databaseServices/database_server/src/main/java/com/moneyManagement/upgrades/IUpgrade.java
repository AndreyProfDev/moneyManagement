package com.moneyManagement.upgrades;

import javax.sql.*;

public interface IUpgrade {

    void setDataSource(DataSource dataSource);

    void apply();
}
