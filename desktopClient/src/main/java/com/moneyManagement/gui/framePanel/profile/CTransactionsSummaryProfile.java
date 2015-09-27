package com.moneyManagement.gui.framePanel.profile;

import com.moneyManagement.api.interfaces.IIncomeExpenseTransactionDAO;
import com.moneyManagement.api.interfaces.ITransferTransactionDAO;
import com.moneyManagement.data.CTransaction;
import com.moneyManagement.data.CUser;
import com.moneyManagement.data.ECategoryType;
import com.moneyManagement.gui.framePanel.CAbstractPanel;
import com.moneyManagement.gui.table.view.CSummaryTableView;
import com.moneyManagement.gui.utils.CDisplay;
import com.moneyManagement.gui.utils.CPropertyHolder;
import com.toedter.calendar.JDateChooser;
import org.springframework.beans.factory.annotation.Autowired;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public abstract class CTransactionsSummaryProfile extends CAbstractPanel implements ActionListener
{
    private enum ETransactionType
    {
        ALL("All"),
        EXPENSE("Expense"),
        INCOME("Income"),
        TRANSFER("Transfer");

        private String description;

        ETransactionType(String description)
        {
            this.description = description;
        }

        @Override
        public String toString()
        {
            return description;
        }
    }

    private JDateChooser chrStartDate;
    private JDateChooser chrEndDate;
    private JComboBox<ETransactionType> cmbTransactionType;
    private JButton btnDisplay;
    private CSummaryTableView tblSummary;
    private JButton btnCancel;

    @Autowired
    private CPropertyHolder<CUser> mainUser;

    @Autowired
    private IIncomeExpenseTransactionDAO incomeExpenseTransactionDAO;

    @Autowired
    private ITransferTransactionDAO transferTransactionDAO;

    @Override
    public void init()
    {
        super.init(mainUser.getValue(), "Transactions summary");
    }

    @Override
    protected void createComponents()
    {
        chrStartDate = new JDateChooser();
        chrEndDate = new JDateChooser();
        cmbTransactionType = new JComboBox<>(ETransactionType.values());
        btnDisplay = new JButton("Show");

        tblSummary = getTransactionsSummaryTable(user);

        btnCancel = new JButton("Cancel");
    }

    @Override
    protected void fillComponents()
    {
        chrStartDate.setLocale(Locale.ENGLISH);
        chrStartDate.setDateFormatString("dd/MM/yyyy");
        chrStartDate.setDate(new Date());

        chrEndDate.setLocale(Locale.ENGLISH);
        chrEndDate.setDateFormatString("dd/MM/yyyy");
        chrEndDate.setDate(new Date());

        cmbTransactionType.setSelectedItem(ETransactionType.ALL);
    }

    @Override
    protected void jbInit()
    {
        setLayout(new GridBagLayout());

        JPanel parametersPanel = new JPanel(new GridBagLayout());
        parametersPanel.add(chrStartDate, new GridBagConstraints(0, 0, 1, 1, 1, 0,
                GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(3, 3, 3, 3), 0, 0));
        parametersPanel.add(chrEndDate, new GridBagConstraints(1, 0, 1, 1, 1, 0,
                GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(3, 3, 3, 3), 0, 0));
        parametersPanel.add(cmbTransactionType, new GridBagConstraints(2, 0, 1, 1, 0, 0,
                GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(3, 3, 3, 3), 0, 0));
        parametersPanel.add(btnDisplay, new GridBagConstraints(3, 0, 1, 1, 0, 0,
                GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(3, 3, 3, 3), 0, 0));

        JPanel okCancelPanel = new JPanel(new GridBagLayout());
        okCancelPanel.add(btnCancel, new GridBagConstraints(0, 0, 1, 1, 0, 0,
                GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(3, 3, 3, 3), 0, 0));

        add(parametersPanel, new GridBagConstraints(0, 0, 1, 1, 1.0, 0,
                GridBagConstraints.PAGE_START, GridBagConstraints.HORIZONTAL, new Insets(3, 3, 3, 3), 0, 0));
        add(new JScrollPane(tblSummary), new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0,
                GridBagConstraints.PAGE_START, GridBagConstraints.BOTH, new Insets(3, 3, 3, 3), 0, 0));
        add(okCancelPanel, new GridBagConstraints(0, 2, 1, 1, 0, 0.0,
                GridBagConstraints.PAGE_START, GridBagConstraints.NONE, new Insets(3, 3, 3, 3), 0, 0));
    }

    @Override
    protected void subscribe()
    {
        btnDisplay.addActionListener(this);
        btnCancel.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == btnDisplay)
        {
            Date startDate = chrStartDate.getDate();
            Date endDate = chrEndDate.getDate();

            if (startDate.after(endDate))
            {
                CDisplay.warning("Start date should be before or equals to end date");
                return;
            }

            List<CTransaction> loadedTransactions = new ArrayList<>();

            switch ((ETransactionType)cmbTransactionType.getSelectedItem())
            {
                case ALL:
                {
                    loadedTransactions.addAll(incomeExpenseTransactionDAO.loadAll(user, startDate, endDate));
                    loadedTransactions.addAll(transferTransactionDAO.loadAll(user, startDate, endDate));
                    break;
                }
                case EXPENSE:
                {
                    loadedTransactions.addAll(incomeExpenseTransactionDAO.loadAll(user, startDate, endDate, ECategoryType.EXPENSE));
                    break;
                }
                case INCOME:
                {
                    loadedTransactions.addAll(incomeExpenseTransactionDAO.loadAll(user, startDate, endDate, ECategoryType.INCOME));
                    break;
                }
                case TRANSFER:
                {
                    loadedTransactions.addAll(transferTransactionDAO.loadAll(user, startDate, endDate));
                    break;
                }
            }

            tblSummary.setRows(loadedTransactions);
            pack();
        }
        else if (e.getSource() == btnCancel)
        {
            dispose();
        }
    }

    public abstract CSummaryTableView getTransactionsSummaryTable(CUser user);
}
