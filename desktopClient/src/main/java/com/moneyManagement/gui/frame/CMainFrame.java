package com.moneyManagement.gui.frame;

import com.moneyManagement.gui.utils.CPair;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyVetoException;
import java.util.List;

public class CMainFrame extends JFrame
{
    @Autowired
    private JDesktopPane desktopPane;

    @Autowired
    private CCentralFrame centralFrame;

    @Resource(name = "menuActions")
    private List<CPair<String, List<AbstractAction>>> menuActions;

    @PostConstruct
    public void init(){
        jbInit();

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(new Dimension(800, 600));
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void jbInit()
    {

        desktopPane.add(centralFrame);
        centralFrame.setVisible(true);

        try
        {
            centralFrame.setMaximum(true);
        }
        catch (PropertyVetoException e)
        {
            e.printStackTrace();
        }

        setContentPane(desktopPane);
        setJMenuBar(createMenuBar());
    }

    private JMenuBar createMenuBar()
    {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);
        fileMenu.add(new AbstractAction("Exit")
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                System.exit(0);
            }
        });

        for (CPair<String, List<AbstractAction>> menu : menuActions){
            JMenu newMenu = new JMenu(menu.getValue1());
            menuBar.add(newMenu);

            for (AbstractAction action : menu.getValue2()){
                newMenu.add(action);
            }
        }

        return menuBar;
    }
}
