package com.moneyManagement.gui.framePanel;

import com.moneyManagement.gui.frame.CCentralFrame;
import com.moneyManagement.gui.utils.CPropertyHolder;
import org.springframework.beans.factory.annotation.Autowired;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyVetoException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

//@Component
//@Lazy
public class CFrameDisplayer {

    @Autowired
    private CCentralFrame centralFrame;

    @Autowired
    private JDesktopPane desktopPane;

    @Autowired
    private CPropertyHolder<Boolean> useInternalFrame;

    public void display(CAbstractPanel framePanel, boolean maximum, List<Object> parameters){

        if (useInternalFrame.getValue()){
            displayInternalFrame(framePanel, maximum, parameters);
        }
        else{
            displayExternalFrame(framePanel, parameters);
        }

        centralFrame.refreshData();
    }

    private <T extends CAbstractPanel> void displayInternalFrame(T framePanel, boolean maximum, List<Object> parameters){
        try
        {
            Class<?>[] parameterTypes = new Class<?>[parameters.size()];
            Object[] parametersValues = new Object[parameters.size()];
            for (int paramIdx = 0; paramIdx < parameters.size(); paramIdx++)
            {
                parameterTypes[paramIdx] = parameters.get(paramIdx).getClass();
                parametersValues[paramIdx] = parameters.get(paramIdx);
            }

            JInternalFrame frame = new JInternalFrame();
            framePanel.setParent(frame);
            frame.setContentPane(framePanel);
            framePanel.getClass().getMethod("init", parameterTypes).invoke(framePanel, parametersValues);
            frame.setVisible(true);
            desktopPane.add(frame);

            if (maximum)
            {
                frame.setMaximum(true);
            }
            frame.moveToFront();
        }
        catch (PropertyVetoException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e)
        {
            if (e.getCause() != null)
            {
                throw new RuntimeException(e.getCause());
            }
            throw new RuntimeException(e);
        }
    }

    private void displayExternalFrame(CAbstractPanel framePanel, List<Object> parameters){
        try
        {
            Class<?>[] parameterTypes = new Class<?>[parameters.size()];
            Object[] parametersValues = new Object[parameters.size()];
            for (int paramIdx = 0; paramIdx < parameters.size(); paramIdx++)
            {
                parameterTypes[paramIdx] = parameters.get(paramIdx).getClass();
                parametersValues[paramIdx] = parameters.get(paramIdx);
            }

            JFrame frame = new JFrame();
            frame.setLayout(new BorderLayout());
            framePanel.setParent(frame);
            frame.add(framePanel, BorderLayout.CENTER);
            framePanel.getClass().getMethod("init", parameterTypes).invoke(framePanel, parametersValues);
            frame.setVisible(true);
        }
        catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e)
        {
            if (e.getCause() != null)
            {
                throw new RuntimeException(e.getCause());
            }
            throw new RuntimeException(e);
        }
    }
}
