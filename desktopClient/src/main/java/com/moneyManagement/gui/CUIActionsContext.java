package com.moneyManagement.gui;

import com.google.common.collect.Lists;
import com.moneyManagement.data.CIncomeExpenseTransaction;
import com.moneyManagement.data.CTransferTransaction;
import com.moneyManagement.data.CUser;
import com.moneyManagement.gui.framePanel.CFrameDisplayer;
import com.moneyManagement.gui.framePanel.profile.CAccountProfilePanel;
import com.moneyManagement.gui.framePanel.profile.CCategoryProfilePanel;
import com.moneyManagement.gui.framePanel.profile.CExchangeRateProfile;
import com.moneyManagement.gui.framePanel.profile.CExchangeRatesSummaryProfile;
import com.moneyManagement.gui.framePanel.profile.CExpenseTransactionProfile;
import com.moneyManagement.gui.framePanel.profile.CIncomeTransactionProfile;
import com.moneyManagement.gui.framePanel.profile.CPlanProfile;
import com.moneyManagement.gui.framePanel.profile.CSetupsPanel;
import com.moneyManagement.gui.framePanel.profile.CTransactionsSummaryProfile;
import com.moneyManagement.gui.framePanel.profile.CTransferTransactionProfile;
import com.moneyManagement.gui.framePanel.report.CCircularReport;
import com.moneyManagement.gui.framePanel.report.CPlanningReport;
import com.moneyManagement.gui.framePanel.report.CUserToUserExpensesReport;
import com.moneyManagement.gui.table.view.CSummaryTableView;
import com.moneyManagement.gui.utils.CPair;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;

import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Configuration
@ComponentScan(basePackages = "com.moneyManagement.gui")
public class CUIActionsContext {

    @Bean
    @Lazy
    public List<CPair<String, List<AbstractAction>>> menuActions(){
        List<CPair<String, List<AbstractAction>>> menuActions = new ArrayList<>();

        menuActions.add(new CPair<String, List<AbstractAction>>("Transactions", Lists.<AbstractAction>newArrayList(
                incomeTransactionAction(),
                expenseTransactionAction(),
                transferTransactionAction(),
                transactionsSummaryAction()
        )));

        menuActions.add(new CPair<String, List<AbstractAction>>("Staff", Lists.<AbstractAction>newArrayList(
                accountAction(),
                categoryAction()
        )));

        menuActions.add(new CPair<String, List<AbstractAction>>("Exchange rates", Lists.<AbstractAction>newArrayList(
                exchangeRateAction(),
                exchangeRatesSummaryAction()
        )));

        menuActions.add(new CPair<String, List<AbstractAction>>("Reports", Lists.<AbstractAction>newArrayList(
                userToUserExpensesAction(),
                circularReportAction()
        )));

        menuActions.add(new CPair<String, List<AbstractAction>>("Planning", Lists.<AbstractAction>newArrayList(
                planAction(),
                planReportAction()
        )));

        menuActions.add(new CPair<String, List<AbstractAction>>("Setups", Lists.<AbstractAction>newArrayList(
                setupsAction()
        )));

        return menuActions;
    }

    @Bean
    @Lazy
    public AbstractAction accountAction(){

        return new AbstractAction("Create Account") {

            @Override
            public void actionPerformed(ActionEvent e) {
                frameDisplayer().display(accountProfile(), false, Collections.emptyList());
            }
        };
    }

    @Bean
    @Lazy
    @Scope(value = "prototype")
    public CAccountProfilePanel accountProfile(){
        return new CAccountProfilePanel();
    }

    @Bean
    @Lazy
    public AbstractAction categoryAction(){
        return new AbstractAction("Create Category") {
            @Override
            public void actionPerformed(ActionEvent e) {
                frameDisplayer().display(categoryProfile(), false, Collections.emptyList());
            }
        };
    }

    @Bean
    @Lazy
    @Scope(value = "prototype")
    public CCategoryProfilePanel categoryProfile(){
        return new CCategoryProfilePanel();
    }

    @Bean
    @Lazy
    public AbstractAction circularReportAction(){
        return new AbstractAction("Circular expenses diagram") {
            @Override
            public void actionPerformed(ActionEvent e) {
                frameDisplayer().display(circularReport(), false, Collections.emptyList());
            }
        };
    }

    @Bean
    @Lazy
    @Scope(value = "prototype")
    public CCircularReport circularReport(){
        return new CCircularReport();
    }

    @Bean
    @Lazy
    public AbstractAction exchangeRateAction(){
        return new AbstractAction("Create Exchange rate") {
            @Override
            public void actionPerformed(ActionEvent e) {
                frameDisplayer().display(exchangeRateProfile(), false, Collections.emptyList());
            }
        };
    }

    @Bean
    @Lazy
    @Scope(value = "prototype")
    public CExchangeRateProfile exchangeRateProfile(){
        return new CExchangeRateProfile();
    }

    @Bean
    @Lazy
    public AbstractAction exchangeRatesSummaryAction(){
        return new AbstractAction("Exchange rates summary") {
            @Override
            public void actionPerformed(ActionEvent e) {
                frameDisplayer().display(exchangeRateSummaryProfile(), false, Collections.emptyList());
            }
        };
    }

