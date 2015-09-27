package com.moneyManagement.gui.framePanel;

import com.moneyManagement.api.interfaces.IAccountDAO;
import com.moneyManagement.api.interfaces.IExchangeRateDAO;
import com.moneyManagement.data.CAccount;
import com.moneyManagement.data.CExchangeRate;
import com.moneyManagement.data.CUser;
import com.moneyManagement.data.EAccountType;
import com.moneyManagement.gui.utils.CDisplay;
import org.springframework.dao.EmptyResultDataAccessException;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CUserPanel extends JPanel implements ActionListener
{
    private JLabel lblCurrentAssets;
    private JFormattedTextField fldCurrentAssets;

    private JLabel lblBaseCurrency;

    private JTable tblBankCard;
    private JTable tblCash;
    private JButton btnRefresh;

    private CUser user;
    private Map<EAccountType, List<CAccount>> accounts;
    private IAccountDAO accountDAO;
    private IExchangeRateDAO exchangeRateDAO;

    public CUserPanel(IAccountDAO accountDAO, IExchangeRateDAO exchangeRateDAO, CUser user, Map<EAccountType, List<CAccount>> accounts)
    {
        this.user = user;
        this.accounts = accounts;
        this.accountDAO = accountDAO;
        this.exchangeRateDAO = exchangeRateDAO;

        createComponents();
        fillComponents();
        jbInit();
        subscribe();
    }

    protected void createComponents()
    {
        tblBankCard = new JTable(new CAssetsModel(accounts.get(EAccountType.BANK_CARD)));
        tblCash = new JTable(new CAssetsModel(accounts.get(EAccountType.CASH)));

        if (user != null){
            lblCurrentAssets = new JLabel("Current assets");
            fldCurrentAssets = new JFormattedTextField(NumberFormat.getInstance());
            fldCurrentAssets.setEditable(false);
            lblBaseCurrency = new JLabel(user.getBaseCurrency());
        }

        btnRefresh = new JButton("Refresh");
    }

    protected void fillComponents()
    {
        if (user != null){
            fldCurrentAssets.setValue(getPrivateUserBalance());
        }
    }

    protected void jbInit()
    {
        setLayout(new GridBagLayout());
        JPanel leftPanel = new JPanel(new GridBagLayout());
        add(btnRefresh, new GridBagConstraints(0, 0, 1, 1, 0, 0,
                GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(3, 3, 3, 3), 0, 0));
        add(leftPanel, new GridBagConstraints(0, 1, 1, 1, 0.5, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(3, 3, 3, 3), 0, 0));

        if (user != null){
            leftPanel.add(lblCurrentAssets, new GridBagConstraints(0, 0, 1, 1, 0, 0,
                    GridBagConstraints.PAGE_START, GridBagConstraints.NONE, new Insets(3, 3, 3, 3), 0, 0));
            leftPanel.add(fldCurrentAssets, new GridBagConstraints(0, 1, 1, 1, 0, 0,
                    GridBagConstraints.PAGE_START, GridBagConstraints.HORIZONTAL, new Insets(3, 3, 3, 3), 0, 0));
            leftPanel.add(lblBaseCurrency, new GridBagConstraints(1, 1, 1, 1, 0, 0,
                    GridBagConstraints.PAGE_START, GridBagConstraints.HORIZONTAL, new Insets(3, 3, 3, 3), 0, 0));
        }


        JPanel rightPanel = new JPanel(new GridBagLayout());
        add(rightPanel, new GridBagConstraints(1, 1, 1, 1, 0.5, 1,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(3, 3, 3, 3), 0, 0));

        JPanel assetsTblPanel = new JPanel(new GridBagLayout());
        assetsTblPanel.add(new JLabel(EAccountType.BANK_CARD.toString()), new GridBagConstraints(0, 0, 1, 1, 0, 0,
                GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(3, 3, 3, 3), 0, 0));
        assetsTblPanel.add(new JScrollPane(tblBankCard), new GridBagConstraints(0, 1, 1, 1, 1, 1,
                GridBagConstraints.FIRST_LINE_START, GridBagConstraints.BOTH, new Insets(3, 3, 3, 3), 0, 0));
        rightPanel.add(assetsTblPanel, new GridBagConstraints(0, 0, 1, 1, 1, 0.5,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(3, 3, 3, 3), 0, 0));

        assetsTblPanel = new JPanel(new GridBagLayout());
        assetsTblPanel.add(new JLabel(EAccountType.CASH.toString()), new GridBagConstraints(0, 0, 1, 1, 0, 0,
                GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(3, 3, 3, 3), 0, 0));
        assetsTblPanel.add(new JScrollPane(tblCash), new GridBagConstraints(0, 1, 1, 1, 1, 1,
                GridBagConstraints.FIRST_LINE_START, GridBagConstraints.BOTH, new Insets(3, 3, 3, 3), 0, 0));
        rightPanel.add(assetsTblPanel, new GridBagConstraints(0, 1, 1, 1, 1, 0.5,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(3, 3, 3, 3), 0, 0));
    }

    private void subscribe()
    {
        btnRefresh.addActionListener(this);
    }

    public void refreshData(Map<EAccountType, List<CAccount>> accounts)
    {
        if (user != null){
            fldCurrentAssets.setValue(getPrivateUserBalance());
        }

        tblBankCard.setModel(new CAssetsModel(accounts.get(EAccountType.BANK_CARD)));
        tblCash.setModel(new CAssetsModel(accounts.get(EAccountType.CASH)));
    }

    private double getPrivateUserBalance()
    {
        double result = 0;

        Map<String, Double> exchangeRates = new HashMap<>();
        List<CAccount> accounts = accountDAO.loadAccounts(user);

        for (CAccount acc : accounts)
        {
            String accountCurrency = acc.getCurrency();

            Double exchangeRate = 1.0;
            if (!accountCurrency.equals(user.getBaseCurrency()))
            {
                exchangeRate = exchangeRates.get(accountCurrency);
                if (exchangeRate == null)
                {
                    try
                    {
                        exchangeRate = exchangeRateDAO.loadLast(accountCurrency, user.getBaseCurrency()).getExchangeRate();
                        exchangeRates.put(accountCurrency, exchangeRate);

                    }
                    catch (EmptyResultDataAccessException ex)
                    {
                        CDisplay.warning("There is no exchange rate " + accountCurrency + "/" + user.getBaseCurrency() +
                                ".\n Please enter this exchange rate before refreshing dashboard panel.");
                        return Double.NaN;
                    }
                }
            }


            result += acc.getBalance() * exchangeRate;
        }

        return result;
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == btnRefresh)
        {
            refreshData(accounts);
        }
    }

    private class CAssetsModel extends DefaultTableModel
    {
        public CAssetsModel(List<CAccount> accounts)
        {
            for (CAccount account : accounts)
            {
                addRow(new Object[] {account.getName(), account.getBalance(), account.getCurrency().toString()});
            }

            if (accounts.isEmpty())
            {
                addRow(new Object[]{"", 0, ""});
            }
        }

        @Override
        public int getColumnCount()
        {
            return 3;
        }

        @Override
        public boolean isCellEditable(int row, int column)
        {
            return false;
        }

        @Override
        public String getColumnName(int columnIndex)
        {
            switch (columnIndex)
            {
                case 0:
                {
                    return "Account Name";
                }
                case 1:
                {
                    return "Account Assets";
                }
                case 2:
                {
                    return "Currency";
                }
            }

            return null;
        }

        @Override
        public Class<?> getColumnClass(int columnIndex)
        {
            switch (columnIndex)
            {
                case 0:
                {
                    return String.class;
                }
                case 1:
                {
                    return Double.class;
                }
                case 2:
                {
                    return String.class;
                }
            }
            return null;
        }
    }

    public CUser getUser() {
        return user;
    }
}
