package com.moneyManagement.gui.framePanel.report;

import com.moneyManagement.api.interfaces.IPlanItemDAO;
import com.moneyManagement.data.CPlanItemResult;
import com.moneyManagement.data.CUser;
import com.moneyManagement.gui.table.model.CPlanResultTableModel;
import com.moneyManagement.gui.table.view.CPlanResultView;
import com.moneyManagement.gui.utils.CDisplay;
import com.moneyManagement.gui.utils.CPropertyHolder;
import org.springframework.beans.factory.annotation.Autowired;

import javax.swing.*;
import java.awt.*;
import java.util.Date;
import java.util.List;

public class CPlanningReport extends CReportFrame
{
    private JScrollPane tblPlanResult;

    @Autowired
    private CPropertyHolder<CUser> mainUser;

    @Autowired
    private IPlanItemDAO planItemDAO;

    @Override
    public void init()
    {
        super.init(mainUser.getValue(), "Planning report");
    }

    @Override
    protected void createComponents()
    {
        super.createComponents();
    }

    @Override
    protected void jbInit()
    {
        setLayout(new GridBagLayout());
        JPanel centralPanel = new JPanel(new GridBagLayout());

        JPanel parametersPanel = new JPanel(new GridBagLayout());

        centralPanel.add(parametersPanel, new GridBagConstraints(0, 0, 1, 1, 1.0, 0,
                GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(3, 3, 3, 3), 0, 0));

        parametersPanel.add(lblStartDate, new GridBagConstraints(0, 0, 1, 1, 0, 0,
                GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(3, 3, 3, 3), 0, 0));
        parametersPanel.add(fldStartDate, new GridBagConstraints(0, 1, 1, 1, 0, 0,
                GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(3, 3, 3, 3), 0, 0));

        parametersPanel.add(lblEndDate, new GridBagConstraints(1, 0, 1, 1, 0, 0,
                GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(3, 3, 3, 3), 0, 0));
        parametersPanel.add(fldEndDate, new GridBagConstraints(1, 1, 1, 1, 0, 0,
                GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(3, 3, 3, 3), 0, 0));

        parametersPanel.add(btnGenerate, new GridBagConstraints(2, 0, 1, 2, 0, 0,
                GridBagConstraints.FIRST_LINE_END, GridBagConstraints.NONE, new Insets(3, 3, 3, 3), 0, 0));

        centralPanel.add(chartPanelContainer, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0,
                GridBagConstraints.FIRST_LINE_START, GridBagConstraints.BOTH, new Insets(3, 3, 3, 3), 0, 0));

        add(centralPanel, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
                GridBagConstraints.FIRST_LINE_START, GridBagConstraints.BOTH, new Insets(3, 3, 3, 3), 0, 0));
    }

    @Override
    protected boolean generateReport()
    {
        if (!super.generateReport())
        {
            return false;
        }

        if (tblPlanResult != null)
        {
            chartPanelContainer.remove(tblPlanResult);
        }

        Date startDate = fldStartDate.getDate();
        Date endDate = fldEndDate.getDate();
        List<CPlanItemResult> results = planItemDAO.loadPlanResults(user, startDate, endDate);

        if (results.isEmpty())
        {
            CDisplay.warning("There is no plans in specified period");
            return false;
        }

        tblPlanResult = new JScrollPane(new CPlanResultView(new CPlanResultTableModel(results, user)));
        chartPanelContainer.add(tblPlanResult, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
                GridBagConstraints.FIRST_LINE_START, GridBagConstraints.BOTH, new Insets(3, 3, 3, 3), 0, 0));
        Dimension oldSize = getSize();
        pack();
        setSize(oldSize);
        return true;
    }
}