    @Bean
    @Lazy
    @Scope(value = "prototype")
    public CExchangeRatesSummaryProfile exchangeRateSummaryProfile(){
        return new CExchangeRatesSummaryProfile();
    }

    @Bean
    @Lazy
    public AbstractAction expenseTransactionAction(){
        return new AbstractAction("Create Expense Transaction") {
            @Override
            public void actionPerformed(ActionEvent e) {
                frameDisplayer().display(expenseTransactionProfile(), false, Collections.emptyList());
            }
        };
    }

    @Bean
    @Lazy
    @Scope(value = "prototype")
    public CExpenseTransactionProfile expenseTransactionProfile(){
        return new CExpenseTransactionProfile();
    }

    @Bean
    @Lazy
    public AbstractAction incomeTransactionAction(){
        return new AbstractAction("Create Income Transaction") {
            @Override
            public void actionPerformed(ActionEvent e) {
                frameDisplayer().display(incomeTransactionProfile(), false, Collections.emptyList());
            }
        };
    }

    @Bean
    @Lazy
    @Scope(value = "prototype")
    public CIncomeTransactionProfile incomeTransactionProfile(){
        return new CIncomeTransactionProfile();
    }

    @Bean
    @Lazy
    public AbstractAction planAction(){
        return new AbstractAction("Plans") {
            @Override
            public void actionPerformed(ActionEvent e) {
                frameDisplayer().display(planProfile(), false, Collections.emptyList());
            }
        };
    }

    @Bean
    @Lazy
    @Scope(value = "prototype")
    public CPlanProfile planProfile(){
        return new CPlanProfile();
    }

    @Bean
    @Lazy
    public AbstractAction planReportAction(){
        return new AbstractAction("Planning report") {
            @Override
            public void actionPerformed(ActionEvent e) {
                frameDisplayer().display(planningReport(), false, Collections.emptyList());
            }
        };
    }

    @Bean
    @Lazy
    @Scope(value = "prototype")
    public CPlanningReport planningReport(){
        return new CPlanningReport();
    }

    @Bean
    @Lazy
    public AbstractAction setupsAction(){
        return new AbstractAction("Setups") {
            @Override
            public void actionPerformed(ActionEvent e) {
                frameDisplayer().display(setupsProfile(), false, Collections.emptyList());
            }
        };
    }

    @Bean
    @Lazy
    @Scope(value = "prototype")
    public CSetupsPanel setupsProfile(){
        return new CSetupsPanel();
    }

    @Bean
    @Lazy
    public AbstractAction transactionsSummaryAction(){
        return new AbstractAction("Transactions summary") {
            @Override
            public void actionPerformed(ActionEvent e) {
                frameDisplayer().display(transactionsSummaryProfile(), false, Collections.emptyList());
            }
        };
    }

    @Bean
    @Lazy
    @Scope(value = "prototype")
    public CTransactionsSummaryProfile transactionsSummaryProfile(){
        return new CTransactionsSummaryProfile() {
            @Override
            public CSummaryTableView getTransactionsSummaryTable(CUser user) {
                return new CSummaryTableView(user) {
                    @Override
                    public void displayIncomeTransactionProfile(CIncomeExpenseTransaction transaction) {
                        CIncomeTransactionProfile profile = incomeTransactionProfile();
                        profile.setTransaction(transaction);
                        frameDisplayer().display(profile, false, Collections.emptyList());
                    }

                    @Override
                    public void displayExpenseTransactionProfile(CIncomeExpenseTransaction transaction) {
                        CExpenseTransactionProfile profile = expenseTransactionProfile();
                        profile.setTransaction(transaction);
                        frameDisplayer().display(profile, false, Collections.emptyList());
                    }

                    @Override
                    public void displayTransferTransactionProfile(CTransferTransaction transaction) {
                        CTransferTransactionProfile profile = transferTransactionProfile();
                        profile.setTransaction(transaction);
                        frameDisplayer().display(profile, false, Collections.emptyList());
                    }
                };
            }
        };
    }

    @Bean
    @Lazy
    public AbstractAction transferTransactionAction(){
        return new AbstractAction("Create Transfer Transaction") {
            @Override
            public void actionPerformed(ActionEvent e) {
                frameDisplayer().display(transferTransactionProfile(), false, Collections.emptyList());
            }
        };
    }

    @Bean
    @Lazy
    @Scope(value = "prototype")
    public CTransferTransactionProfile transferTransactionProfile(){
        return new CTransferTransactionProfile();
    }

    @Bean
    @Lazy
    public AbstractAction userToUserExpensesAction(){
        return new AbstractAction("User to user expenses") {
            @Override
            public void actionPerformed(ActionEvent e) {
                frameDisplayer().display(userToUserExpensesReport(), false, Collections.emptyList());
            }
        };
    }

    @Bean
    @Lazy
    @Scope(value = "prototype")
    public CUserToUserExpensesReport userToUserExpensesReport(){
        return new CUserToUserExpensesReport();
    }

    @Bean
    @Lazy
    public CFrameDisplayer frameDisplayer(){
        return new CFrameDisplayer();
    }
}
