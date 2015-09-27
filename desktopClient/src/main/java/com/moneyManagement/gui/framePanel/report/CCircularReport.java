package com.moneyManagement.gui.framePanel.report;

import com.moneyManagement.api.interfaces.IAccountDAO;
import com.moneyManagement.api.interfaces.IExchangeRateDAO;
import com.moneyManagement.api.interfaces.IIncomeExpenseTransactionDAO;
import com.moneyManagement.data.CAccount;
import com.moneyManagement.data.CIncomeExpenseTransaction;
import com.moneyManagement.data.CIncomeExpenseTransactionItem;
import com.moneyManagement.data.CUser;
import com.moneyManagement.data.ECategoryType;
import com.moneyManagement.gui.utils.CPropertyHolder;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.util.Rotation;
import org.springframework.beans.factory.annotation.Autowired;

import javax.swing.*;
import java.awt.*;
import java.text.NumberFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CCircularReport extends CReportFrame
{
    private ChartPanel chartPanel;

    @Autowired
    private CPropertyHolder<CUser> mainUser;

    @Autowired
    private IIncomeExpenseTransactionDAO incomeExpenseTransactionDAO;

    @Autowired
    private IAccountDAO accountDAO;

    @Autowired
    private IExchangeRateDAO exchangeRateDAO;

    @Override
    public void init()
    {
        super.init(mainUser.getValue(), "Circular report");
    }

    @Override
    protected void jbInit()
    {
        setLayout(new GridBagLayout());
        JPanel centralPanel = new JPanel(new GridBagLayout());

        JPanel parametersPanel = new JPanel(new GridBagLayout());

        centralPanel.add(parametersPanel, new GridBagConstraints(0, 0, 1, 1, 1.0, 0,
                GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(3, 3, 3, 3), 0, 0));

        parametersPanel.add(lblStartDate, new GridBagConstraints(0, 0, 1, 1, 0, 0,
                GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(3, 3, 3, 3), 0, 0));
        parametersPanel.add(fldStartDate, new GridBagConstraints(0, 1, 1, 1, 0, 0,
                GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(3, 3, 3, 3), 0, 0));

        parametersPanel.add(lblEndDate, new GridBagConstraints(1, 0, 1, 1, 0, 0,
                GridBagConstraints.FIRST_LINE_START, GridBagConstraints.NONE, new Insets(3, 3, 3, 3), 0, 0));
        parametersPanel.add(fldEndDate, new GridBagConstraints(1, 1, 1, 1, 0, 0,
                GridBagConstraints.FIRST_LINE_START, GridBagConstraints.HORIZONTAL, new Insets(3, 3, 3, 3), 0, 0));

        parametersPanel.add(btnGenerate, new GridBagConstraints(2, 0, 1, 2, 0, 0,
                GridBagConstraints.FIRST_LINE_END, GridBagConstraints.NONE, new Insets(3, 3, 3, 3), 0, 0));

        centralPanel.add(chartPanelContainer, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0,
                GridBagConstraints.FIRST_LINE_START, GridBagConstraints.BOTH, new Insets(3, 3, 3, 3), 0, 0));

        add(centralPanel, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
                GridBagConstraints.FIRST_LINE_START, GridBagConstraints.BOTH, new Insets(3, 3, 3, 3), 0, 0));
    }

    private ChartPanel createChartPanel(Date startDate, Date endDate)
    {
        PieDataset dataset = createDataset(startDate, endDate);
        JFreeChart result = ChartFactory.createPieChart3D("Expenses by categories", dataset, true, true, true);
        PiePlot3D plot = (PiePlot3D) result.getPlot();
        plot.setDirection(Rotation.CLOCKWISE);
        plot.setForegroundAlpha(0.60f);
        plot.setBackgroundPaint(Color.white);
        plot.setDepthFactor(0.25);
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator(
                "{0} = {1}({2})", NumberFormat.getNumberInstance(), NumberFormat.getPercentInstance()
        ));
        plot.setNoDataMessage("No data available");

        return new ChartPanel(result);
    }

    private DefaultPieDataset createDataset(Date startDate, Date endDate)
    {
        DefaultPieDataset result = new DefaultPieDataset();
        Map<String, Double> categoryToExpense = new HashMap<>();

        HashMap<Integer, CAccount> accounts = new HashMap<>();
        HashMap<String, Double> exchangeRates = new HashMap<>();
        exchangeRates.put(user.getBaseCurrency(), 1.0);

        for (CIncomeExpenseTransaction transaction : incomeExpenseTransactionDAO.loadAll(user, startDate, endDate, ECategoryType.EXPENSE))
        {
            CAccount account = accounts.get(transaction.getAccountId());
            if (account == null){
                account = accountDAO.loadAccount(transaction.getAccountId());
                accounts.put(transaction.getAccountId(), account);
            }

            Double exchangeRate = exchangeRates.get(account.getCurrency());
            if (exchangeRate == null){
                exchangeRate = exchangeRateDAO.loadLast(account.getCurrency(), user.getBaseCurrency()).getExchangeRate();
                exchangeRates.put(account.getCurrency(), exchangeRate);
            }

            for (CIncomeExpenseTransactionItem item : transaction.getItems())
            {
                String categoryName = item.getCategory().getName();
                Double expense = item.getAmount();

                Double currentValue = categoryToExpense.get(categoryName);

                if (currentValue == null)
                {
                    currentValue = 0.0;
                }

                currentValue += expense * exchangeRate;
                categoryToExpense.put(categoryName, currentValue);
            }
        }

        for (String categoryName : categoryToExpense.keySet())
        {
            result.setValue(categoryName, categoryToExpense.get(categoryName));
        }

        return result;
    }

    @Override
    protected boolean generateReport()
    {
        if (!super.generateReport())
        {
            return false;
        }

        if (chartPanel != null)
        {
            chartPanelContainer.remove(chartPanel);
        }

        Date startDate = fldStartDate.getDate();
        Date endDate = fldEndDate.getDate();

        chartPanel = createChartPanel(startDate, endDate);
        chartPanelContainer.add(chartPanel, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
                GridBagConstraints.FIRST_LINE_START, GridBagConstraints.BOTH, new Insets(3, 3, 3, 3), 0, 0));

        Dimension oldSize = getSize();
        pack();
        setSize(oldSize);
        return true;
    }
}
