package com.moneyManagement.gui.framePanel.profile;

import com.moneyManagement.data.CTransaction;
import com.moneyManagement.gui.framePanel.CAbstractPanel;
import com.moneyManagement.gui.table.view.CTableView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public abstract class CTransactionProfile<T extends CTransaction> extends CAbstractPanel implements ActionListener
{
    protected CTableView table;
    protected JButton btnCreate;
    protected JButton btnUpdate;
    private JButton btnCancel;

    protected T transaction;

    @Override
    protected void createComponents()
    {
        btnCreate = new JButton("Create");
        btnUpdate = new JButton("Update");
        btnCancel = new JButton("Cancel");

        table = getEmptyTable();
    }

    @Override
    protected abstract void fillComponents();

    @Override
    protected void jbInit()
    {
        JPanel okCancelPanel = new JPanel();

        setLayout(new GridBagLayout());
        add(new JScrollPane(table), new GridBagConstraints(0, 0, 1, 1, 1, 1,
                GridBagConstraints.FIRST_LINE_START, GridBagConstraints.BOTH, new Insets(3, 3, 3, 3), 0, 0));
        add(okCancelPanel, new GridBagConstraints(0, 1, 1, 1, 0, 0,
                GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(3, 3, 3, 3), 0, 0));

        okCancelPanel.add(btnCreate, new GridBagConstraints(0, 0, 1, 1, 0, 0,
                GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(3, 3, 3, 3), 0, 0));
        okCancelPanel.add(btnUpdate, new GridBagConstraints(1, 0, 1, 1, 0, 0,
                GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(3, 3, 3, 3), 0, 0));
        okCancelPanel.add(btnCancel, new GridBagConstraints(2, 0, 1, 1, 0, 0,
                GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(3, 3, 3, 3), 0, 0));
    }

    @Override
    protected void subscribe()
    {
        btnCreate.addActionListener(this);
        btnCancel.addActionListener(this);
        btnUpdate.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == btnCreate)
        {
            if (create())
            {
                dispose();
            }
        }
        else if (e.getSource() == btnUpdate)
        {
            if (update())
            {
                dispose();
            }
        }
        else if (e.getSource() == btnCancel)
        {
            dispose();
        }
    }

    protected abstract CTableView getEmptyTable();

    public abstract boolean create();

    public abstract boolean update();
}
