package com.moneyManagement.gui.table.model;

import com.moneyManagement.data.CAccount;
import com.moneyManagement.data.CUser;
import com.moneyManagement.data.EAccountType;
import com.moneyManagement.gui.table.cell.CTableItem;

import java.util.Arrays;
import java.util.List;

import static com.moneyManagement.gui.table.cell.CTableItem.EDataState;

public class CAccountsTableModel extends CTableModel<CAccount>
{
    public static final String SHARED_ACCOUNT_COLUMN = "SHARED_ACCOUNT_COLUMN";
    public static final String PARTICIPATE_IN_PLANNING_COLUMN = "PARTICIPATE_IN_PLANNING_COLUMN";
    public static final String ACCOUNT_TYPE_COLUMN = "ACCOUNT_TYPE_COLUMN";
    public static final String ACCOUNT_NAME_COLUMN = "ACCOUNT_NAME_COLUMN";
    public static final String ACCOUNT_BALANCE_COLUMN = "ACCOUNT_BALANCE_COLUMN";
    public static final String ACCOUNT_CURRENCY_COLUMN = "ACCOUNT_CURRENCY_COLUMN";

    public CAccountsTableModel(List<CAccount> alreadyExistedAccounts, CUser user)
    {
        super(alreadyExistedAccounts, user, Arrays.asList(STATUS_COLUMN,
                                                          SHARED_ACCOUNT_COLUMN,
                                                          PARTICIPATE_IN_PLANNING_COLUMN,
                                                          ACCOUNT_TYPE_COLUMN,
                                                          ACCOUNT_NAME_COLUMN,
                                                          ACCOUNT_BALANCE_COLUMN,
                                                          ACCOUNT_CURRENCY_COLUMN,
                                                          EDIT_COLUMN,
                                                          ADD_REMOVE_COLUMN));
    }

    @Override
    protected Object getValueAt(int rowIdx, String columnName)
    {
        if (rowIdx >= rows.size()){
            return super.getValueAt(rowIdx, columnName);
        }

        CAccount account = rows.get(rowIdx).getValue();

        switch (columnName)
        {
            case SHARED_ACCOUNT_COLUMN:
            {
                return account.getUserId() == null;
            }
            case PARTICIPATE_IN_PLANNING_COLUMN:
            {
                return account.isIncludeInPlanning();
            }
            case ACCOUNT_TYPE_COLUMN:
            {
                return account.getType();
            }
            case ACCOUNT_NAME_COLUMN:
            {
                return account.getName();
            }
            case ACCOUNT_BALANCE_COLUMN:
            {
                return account.getBalance();
            }
            case ACCOUNT_CURRENCY_COLUMN:
            {
                return account.getCurrency();
            }
        }

        return super.getValueAt(rowIdx, columnName);
    }

    @Override
    protected String getColumnName(String columnName)
    {
        switch (columnName)
        {
            case SHARED_ACCOUNT_COLUMN:
            {
                return "Shared account";
            }
            case PARTICIPATE_IN_PLANNING_COLUMN:
            {
                return "Part-t in planning";
            }
            case ACCOUNT_TYPE_COLUMN:
            {
                return "Account Type";
            }
            case ACCOUNT_NAME_COLUMN:
            {
                return "Account name";
            }
            case ACCOUNT_BALANCE_COLUMN:
            {
                return "Balance";
            }
            case ACCOUNT_CURRENCY_COLUMN:
            {
                return "Currency";
            }
        }

        return super.getColumnName(columnName);
    }

    @Override
    public void setValueAt(Object aValue, int rowIdx, String columnName)
    {
        CTableItem<CAccount> row = rows.get(rowIdx);
        CAccount.CBuilder builder = new CAccount.CBuilder(row.getValue());

        switch (columnName)
        {
            case SHARED_ACCOUNT_COLUMN:
            {
                builder.setUserId(Boolean.TRUE.equals(aValue) ? null : user.getId());
                break;
            }
            case PARTICIPATE_IN_PLANNING_COLUMN:
            {
                builder.setIncludeInPlanning((Boolean) aValue);
                break;
            }
            case ACCOUNT_TYPE_COLUMN:
            {
                builder.setType((EAccountType) aValue);
                break;
            }
            case ACCOUNT_NAME_COLUMN:
            {
                if (aValue instanceof String && ((String) aValue).isEmpty())
                {
                    return;
                }

                builder.setName((String) aValue);
                break;
            }
            case ACCOUNT_BALANCE_COLUMN:
            {
                if (aValue instanceof Double && ((Double) aValue) < 0)
                {
                    return;
                }

                builder.setBalance((Double) aValue);
                break;
            }
            case ACCOUNT_CURRENCY_COLUMN:
            {
                builder.setCurrency((String) aValue);
                break;
            }
        }

        rows.set(rowIdx, new CTableItem<>(row.getDataState(), builder.build()));

        super.setValueAt(aValue, rowIdx, columnName);
    }

    @Override
    protected boolean isCellEditable(int row, String column)
    {

        switch (column){
            case SHARED_ACCOUNT_COLUMN:
            case PARTICIPATE_IN_PLANNING_COLUMN:
            case ACCOUNT_NAME_COLUMN:{
                return row < rows.size() &&
                       (EDataState.NEW.equals(rows.get(row).getDataState()) ||
                       EDataState.UPDATED.equals(rows.get(row).getDataState()));
            }
            case ACCOUNT_BALANCE_COLUMN:
            case ACCOUNT_CURRENCY_COLUMN:
            case ACCOUNT_TYPE_COLUMN:{
                return row < rows.size() &&
                        EDataState.NEW.equals(rows.get(row).getDataState());
            }
        }

        return super.isCellEditable(row, column);
    }

    @Override
    public Class<?> getColumnClass(String column)
    {
        switch (column)
        {
            case SHARED_ACCOUNT_COLUMN:
            case PARTICIPATE_IN_PLANNING_COLUMN:{
                return Boolean.class;
            }
            case ACCOUNT_TYPE_COLUMN:
            {
                return EAccountType.class;
            }
            case ACCOUNT_NAME_COLUMN:
            {
                return String.class;
            }
            case ACCOUNT_BALANCE_COLUMN:
            {
                return Double.class;
            }
            case ACCOUNT_CURRENCY_COLUMN:
            {
                return EDataState.class;
            }
        }
        return super.getColumnClass(column);
    }

    public void addRow()
    {
        CAccount newAccount = new CAccount.CBuilder()
            .setName("new account")
            .setType(EAccountType.BANK_CARD)
            .setUserId(user.getId())
            .setBalance(0)
            .setCurrency(user.getBaseCurrency())
            .setIncludeInPlanning(false)
            .build();

        addRow(true, newAccount);
    }
}
