package com.moneyManagement.gui.utils;

public class CPropertyHolder<T> {
    private T value;

    public CPropertyHolder() {
    }

    public CPropertyHolder(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }
}
