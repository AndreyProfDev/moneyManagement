package com.moneyManagement.gui;

import com.caucho.hessian.client.HessianProxyFactory;
import com.moneyManagement.api.interfaces.IAccountDAO;
import com.moneyManagement.api.interfaces.ICategoryDAO;
import com.moneyManagement.api.interfaces.ICurrencyDAO;
import com.moneyManagement.api.interfaces.IExchangeRateDAO;
import com.moneyManagement.api.interfaces.IIncomeExpenseTransactionDAO;
import com.moneyManagement.api.interfaces.IPlanItemDAO;
import com.moneyManagement.api.interfaces.ITransferTransactionDAO;
import com.moneyManagement.api.interfaces.IUserDAO;
import com.moneyManagement.gui.utils.CPropertyHolder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.net.MalformedURLException;

@Configuration
public class CServicesContext {

    @Bean
    public CPropertyHolder<String> serverURL(){
        return new CPropertyHolder<>();
    }

    @Bean
    @Lazy
    public IAccountDAO accountDAOService() throws MalformedURLException {
        HessianProxyFactory factory = new HessianProxyFactory();
        factory.setOverloadEnabled(true);
        return (IAccountDAO) factory.create(IAccountDAO.class, serverURL().getValue() + "/remoting/accountDAOService");
    }

    @Bean
    @Lazy
    public IUserDAO userDAOService() throws MalformedURLException {
        HessianProxyFactory factory = new HessianProxyFactory();
        factory.setOverloadEnabled(true);
        return (IUserDAO) factory.create(IUserDAO.class, serverURL().getValue() + "/remoting/userDAOService");
    }

    @Bean
    @Lazy
    public ICategoryDAO categoryDAOService() throws MalformedURLException {
        HessianProxyFactory factory = new HessianProxyFactory();
        factory.setOverloadEnabled(true);
        return (ICategoryDAO) factory.create(ICategoryDAO.class, serverURL().getValue() + "/remoting/categoryDAOService");
    }

    @Bean
    @Lazy
    public IExchangeRateDAO exchangeRateDaoService() throws MalformedURLException {
        HessianProxyFactory factory = new HessianProxyFactory();
        factory.setOverloadEnabled(true);
        return (IExchangeRateDAO) factory.create(IExchangeRateDAO.class, serverURL().getValue() + "/remoting/exchangeRateDAOService");
    }

    @Bean
    @Lazy
    public IIncomeExpenseTransactionDAO incomeExpenseTransactionDAOService() throws MalformedURLException {
        HessianProxyFactory factory = new HessianProxyFactory();
        factory.setOverloadEnabled(true);
        return (IIncomeExpenseTransactionDAO) factory.create(IIncomeExpenseTransactionDAO.class,
                serverURL().getValue() + "/remoting/incomeExpenseTransactionDAOService");
    }

    @Bean
    @Lazy
    public IPlanItemDAO planItemDAOService() throws MalformedURLException {
        HessianProxyFactory factory = new HessianProxyFactory();
        factory.setOverloadEnabled(true);
        return (IPlanItemDAO) factory.create(IPlanItemDAO.class,
                serverURL().getValue() + "/remoting/planItemDAOService");
    }

    @Bean
    @Lazy
    public ITransferTransactionDAO transferTransactionDAOService() throws MalformedURLException {
        HessianProxyFactory factory = new HessianProxyFactory();
        factory.setOverloadEnabled(true);
        return (ITransferTransactionDAO) factory.create(ITransferTransactionDAO.class,
                serverURL().getValue() + "/remoting/transferTransactionDAOService");
    }

    @Bean
    @Lazy
    public ICurrencyDAO currencyDAOService() throws MalformedURLException {
        HessianProxyFactory factory = new HessianProxyFactory();
        factory.setOverloadEnabled(true);
        return (ICurrencyDAO) factory.create(ICurrencyDAO.class,
                serverURL().getValue() + "/remoting/currencyDAOService");
    }
}
