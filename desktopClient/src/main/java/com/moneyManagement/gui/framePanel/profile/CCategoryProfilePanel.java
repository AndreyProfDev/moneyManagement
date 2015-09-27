package com.moneyManagement.gui.framePanel.profile;

import com.moneyManagement.api.interfaces.ICategoryDAO;
import com.moneyManagement.data.CCategory;
import com.moneyManagement.data.CUser;
import com.moneyManagement.gui.framePanel.CAbstractPanel;
import com.moneyManagement.gui.table.model.CCategoriesTableModel;
import com.moneyManagement.gui.table.view.CCategoriesTableView;
import com.moneyManagement.gui.utils.CPropertyHolder;
import org.springframework.beans.factory.annotation.Autowired;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class CCategoryProfilePanel extends CAbstractPanel implements ActionListener
{
    private JButton btnSubmit;
    private JButton btnCancel;
    private CCategoriesTableView tblCategories;

    @Autowired
    private CPropertyHolder<CUser> mainUser;

    @Autowired
    private ICategoryDAO categoryDAO;

    @Override
    public void init()
    {
        super.init(mainUser.getValue(), "Categories");
    }

    protected void createComponents()
    {
        btnSubmit = new JButton("Submit");
        btnCancel = new JButton("Cancel");

        tblCategories = new CCategoriesTableView(new CCategoriesTableModel(categoryDAO.loadCategories(user), user));
    }

    protected void jbInit()
    {
        setLayout(new GridBagLayout());
        add(new JScrollPane(tblCategories), new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
                GridBagConstraints.PAGE_START, GridBagConstraints.BOTH, new Insets(3, 3, 3, 3), 0, 0));

        JPanel okCancelPanel = new JPanel();

        add(okCancelPanel, new GridBagConstraints(0, 1, 1, 1, 1.0, 0,
                GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(3, 3, 3, 3), 0, 0));

        okCancelPanel.add(btnSubmit, new GridBagConstraints(0, 0, 1, 1, 0, 0,
                GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(3, 3, 3, 3), 0, 0));
        okCancelPanel.add(btnCancel, new GridBagConstraints(1, 0, 1, 1, 0, 0,
                GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(3, 3, 3, 3), 0, 0));
    }

    @Override
    protected void subscribe()
    {
        btnSubmit.addActionListener(this);
        btnCancel.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == btnSubmit)
        {
            if (tblCategories.isEditing())
            {
                tblCategories.getCellEditor().stopCellEditing();
            }

            new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    for (CCategory category : tblCategories.getModel().getCreatedRows()){
                        categoryDAO.create(category);
                    }

                    for (CCategory category : tblCategories.getModel().getUpdatedRows()){
                        categoryDAO.update(category);
                    }

                    for (CCategory category : tblCategories.getModel().getRemovedRows()){
                        categoryDAO.remove(category);
                    }

                    return null;
                }
            }.execute();

            tblCategories.saveTableLayout();
            dispose();
        }
        else if (e.getSource() == btnCancel)
        {
            tblCategories.saveTableLayout();
            dispose();
        }
    }
}
