package com.moneyManagement.gui.framePanel.profile;

import com.moneyManagement.api.interfaces.IAccountDAO;
import com.moneyManagement.api.interfaces.ITransferTransactionDAO;
import com.moneyManagement.api.interfaces.IUserDAO;
import com.moneyManagement.data.CAccount;
import com.moneyManagement.data.CTransferTransaction;
import com.moneyManagement.data.CUser;
import com.moneyManagement.gui.frame.CCentralFrame;
import com.moneyManagement.gui.framePanel.CAbstractPanel;
import com.moneyManagement.gui.utils.CDisplay;
import com.moneyManagement.gui.utils.CPropertyHolder;
import com.toedter.calendar.JDateChooser;
import org.springframework.beans.factory.annotation.Autowired;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CTransferTransactionProfile extends CAbstractPanel implements ActionListener
{
    /**
     * from
     */
    private JLabel lblFrom;
    private JLabel lblAccountFrom;
    private JComboBox<CAccount> cmbFromAccounts;

    /**
     * to
     */
    private JLabel lbtTo;
    private JRadioButton rbtnInternal;
    private JComboBox<CAccount> cmbToAccounts;
    private JRadioButton rbtnExternal;
    private JComboBox<CUser> cmbUsers;

    private JLabel lblTransferAmount;
    private JFormattedTextField fldTransferAmount;

    private JLabel lblTransferReason;
    private JFormattedTextField fldTransferReason;

    private JButton btnCreate;
    private JButton btnUpdate;
    private JButton btnCancel;

    private JLabel errorLabel;

    private JLabel lblDate;
    private JDateChooser fldDateChooser;

    private CTransferTransaction transaction;

    @Autowired
    private CPropertyHolder<CUser> mainUser;

    @Autowired
    private IAccountDAO accountDAO;

    @Autowired
    private IUserDAO userDAO;

    @Autowired
    private ITransferTransactionDAO transferTransactionDAO;

    @Autowired
    private CCentralFrame centralFrame;

    @Override
    public void init()
    {
        if (!accountDAO.checkAccountsExists(mainUser.getValue()) && !accountDAO.checkAccountsExists(null))
        {
            CDisplay.warning("Accounts should exist to create this transaction profile");
            return;
        }

        super.init(mainUser.getValue(), "Transfer transaction profile");
    }

    public void setTransaction(CTransferTransaction transaction){
        this.transaction = transaction;
    }

    @Override
    protected void createComponents()
    {
        List<CAccount> accounts = new ArrayList<>();
        accounts.addAll(accountDAO.loadAccounts(user));
        accounts.addAll(accountDAO.loadAccounts(null));

        List<CUser> toUsers = userDAO.loadAll(Arrays.asList(user));

        lblFrom = new JLabel("From:");
        lblAccountFrom = new JLabel("Extract from:");
        cmbFromAccounts = new JComboBox<>(accounts.toArray(new CAccount[accounts.size()]));

        lbtTo = new JLabel("Transfer to:");
        rbtnInternal = new JRadioButton("Internal");
        rbtnExternal = new JRadioButton("External");
        rbtnExternal.setEnabled(!toUsers.isEmpty());
        cmbUsers = new JComboBox<>(toUsers.toArray(new CUser[toUsers.size()]));
        cmbToAccounts = new JComboBox<>(accounts.toArray(new CAccount[accounts.size()]));

        lblTransferAmount = new JLabel("Transfer amount");
        fldTransferAmount = new JFormattedTextField(NumberFormat.getNumberInstance());

        lblTransferReason = new JLabel("Transfer reason");
        fldTransferReason = new JFormattedTextField();

        btnCreate = new JButton("Create");
        btnUpdate = new JButton("Update");
        btnCancel = new JButton("Cancel");

        errorLabel = new JLabel();

        lblDate = new JLabel("Transaction date");
        fldDateChooser = new JDateChooser();
    }

    @Override
    protected void fillComponents()
    {
        rbtnInternal.setSelected(true);

        cmbToAccounts.setEnabled(true);
        cmbUsers.setEnabled(false);
        fldTransferAmount.setValue(1);

        rbtnExternal.setEnabled(false);
        errorLabel.setVisible(false);

        fldDateChooser.setLocale(Locale.ENGLISH);
        fldDateChooser.setDateFormatString("dd/MM/yyyy");
        fldDateChooser.setDate(new Date());

        if (transaction != null)
        {
            cmbFromAccounts.setSelectedItem(transaction.getFromAccountId());
            rbtnInternal.setSelected(true);
            cmbToAccounts.setSelectedItem(transaction.getToAccountId());
            fldTransferAmount.setValue(transaction.getTransferAmount());
            fldTransferReason.setValue(transaction.getTransferReason());
            fldDateChooser.setDate(transaction.getDate());
        }
    }

    @Override
    protected void jbInit()
    {
        ButtonGroup btnsGroup = new ButtonGroup();
        btnsGroup.add(rbtnInternal);
        btnsGroup.add(rbtnExternal);

        setLayout(new GridBagLayout());

        JPanel fromPanel = new JPanel(new GridBagLayout());
        fromPanel.add(lblFrom, new GridBagConstraints(0, 0, 1, 1, 0, 0,
                GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(3, 3, 3, 3), 0, 0));
        fromPanel.add(lblAccountFrom, new GridBagConstraints(0, 1, 1, 1, 0, 0,
                GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(3, 3, 3, 3), 0, 0));
        fromPanel.add(cmbFromAccounts, new GridBagConstraints(0, 2, 1, 1, 0, 0,
                GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(3, 3, 3, 3), 0, 0));

        JPanel toPanel = new JPanel(new GridBagLayout());
        toPanel.add(lbtTo, new GridBagConstraints(0, 0, 2, 1, 0, 0,
                GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(3, 3, 3, 3), 0, 0));
        toPanel.add(rbtnInternal, new GridBagConstraints(0, 1, 1, 1, 0, 0,
                GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(3, 3, 3, 3), 0, 0));
        toPanel.add(rbtnExternal, new GridBagConstraints(0, 2, 1, 1, 0, 0,
                GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(3, 3, 3, 3), 0, 0));
        toPanel.add(cmbToAccounts, new GridBagConstraints(1, 1, 1, 1, 0, 0,
                GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(3, 3, 3, 3), 0, 0));
        toPanel.add(cmbUsers, new GridBagConstraints(1, 2, 1, 1, 0, 0,
                GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(3, 3, 3, 3), 0, 0));

        JPanel lblTransferAmountPanel = new JPanel(new GridBagLayout());
        lblTransferAmountPanel.add(lblTransferAmount, new GridBagConstraints(0, 0, 1, 1, 0, 0,
                GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(3, 3, 3, 3), 0, 0));
        lblTransferAmountPanel.add(fldTransferAmount, new GridBagConstraints(1, 0, 1, 1, 1.0, 0,
                GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(3, 3, 3, 3), 0, 0));
        lblTransferAmountPanel.add(lblTransferReason, new GridBagConstraints(0, 1, 1, 1, 0, 0,
                GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(3, 3, 3, 3), 0, 0));
        lblTransferAmountPanel.add(fldTransferReason, new GridBagConstraints(1, 1, 1, 1, 1.0, 0,
                GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(3, 3, 3, 3), 0, 0));

        JPanel submitCancelPanel = new JPanel(new GridBagLayout());
        submitCancelPanel.add(btnCreate, new GridBagConstraints(0, 0, 1, 1, 0, 0,
                GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(3, 3, 3, 3), 0, 0));
        submitCancelPanel.add(btnCancel, new GridBagConstraints(1, 0, 1, 1, 0, 0,
                GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(3, 3, 3, 3), 0, 0));

        add(errorLabel, new GridBagConstraints(0, 0, 3, 1, 1.0, 0,
                GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(3, 3, 3, 3), 0, 0));
        add(fromPanel, new GridBagConstraints(0, 1, 1, 1, 0, 0,
                GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(3, 3, 3, 3), 0, 0));
        add(toPanel, new GridBagConstraints(1, 1, 1, 1, 0, 0,
                GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(3, 3, 3, 3), 0, 0));
        add(lblDate, new GridBagConstraints(0, 2, 1, 1, 0, 0,
                GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(3, 3, 3, 3), 0, 0));
        add(fldDateChooser, new GridBagConstraints(1, 2, 1, 1, 0, 0,
                GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(3, 3, 3, 3), 0, 0));
        add(lblTransferAmountPanel, new GridBagConstraints(0, 3, 2, 1, 1.0, 0,
                GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(3, 3, 3, 3), 0, 0));

        add(submitCancelPanel, new GridBagConstraints(0, 4, 3, 1, 0, 0,
                GridBagConstraints.PAGE_START, GridBagConstraints.NONE, new Insets(3, 3, 3, 3), 0, 0));
    }

    @Override
    protected void subscribe()
    {
        rbtnExternal.addActionListener(this);
        rbtnInternal.addActionListener(this);
        btnCreate.addActionListener(this);
        btnUpdate.addActionListener(this);
        btnCancel.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == rbtnInternal)
        {
            cmbToAccounts.setEnabled(true);
            cmbUsers.setEnabled(false);
        }
        else if (e.getSource() == rbtnExternal)
        {
            cmbToAccounts.setEnabled(false);
            cmbUsers.setEnabled(true);
        }
        else if (e.getSource() == btnCreate)
        {
            //TODO process updating income transaction

            CAccount fromAccount = (CAccount) cmbFromAccounts.getSelectedItem();
            String transferReason = fldTransferReason.getText();
            Number transferAmount = (Number) fldTransferAmount.getValue();

            if (transferAmount.doubleValue() <= 0)
            {
                showError("Transfer amount could not be empty.");
                return;
            }

            if (rbtnInternal.isSelected())
            {

                CAccount toAccount = (CAccount) cmbToAccounts.getSelectedItem();
                transferTransactionDAO.create(new CTransferTransaction.CBuilder()
                        .setTrDate(fldDateChooser.getDate())
                        .setFromUserId(user.getId())
                        .setToUserId(user.getId())
                        .setFromAccountId(fromAccount.getId())
                        .setTransferAmount(transferAmount.doubleValue())
                        .setTransferReason(transferReason)
                        .setToAccountId(toAccount.getId())
                        .build());
            }
//                else
//                {
//                    CUser toUser = (CUser) cmbUsers.getSelectedItem();
//                    CTransferTransactionExternalDAO.create(new CTransferTransactionExternal(fldDateChooser.getDate(), user, fromAccount, transferAmount.doubleValue(), transferReason, toUser));
//                }

            centralFrame.refreshData();
            dispose();
        }
        else if (e.getSource() == btnUpdate)
        {
            CAccount fromAccount = (CAccount) cmbFromAccounts.getSelectedItem();
            String transferReason = fldTransferReason.getText();
            Number transferAmount = (Number) fldTransferAmount.getValue();

            if (transferAmount.doubleValue() <= 0)
            {
                showError("Transfer amount could not be empty.");
                return;
            }

            if (rbtnInternal.isSelected())
            {
                CAccount toAccount = (CAccount) cmbToAccounts.getSelectedItem();

                transaction = new CTransferTransaction.CBuilder()
                        .setTrDate(fldDateChooser.getDate())
                        .setFromAccountId(fromAccount.getId())
                        .setToAccountId(toAccount.getId())
                        .setTransferReason(transferReason)
                        .setTransferAmount(transferAmount.doubleValue())
                        .build();


                transferTransactionDAO.update(transaction);
            }
//                else
//                {
//                    CUser toUser = (CUser) cmbUsers.getSelectedItem();
//                    CTransferTransactionExternalDAO.create(new CTransferTransactionExternal(fldDateChooser.getDate(), user, fromAccount, transferAmount.doubleValue(), transferReason, toUser));
//                }

            centralFrame.refreshData();
            dispose();
        }
        else if (e.getSource() == btnCancel)
        {
            dispose();
        }
    }

    private void showError(String errorMessage)
    {
        errorLabel.setText(errorMessage);
        errorLabel.setVisible(true);
        setResizable(true);
        pack();
        setResizable(false);
    }
}
