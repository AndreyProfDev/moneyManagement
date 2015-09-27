package com.moneyManagement.data;

import com.moneyManagement.exceptions.*;
import com.moneyManagement.utils.*;

import java.io.*;
import java.util.regex.*;

public class CAccount implements Serializable
{
    public static class CBuilder
    {
        private static Pattern accountNamePattern = Pattern.compile("^.{3,100}$");

        private Integer id;
        private Integer userId;
        private String name;
        private EAccountType type;
        private String currency;
        private Double balance;
        private Boolean includeInPlanning;

        public CBuilder()
        {
        }

        public CBuilder(CAccount prototype)
        {
            this.id = prototype.getId();
            this.userId = prototype.userId;
            this.name = prototype.name;
            this.type = prototype.type;
            this.currency = prototype.currency;
            this.balance = prototype.balance;
            this.includeInPlanning = prototype.includeInPlanning;
        }

        public CBuilder setUserId(Integer userId)
        {
            this.userId = userId;
            return this;
        }

        public CBuilder setName(String name)
        {
            this.name = name;
            return this;
        }

        public CBuilder setType(EAccountType type)
        {
            this.type = type;
            return this;
        }

        public CBuilder setBalance(double balance)
        {
            this.balance = balance;
            return this;
        }

        public CBuilder setIncludeInPlanning(boolean includeInPlanning)
        {
            this.includeInPlanning = includeInPlanning;
            return this;
        }

        public CBuilder setCurrency(String currency)
        {
            this.currency = currency;
            return this;
        }

        public CBuilder setId(Integer id) {
            this.id = id;
            return this;
        }

        public CAccount build()
        {
            if (name == null || name.isEmpty())
            {
                throw new CValidationException("Account name was not initialized");
            }
            else if (!accountNamePattern.matcher(name).matches())
            {
                throw new CValidationException("Account name '" + name + "' is invalid.\n" +
                        "It could contain only uppercase and lowercase symbols, space, symbol '_', and its length must be from 3 to 100");
            }
            else if (type == null)
            {
                throw new CValidationException("Account type for account '" + name + "' was not initialized");
            }
            else if (currency == null)
            {
                throw new CValidationException("Account currency name for account '" + name + "' was not initialized");
            }
            else if (balance == null)
            {
                throw new CValidationException("Account user for account '" + name + "' was not initialized");
            }
            else if (balance < 0)
            {
                throw new CValidationException("Account balance for account '" + name + "' is negative");
            }
            else if (includeInPlanning == null)
            {
                throw new CValidationException("Account planning state for account '" + name + "' was not initialized");
            }

            return new CAccount(id, userId, name, type, currency, balance, includeInPlanning);
        }
    }

    private Integer id;
    private final Integer userId;
    private final String name;
    private final EAccountType type;
    private final String currency;
    private final Double balance;
    private final Boolean includeInPlanning;

    private CAccount(Integer id, Integer userId, String name, EAccountType type, String currency, double balance, boolean includeInPlanning)
    {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.type = type;
        this.currency = currency;
        this.balance = balance;
        this.includeInPlanning = includeInPlanning;
    }

    public Integer getUserId()
    {
        return userId;
    }

    public String getName()
    {
        return name;
    }

    public EAccountType getType()
    {
        return type;
    }

    public double getBalance()
    {
        return balance;
    }

    public boolean isIncludeInPlanning()
    {
        return includeInPlanning;
    }

    public String getCurrency()
    {
        return currency;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    @Override
    public String toString()
    {
        return name;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }

        if (!(obj instanceof CAccount))
        {
            return false;
        }

        CAccount other = (CAccount) obj;

        return CDataUtils.equalsNullSafe(userId, other.userId) &&
               CDataUtils.equalsNullSafe(name, other.name) &&
               CDataUtils.equalsNullSafe(type, other.type) &&
               CDataUtils.equalsNullSafe(currency, other.currency) &&
               CDataUtils.equalsNullSafe(balance, other.balance) &&
               CDataUtils.equalsNullSafe(includeInPlanning, other.includeInPlanning);
    }

    @Override
    public int hashCode()
    {
        return CDataUtils.calculateHashCode(userId, name, type, currency, balance, includeInPlanning);
    }
}
