package com.moneyManagement.gui.framePanel.profile;

import com.moneyManagement.api.interfaces.ICurrencyDAO;
import com.moneyManagement.api.interfaces.IExchangeRateDAO;
import com.moneyManagement.data.CExchangeRate;
import com.moneyManagement.data.CUser;
import com.moneyManagement.gui.framePanel.CAbstractPanel;
import com.moneyManagement.gui.framePanel.CFrameDisplayer;
import com.moneyManagement.gui.table.view.CExchangeRatesSummaryView;
import com.moneyManagement.gui.utils.CDisplay;
import com.moneyManagement.gui.utils.CPropertyHolder;
import com.toedter.calendar.JDateChooser;
import org.springframework.beans.factory.annotation.Autowired;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class CExchangeRatesSummaryProfile extends CAbstractPanel implements ActionListener
{
    private JDateChooser fldStartDate;
    private JDateChooser fldEndDate;
    private JComboBox<String> cmbCurrency;
    private JButton btnDisplay;
    private JButton btnSubmit;
    private JButton btnCancel;
    private CExchangeRatesSummaryView tblSummary;

    @Autowired
    private CPropertyHolder<CUser> mainUser;

    @Autowired
    private IExchangeRateDAO exchangeRateDAO;

    @Autowired
    private CFrameDisplayer frameDisplayer;

    @Autowired
    private ICurrencyDAO currencyDAO;

    @Autowired
    private CExchangeRateProfile exchangeRateProfile;

    private Set<String> currencies;

    @Override
    public void init()
    {
        currencies = currencyDAO.loadAll();
        super.init(mainUser.getValue(), "Exchange rates summary");
    }

    @Override
    protected void createComponents()
    {
        fldStartDate = new JDateChooser();
        fldEndDate = new JDateChooser();
        String[] currenciesArr = new String[currencies.size()];
        currencies.toArray(currenciesArr);
        cmbCurrency = new JComboBox<>(currenciesArr);
        btnDisplay = new JButton("Show");
        btnSubmit = new JButton("Submit changes");
        btnCancel = new JButton("Cancel");

        tblSummary = new CExchangeRatesSummaryView(user, frameDisplayer, exchangeRateProfile);
    }

    @Override
    protected void fillComponents()
    {
        fldStartDate.setLocale(Locale.ENGLISH);
        fldStartDate.setDateFormatString("dd/MM/yyyy");
        fldStartDate.setDate(new Date());

        fldEndDate.setLocale(Locale.ENGLISH);
        fldEndDate.setDateFormatString("dd/MM/yyyy");
        fldEndDate.setDate(new Date());
    }

    @Override
    protected void jbInit()
    {
        setLayout(new GridBagLayout());

        JPanel parametersPanel = new JPanel(new GridBagLayout());
        parametersPanel.add(fldStartDate, new GridBagConstraints(0, 0, 1, 1, 1, 0,
                GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(3, 3, 3, 3), 0, 0));
        parametersPanel.add(fldEndDate, new GridBagConstraints(1, 0, 1, 1, 1, 0,
                GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(3, 3, 3, 3), 0, 0));
        parametersPanel.add(cmbCurrency, new GridBagConstraints(2, 0, 1, 1, 0, 0,
                GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(3, 3, 3, 3), 0, 0));
        parametersPanel.add(btnDisplay, new GridBagConstraints(3, 0, 1, 1, 0, 0,
                GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(3, 3, 3, 3), 0, 0));

        JPanel okCancelPanel = new JPanel(new GridBagLayout());
        okCancelPanel.add(btnSubmit, new GridBagConstraints(0, 0, 1, 1, 0, 0,
                GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(3, 3, 3, 3), 0, 0));
        okCancelPanel.add(btnCancel, new GridBagConstraints(1, 0, 1, 1, 0, 0,
                GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(3, 3, 3, 3), 0, 0));

        add(parametersPanel, new GridBagConstraints(0, 0, 1, 1, 1.0, 0,
                GridBagConstraints.PAGE_START, GridBagConstraints.HORIZONTAL, new Insets(3, 3, 3, 3), 0, 0));
        add(new JScrollPane(tblSummary), new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0,
                GridBagConstraints.PAGE_START, GridBagConstraints.BOTH, new Insets(3, 3, 3, 3), 0, 0));
        add(okCancelPanel, new GridBagConstraints(0, 2, 1, 1, 0, 0.0,
                GridBagConstraints.PAGE_START, GridBagConstraints.NONE, new Insets(3, 3, 3, 3), 0, 0));
    }

    @Override
    public void subscribe()
    {
        btnDisplay.addActionListener(this);
        btnSubmit.addActionListener(this);
        btnCancel.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == btnDisplay)
        {
            Date startDate = fldStartDate.getDate();
            Date endDate = fldEndDate.getDate();

            if (startDate.after(endDate))
            {
                CDisplay.warning("Start date should be before or equals to end date");
                return;
            }

            List<CExchangeRate> loadedTransactions = exchangeRateDAO.loadAll((String)cmbCurrency.getSelectedItem());

            tblSummary.setRows(loadedTransactions);
            pack();
        }
        else if (e.getSource() == btnSubmit)
        {
            for (CExchangeRate exchangeRate : tblSummary.getModel().getCreatedRows()){
                exchangeRateDAO.create(exchangeRate);
            }

            for (CExchangeRate exchangeRate : tblSummary.getModel().getUpdatedRows()){
                exchangeRateDAO.update(exchangeRate);
            }

            for (CExchangeRate exchangeRate : tblSummary.getModel().getRemovedRows()){
                exchangeRateDAO.remove(exchangeRate);
            }

            dispose();
        }
        else if (e.getSource() == btnCancel)
        {
            dispose();
        }
    }
}
