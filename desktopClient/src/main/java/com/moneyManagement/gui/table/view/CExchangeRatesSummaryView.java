package com.moneyManagement.gui.table.view;

import com.moneyManagement.data.CExchangeRate;
import com.moneyManagement.data.CUser;
import com.moneyManagement.gui.framePanel.CFrameDisplayer;
import com.moneyManagement.gui.framePanel.profile.CExchangeRateProfile;
import com.moneyManagement.gui.table.cell.CTableButton;
import com.moneyManagement.gui.table.model.CExchangeRateSummaryTableModel;
import com.moneyManagement.gui.table.model.CTableModel;

import javax.swing.table.*;
import java.awt.event.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CExchangeRatesSummaryView extends CTableView
{
    public CExchangeRatesSummaryView(CUser user, final CFrameDisplayer frameDisplayer, final CExchangeRateProfile exchangeRateProfile)
    {
        super(new CExchangeRateSummaryTableModel(Collections.<CExchangeRate>emptyList(), user), false);
        setAutoCreateColumnsFromModel(true);

        CTableButton editRowTableButton = new CTableButton(this)
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                final int rowIndex = table.convertRowIndexToModel(table.getEditingRow());
                fireEditingStopped();

                CExchangeRate exchangeRate = getModel().getRow(rowIndex);
                frameDisplayer.display(exchangeRateProfile, false, Arrays.<Object>asList(exchangeRate));

                repaint();
            }
        };

        TableColumn editColumn = getColumnByName(CTableModel.EDIT_COLUMN);
        editColumn.setCellRenderer(editRowTableButton);
        editColumn.setCellEditor(editRowTableButton);
    }

    public void setRows(List<CExchangeRate> rows)
    {
        getModel().setRows(rows);
    }

    @Override
    public CExchangeRateSummaryTableModel getModel()
    {
        return (CExchangeRateSummaryTableModel) super.getModel();
    }

    @Override
    protected String getTableName()
    {
        return "exchange_rates__summary";
    }
}