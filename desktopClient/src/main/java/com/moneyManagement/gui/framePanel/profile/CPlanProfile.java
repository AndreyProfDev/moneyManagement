package com.moneyManagement.gui.framePanel.profile;

import com.moneyManagement.api.interfaces.IPlanItemDAO;
import com.moneyManagement.data.CPlanItem;
import com.moneyManagement.data.CUser;
import com.moneyManagement.gui.framePanel.CAbstractPanel;
import com.moneyManagement.gui.table.model.CPlanItemsTableModel;
import com.moneyManagement.gui.table.view.CPlanItemsView;
import com.moneyManagement.gui.utils.CPropertyHolder;
import org.springframework.beans.factory.annotation.Autowired;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class CPlanProfile extends CAbstractPanel implements ActionListener
{
    private JButton btnSubmit;
    private JButton btnCancel;
    private CPlanItemsView tblPlans;

    @Autowired
    private IPlanItemDAO planItemDAO;

    @Autowired
    private CPropertyHolder<CUser> mainUser;

    @Override
    public void init()
    {
        super.init(mainUser.getValue(), "Plans");
    }

    protected void createComponents()
    {
        btnSubmit = new JButton("Submit");
        btnCancel = new JButton("Cancel");
        List<CPlanItem> plans = new ArrayList<>();
        plans.addAll(planItemDAO.loadPlans(user));

        tblPlans = new CPlanItemsView(new CPlanItemsTableModel(plans, user));
    }

    protected void jbInit()
    {
        setLayout(new GridBagLayout());
        add(new JScrollPane(tblPlans), new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
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
            if (tblPlans.isEditing())
            {
                tblPlans.getCellEditor().stopCellEditing();
            }

            new Thread(){
                @Override
                public void run() {
                    for (CPlanItem planItem : tblPlans.getModel().getCreatedRows()){
                        planItemDAO.create(planItem);
                    }

                    for (CPlanItem planItem : tblPlans.getModel().getUpdatedRows()){
                        planItemDAO.update(planItem);
                    }

                    for (CPlanItem planItem : tblPlans.getModel().getRemovedRows()){
                        planItemDAO.remove(planItem);
                    }
                }
            }.start();

            tblPlans.saveTableLayout();
            dispose();
        }
        else if (e.getSource() == btnCancel)
        {
            tblPlans.saveTableLayout();
            dispose();
        }
    }
}
