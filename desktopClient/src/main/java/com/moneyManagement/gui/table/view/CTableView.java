package com.moneyManagement.gui.table.view;

import com.moneyManagement.gui.table.cell.CTableButton;
import com.moneyManagement.gui.table.cell.CTableRowSorter;
import com.moneyManagement.gui.table.model.CTableModel;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.event.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.prefs.Preferences;

public abstract class CTableView extends JTable
{
    private static final String TBL_PREFIX = "tbl_";
    private static final String TBL_SUFFIX = "_layout";
    private static final Preferences prefs = Preferences.userNodeForPackage(CTableView.class);

    public CTableView(CTableModel model)
    {
        this(model, true);
    }

    public CTableView(CTableModel model, boolean useRowSorter)
    {
        super(model);

        loadTableLayout();

        if (useRowSorter)
        {
            setRowSorter(new CTableRowSorter(getModel()));
        }

        TableColumn column = getColumnByName(CTableModel.STATUS_COLUMN);
        if (column != null){
            column.setMaxWidth(20);
            column.setMinWidth(20);
        }

        column = getColumnByName(CTableModel.EDIT_COLUMN);
        if (column != null){

            CTableButton editRowTableButton = new CTableButton(this)
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    int rowIndex = table.convertRowIndexToModel(table.getEditingRow());
                    fireEditingStopped();

                    getModel().markRowAsUpdated(rowIndex);

                    repaint();
                }
            };

            column.setMaxWidth(40);
            column.setMinWidth(40);
            column.setCellRenderer(editRowTableButton);
            column.setCellEditor(editRowTableButton);
        }

        column = getColumnByName(CTableModel.ADD_REMOVE_COLUMN);
        if (column != null){

            CTableButton addRemoveTableButton = new CTableButton(this)
            {
                @Override
                public void actionPerformed(ActionEvent e)
                {
                    int row = table.convertRowIndexToModel(table.getEditingRow());
                    fireEditingStopped();

                    if (row == table.getModel().getRowCount())
                    {
                        getModel().addRow();
                    }
                    else
                    {
                        getModel().removeRow(row);
                    }

                    repaint();
                }
            };

            column.setMaxWidth(30);
            column.setMinWidth(30);
            column.setCellRenderer(addRemoveTableButton);
            column.setCellEditor(addRemoveTableButton);
        }

        repaint();
    }

    public void saveTableLayout()
    {
        Map<Integer, Integer> modelToViewColumnsIds = new TreeMap<>();
        TableColumnModel columnModel = getColumnModel();

        for (int columnViewIdx = 0;  columnViewIdx < columnModel.getColumnCount(); columnViewIdx++)
        {
            int columnModelIndex = convertColumnIndexToModel(columnViewIdx);
            modelToViewColumnsIds.put(columnModelIndex, columnViewIdx);
        }


        try (ByteArrayOutputStream bos = new ByteArrayOutputStream())
        {
            try (ObjectOutput out = new ObjectOutputStream(bos))
            {
                out.writeObject(modelToViewColumnsIds);
                prefs.putByteArray(TBL_PREFIX + getTableName() + TBL_SUFFIX, bos.toByteArray());
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void loadTableLayout()
    {
        byte[] propertiesBytes = prefs.getByteArray(TBL_PREFIX + getTableName() + TBL_SUFFIX, null);

        if (propertiesBytes == null)
        {
            return;
        }

        Map<Integer, Integer> modelToViewColumnsIds = new HashMap<>();

        try (ByteArrayInputStream bis = new ByteArrayInputStream(propertiesBytes))
        {
            try (ObjectInput in = new ObjectInputStream(bis))
            {
                modelToViewColumnsIds = (Map<Integer, Integer>) in.readObject();
            }
        }
        catch (ClassNotFoundException | IOException e)
        {
            e.printStackTrace();
        }

        List<Integer> oldColumnsIds = new ArrayList<>(modelToViewColumnsIds.size());

        for (int columnIdx = 0; columnIdx < modelToViewColumnsIds.size(); columnIdx++)
        {
            oldColumnsIds.add(0);
        }

        for (Map.Entry<Integer, Integer> entry : modelToViewColumnsIds.entrySet())
        {
            int columnModelIdx = entry.getKey();
            int columnViewIdx = entry.getValue();
            oldColumnsIds.set(columnViewIdx, columnModelIdx);
        }

        for (Integer newColumnIdx = 0; newColumnIdx < oldColumnsIds.size(); newColumnIdx++)
        {
            getColumnModel().moveColumn(convertColumnIndexToView(oldColumnsIds.get(newColumnIdx)), newColumnIdx);
        }
    }

    protected abstract String getTableName();

    public TableColumn getColumnByName(String columnName){
        int columnModelIndex = getModel().getColumnIndexByName(columnName);
        if (columnModelIndex == -1){
            return null;
        }
        int columnViewIndex = convertColumnIndexToView(columnModelIndex);

        return getColumnModel().getColumn(columnViewIndex);
    }

    @Override
    public CTableModel getModel() {
        return (CTableModel) super.getModel();
    }
}
