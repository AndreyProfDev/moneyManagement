package com.moneyManagement.gui.framePanel;

import com.moneyManagement.data.CUser;

import javax.swing.*;
import java.awt.*;

public abstract class CAbstractPanel extends JPanel
{
    protected CUser user;
    private Container parent;

    public void setParent(Container parent) {
        this.parent = parent;
    }

    public abstract void init();

    protected void init(CUser user, String title)
    {
        this.user = user;

        setMaximizable(true);
        setResizable(true);
        setClosable(true);

        setTitle(title);

        createComponents();
        fillComponents();
        jbInit();
        pack();
        subscribe();
    }

    protected void createComponents() {
    }

    protected void jbInit()
    {
    }

    protected void subscribe()
    {
    }

    protected void fillComponents() {
    }

    protected void dispose()
    {
        if (parent instanceof JInternalFrame)
        {
            ((JInternalFrame) parent).dispose();
        }
        else if (parent instanceof JFrame)
        {
            ((JFrame) parent).dispose();
        }
    }

    protected void pack()
    {
        if (parent instanceof JInternalFrame)
        {
            ((JInternalFrame) parent).pack();
        }
        else if (parent instanceof JFrame)
        {
            ((JFrame) parent).pack();
        }
    }

    protected void setResizable(boolean resizable)
    {
        if (parent instanceof JInternalFrame)
        {
            ((JInternalFrame) parent).setResizable(resizable);
        }
        else if (parent instanceof JFrame)
        {
            ((JFrame) parent).setResizable(resizable);
        }
    }

    protected void setMaximizable(boolean maximizable)
    {
        if (parent instanceof JInternalFrame)
        {
            ((JInternalFrame) parent).setMaximizable(maximizable);
        }
    }

    protected void setClosable(boolean closable)
    {
        if (parent instanceof JInternalFrame)
        {
            ((JInternalFrame) parent).setClosable(closable);
        }
    }

    protected void setTitle(String title)
    {
        if (parent instanceof JInternalFrame)
        {
            ((JInternalFrame) parent).setTitle(title);
        }
        else if (parent instanceof JFrame)
        {
            ((JFrame) parent).setTitle(title);
        }
    }
}
