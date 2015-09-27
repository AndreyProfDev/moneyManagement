package com.moneyManagement.gui.framePanel.profile;

import com.moneyManagement.api.interfaces.ICurrencyDAO;
import com.moneyManagement.api.interfaces.IExchangeRateDAO;
import com.moneyManagement.data.CExchangeRate;
import com.moneyManagement.data.CUser;
import com.moneyManagement.gui.framePanel.CAbstractPanel;
import com.moneyManagement.gui.table.view.CExchangeRateTableView;
import com.moneyManagement.gui.utils.CDisplay;
import com.moneyManagement.gui.utils.CPropertyHolder;
import com.toedter.calendar.JDateChooser;
import org.springframework.beans.factory.annotation.Autowired;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Date;
import java.util.Locale;
import java.util.Set;

public class CExchangeRateProfile extends CAbstractPanel implements ActionListener, PropertyChangeListener
{
    private JButton btnOk;
    private JButton btnCancel;
    private CExchangeRateTableView tblExchangeRate;
    private JComboBox<String> cmbFirstCurrency;
    private JComboBox<String> cmbSecondCurrency;
    private JDateChooser fldDateChooser;
    private CExchangeRate exchangeRate;
    private JLabel lblFirstCurrency;
    private JLabel lblSecondCurrency;
    private JLabel lblEffectiveDate;

    @Autowired
    private IExchangeRateDAO exchangeRateDAO;

    @Autowired
    private CPropertyHolder<CUser> mainUser;

    @Autowired
    private ICurrencyDAO currencyDAO;

    private Set<String> currencies;

    boolean created;

    @Override
    public void init()
    {
        init(true, new CExchangeRate.CBuilder()
                .setEffectiveDate(new Date())
                .setEnterDate(new Date())
                .setFirstCurrency("EUR")
                .setSecondCurrency("DOL")
                .setExchangeRate(1.0)
                .build(), "Create exchange rate");
    }

    public void init(CExchangeRate exchangeRate)
    {
        init(false, new CExchangeRate.CBuilder(exchangeRate)
                .build(), "Edit exchange rate");


    }

    private void init(boolean created, CExchangeRate exchangeRate, String title)
    {
        this.user = mainUser.getValue();
        this.exchangeRate = exchangeRate;
        this.created = created;
        currencies = currencyDAO.loadAll();

        setMaximizable(true);
        setResizable(true);
        setClosable(true);

        setTitle(title);

        createComponents();
        fillComponents();
        jbInit();
        pack();
        subscribe();
    }

    @Override
    protected void createComponents()
    {
        btnOk = new JButton(created ? "Create" : "Update");
        btnCancel = new JButton("Cancel");

        String[] currenciesArr = new String[currencies.size()];
        currencies.toArray(currenciesArr);
        cmbFirstCurrency = new JComboBox<>(currenciesArr);
        cmbSecondCurrency = new JComboBox<>(currenciesArr);
        fldDateChooser = new JDateChooser();

        tblExchangeRate = new CExchangeRateTableView(exchangeRate, mainUser.getValue());

        lblFirstCurrency = new JLabel("First currency");
        lblSecondCurrency = new JLabel("Second currency");
        lblEffectiveDate = new JLabel("Effective date");
    }

    protected void fillComponents()
    {
        fldDateChooser.setLocale(Locale.ENGLISH);
        fldDateChooser.setDateFormatString("dd/MM/yyyy");
        fldDateChooser.setDate(new Date());

        cmbFirstCurrency.setSelectedItem(exchangeRate.getFirstCurrency());
        cmbSecondCurrency.setSelectedItem(exchangeRate.getSecondCurrency());
    }

