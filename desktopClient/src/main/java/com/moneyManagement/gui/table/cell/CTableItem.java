package com.moneyManagement.gui.table.cell;

import javax.swing.*;

public class CTableItem<T> {
    public enum EDataState{
        NEW(new ImageIcon(CTableItem.class.getResource("images/add.png"), "plus")),
        UPDATED(new ImageIcon(CTableItem.class.getResource("images/update.png"), "update")),
        REMOVED(new ImageIcon(CTableItem.class.getResource("images/remove.png"), "remove")),
        NONE(null);

        private Icon icon;

        EDataState(Icon icon)
        {
            this.icon = icon;
        }

        public Icon getIcon()
        {
            return icon;
        }
    }

    public CTableItem(EDataState dataState, T value) {
        this.dataState = dataState;
        this.value = value;
    }

    private EDataState dataState;

    private T value;

    public EDataState getDataState() {
        return dataState;
    }

    public void setDataState(EDataState dataState) {
        this.dataState = dataState;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}
