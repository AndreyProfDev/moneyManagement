package com.moneyManagement.gui.utils;

public class CPair<T, V>
{
    private T value1;
    private V value2;

    public CPair(T value1, V value2)
    {
        this.value1 = value1;
        this.value2 = value2;
    }

    public T getValue1()
    {
        return value1;
    }

    public V getValue2()
    {
        return value2;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (value1 != null)
        {
            return value2.equals(value1);
        }
        else if (value2 != null)
        {
            return value2.equals(value1);
        }

        return true;
    }
}
