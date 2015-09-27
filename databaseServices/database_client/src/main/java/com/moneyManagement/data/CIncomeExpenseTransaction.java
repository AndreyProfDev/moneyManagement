package com.moneyManagement.data;

import com.moneyManagement.exceptions.*;
import com.moneyManagement.utils.*;

import java.util.*;

public class CIncomeExpenseTransaction extends CTransaction
{
    public static class CBuilder
    {
        private Integer id;
        private Date date;
        private Integer userId;
        private Integer accountId;
        private ECategoryType category;
        private List<CIncomeExpenseTransactionItem> items = new ArrayList<>();

        public CBuilder()
        {

        }

        public CBuilder(CIncomeExpenseTransaction prototype)
        {
            id = prototype.getId();
            date = prototype.getDate();
            userId = prototype.getUserId();
            accountId = prototype.accountId;
            items = prototype.items;
            category = prototype.category;
        }

        public CBuilder setDate(Date date)
        {
            this.date = date;
            return this;
        }

        public CBuilder setUserId(int userId)
        {
            this.userId = userId;
            return this;
        }

        public CBuilder setAccountId(int accountId)
        {
            this.accountId = accountId;
            return this;
        }

        public CBuilder addItem(CIncomeExpenseTransactionItem item)
        {
            items.add(item);
            return this;
        }

        public CBuilder setItems(List<CIncomeExpenseTransactionItem> items)
        {
            this.items = items;
            return this;
        }

        public CBuilder setId(Integer id) {
            this.id = id;
            return this;
        }

        public CBuilder setCategory(ECategoryType category) {
            this.category = category;
            return this;
        }

        public CIncomeExpenseTransaction build() {
            if (date == null) {
                throw new CValidationException("Date for income transaction was not set");
            } else if (userId == null) {
                throw new CValidationException("User for income transaction was not set");
            } else if (accountId == null) {
                throw new CValidationException("Account for income transaction was not set");
            } else if (items.isEmpty()) {
                throw new CValidationException("Items for income transaction were not set");
            } else if (category == null) {
                throw new CValidationException("Transaction category was not set");
            }

            return new CIncomeExpenseTransaction(id, category, userId, accountId, date, items);
        }
    }

    private final Integer accountId;
    private final List<CIncomeExpenseTransactionItem> items;
    private final double sum;
    private final ECategoryType category;

    private CIncomeExpenseTransaction(Integer id, ECategoryType category, Integer userId, Integer accountId, Date date, List<CIncomeExpenseTransactionItem> items)
    {
        super(id, date, userId);
        this.items = items;
        this.accountId = accountId;

        double sum = 0;
        for (CIncomeExpenseTransactionItem item : items)
        {
            sum += item.getAmount();
        }

        this.sum = sum;
        this.category = category;
    }

    public int getAccountId()
    {
        return accountId;
    }

    public List<CIncomeExpenseTransactionItem> getItems()
    {
        return items;
    }

    public double getSum()
    {
        return sum;
    }

    @Override
    public String getTransactionName()
    {
        return ECategoryType.INCOME.equals(getCategory()) ? "Income transaction" : "Expense transaction";
    }

    public ECategoryType getCategory() {
        return category;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }

        if (!(obj instanceof CIncomeExpenseTransaction))
        {
            return false;
        }

        CIncomeExpenseTransaction other = (CIncomeExpenseTransaction) obj;

        for (CIncomeExpenseTransactionItem item : items)
        {
            if (!other.items.contains(item))
            {
                return false;
            }
        }

        return CDataUtils.equalsNullSafe(accountId, other.accountId) &&
                CDataUtils.equalsNullSafe(sum, other.sum) &&
                CDataUtils.equalsNullSafe(getDate(), other.getDate());
    }

    @Override
    public int hashCode()
    {
        return CDataUtils.calculateHashCode(accountId, sum, items, getDate());
    }
}
