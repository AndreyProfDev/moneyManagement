package com.moneyManagement.gui.frame;

import com.moneyManagement.api.interfaces.IAccountDAO;
import com.moneyManagement.api.interfaces.IExchangeRateDAO;
import com.moneyManagement.api.interfaces.IUserDAO;
import com.moneyManagement.data.CAccount;
import com.moneyManagement.data.CUser;
import com.moneyManagement.data.EAccountType;
import com.moneyManagement.gui.framePanel.CUserPanel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component(value = "centralFrame")
@Lazy
public class CCentralFrame extends JInternalFrame
{
    private JTabbedPane tabbedPane;
    private CUserPanel[] userPanels;

    @Autowired
    private IUserDAO userDAO;

    @Autowired
    private IAccountDAO accountDAO;

    @Autowired
    private IExchangeRateDAO exchangeRateDAO;

    public CCentralFrame(){
        super();
    }

    @PostConstruct
    public void init() throws Exception {
        setMaximizable(false);
        setResizable(false);
        setClosable(false);

        setTitle("");

        createComponents();
        jbInit();
        pack();

        setLayer(-1);
    }

    protected void createComponents()
    {
        tabbedPane = new JTabbedPane();
        java.util.List<CUser> usersList = userDAO.loadAll();
        usersList.add(null);
        userPanels = new CUserPanel[usersList.size()];

        for (int userIdx = 0; userIdx < usersList.size(); userIdx++)
        {
            CUser user = usersList.get(userIdx);
            Map<EAccountType, List<CAccount>> userAccounts = new HashMap<>();
            userAccounts.put(EAccountType.BANK_CARD, accountDAO.loadAccounts(user, EAccountType.BANK_CARD));
            userAccounts.put(EAccountType.CASH, accountDAO.loadAccounts(user, EAccountType.CASH));

            userPanels[userIdx] = new CUserPanel(accountDAO, exchangeRateDAO, user, userAccounts);
        }
    }

    public void refreshData()
    {
        for (CUserPanel userPanel : userPanels)
        {
            Map<EAccountType, List<CAccount>> userAccounts = new HashMap<>();
            userAccounts.put(EAccountType.BANK_CARD, accountDAO.loadAccounts(userPanel.getUser(), EAccountType.BANK_CARD));
            userAccounts.put(EAccountType.CASH, accountDAO.loadAccounts(userPanel.getUser(), EAccountType.CASH));
            userPanel.refreshData(userAccounts);
        }
    }

    protected void jbInit()
    {
        JPanel centralPanel = new JPanel(new GridBagLayout());

        centralPanel.add(tabbedPane, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
                GridBagConstraints.PAGE_START, GridBagConstraints.BOTH, new Insets(3, 3, 3, 3), 0, 0));

        for (CUserPanel userPanel : userPanels)
        {
            CUser user = userPanel.getUser();

            if (user != null)
            {
                tabbedPane.add(user.getLogin() + "'s assets", userPanel);
            }
            else
            {
                tabbedPane.add("General assets", userPanel);
            }

        }
        setContentPane(centralPanel);
    }
}
