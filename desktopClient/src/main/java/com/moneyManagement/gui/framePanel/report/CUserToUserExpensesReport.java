package com.moneyManagement.gui.framePanel.report;

import com.moneyManagement.api.interfaces.IAccountDAO;
import com.moneyManagement.api.interfaces.IExchangeRateDAO;
import com.moneyManagement.api.interfaces.IIncomeExpenseTransactionDAO;
import com.moneyManagement.api.interfaces.IUserDAO;
import com.moneyManagement.data.CAccount;
import com.moneyManagement.data.CIncomeExpenseTransaction;
import com.moneyManagement.data.CUser;
import com.moneyManagement.data.ECategoryType;
import com.moneyManagement.gui.utils.CPropertyHolder;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.labels.StandardXYItemLabelGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.time.SimpleTimePeriod;
import org.jfree.data.time.TimePeriodValues;
import org.jfree.data.time.TimePeriodValuesCollection;
import org.jfree.data.xy.IntervalXYDataset;
import org.springframework.beans.factory.annotation.Autowired;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CUserToUserExpensesReport extends CReportFrame
{
    private ChartPanel chartPanel;
    private JComboBox<CUser> cmbOtherUser;

    @Autowired
    private CPropertyHolder<CUser> mainUser;

    @Autowired
    private IUserDAO userDAO;

    @Autowired
    private IIncomeExpenseTransactionDAO incomeExpenseTransactionDAO;

    @Autowired
    private IAccountDAO accountDAO;

    @Autowired
    private IExchangeRateDAO exchangeRateDAO;

    @Override
    public void init()
    {
        super.init(mainUser.getValue(), "User to user expenses");
    }

    @Override
    protected void createComponents()
    {
        super.createComponents();

        List<CUser> users = new ArrayList<>();
        users.add(null);
        users.addAll(userDAO.loadAll(Arrays.asList(user)));
        cmbOtherUser = new JComboBox<>(users.toArray(new CUser[users.size()]));
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

        if (cmbOtherUser.getItemCount() > 1){
            parametersPanel.add(cmbOtherUser, new GridBagConstraints(2, 0, 1, 2, 0, 0,
                    GridBagConstraints.FIRST_LINE_END, GridBagConstraints.NONE, new Insets(3, 3, 3, 3), 0, 0));
        }

        parametersPanel.add(btnGenerate, new GridBagConstraints(3, 0, 1, 2, 0, 0,
                GridBagConstraints.FIRST_LINE_END, GridBagConstraints.NONE, new Insets(3, 3, 3, 3), 0, 0));

        centralPanel.add(chartPanelContainer, new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0,
                GridBagConstraints.FIRST_LINE_START, GridBagConstraints.BOTH, new Insets(3, 3, 3, 3), 0, 0));

        add(centralPanel, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0,
                GridBagConstraints.FIRST_LINE_START, GridBagConstraints.BOTH, new Insets(3, 3, 3, 3), 0, 0));
    }

    private ChartPanel createChartPanel(Date startDate, Date endDate)
    {
        IntervalXYDataset dataset = createDataset(startDate, endDate);
        JFreeChart result = ChartFactory.createXYBarChart("Expenses summary", "Time", true, "Expenses", dataset, PlotOrientation.VERTICAL, true, true, false);
        XYPlot plot = result.getXYPlot();
        XYBarRenderer renderer = (XYBarRenderer) plot.getRenderer();
        renderer.setShadowVisible(false);
        renderer.setBaseItemLabelGenerator(new StandardXYItemLabelGenerator());
        renderer.setBaseItemLabelsVisible(true);

        DateAxis axis = (DateAxis) plot.getDomainAxis();
        axis.setRange(startDate, endDate);

        return new ChartPanel(result);
    }

    private TimePeriodValuesCollection createDataset(Date startDate, Date endDate)
    {
        TimePeriodValuesCollection result = new TimePeriodValuesCollection();

        if (cmbOtherUser.getItemCount() == 1 || cmbOtherUser.getSelectedItem() == null)
        {
            result.addSeries(createSeriesForUser(user, startDate, endDate, 3.0/24, 21.0 / 24));
        }
        else
        {
            result.addSeries(createSeriesForUser(user, startDate, endDate, 3.0/24, 11.0 / 24));
            result.addSeries(createSeriesForUser((CUser) cmbOtherUser.getSelectedItem(), startDate, endDate, 13.0/24, 21.0 / 24));
        }

        return result;
    }

    private TimePeriodValues createSeriesForUser(CUser user, Date startDate, Date endDate, double dayStartPos, double dayEndPos)
    {
        TimePeriodValues series = new TimePeriodValues(user.getLogin());

        int start = (int) (24 * 60 * 60 * dayStartPos);
        int end = (int) (24 * 60 * 60 * dayEndPos);

        int secondsStart = start % 60;
        start /= 60;

        int minuteStart = start % 60;
        start /= 60;

        int hourStart = start;

        int secondEnd = end % 60;
        end /= 60;

        int minuteEnd = end % 60;
        end /= 60;

        int hourEnd = end;

        Map<Calendar, Double> dayToExpense = new HashMap<>();

        HashMap<String, Double> exchangeRates = new HashMap<>();
        exchangeRates.put(user.getBaseCurrency(), 1.0);

        HashMap<Integer, CAccount> accounts = new HashMap<>();

        for (CIncomeExpenseTransaction transaction : incomeExpenseTransactionDAO.loadAll(user, startDate, endDate, ECategoryType.EXPENSE))
        {
            Calendar trDate = new GregorianCalendar();
            trDate.setTimeInMillis(transaction.getDate().getTime());

            Double prevValue = dayToExpense.get(trDate);

            if (prevValue == null)
            {
                prevValue = 0.0;
            }

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

            prevValue += transaction.getSum() * exchangeRate;

            dayToExpense.put(trDate, prevValue);
        }


        for (Calendar trDate : dayToExpense.keySet())
        {
            Calendar date1 = new GregorianCalendar(trDate.get(Calendar.YEAR),
                    trDate.get(Calendar.MONTH),
                    trDate.get(Calendar.DAY_OF_MONTH),
                    hourStart, minuteStart, secondsStart);

            Calendar date2 = new GregorianCalendar(trDate.get(Calendar.YEAR),
                    trDate.get(Calendar.MONTH),
                    trDate.get(Calendar.DAY_OF_MONTH),
                    hourEnd, minuteEnd, secondEnd);

            series.add(new SimpleTimePeriod(date1.getTime(), date2.getTime()), dayToExpense.get(trDate));
        }

        return series;
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
