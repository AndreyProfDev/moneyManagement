package com.moneyManagement.gui.framePanel.profile;

import com.moneyManagement.data.CUser;
import com.moneyManagement.gui.framePanel.CAbstractPanel;
import com.moneyManagement.gui.utils.CPropertyHolder;
import org.springframework.beans.factory.annotation.Autowired;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.prefs.Preferences;

public class CSetupsPanel extends CAbstractPanel implements ActionListener
{
    private JButton btnOk;
    private JButton btnCancel;
    private JCheckBox chbExternalFrames;

    private static final String USE_INTERNAL_FRAMES_PREF = "use_internal_frames";
    private static final Preferences prefs = Preferences.userNodeForPackage(CSetupsPanel.class);

    @Autowired
    private CPropertyHolder<CUser> mainUser;

    @Autowired
    private CPropertyHolder<Boolean> useInternalFrames;

    @Override
    public void init()
    {
        super.init(mainUser.getValue(), "Setups");
    }

    @Override
    protected void createComponents()
    {
        btnOk = new JButton("Ok");
        btnCancel = new JButton("Cancel");
        chbExternalFrames = new JCheckBox("Use external frames");
    }

    @Override
    protected void jbInit()
    {
        setLayout(new GridBagLayout());

        add(chbExternalFrames, new GridBagConstraints(0, 0, 1, 1, 0, 0,
                GridBagConstraints.PAGE_START, GridBagConstraints.HORIZONTAL, new Insets(3, 3, 3, 3), 0, 0));

        JPanel okCancelPanel = new JPanel();

        add(okCancelPanel, new GridBagConstraints(0, 1, 1, 1, 1.0, 0,
                GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(3, 3, 3, 3), 0, 0));

        okCancelPanel.add(btnOk, new GridBagConstraints(0, 0, 1, 1, 0, 0,
                GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(3, 3, 3, 3), 0, 0));
        okCancelPanel.add(btnCancel, new GridBagConstraints(1, 0, 1, 1, 0, 0,
                GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(3, 3, 3, 3), 0, 0));
    }

    @Override
    protected void fillComponents()
    {
        chbExternalFrames.setSelected(!useInternalFrames.getValue());
    }

    @Override
    protected void subscribe()
    {
        btnOk.addActionListener(this);
        btnCancel.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == btnOk)
        {
            useInternalFrames.setValue(!chbExternalFrames.isSelected());
            prefs.putBoolean(USE_INTERNAL_FRAMES_PREF, useInternalFrames.getValue());
            dispose();
        }
        else if (e.getSource() == btnCancel)
        {
            dispose();
        }
    }
}
