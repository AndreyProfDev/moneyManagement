package com.moneyManagement.gui.framePanel.profile;

import com.moneyManagement.api.interfaces.IAccountDAO;
import com.moneyManagement.api.interfaces.ICurrencyDAO;
import com.moneyManagement.data.CAccount;
import com.moneyManagement.data.CUser;
import com.moneyManagement.gui.frame.CCentralFrame;
import com.moneyManagement.gui.table.model.CAccountsTableModel;
import com.moneyManagement.gui.utils.CPropertyHolder;
import com.moneyManagement.gui.framePanel.CAbstractPanel;
import com.moneyManagement.gui.table.view.CAccountsTableView;
import org.springframework.beans.factory.annotation.Autowired;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class CAccountProfilePanel extends CAbstractPanel implements ActionListener
{
    private JButton btnOk;
    private JButton btnCancel;
    private CAccountsTableView tblAccounts;

    @Autowired
    private CCentralFrame centralFrame;

    @Autowired
    private CPropertyHolder<CUser> mainUser;

    @Autowired
    private IAccountDAO accountDAO;

    @Autowired
    private ICurrencyDAO currencyDAO;

    @Override
    public void init()
    {
        super.init(mainUser.getValue(), "Accounts");
    }

    protected void createComponents()
    {
        btnOk = new JButton("Ok");
        btnCancel = new JButton("Cancel");
        List<CAccount> accounts = new ArrayList<>();
        accounts.addAll(accountDAO.loadAccounts(user));
        accounts.addAll(accountDAO.loadAccounts(null));

        tblAccounts = new CAccountsTableView(new CAccountsTableModel(accounts, user), currencyDAO.loadAll());
    }

    protected void jbInit()
    {
        setLayout(new GridBagLayout());
        add(new JScrollPane(tblAccounts), new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
                GridBagConstraints.PAGE_START, GridBagConstraints.BOTH, new Insets(3, 3, 3, 3), 0, 0));

        JPanel okCancelPanel = new JPanel();

        add(okCancelPanel, new GridBagConstraints(0, 1, 1, 1, 1.0, 0,
                GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(3, 3, 3, 3), 0, 0));

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
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == btnOk)
        {
            if (tblAccounts.isEditing())
            {
                tblAccounts.getCellEditor().stopCellEditing();
            }

            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    for (CAccount account : tblAccounts.getModel().getCreatedRows()){
                        accountDAO.create(account);
                    }

                    for (CAccount account : tblAccounts.getModel().getUpdatedRows()){
                        accountDAO.update(account);
                    }

                    for (CAccount account : tblAccounts.getModel().getRemovedRows()){
                        accountDAO.remove(account);
                    }

                    return null;
                }

                @Override
                protected void done() {
                    centralFrame.refreshData();
                }
            }.execute();
        }
        else if (e.getSource() == btnCancel)
        {
            //do nothing
        }

        tblAccounts.saveTableLayout();
        dispose();
    }
}
