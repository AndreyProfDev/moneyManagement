package com.moneyManagement.gui.widget;

import javax.swing.*;
import java.awt.event.*;

public class CTextField extends JTextField implements FocusListener
{
    private final String hint;
    private boolean showingHint;

    public CTextField(final String hiddenText)
    {
        super(hiddenText);
        this.hint = hiddenText;
        this.showingHint = true;
        super.addFocusListener(this);
    }

    @Override
    public void setText(String t)
    {
        super.setText(t);
        showingHint = false;
    }

    @Override
    public void focusGained(FocusEvent e)
    {
        if(this.getText().isEmpty())
        {
            super.setText("");
            showingHint = false;
        }
    }

    @Override
    public void focusLost(FocusEvent e)
    {
        if(this.getText().isEmpty())
        {
            super.setText(hint);
            showingHint = true;
        }
    }

    @Override
    public String getText()
    {
        return showingHint ? "" : super.getText();
    }
}