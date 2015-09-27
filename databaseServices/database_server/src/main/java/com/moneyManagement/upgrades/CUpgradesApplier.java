package com.moneyManagement.upgrades;

import javax.sql.*;
import java.util.*;

public class CUpgradesApplier{

    private List<IUpgrade> commonUpgrades;

    private DataSource dataSource;

    public void setCommonUpgrades(List<IUpgrade> commonUpgrades) {
        this.commonUpgrades = commonUpgrades;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void applyUpgrades(){
        for (IUpgrade upgrade : commonUpgrades){
            upgrade.setDataSource(dataSource);
            upgrade.apply();
        }
    }
}
