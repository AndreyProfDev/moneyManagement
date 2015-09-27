package com.moneyManagement.data;

public enum ECategoryType
{
    INCOME("income"),
    EXPENSE("expense");

    private String name;

    ECategoryType(String name)
    {
        this.name = name;
    }

    public String toString()
    {
        return name;
    }

    public static ECategoryType fromString(String categoryStr)
    {
        for (ECategoryType catType : ECategoryType.values())
        {
            if (catType.toString().equals(categoryStr))
            {
                return catType;
            }
        }

        return null;
    }
}
