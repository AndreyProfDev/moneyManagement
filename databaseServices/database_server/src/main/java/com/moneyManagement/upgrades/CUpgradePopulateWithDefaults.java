package com.moneyManagement.upgrades;

import com.moneyManagement.api.implementations.CCurrencyDAO;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;

public class CUpgradePopulateWithDefaults implements IUpgrade{

    @Autowired
    private CCurrencyDAO currencyDAO;

    public void setDataSource(DataSource dataSource) {
    }

    public void apply() {

        currencyDAO.create("EUR");
        currencyDAO.create("RUB");
        currencyDAO.create("DOL");
    }
}
