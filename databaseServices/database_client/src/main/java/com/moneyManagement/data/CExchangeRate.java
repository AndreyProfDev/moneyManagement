package com.moneyManagement.data;

import com.moneyManagement.exceptions.*;
import com.moneyManagement.utils.*;
import org.springframework.util.*;

import java.io.*;
import java.util.*;

public class CExchangeRate implements Serializable
{
    private Integer id;
    private final String firstCurrency;
    private final String secondCurrency;
    private final Double exchangeRate;
    private final Date enterDate;
    private final Date effectieDate;

    public static class CBuilder
    {
        private Integer id;
        private String firstCurrency;
        private String secondCurrency;
        private Double exchangeRate;
        private Date enterDate;
        private Date effectiveDate;

        public CBuilder()
        {
        }

        public CBuilder(CExchangeRate prototype)
        {
            this.id = prototype.id;
            this.firstCurrency = prototype.firstCurrency;
            this.secondCurrency = prototype.secondCurrency;
            this.exchangeRate = prototype.exchangeRate;
            this.enterDate = prototype.enterDate;
            this.effectiveDate = prototype.effectieDate;
        }

        public CBuilder setFirstCurrency(String firstCurrency)
        {
            this.firstCurrency = firstCurrency;
            return this;
        }

        public CBuilder setSecondCurrency(String secondCurrency)
        {
            this.secondCurrency = secondCurrency;
            return this;
        }

        public CBuilder setExchangeRate(Double exchangeRate)
        {
            this.exchangeRate = exchangeRate;
            return this;
        }

        public CBuilder setEnterDate(Date enterDate)
        {
            this.enterDate = enterDate;
            return this;
        }

        public CBuilder setEffectiveDate(Date effectiveDate)
        {
            this.effectiveDate = effectiveDate;
            return this;
        }

        public CBuilder setId(Integer id) {
            this.id = id;
            return this;
        }

        public CExchangeRate build()
        {
            if (StringUtils.isEmpty(firstCurrency))
            {
                throw new CValidationException("First currency was not set");
            }
            else if (StringUtils.isEmpty(secondCurrency))
            {
                throw new CValidationException("Second currency was not set");
            }
            else if (firstCurrency.equals(secondCurrency))
            {
                throw new CValidationException("Second currency could not be equal first currency");
            }
            else if (exchangeRate == null)
            {
                throw new CValidationException("Exchange rate was not set");
            }
//            else if (enterDate == null)
//            {
//                throw new CValidationException("Enter date was not set");
//            }
            else if (effectiveDate == null)
            {
                throw new CValidationException("Effective date was not set");
            }
            else if (exchangeRate <= 0)
            {
                throw new CValidationException("Exchange rate could not be less or equal zero");
            }

            return new CExchangeRate(id, firstCurrency, secondCurrency, exchangeRate, enterDate, effectiveDate);
        }
    }

    private CExchangeRate(Integer id, String firstCurrency, String secondCurrency, Double exchangeRate, Date enterDate, Date effectieDate)
    {
        this.id = id;
        this.firstCurrency = firstCurrency;
        this.secondCurrency = secondCurrency;
        this.exchangeRate = exchangeRate;
        this.enterDate = enterDate;
        this.effectieDate = effectieDate;
    }

    public String getFirstCurrency()
    {
        return firstCurrency;
    }

    public String getSecondCurrency()
    {
        return secondCurrency;
    }

    public Double getExchangeRate()
    {
        return exchangeRate;
    }

    public Date getEnterDate()
    {
        return enterDate;
    }

    public Date getEffectiveDate()
    {
        return effectieDate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }

        if (!(obj instanceof CExchangeRate))
        {
            return false;
        }

        CExchangeRate other = (CExchangeRate) obj;

        return CDataUtils.equalsNullSafe(firstCurrency, other.firstCurrency) &&
                CDataUtils.equalsNullSafe(secondCurrency, other.secondCurrency) &&
                CDataUtils.equalsNullSafe(exchangeRate, other.exchangeRate) &&
                CDataUtils.equalsNullSafe(enterDate, other.enterDate) &&
                CDataUtils.equalsNullSafe(effectieDate, other.effectieDate);
    }

    public int hashCode()
    {
        return CDataUtils.calculateHashCode(firstCurrency, secondCurrency, exchangeRate, enterDate, effectieDate);
    }
}
