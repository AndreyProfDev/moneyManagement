package com.moneyManagement.data;

import com.moneyManagement.exceptions.*;
import com.moneyManagement.utils.*;

import java.io.*;

public class CIncomeExpenseTransactionItem implements Serializable
{
    public static class CBuilder
    {
        private Integer id;
        private Integer transactionId;
        private CCategory category;
        private String comment;
        private Double amount;

        public CBuilder()
        {

        }

        public CBuilder(CIncomeExpenseTransactionItem prototype)
        {
            id = prototype.id;
            comment = prototype.comment;
            transactionId = prototype.transactionId;
            category = prototype.category;
            amount = prototype.amount;
        }

        public CBuilder setCategory(CCategory category)
        {
            this.category = category;
            return this;
        }

        public CBuilder setAmount(Double amount)
        {
            this.amount = amount;
            return this;
        }

        public CBuilder setTransactionId(Integer transactionId)
        {
            this.transactionId = transactionId;
            return this;
        }

        public CBuilder setComment(String comment) {
            this.comment = comment;
            return this;
        }

        public CBuilder setId(Integer id) {
            this.id = id;
            return this;
        }

        public CIncomeExpenseTransactionItem build()
        {
            if (category == null)
            {
                throw new CValidationException("Category for income transaction item was not set");
            }
            else if (amount == null || amount <= 0)
            {
                throw new CValidationException("Amount for income transaction item was not set or negative");
            }

            return new CIncomeExpenseTransactionItem(id, comment, transactionId, category, amount);
        }
    }

    private Integer id;
    private final String comment;
    private Integer transactionId;
    private final CCategory category;
    private final double amount;

    private CIncomeExpenseTransactionItem(Integer id, String comment, Integer transactionId, CCategory category, Double amount)
    {
        this.id = id;
        this.category = category;
        this.amount = amount;
        this.transactionId = transactionId;
        this.comment = comment;
    }

    public int getTransactionId()
    {
        return transactionId;
    }

    public CCategory getCategory()
    {
        return category;
    }

    public double getAmount()
    {
        return amount;
    }

    public String getComment() {
        return comment;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setTransactionId(Integer transactionId) {
        this.transactionId = transactionId;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }

        if (!(obj instanceof CIncomeExpenseTransactionItem))
        {
            return false;
        }

        CIncomeExpenseTransactionItem other = (CIncomeExpenseTransactionItem) obj;

        return CDataUtils.equalsNullSafe(category, other.category) &&
                CDataUtils.equalsNullSafe(amount, other.amount);
    }

    @Override
    public int hashCode()
    {
        return CDataUtils.calculateHashCode(transactionId, category, amount);
    }
}
