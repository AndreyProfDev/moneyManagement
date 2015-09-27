package com.moneyManagement.upgrades;

import org.springframework.jdbc.core.*;

import javax.sql.*;

public class CUpgradeInitDatabase implements IUpgrade {

    public static final String ACCOUNT_TABLE_NAME = "ACCOUNT";
    public static final String CATEGORY_TABLE_NAME = "CATEGORY";
    public static final String INCOME_EXPENSE_TRANSACTION_TABLE_NAME = "INCOME_EXPENSE_TRANSACTION";
    public static final String INCOME_EXPENSE_TRANSACTION_ITEM_TABLE_NAME = "INCOME_EXPENSE_TRANSACTION_ITEM";
    public static final String PLAN_ITEM_TABLE_NAME = "PLAN_ITEM";
    public static final String TRANSFER_TRANSACTION_TABLE_NAME = "TRANSFER_TRANSACTION";
    public static final String USER_TABLE_NAME = "USER";
    public static final String CHANGE_CURRENCY_TRANSACTION_TABLE_NAME = "CHANGE_CURRENCY_TRANSACTION";
    public static final String CURRENCY_TABLE_NAME = "CURRENCY";
//    public static final String UPDATE_TABLE_TABLE_NAME = "UPDATE_TABLE";
    public static final String EXCHANGE_RATE_NAME = "EXCHANGE_RATE";

    private JdbcTemplate jdbcTemplate;

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void createUserTable()
    {
        getJdbcTemplate().execute("CREATE TABLE IF NOT EXISTS " + USER_TABLE_NAME +
                " (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                "user_name VARCHAR(100), " +
                "user_password VARCHAR(20), " +
                "user_currency VARCHAR(4)," +
                "UNIQUE(user_name))");
    }

    public void createTransferTransactionTable()
    {
        getJdbcTemplate().execute("CREATE TABLE IF NOT EXISTS " + TRANSFER_TRANSACTION_TABLE_NAME +
                " (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                "transfer_reason VARCHAR(100) NOT NULL, " +
                "transfer_amount DOUBLE NOT NULL, " +
                "tr_date TIMESTAMP NOT NULL, " +
                "account_to_id INT NOT NULL, " +
                "user_to_id INT NOT NULL, " +
                "account_from_id INT NOT NULL, " +
                "user_from_id INT NOT NULL)");
    }

    public void createPlanItemTable()
    {
        getJdbcTemplate().execute("CREATE TABLE IF NOT EXISTS " + PLAN_ITEM_TABLE_NAME +
                " (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                "plan_item_date TIMESTAMP NOT NULL, " +
                "plan_item_amount DOUBLE NOT NULL, " +
                "user_id INT NOT NULL, " +
                "UNIQUE (plan_item_date))");
    }

    public void createIncomeExpenseTransactionItemTable()
    {
        getJdbcTemplate().execute("CREATE TABLE IF NOT EXISTS " + INCOME_EXPENSE_TRANSACTION_ITEM_TABLE_NAME +
                " (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                "reason VARCHAR(100), " +
                "amount DOUBLE NOT NULL, " +
                "category_id INT NOT NULL, " +
                "transaction_id INT NOT NULL, " +
                "FOREIGN KEY (transaction_id) REFERENCES " + INCOME_EXPENSE_TRANSACTION_TABLE_NAME + "(id) ON DELETE CASCADE, " +
                "FOREIGN KEY (category_id) REFERENCES " + CATEGORY_TABLE_NAME + "(id) ON DELETE CASCADE)");
    }

    public void createIncomeExpenseTransactionTable()
    {
        getJdbcTemplate().execute("CREATE TABLE IF NOT EXISTS " + INCOME_EXPENSE_TRANSACTION_TABLE_NAME +
                " (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                "tr_date TIMESTAMP NOT NULL, " +
                "user_id INT NOT NULL," +
                "account_id INT NOT NULL, " +
                "category_type VARCHAR(7) NOT NULL, " +
                "FOREIGN KEY (user_id) REFERENCES " + USER_TABLE_NAME + "(id) ON DELETE CASCADE, " +
                "FOREIGN KEY (account_id) REFERENCES " + ACCOUNT_TABLE_NAME + "(id) ON DELETE CASCADE)");
    }

    public void createCategoryTable()
    {
        getJdbcTemplate().execute("CREATE TABLE IF NOT EXISTS " + CATEGORY_TABLE_NAME +
                " (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                "category_name VARCHAR(100) NOT NULL, " +
                "category_type VARCHAR(100) NOT NULL, " +
                "user_id INT NOT NULL, " +
                "FOREIGN KEY (user_id) REFERENCES " + USER_TABLE_NAME + "(id) ON DELETE CASCADE, " +
                "UNIQUE (category_type, category_name))");
    }

    public void createAccountTable()
    {
        getJdbcTemplate().execute("CREATE TABLE IF NOT EXISTS " + ACCOUNT_TABLE_NAME +
                " (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                " account_name VARCHAR(100) NOT NULL, " +
                " account_type VARCHAR(100) NOT NULL, " +
                " balance DOUBLE NOT NULL, " +
                " participate_in_planning BOOLEAN NOT NULL DEFAULT false, " +
                " user_id INT DEFAULT NULL, " + //could be null for shared account
                " currency_type VARCHAR(4) DEFAULT 'RUB'," +
                " creation_date DATE NOT NULL, " +
                " UNIQUE (user_id, account_name, account_type))");
    }

    public void createExchangeRateTable()
    {
        getJdbcTemplate().execute("CREATE TABLE IF NOT EXISTS " + EXCHANGE_RATE_NAME +
                " (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                "first_currency VARCHAR(4) NOT NULL, " +
                "second_currency VARCHAR(4) NOT NULL, " +
                "exchange_rate DOUBLE NOT NULL, " +
                "enter_date TIMESTAMP NOT NULL, " +
                "effective_date TIMESTAMP NOT NULL)");
    }

    public void createChangeCurrencyTransactionTable()
    {
        getJdbcTemplate().execute("CREATE TABLE IF NOT EXISTS " + CHANGE_CURRENCY_TRANSACTION_TABLE_NAME +
                " (id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                "tr_date TIMESTAMP NOT NULL, " +
                "user_id INT NOT NULL," +
                "account_id INT NOT NULL, " +
                "currency_type VARCHAR(4) NOT NULL, " +
                "exchange_curs DOUBLE NOT NULL, " +
                "prev_currency_type VARCHAR(4) NOT NULL, " +
                "FOREIGN KEY (user_id) REFERENCES " + USER_TABLE_NAME + "(id) ON DELETE CASCADE, " +
                "FOREIGN KEY (account_id) REFERENCES " + ACCOUNT_TABLE_NAME + "(id) ON DELETE CASCADE)");
    }

    public void createCurrencyTable()
    {
        getJdbcTemplate().execute("CREATE TABLE IF NOT EXISTS " + CURRENCY_TABLE_NAME +
                " (currency_name VARCHAR(4) NOT NULL PRIMARY KEY)");
    }

    public void apply() {

        System.out.println("Started creating/updating com.moneyManagement.database tables");
        createUserTable();
        createAccountTable();
        createCategoryTable();
        createIncomeExpenseTransactionTable();
        createIncomeExpenseTransactionItemTable();
        createPlanItemTable();
        createTransferTransactionTable();
        createChangeCurrencyTransactionTable();
        createExchangeRateTable();
        createCurrencyTable();
        System.out.println("Database was inited successfully");
    }
}
