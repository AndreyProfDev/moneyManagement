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
import java.util.Date;

//@Component
//@Transactional(propagation= Propagation.REQUIRED, readOnly=true)
public class CExchangeRateDAO implements IExchangeRateDAO
{
    public static final String TABLE_NAME = "EXCHANGE_RATE";

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public CExchangeRateDAO(DataSource dataSource)
    {
        super();
        setDataSource(dataSource);
    }

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Transactional(propagation= Propagation.REQUIRED, readOnly=false)
    @Override
    public CExchangeRate create(final CExchangeRate exchangeRate)
    {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(new PreparedStatementCreator() {
            @Override
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                PreparedStatement ps = connection.prepareStatement("INSERT INTO " + TABLE_NAME + " " +
                        "(first_currency, second_currency, exchange_rate, enter_date, effective_date) " +
                        "VALUES(?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, exchangeRate.getFirstCurrency().toString());
                ps.setString(2, exchangeRate.getSecondCurrency().toString());
                ps.setDouble(3, exchangeRate.getExchangeRate());
                ps.setTimestamp(4, new Timestamp(exchangeRate.getEnterDate().getTime()));
                ps.setTimestamp(5, new Timestamp(exchangeRate.getEffectiveDate().getTime()));
                return ps;
            }
        }, keyHolder);
        exchangeRate.setId(keyHolder.getKey().intValue());

        return exchangeRate;
    }

    @Transactional(propagation= Propagation.REQUIRED, readOnly=false)
    @Override
    public void update(CExchangeRate exchangeRate)
    {
        jdbcTemplate.update("UPDATE " + TABLE_NAME + " SET first_currency = ?, second_currency = ?, exchange_rate = ?, enter_date = ?, effective_date = ? WHERE id = ?",
                exchangeRate.getFirstCurrency().toString(), exchangeRate.getSecondCurrency().toString(),
                exchangeRate.getExchangeRate(), new Timestamp(exchangeRate.getEnterDate().getTime()),
                exchangeRate.getEffectiveDate(), exchangeRate.getId());
    }

    @Transactional(propagation= Propagation.REQUIRED, readOnly=false)
    @Override
    public void remove(CExchangeRate exchangeRate)
    {
        jdbcTemplate.update("DELETE FROM " + TABLE_NAME + " WHERE id = ?", exchangeRate.getId());
    }

    @Override
    public List<CExchangeRate> loadAll(String currency)
    {
        List<CExchangeRate> result = new ArrayList<>();

        List<Map<String, Object>> rows = jdbcTemplate.queryForList("SELECT id, first_currency, second_currency, exchange_rate, enter_date, effective_date " +
                        "FROM " + TABLE_NAME + " WHERE first_currency = ? OR second_currency = ?",
                currency, currency);

        for (Map<String, Object> map : rows)
        {
            int id = (int) map.get("id");

            result.add(new CExchangeRate.CBuilder()
                    .setFirstCurrency((String) map.get("first_currency"))
                    .setSecondCurrency((String) map.get("second_currency"))
                    .setExchangeRate((Double) map.get("exchange_rate"))
                    .setEnterDate(new Date(((Timestamp) map.get("enter_date")).getTime()))
                    .setEffectiveDate(new Date(((Timestamp) map.get("effective_date")).getTime()))
                    .setId(id)
                    .build());
        }

        return result;
    }

//    @Override
//    public CExchangeRate loadLast(String currency)
//    {
//
//        return jdbcTemplate.queryForObject("SELECT id, first_currency, second_currency, exchange_rate, enter_date, effective_date " +
//                        "FROM " + TABLE_NAME + " WHERE effective_date = " +
//                        "(SELECT MAX(effective_date) FROM " + TABLE_NAME + " WHERE " +
//                        "first_currency = ? OR second_currency = ?)",
//                new RowMapper<CExchangeRate>()
//                {
//                    @Override
//                    public CExchangeRate mapRow(ResultSet resultSet, int i) throws SQLException
//                    {
//                        return new CExchangeRate.CBuilder()
//                                .setFirstCurrency(resultSet.getString("first_currency"))
//                                .setSecondCurrency(resultSet.getString("second_currency"))
//                                .setExchangeRate(resultSet.getDouble("exchange_rate"))
//                                .setEnterDate(new Date( resultSet.getTimestamp("enter_date").getTime()))
//                                .setEffectiveDate(new Date(resultSet.getTimestamp("effective_date").getTime()))
//                                .setId(resultSet.getInt("id"))
//                                .build();
//                    }
//                },
//                currency.toString(), currency.toString());
//    }

    public CExchangeRate loadLast(String firstCurrency, String secondCurrency)
    {

        CExchangeRate exchangeRate = jdbcTemplate.queryForObject("SELECT id, first_currency, second_currency, exchange_rate, enter_date, effective_date " +
                        "FROM " + TABLE_NAME + " WHERE effective_date = " +
                        "(SELECT MAX(enter_date) FROM " + TABLE_NAME + " WHERE " +
                        "(first_currency = ? AND second_currency = ?) OR (first_currency = ? AND second_currency = ?))",
                new RowMapper<CExchangeRate>()
                {
                    @Override
                    public CExchangeRate mapRow(ResultSet resultSet, int i) throws SQLException
                    {
                        return new CExchangeRate.CBuilder()
                                .setFirstCurrency(resultSet.getString("first_currency"))
                                .setSecondCurrency(resultSet.getString("second_currency"))
                                .setExchangeRate(resultSet.getDouble("exchange_rate"))
                                .setEnterDate(new Date( resultSet.getTimestamp("enter_date").getTime()))
                                .setEffectiveDate(new Date(resultSet.getTimestamp("effective_date").getTime()))
                                .setId(resultSet.getInt("id"))
                                .build();
                    }
                },
                firstCurrency, secondCurrency, secondCurrency, firstCurrency);

        if (exchangeRate != null && !exchangeRate.getFirstCurrency().equals(firstCurrency)){
            exchangeRate = new CExchangeRate.CBuilder(exchangeRate)
                    .setFirstCurrency(firstCurrency)
                    .setSecondCurrency(secondCurrency)
                    .setExchangeRate(1 / exchangeRate.getExchangeRate())
                    .build();
        }

        return exchangeRate;
    }

    public boolean checkExists(String firstCurrency, String secondCurrency){
        return jdbcTemplate.queryForObject("select count(*) from " + TABLE_NAME +
                " WHERE (first_currency = ? and second_currency = ?) " +
                " or (second_currency = ? and first_currency = ?) ", Integer.class, firstCurrency, secondCurrency, firstCurrency, secondCurrency) > 0;
    }
}
