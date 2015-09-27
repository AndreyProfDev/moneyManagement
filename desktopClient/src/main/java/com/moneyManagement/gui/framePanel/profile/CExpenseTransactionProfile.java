package com.moneyManagement.gui.framePanel.profile;

import com.moneyManagement.api.interfaces.IAccountDAO;
import com.moneyManagement.api.interfaces.ICategoryDAO;
import com.moneyManagement.api.interfaces.IIncomeExpenseTransactionDAO;
import com.moneyManagement.data.CAccount;
import com.moneyManagement.data.CIncomeExpenseTransaction;
import com.moneyManagement.data.CIncomeExpenseTransactionItem;
import com.moneyManagement.data.CUser;
import com.moneyManagement.data.ECategoryType;
import com.moneyManagement.gui.frame.CCentralFrame;
import com.moneyManagement.gui.table.model.CExpenseTableModel;
import com.moneyManagement.gui.table.view.CExpenseTableView;
import com.moneyManagement.gui.table.view.CTableView;
import com.moneyManagement.gui.utils.CDisplay;
import com.moneyManagement.gui.utils.CPropertyHolder;
import com.toedter.calendar.JDateChooser;
import org.springframework.beans.factory.annotation.Autowired;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CExpenseTransactionProfile extends CTransactionProfile<CIncomeExpenseTransaction>
{
    private JLabel lblIncomeReceiver;
    private JComboBox<CAccount> cmbIncomeReceiver;

    private JLabel lblDate;
    private JDateChooser fldDateChooser;

    @Autowired
    private CPropertyHolder<CUser> mainUser;

    @Autowired
    private IAccountDAO accountDAO;

    @Autowired
    private CCentralFrame centralFrame;

    @Autowired
    private IIncomeExpenseTransactionDAO incomeExpenseTransactionDAO;

    @Autowired
    private ICategoryDAO categoryDAO;

    @Override
    public void init()
    {
        if (!categoryDAO.checkCategoriesExists(mainUser.getValue()))
        {
            CDisplay.warning("Expense categories should exist to create this transaction profile");
            return;
        }
        else if (!accountDAO.checkAccountsExists(mainUser.getValue()))
        {
            CDisplay.warning("Accounts should exist to create this transaction profile");
            return;
        }

        super.init(mainUser.getValue(), "Expense transaction profile");

        btnUpdate.setVisible(transaction != null);
    }

    public void setTransaction(CIncomeExpenseTransaction expenseTransaction){
        this.transaction = expenseTransaction;
    }

    @Override
    protected void createComponents()
    {
        super.createComponents();

        lblIncomeReceiver = new JLabel("Select expense receiver:");
        cmbIncomeReceiver = new JComboBox<>();
        lblDate = new JLabel("Enter date:");
        fldDateChooser = new JDateChooser();
    }

    protected void fillComponents()
    {
        fldDateChooser.setLocale(Locale.ENGLISH);
        fldDateChooser.setDateFormatString("dd/MM/yyyy");
        fldDateChooser.setDate(new Date());

        List<CAccount> accounts = new ArrayList<>();
        accounts.addAll(accountDAO.loadAccounts(user));
        accounts.addAll(accountDAO.loadAccounts(null));

        for (CAccount acc : accounts)
        {
            cmbIncomeReceiver.addItem(acc);
        }

        if (transaction != null)
        {
            btnCreate.setVisible(false);
            cmbIncomeReceiver.setEditable(false);
            cmbIncomeReceiver.setSelectedItem(accountDAO.loadAccount(transaction.getAccountId()));
            fldDateChooser.setDate(transaction.getDate());
            ((CExpenseTableView) table).getModel().setRows(transaction.getItems());
        }
    }

    @Override
    protected void jbInit()
    {
        super.jbInit();

        JPanel incomeRightPanel = new JPanel(new GridBagLayout());
        add(incomeRightPanel, new GridBagConstraints(1, 0, 1, 1, 0, 0,
                GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(3, 3, 3, 3), 0, 0));

        incomeRightPanel.add(lblIncomeReceiver, new GridBagConstraints(0, 0, 1, 1, 1, 0,
                GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(3, 3, 3, 3), 0, 0));
        incomeRightPanel.add(cmbIncomeReceiver, new GridBagConstraints(0, 1, 1, 1, 1, 0,
                GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(3, 3, 3, 3), 0, 0));
        incomeRightPanel.add(lblDate, new GridBagConstraints(0, 2, 1, 1, 1, 0,
                GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(3, 3, 3, 3), 0, 0));
        incomeRightPanel.add(fldDateChooser, new GridBagConstraints(0, 3, 1, 1, 1, 0,
                GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(3, 3, 3, 3), 0, 0));
    }
    @Override
    public boolean create()
    {
        //TODO process updating expense transaction

        if (table.isEditing())
        {
            table.getCellEditor().stopCellEditing();
        }

        List<CIncomeExpenseTransactionItem> items = ((CExpenseTableModel) table.getModel()).getRows();

        if (items.size() == 0)
        {
            CDisplay.warning("Please, enter items.");
            return false;
        }
        CAccount account = (CAccount) cmbIncomeReceiver.getSelectedItem();

        incomeExpenseTransactionDAO.create(new CIncomeExpenseTransaction.CBuilder()
                .setDate(fldDateChooser.getDate())
                .setCategory(ECategoryType.EXPENSE)
                .setAccountId(account.getId())
                .setUserId(user.getId())
                .setItems(items)
                .build());

        centralFrame.refreshData();

        table.saveTableLayout();

        return true;
    }

    @Override
    public boolean update()
    {
        if (table.isEditing())
        {
            table.getCellEditor().stopCellEditing();
        }

        List<CIncomeExpenseTransactionItem> rows = ((CExpenseTableModel) table.getModel()).getRows();

        if (rows.size() == 0)
        {
            CDisplay.warning("Please, enter items.");
            return false;
        }

        CAccount account = (CAccount) cmbIncomeReceiver.getSelectedItem();

        transaction = new CIncomeExpenseTransaction.CBuilder(transaction)
                .setAccountId(account.getId())
                .setItems(rows)
                .setDate(fldDateChooser.getDate())
                .build();

        incomeExpenseTransactionDAO.update(transaction);

        centralFrame.refreshData();

        table.saveTableLayout();

        return true;
    }

    @Override
    protected CTableView getEmptyTable()
    {
        return new CExpenseTableView(user, categoryDAO.loadCategories(ECategoryType.EXPENSE, user));
    }
}
