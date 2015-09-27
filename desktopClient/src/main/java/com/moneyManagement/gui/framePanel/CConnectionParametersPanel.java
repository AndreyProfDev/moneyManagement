package com.moneyManagement.gui.framePanel;

import com.moneyManagement.gui.widget.IPAddressFormatter;
import org.jdesktop.swingx.text.StrictNumberFormatter;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.prefs.Preferences;

public class CConnectionParametersPanel extends JPanel{
    private static final String REMOTE_IP_ADDRESS_PREF = "remote_ip_address";
    private static final String REMOTE_PORT_PREF = "remote_port";
    private final Preferences prefs = Preferences.userNodeForPackage(CConnectionParametersPanel.class);

    private JLabel lblIpAddress;
    private JTextField fldIpAddress;

    private JLabel lblPort;
    private JTextField fldPort;

    public CConnectionParametersPanel(){
        createComponents();
        fillComponents();
        jbInit();
    }


    private void createComponents()
    {
        lblIpAddress = new JLabel("Remote address");

        lblPort = new JLabel("Remote Port");

        fldIpAddress = new JFormattedTextField(new IPAddressFormatter());

        NumberFormatter formatter = new StrictNumberFormatter(new DecimalFormat());
        formatter.setMinimum(0);
        formatter.setMaximum(32768);
        fldPort = new JFormattedTextField(formatter);
    }

    private void fillComponents(){
        fldIpAddress.setText(prefs.get(REMOTE_IP_ADDRESS_PREF, "127.0.0.1"));
        fldPort.setText(prefs.get(REMOTE_PORT_PREF, "8080"));
    }

    private void jbInit()
    {
        setLayout(new GridBagLayout());

        add(lblIpAddress, new GridBagConstraints(0, 0, 1, 1, 0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(3, 3, 3, 3), 0, 0));

        add(lblPort, new GridBagConstraints(0, 1, 1, 1, 0, 0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(3, 3, 3, 3), 0, 0));
        add(fldIpAddress, new GridBagConstraints(1, 0, 1, 1, 0, 0,
                GridBagConstraints.FIRST_LINE_END, GridBagConstraints.HORIZONTAL, new Insets(3, 3, 3, 3), 0, 0));
        add(fldPort, new GridBagConstraints(1, 1, 1, 1, 0, 0,
                GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(3, 3, 3, 3), 0, 0));
    }

    public String getRemoteIPAddress(){
        return fldIpAddress.getText();
    }

    public String getRemotePort(){
        return fldPort.getText();
    }

    public void updateSystemProperties(){
        prefs.put(REMOTE_IP_ADDRESS_PREF, getRemoteIPAddress());
        prefs.put(REMOTE_PORT_PREF, getRemotePort());
    }
}
