package com.moneyManagement.data;

import com.moneyManagement.utils.*;

import java.io.*;
import java.util.*;

public abstract class CTransaction implements Serializable
{
    private Integer id;
    private Date date;
    private int userId;

    protected CTransaction(Integer id, Date date, int userId)
    {
        this.id = id;
        this.date = date;
        this.userId = userId;
    }

    public Date getDate()
    {
        return date;
    }

    public void setDate(Date date)
    {
        this.date = date;
    }

    public int getUserId()
    {
        return userId;
    }

    public void setUserId(int userId)
    {
        this.userId = userId;
    }

    public abstract String getTransactionName();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof CTransaction))
        {
            return false;
        }

        CTransaction other = (CTransaction) obj;

        return CDataUtils.equalsNullSafe(date, other.date) &&
               userId == other.userId;
    }
}
