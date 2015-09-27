package com.moneyManagement.api.implementations;

import com.moneyManagement.data.CExchangeRate;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CExchangeRateDAOTest extends CAbstractDAOTest
{
    @Autowired
    private CExchangeRateDAO exchangeRateDAO;

    private CExchangeRate exchangeRate1;
    private CExchangeRate exchangeRate2;
    private CExchangeRate exchangeRate3;

    private Date yesterday;
    private Date today;
    private Date tomorrow;

    @Before
    public void before()
    {
        today = new Date();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(today);
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        yesterday = calendar.getTime();
        calendar.add(Calendar.DAY_OF_YEAR, 2);
        tomorrow = calendar.getTime();

        exchangeRate1 = exchangeRateDAO.create(new CExchangeRate.CBuilder()
                                                .setExchangeRate(1.5)
                                                .setEffectiveDate(new Date())
                                                .setEnterDate(today)
                                                .setFirstCurrency("DOL")
                                                .setSecondCurrency("EUR")
                                                .build());

        exchangeRate2 = exchangeRateDAO.create(new CExchangeRate.CBuilder()
                                                .setExchangeRate(1.7)
                                                .setEffectiveDate(new Date())
                                                .setEnterDate(yesterday)
                                                .setFirstCurrency("RUB")
                                                .setSecondCurrency("DOL")
                                                .build());

        exchangeRate3 = exchangeRateDAO.create(new CExchangeRate.CBuilder()
                                                    .setExchangeRate(1.8)
                                                    .setEffectiveDate(new Date())
                                                    .setEnterDate(tomorrow)
                                                    .setFirstCurrency("RUB")
                                                    .setSecondCurrency("EUR")
                                                    .build());
    }

    @Test
    public void test()
    {
        /**
         * Check that loading works
         */

        List<CExchangeRate> rates = exchangeRateDAO.loadAll("DOL");
        assertEquals(2, rates.size());
        assertTrue(rates.contains(exchangeRate1));
        assertTrue(rates.contains(exchangeRate2));

        rates = exchangeRateDAO.loadAll("RUB");
        assertEquals(2, rates.size());
        assertTrue(rates.contains(exchangeRate2));
        assertTrue(rates.contains(exchangeRate3));

        rates = exchangeRateDAO.loadAll("EUR");
        assertEquals(2, rates.size());
        assertTrue(rates.contains(exchangeRate3));
        assertTrue(rates.contains(exchangeRate1));

        /**
         * Check that update works
         */
        exchangeRate3 = new CExchangeRate.CBuilder(exchangeRate3)
                .setExchangeRate(1.8)
                .setEffectiveDate(new Date())
                .setEnterDate(new Date())
                .setFirstCurrency("DOL")
                .setSecondCurrency("EUR")
                .build();
        exchangeRateDAO.update(exchangeRate3);

        rates = exchangeRateDAO.loadAll("DOL");
        assertEquals(3, rates.size());
        assertTrue(rates.contains(exchangeRate1));
        assertTrue(rates.contains(exchangeRate2));
        assertTrue(rates.contains(exchangeRate3));

        rates = exchangeRateDAO.loadAll("RUB");
        assertEquals(1, rates.size());
        assertTrue(rates.contains(exchangeRate2));

        rates = exchangeRateDAO.loadAll("EUR");
        assertEquals(2, rates.size());
        assertTrue(rates.contains(exchangeRate3));
        assertTrue(rates.contains(exchangeRate1));

        exchangeRate3 = new CExchangeRate.CBuilder(exchangeRate3)
                .setExchangeRate(1 / 1.8)
                .setFirstCurrency("EUR")
                .setSecondCurrency("DOL")
                .build();

        assertEquals(exchangeRate3, exchangeRateDAO.loadLast("EUR", "DOL"));

        assertTrue(exchangeRateDAO.checkExists("EUR", "DOL"));
        assertFalse(exchangeRateDAO.checkExists("EUR", "JPY"));
    }

    @After
    public void after(){
        exchangeRateDAO.remove(exchangeRate1);
        exchangeRateDAO.remove(exchangeRate2);
        exchangeRateDAO.remove(exchangeRate3);
    }
}
