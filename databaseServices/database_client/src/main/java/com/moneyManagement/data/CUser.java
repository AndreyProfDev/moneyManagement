package com.moneyManagement.data;

import com.moneyManagement.exceptions.*;
import com.moneyManagement.utils.*;
import org.springframework.util.*;

import java.io.*;
import java.util.regex.*;

public class CUser implements Serializable
{
    public static class CBuilder
    {
        private static Pattern usernamePattern = Pattern.compile("^.{3,100}$");

        private static Pattern userpasswordPattern = Pattern.compile(
        "^" +                       //start-of-string
            "(?=.*[0-9])" +         //a digit must occur at least once
            "(?=.*[a-z])" +         //a lower case letter must occur at least once
            "(?=.*[A-Z])" +         //an upper case letter must occur at least once
            "(?=.*[@#$%^&+=])" +    //a special character must occur at least once
            "(?=\\S+$)" +           //no whitespace allowed in the entire string
            ".{8,20}" +             //anything, at least twenty places though
        "$");                       //end-of-string;

        private Integer id;
        private String login;
        private String password;
        private String baseCurrency;

        public CBuilder()
        {
        }

        public CBuilder(CUser prototype)
        {
            this.id = prototype.getId();
            login = prototype.getLogin();
            password = prototype.getPassword();
            baseCurrency = prototype.getBaseCurrency();
        }

        public CBuilder setId(Integer id)
        {
            this.id = id;
            return this;
        }

        public CBuilder setBaseCurrency(String baseCurrency) {
            this.baseCurrency = baseCurrency;
            return this;
        }

        public CBuilder setLogin(String login)
        {
            this.login = login;
            return this;
        }

        public CBuilder setPassword(String password)
        {
            this.password = password;
            return this;
        }

        public CUser build()
        {
            if (login == null || login.isEmpty())
            {
                throw new CValidationException("Login could not be empty");
            }
            else if (password == null || password.isEmpty())
            {
                throw new CValidationException("Password for prototype " + login + " could not be empty");
            }
            else if (!usernamePattern.matcher(login).matches())
            {
                throw new CValidationException("Login is invalid.\n" +
                        "It could contain only uppercase and lowercase symbols, space, symbol '_', and its length must be from 3 to 20");
            }
            else if (!userpasswordPattern.matcher(password).matches())
            {
                throw new CValidationException("Password for " + login + " is invalid. Please, see conditions:\n" +
                        "1) a digit must occur at least once;\n" +
                        "2) a lower case letter must occur at least once;\n" +
                        "3) an upper case letter must occur at least once;\n" +
                        "4) a special character(@ # $ % ^ & + =) must occur at least once;\n" +
                        "5) no whitespace allowed in the entire string;\n" +
                        "6) its length must be from 8 to 20");
            }
            else if (StringUtils.isEmpty(baseCurrency)){
                throw new CValidationException("Base currency could not be empty");
            }

            return new CUser(id, login, password, baseCurrency);
        }
    }

    private Integer id;
    private final String login;
    private final String password;
    private final String baseCurrency;

    private CUser(Integer id, String login, String password, String baseCurrency)
    {
        this.id = id;
        this.login = login;
        this.password = password;
        this.baseCurrency = baseCurrency;
    }

    public String getPassword()
    {
        return password;
    }

    public String getLogin()
    {
        return login;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBaseCurrency() {
        return baseCurrency;
    }

    @Override
    public String toString()
    {
        return login;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }

        if (!(obj instanceof CUser))
        {
            return false;
        }

        CUser other = (CUser) obj;

        return CDataUtils.equalsNullSafe(login, other.login) &&
               CDataUtils.equalsNullSafe(password, other.password) &&
               CDataUtils.equalsNullSafe(baseCurrency, other.baseCurrency);
    }

    @Override
    public int hashCode()
    {
        return CDataUtils.calculateHashCode(login, password, baseCurrency);
    }
}
