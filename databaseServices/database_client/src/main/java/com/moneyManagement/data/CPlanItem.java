package com.moneyManagement.data;

import com.moneyManagement.exceptions.*;
import com.moneyManagement.utils.*;

import java.util.*;

public class CPlanItem
{
    public static class CBuilder
    {
        private Integer id;
        private Integer userId;
        private Date date;
        private Double amount;

        public CBuilder()
        {
        }

        public CBuilder(CPlanItem planItem)
        {
            id = planItem.id;
            userId = planItem.userId;
            date = planItem.date;
            amount = planItem.amount;
        }

        public CBuilder setUserId(Integer userId)
        {
            this.userId = userId;
            return this;
        }

        public CBuilder setDate(Date date)
        {
            this.date = date;
            return this;
        }

        public CBuilder setAmount(Double amount)
        {
            this.amount = amount;
            return this;
        }

        public CBuilder setId(Integer id) {
            this.id = id;
            return this;
        }

        public CPlanItem build()
        {
            if (date == null)
            {
                throw new CValidationException("Date for plan item was not set");
            }
            else if (amount == null || amount < 0)
            {
                throw new CValidationException("Amount for plan item was not set or negative");
            }
            else if (userId == null)
            {
                throw new CValidationException("User for plan item was not set");
            }

            return new CPlanItem(id, userId, date, amount);
        }
    }

    private Integer id;
    private final Integer userId;
    private final Date date;
    private final Double amount;

    protected CPlanItem(Integer id, Integer userId, Date date, Double amount)
    {
        this.id = id;
        this.userId = userId;
        this.date = date;
        this.amount = amount;
    }

    public Date getDate()
    {
        return date;
    }

    public Double getAmount()
    {
        return amount;
    }

    public Integer getUserId()
    {
        return userId;
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
        if (this == obj)
        {
            return true;
        }

        if (!(obj instanceof CPlanItem))
        {
            return false;
        }

        CPlanItem other = (CPlanItem) obj;

        return CDataUtils.equalsNullSafe(userId, other.userId) &&
                CDataUtils.equalsNullSafe(date, other.date) &&
                CDataUtils.equalsNullSafe(amount, other.amount);
    }

    @Override
    public int hashCode()
    {
        return CDataUtils.calculateHashCode(userId, date, amount);
    }
}
