package com.moneyManagement.data;

public enum EAccountType
{
    BANK_CARD("Bank card"),
    CASH("Cash");

    private String description;

    EAccountType(String description)
    {
        this.description = description;
    }

    public String toString()
    {
        return description;
    }

    public static EAccountType fromString(String accountStr)
    {
        for (EAccountType accType : EAccountType.values())
        {
            if (accType.toString().equals(accountStr))
            {
                return accType;
            }
        }

        return null;
    }
}