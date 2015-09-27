package com.moneyManagement.gui.framePanel.report;

import com.moneyManagement.gui.framePanel.CAbstractPanel;
import com.moneyManagement.gui.utils.CDisplay;
import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public abstract class CReportFrame extends CAbstractPanel implements ActionListener
{
    protected JPanel chartPanelContainer;

    protected JLabel lblStartDate;
    protected JDateChooser fldStartDate;

    protected JLabel lblEndDate;
    protected JDateChooser fldEndDate;

    protected JButton btnGenerate;

    @Override
    protected void createComponents() {
        lblStartDate = new JLabel("Start date");
        fldStartDate = new JDateChooser();

        lblEndDate = new JLabel("End date");
        fldEndDate = new JDateChooser();

        btnGenerate = new JButton("Generate");

        chartPanelContainer = new JPanel(new GridBagLayout());
    }

    @Override
    protected void fillComponents()
    {


        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());

        calendar.add(Calendar.DAY_OF_MONTH, -1);
        fldStartDate.setLocale(Locale.ENGLISH);
        fldStartDate.setDateFormatString("dd/MM/yyyy");
        fldStartDate.setCalendar((Calendar) calendar.clone());

        calendar.add(Calendar.DAY_OF_MONTH, 2);
        fldEndDate.setLocale(Locale.ENGLISH);
        fldEndDate.setDateFormatString("dd/MM/yyyy");
        fldEndDate.setCalendar((Calendar) calendar.clone());
    }

    @Override
    protected void subscribe()
    {
        btnGenerate.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == btnGenerate)
        {
            generateReport();
        }
    }

    protected boolean generateReport()
    {
        Calendar startDate = new GregorianCalendar();
        startDate.setTime(fldStartDate.getDate());

        Calendar endDate = new GregorianCalendar();
        endDate.setTime(fldEndDate.getDate());

        if (startDate.after(endDate) || startDate.equals(endDate))
        {
            CDisplay.warning("Start date should be after end date.");

            return false;
        }

        return true;
    }
}