    @Override
    protected void jbInit()
    {
        JPanel okCancelPanel = new JPanel();

        setLayout(new GridBagLayout());

        add(lblFirstCurrency, new GridBagConstraints(0, 0, 1, 1, 0, 0,
                GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(3, 3, 3, 3), 0, 0));
        add(cmbFirstCurrency, new GridBagConstraints(0, 1, 1, 1, 0, 0,
                GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(3, 3, 3, 3), 0, 0));

        add(lblSecondCurrency, new GridBagConstraints(1, 0, 1, 1, 0, 0,
                GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(3, 3, 3, 3), 0, 0));
        add(cmbSecondCurrency, new GridBagConstraints(1, 1, 1, 1, 0, 0,
                GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(3, 3, 3, 3), 0, 0));

        add(lblEffectiveDate, new GridBagConstraints(2, 0, 1, 1, 0, 0,
                GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(3, 3, 3, 3), 0, 0));
        add(fldDateChooser, new GridBagConstraints(2, 1, 1, 1, 0, 0,
                GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(3, 3, 3, 3), 0, 0));

        add(new JScrollPane(tblExchangeRate), new GridBagConstraints(0, 2, 3, 1, 1, 1,
                GridBagConstraints.FIRST_LINE_START, GridBagConstraints.BOTH, new Insets(3, 3, 3, 3), 0, 0));

        add(okCancelPanel, new GridBagConstraints(0, 3, 3, 1, 0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(3, 3, 3, 3), 0, 0));

        okCancelPanel.add(btnOk, new GridBagConstraints(0, 0, 1, 1, 0, 0,
                GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(3, 3, 3, 3), 0, 0));
        okCancelPanel.add(btnCancel, new GridBagConstraints(1, 0, 1, 1, 0, 0,
                GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(3, 3, 3, 3), 0, 0));
    }

    @Override
    protected void subscribe()
    {
        btnOk.addActionListener(this);
        btnCancel.addActionListener(this);
        cmbFirstCurrency.addActionListener(this);
        cmbSecondCurrency.addActionListener(this);
        fldDateChooser.getDateEditor().addPropertyChangeListener(this);

    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == btnOk)
        {
            if (created){
                exchangeRateDAO.create(tblExchangeRate.getExchangeRate());
            }
            else{
                exchangeRateDAO.update(tblExchangeRate.getExchangeRate());
            }
            dispose();
        }
        else if (e.getSource() == btnCancel)
        {
            dispose();
        }
        else if (e.getSource() == cmbFirstCurrency)
        {
            if (cmbFirstCurrency.getSelectedItem() == cmbSecondCurrency.getSelectedItem())
            {
                for (String currencyName : currencies)
                {
                    if (!currencyName.equals(cmbFirstCurrency.getSelectedItem()) &&
                        !currencyName.equals(tblExchangeRate.getExchangeRate().getFirstCurrency()))
                    {
                        CDisplay.warning("Second currency could not be equal first currency." + currencyName);
                        cmbFirstCurrency.setSelectedItem(tblExchangeRate.getExchangeRate().getFirstCurrency());

                        break;
                    }
                }
            }

            tblExchangeRate.setFirstCurrencyName((String) cmbFirstCurrency.getSelectedItem());
        }
        else if (e.getSource() == cmbSecondCurrency)
        {
            if (cmbSecondCurrency.getSelectedItem() == cmbFirstCurrency.getSelectedItem())
            {
                for (String currencyName : currencies)
                {
                    if (!currencyName.equals(cmbSecondCurrency.getSelectedItem()) &&
                        !currencyName.equals(tblExchangeRate.getExchangeRate().getSecondCurrency())) {
                        CDisplay.warning("Second currency could not be equal first currency." + currencyName);
                        cmbSecondCurrency.setSelectedItem(tblExchangeRate.getExchangeRate().getSecondCurrency());
                        break;
                    }
                }
            }

            tblExchangeRate.setSecondCurrencyName((String) cmbSecondCurrency.getSelectedItem());
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt)
    {
        if ("date".equals(evt.getPropertyName()))
        {
            tblExchangeRate.setEffectiveDate((Date) evt.getNewValue());
        }
    }
}
