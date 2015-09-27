package com.moneyManagement.gui;

import com.moneyManagement.gui.utils.CEventQueueProxy;
import com.moneyManagement.gui.frame.CLoginFrame;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javax.swing.*;
import java.awt.*;

public class CLauncher {
    public static void main(String[] args){

//        Catch all exceptions un EDT
        EventQueue queue = Toolkit.getDefaultToolkit().getSystemEventQueue();
        queue.push(new CEventQueueProxy());

        try
        {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels())
            {
                if ("Nimbus".equals(info.getName()))
                {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(CServicesContext.class, CGUIContext.class, CUIActionsContext.class);

        CLoginFrame loginFrame = ctx.getBean(CLoginFrame.class);
        loginFrame.setVisible(true);

    }
}
