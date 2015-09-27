package com.moneyManagement.gui.frame;


import com.caucho.hessian.client.HessianProxyFactory;
import com.moneyManagement.api.interfaces.IPing;
import com.moneyManagement.api.interfaces.IUserDAO;
import com.moneyManagement.data.CUser;
import com.moneyManagement.gui.framePanel.CConnectionParametersPanel;
import com.moneyManagement.gui.utils.CDisplay;
import com.moneyManagement.gui.utils.CPropertyHolder;
import com.moneyManagement.gui.widget.CTextField;
import org.jdesktop.swingx.JXTaskPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

public abstract class CLoginFrame extends JFrame implements ActionListener
{
    private static final String LOGIN_PREF = "user_login";
    private static final String PASSWORD_PREF = "user_password";
    private final Preferences prefs = Preferences.userNodeForPackage(CLoginFrame.class);

    private JLabel lblCheckLoginPassword;

    private JLabel lblLogin;
    private CTextField fldLogin;

    private JLabel lblPassword;
    private JPasswordField fldPassword;

    private JCheckBox chbRememberSettings;

    private JButton btnSubmit;
    private JButton btnCancel;

    private CConnectionParametersPanel pnlConnectionParams;
    private JXTaskPane connectionTaskPane;

    @Autowired
    private CPropertyHolder<String> serverURL;

    @Autowired
    private CPropertyHolder<CUser> mainUser;

    @PostConstruct
    public void init() throws Exception {
        createComponents();
        fillComponents();
        jbInit();
        pack();
        subscribe();

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
    //        setVisible(true);
    }

    private void createComponents()
    {
        lblCheckLoginPassword = new JLabel("Login or password invalid. Please try again.");

        lblLogin = new JLabel("Login:");
        fldLogin = new CTextField("Enter login");

        lblPassword = new JLabel("Password:");
        fldPassword = new JPasswordField();

        btnSubmit = new JButton("Submit");
        btnCancel = new JButton("Cancel");

        chbRememberSettings = new JCheckBox("Remember settings on this PC");

        pnlConnectionParams = new CConnectionParametersPanel();
    }

    private void fillComponents()
    {
        fldLogin.setText(prefs.get(LOGIN_PREF, ""));
        fldPassword.setText(prefs.get(PASSWORD_PREF, ""));

        connectionTaskPane = new JXTaskPane("Connection settings");
        connectionTaskPane.setCollapsed(true);
    }

    private void jbInit()
    {
        setLayout(new GridBagLayout());

        JPanel centralPanel = new JPanel(new GridBagLayout());
        add(centralPanel, new GridBagConstraints(0, 0, 1, 1, 1, 0,
                GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(3, 3, 3, 3), 0, 0));

        centralPanel.add(lblCheckLoginPassword, new GridBagConstraints(0, 0, 2, 1, 0, 0,
                GridBagConstraints.PAGE_START, GridBagConstraints.NONE, new Insets(3, 3, 3, 3), 0, 0));

        centralPanel.add(lblLogin, new GridBagConstraints(0, 1, 1, 1, 0, 0,
                GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(3, 3, 3, 3), 0, 0));
        centralPanel.add(fldLogin, new GridBagConstraints(1, 1, 1, 1, 1, 0,
                GridBagConstraints.FIRST_LINE_END, GridBagConstraints.HORIZONTAL, new Insets(3, 3, 3, 3), 0, 0));
        centralPanel.add(lblPassword, new GridBagConstraints(0, 2, 1, 1, 0, 0,
                GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(3, 3, 3, 3), 0, 0));
        centralPanel.add(fldPassword, new GridBagConstraints(1, 2, 1, 1, 1, 0,
                GridBagConstraints.FIRST_LINE_END, GridBagConstraints.HORIZONTAL, new Insets(3, 3, 3, 3), 0, 0));
        centralPanel.add(chbRememberSettings, new GridBagConstraints(0, 3, 2, 1, 1, 0,
                GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(3, 3, 3, 3), 0, 0));

        JPanel btnsPanel = new JPanel(new GridBagLayout());
        add(btnsPanel, new GridBagConstraints(0, 1, 1, 1, 1, 0,
                GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(3, 3, 3, 3), 0, 0));

        btnsPanel.add(btnSubmit, new GridBagConstraints(0, 0, 1, 1, 0, 0,
                GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(3, 3, 3, 3), 0, 0));
        btnsPanel.add(btnCancel, new GridBagConstraints(1, 0, 1, 1, 0, 0,
                GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(3, 3, 3, 3), 0, 0));

        connectionTaskPane.add(pnlConnectionParams);
        add(connectionTaskPane, new GridBagConstraints(0, 2, 1, 1, 1, 0,
                GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(3, 3, 3, 3), 0, 0));

        lblCheckLoginPassword.setVisible(false);
        pack();
    }

    private void subscribe()
    {
        btnSubmit.addActionListener(this);
        btnCancel.addActionListener(this);
        connectionTaskPane.addPropertyChangeListener("collapsed", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                setResizable(true);
                pack();
                setResizable(false);
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == btnSubmit)
        {
            String login = fldLogin.getText();
            String password = String.valueOf(fldPassword.getPassword());

            if (!validateInput(login, password)){
                return;
            }

            try{

                String remoteAddress = pnlConnectionParams.getRemoteIPAddress();
                String remotePort = pnlConnectionParams.getRemotePort();

                HessianProxyFactory factory = new HessianProxyFactory();
                IPing pingService = (IPing) factory.create(IPing.class, "http://" + remoteAddress + ":" + remotePort + "/remoting/pingService");

                if (!pingService.ping()){
                    return;
                }

                serverURL.setValue("http://" + remoteAddress + ":" + remotePort);

                factory = new HessianProxyFactory();
                IUserDAO userDAO = (IUserDAO) factory.create(IUserDAO.class, "http://" + remoteAddress + ":" + remotePort + "/remoting/userDAOService");

                if (!userDAO.checkExists(login, password))
                {
                    CDisplay.warning("Login or password invalid. Please, try again.");
                    return;
                }

                mainUser.setValue(userDAO.loadByLogin(login));

                if (chbRememberSettings.isSelected())
                {
                    updateSystemProperties();
                }

                getMainFrame().setVisible(true);
                dispose();
            }
            catch (Exception ex){
                CDisplay.error("An error occurred during checking credentials.\nPlease check you connection settings and try again.");
                ex.printStackTrace();
                return;
            }
        }
        else if (e.getSource() == btnCancel)
        {
            setVisible(false);
            System.exit(0);
        }
//        else if (e.getSource() == btnRegister)
//        {
//            setVisible(false);
//            CRegisterFrame registerFrame = new CRegisterFrame();
//
//            CUser newUser = registerFrame.getUser();
//
//            if (newUser != null)
//            {
//                fldLogin.setText(newUser.getLogin());
//                fldPassword.setText(newUser.getPassword());
//            }
//
//            setVisible(true);
//        }
    }


    private boolean validateInput(String login, String password)
    {
        if (login == null || login.isEmpty())
        {
            CDisplay.warning("Please, enter login.");
            return false;
        }
        else if (password == null || password.isEmpty())
        {
            CDisplay.warning("Please, enter password.");
            return false;
        }

        return true;
    }

    private void updateSystemProperties(){

        try {
            prefs.put(LOGIN_PREF, fldLogin.getText());
            prefs.put(PASSWORD_PREF, new String(fldPassword.getPassword()));
            pnlConnectionParams.updateSystemProperties();
            prefs.flush();
        } catch (BackingStoreException e) {
            e.printStackTrace();
        }

    }

    public abstract CMainFrame getMainFrame();
}
