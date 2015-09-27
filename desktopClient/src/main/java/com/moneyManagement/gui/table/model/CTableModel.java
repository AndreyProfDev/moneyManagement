package com.moneyManagement.gui.table.model;

import com.moneyManagement.data.CUser;
import com.moneyManagement.gui.table.cell.CTableItem;

import javax.swing.*;
import javax.swing.table.*;
import java.util.ArrayList;
import java.util.List;

import static com.moneyManagement.gui.table.cell.CTableItem.EDataState;

public abstract class CTableModel<T> extends DefaultTableModel {
    public static final String STATUS_COLUMN = "STATUS_COLUMN";
    public static final String EDIT_COLUMN = "EDIT_COLUMN";
    public static final String ADD_REMOVE_COLUMN = "ADD_REMOVE_COLUMN";

    protected Icon plusIcon;
    protected Icon minusIcon;
    protected Icon editIcon;

    protected int existingEntitiesCount;

    protected List<CTableItem<T>> rows;
    private List<String> columns;

    protected CUser user;

    public CTableModel(List<T> alreadyExistedEntities, CUser user, List<String> columns)
    {
        plusIcon = new ImageIcon(CTableModel.class.getResource("images/plus.png"), "plus");
        minusIcon = new ImageIcon(CTableModel.class.getResource("images/minus.png"), "minus");
        editIcon = new ImageIcon(CTableModel.class.getResource("images/edit.png"), "edit");
        this.user = user;
        existingEntitiesCount = alreadyExistedEntities.size();
        rows = new ArrayList<>();
        this.columns = columns;

        setRows(alreadyExistedEntities);
    }

    @Override
    public int getColumnCount() {
        return columns.size();
    }

    @Override
    public final Object getValueAt(int row, int column)
    {
        if (row == getRowCount() && ADD_REMOVE_COLUMN.equals(columns.get(column))){
            return plusIcon;
        }
        else if (column >= getColumnCount()){
            return null;
        }

        return getValueAt(row, columns.get(column));
    }

    protected Object getValueAt(int row, String column){

        if (row >= getRowCount()){
            return null;
        }

        switch (column){
            case STATUS_COLUMN:{
                return rows.get(row).getDataState().getIcon();
            }
            case EDIT_COLUMN:{
                if (EDataState.NONE.equals(rows.get(row).getDataState()) || EDataState.REMOVED.equals(rows.get(row).getDataState()))
                {
                    return editIcon;
                }
                else
                {
                    return null;
                }
            }
            case ADD_REMOVE_COLUMN:{
                return minusIcon;
            }
        }

        return super.getValueAt(row, getColumnIndexByName(column));
    }

    @Override
    public final String getColumnName(int columnIndex){
        return getColumnName(columns.get(columnIndex));
    }

    protected String getColumnName(String columnName){
        switch (columnName)
        {
            case STATUS_COLUMN:
            {
                return ""; //status column
            }
            case EDIT_COLUMN:
            {
                return "Edit";
            }
            case ADD_REMOVE_COLUMN:
            {
                return ""; //add/remove button
            }
        }

        return super.getColumnName(getColumnIndexByName(columnName));
    }

    @Override
    public final boolean isCellEditable(int row, int column)
    {
        return isCellEditable(row, columns.get(column));
    }

    protected boolean isCellEditable(int row, String column)
    {
        switch (column) {
            case STATUS_COLUMN: {
                return false; //status column
            }
            case EDIT_COLUMN: {
                return row < rows.size() &&
                        (EDataState.REMOVED.equals(rows.get(row).getDataState()) ||
                       EDataState.NONE.equals(rows.get(row).getDataState()));
            }
            case ADD_REMOVE_COLUMN: {
                return true; //add/remove button
            }
        }

        return false;//super.isCellEditable(row, getColumnIndexByName(column));
    }

    @Override
    public final Class<?> getColumnClass(int columnIndex) {
        return getColumnClass(columns.get(columnIndex));
    }

    protected Class<?> getColumnClass(String column) {
        switch (column) {
            case STATUS_COLUMN:
            case EDIT_COLUMN:
            case ADD_REMOVE_COLUMN: {
                return Icon.class; //add/remove button
            }
        }

        return super.getColumnClass(getColumnIndexByName(column));
    }

    @Override
    public final void setValueAt(Object aValue, int row, int column) {
        if (row >= getRowCount() || column >= getColumnCount())
        {
            return;
        }

        setValueAt(aValue, row, columns.get(column));
    }

    public void setValueAt(Object aValue, int row, String column) {
        super.setValueAt(aValue, row, getColumnIndexByName(column));
    }

    public CTableItem<T> addRow(boolean newRow, T entity)
    {
        CTableItem<T> result = new CTableItem<T>(newRow ? EDataState.NEW : EDataState.NONE, entity);
        rows.add(result);

        if (!newRow)
        {
            existingEntitiesCount++;
        }

        Object[] values = new Object[getColumnCount()];
        for (int i = 0; i < getColumnCount(); i++){
            values[i] = getValueAt(rows.size() - 1, i);
        }

        addRow(values);

        return result;
    }

    public abstract void addRow();

    @Override
    public void removeRow(int row)
    {
        if (row < existingEntitiesCount)
        {
            rows.get(row).setDataState(EDataState.REMOVED);
            return;
        }
        rows.remove(row);
        super.removeRow(row);
    }

    public void setRows(List<T> alreadyExistedEntities)
    {
        rows.clear();
        existingEntitiesCount = 0;

        while (getRowCount() > 0)
        {
            super.removeRow(0);
        }

        for (T entity : alreadyExistedEntities)
        {
            addRow(false, entity);
        }

        fireTableDataChanged();
    }

    public void markRowAsUpdated(int rowIndex)
    {
        rows.get(rowIndex).setDataState(EDataState.UPDATED);
    }

    public List<T> getCreatedRows(){
        List<T> result = new ArrayList<>();

        for (CTableItem<T> row : rows)
        {
            if (EDataState.NEW.equals(row.getDataState())){
                result.add(row.getValue());
            }
        }

        return result;
    }

    public List<T> getUpdatedRows(){
        List<T> result = new ArrayList<>();

        for (CTableItem<T> row : rows)
        {
            if (EDataState.UPDATED.equals(row.getDataState())){
                result.add(row.getValue());
            }
        }

        return result;
    }

    public List<T> getRemovedRows(){
        List<T> result = new ArrayList<>();

        for (CTableItem<T> row : rows)
        {
            if (EDataState.REMOVED.equals(row.getDataState())){
                result.add(row.getValue());
            }
        }

        return result;
    }

    public T getRow(int rowIdx){
        return rows.get(rowIdx).getValue();
    }

    public List<T> getRows(){

        List<T> result = new ArrayList<>();

        for (CTableItem<T> row : rows){
            result.add(row.getValue());
        }

        return result;
    }


    public int getColumnIndexByName(String columnName){
        return columns.indexOf(columnName);
    }
}
