package com.moneyManagement.data;

import com.moneyManagement.exceptions.*;
import com.moneyManagement.utils.*;

import java.io.*;
import java.util.regex.*;

public class CCategory implements Serializable
{
    public static class CBuilder
    {
        private static Pattern categoryNamePattern = Pattern.compile("^.{3,100}$");

        private Integer id;
        private String name;
        private ECategoryType type;
        private Integer userId;

        public CBuilder()
        {
        }

        public CBuilder(CCategory prototype)
        {
            id = prototype.id;
            name = prototype.name;
            type = prototype.type;
            userId = prototype.userId;
        }

        public CBuilder setName(String name)
        {
            this.name = name;
            return this;
        }

        public CBuilder setType(ECategoryType type)
        {
            this.type = type;
            return this;
        }

        public CBuilder setUserId(Integer userId)
        {
            this.userId = userId;
            return this;
        }

        public CBuilder setId(Integer id) {
            this.id = id;
            return this;
        }

        public CCategory build()
        {
            if (name == null)
            {
                throw new CValidationException("Category name was not initialized");
            }
            else if (!categoryNamePattern.matcher(name).matches())
            {
                throw new CValidationException("Category name " + name + " is invalid.\n" +
                        "It could contain only uppercase and lowercase symbols, space, symbol '_', and its length must be from 3 to 50");
            }
            else if (type == null)
            {
                throw new CValidationException("Category type was not initialized");
            }
            else if (userId == null)
            {
                throw new CValidationException("Category user was not initialized");
            }

            return new CCategory(id, name, type, userId);
        }
    }

    private Integer id;
    private final String name;
    private final ECategoryType type;
    private final Integer userId;

    private CCategory(Integer id, String name, ECategoryType type, int userId)
    {
        this.id = id;
        this.name = name;
        this.type = type;
        this.userId = userId;
    }

    public String getName()
    {
        return name;
    }

    public ECategoryType getType()
    {
        return type;
    }

    public int getUserId()
    {
        return userId;
    }

    @Override
    public String toString()
    {
        return name;
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

        if (!(obj instanceof CCategory))
        {
            return false;
        }

        CCategory other = (CCategory) obj;

        return CDataUtils.equalsNullSafe(name, other.name) &&
               CDataUtils.equalsNullSafe(type, other.type) &&
               CDataUtils.equalsNullSafe(userId, other.userId);
    }

    @Override
    public int hashCode()
    {
        return CDataUtils.calculateHashCode(name, type, userId);
    }
}
