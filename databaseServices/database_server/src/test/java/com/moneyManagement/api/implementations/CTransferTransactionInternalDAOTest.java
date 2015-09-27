package com.moneyManagement.api.implementations;

import com.moneyManagement.api.implementations.*;
import com.moneyManagement.data.*;
import org.junit.*;
import org.springframework.beans.factory.annotation.*;

import java.util.*;

import static org.junit.Assert.*;

public class CTransferTransactionInternalDAOTest extends CAbstractDAOTest
{
    @Autowired
    private CUserDAO userDAO;

    @Autowired
    private CAccountDAO accountDAO;

    @Autowired
    private CTransferTransactionDAO transferTransactionDAO;

    private Date today;
    private Date future;

    private CTransferTransaction tr;
    private CTransferTransaction tr1;
    private CUser user;
    private CUser user1;
    private CAccount fromAcc;
    private CAccount fromAcc1;
    private CAccount toAcc;
    private CAccount toAcc1;

    @Before
    public void before()
    {
        today = new Date();

        Calendar calendar = new GregorianCalendar();
        calendar.setTime(today);
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        future = calendar.getTime();

        user = userDAO.create(new CUser.CBuilder()
                .setLogin(UUID.randomUUID().toString())
                .setPassword("Password1^")
                .setBaseCurrency("DOL")
                .build());

        user1 = userDAO.create(new CUser.CBuilder()
                .setLogin(UUID.randomUUID().toString())
                .setPassword("Password1^")
                .setBaseCurrency("DOL")
                .build());

        fromAcc = accountDAO.create(new CAccount.CBuilder()
                .setBalance(1000.0)
                .setCurrency("DOL")
                .setIncludeInPlanning(true)
                .setName(UUID.randomUUID().toString())
                .setType(EAccountType.BANK_CARD)
                .setUserId(user.getId())
                .build());

        fromAcc1 = accountDAO.create(new CAccount.CBuilder()
                .setBalance(1000.0)
                .setCurrency("DOL")
                .setIncludeInPlanning(true)
                .setName(UUID.randomUUID().toString())
                .setType(EAccountType.BANK_CARD)
                .setUserId(user.getId())
                .build());

        toAcc = accountDAO.create(new CAccount.CBuilder()
                .setBalance(10.0)
                .setCurrency("DOL")
                .setIncludeInPlanning(true)
                .setName(UUID.randomUUID().toString())
                .setType(EAccountType.CASH)
                .setUserId(user.getId())
                .build());

        toAcc1 = accountDAO.create(new CAccount.CBuilder()
                .setBalance(10.0)
                .setCurrency("DOL")
                .setIncludeInPlanning(true)
                .setName(UUID.randomUUID().toString())
                .setType(EAccountType.CASH)
                .setUserId(user.getId())
                .build());

        tr = transferTransactionDAO.create(new CTransferTransaction.CBuilder()
                .setTrDate(today)
                .setFromAccountId(fromAcc.getId())
                .setToAccountId(toAcc.getId())
                .setTransferAmount(100.0)
                .setTransferReason("Reason")
                .setToUserId(user.getId())
                .setFromUserId(user.getId())
                .build());

        tr1 = new CTransferTransaction.CBuilder()
                .setTrDate(future)
                .setFromAccountId(fromAcc1.getId())
                .setToAccountId(toAcc1.getId())
                .setTransferAmount(110.0)
                .setTransferReason("Reason")
                .setToUserId(user.getId())
                .setFromUserId(user.getId())
                .build();
        transferTransactionDAO.create(tr1);
    }

    @Test
    public void test()
    {
        assertEquals(accountDAO.loadAccount(fromAcc.getId()).getBalance(), 900.0, 0.1);
        assertEquals(accountDAO.loadAccount(toAcc.getId()).getBalance(), 110.0, 0.1);

        assertEquals(accountDAO.loadAccount(fromAcc1.getId()).getBalance(), 890.0, 0.1);
        assertEquals(accountDAO.loadAccount(toAcc1.getId()).getBalance(), 120.0, 0.1);

        /**
         * Check that loading works
         */
        CTransferTransaction tr2 = transferTransactionDAO.loadById(tr.getId());
        assertEquals(tr, tr2);
        assertEquals(tr.getId(), tr2.getId());

        List<CTransferTransaction> trs = transferTransactionDAO.loadAll(user, today, today);
        assertEquals(1, trs.size());
        assertEquals(tr, trs.get(0));
        assertEquals(tr.getId(), trs.get(0).getId());

        trs = transferTransactionDAO.loadAll(user, future, future);
        assertEquals(1, trs.size());
        assertEquals(tr1, trs.get(0));
        assertEquals(tr1.getId(), trs.get(0).getId());

        trs = transferTransactionDAO.loadAllFrom(user, fromAcc, today, today);
        assertEquals(1, trs.size());
        assertEquals(tr, trs.get(0));
        assertEquals(tr.getId(), trs.get(0).getId());

        trs = transferTransactionDAO.loadAllFrom(user, fromAcc1, future, future);
        assertEquals(1, trs.size());
        assertEquals(tr1, trs.get(0));
        assertEquals(tr1.getId(), trs.get(0).getId());

        trs = transferTransactionDAO.loadAllTo(user, toAcc, future, future);
        assertEquals(0, trs.size());

        trs = transferTransactionDAO.loadAllTo(user, toAcc, today, today);
        assertEquals(1, trs.size());
        assertEquals(tr, trs.get(0));
        assertEquals(tr.getId(), trs.get(0).getId());

        assertEquals(1, transferTransactionDAO.getTransactionsCount(fromAcc));
        assertEquals(1, transferTransactionDAO.getTransactionsCount(fromAcc1));
        assertEquals(1, transferTransactionDAO.getTransactionsCount(toAcc));
        assertEquals(1, transferTransactionDAO.getTransactionsCount(toAcc1));

        /**
         * Check update works
         */
        tr = new CTransferTransaction.CBuilder(tr)
                .setTrDate(future)
                .setFromAccountId(fromAcc1.getId())
                .setToAccountId(toAcc1.getId())
                .setTransferAmount(800.0)
                .setTransferReason("Reason1")
                .setToUserId(user1.getId())
                .setFromUserId(user1.getId())
                .build();
        transferTransactionDAO.update(tr);

        tr2 = transferTransactionDAO.loadById(tr.getId());
        assertEquals(future, tr2.getTrDate());
        assertEquals(tr.getId(), tr2.getId());
        assertEquals(user.getId(), tr2.getToUserId());
        assertEquals(user.getId(), tr2.getFromUserId());
        assertEquals(toAcc1.getId(), tr2.getToAccountId());
        assertEquals(fromAcc1.getId(), tr2.getFromAccountId());
        assertEquals(800.0, tr2.getTransferAmount(), 0.1);
        assertEquals("Reason1", tr2.getTransferReason());

        assertEquals(accountDAO.loadAccount(fromAcc.getId()).getBalance(), 1000.0, 0.1);
        assertEquals(accountDAO.loadAccount(toAcc.getId()).getBalance(), 10.0, 0.1);

        assertEquals(accountDAO.loadAccount(fromAcc1.getId()).getBalance(), 90.0, 0.1);
        assertEquals(accountDAO.loadAccount(toAcc1.getId()).getBalance(), 920.0, 0.1);

        tr = new CTransferTransaction.CBuilder(tr)
                .setTrDate(today)
                .setFromAccountId(fromAcc.getId())
                .setToAccountId(toAcc.getId())
                .setTransferAmount(810.0)
                .setTransferReason("Reason2")
                .setToUserId(user.getId())
                .setFromUserId(user.getId())
                .build();

        transferTransactionDAO.update(tr);

        tr2 = transferTransactionDAO.loadById(tr.getId());
        assertEquals(today, tr2.getTrDate());
        assertEquals(tr.getId(), tr2.getId());
        assertEquals(user.getId(), tr2.getToUserId());
        assertEquals(user.getId(), tr2.getFromUserId());
        assertEquals(toAcc.getId(), tr2.getToAccountId());
        assertEquals(fromAcc.getId(), tr2.getFromAccountId());
        assertEquals(810.0, tr2.getTransferAmount(), 0.1);
        assertEquals("Reason2", tr2.getTransferReason());

        assertEquals(accountDAO.loadAccount(fromAcc.getId()).getBalance(), 190.0, 0.1);
        assertEquals(accountDAO.loadAccount(toAcc.getId()).getBalance(), 820.0, 0.1);

        assertEquals(accountDAO.loadAccount(fromAcc1.getId()).getBalance(), 890.0, 0.1);
        assertEquals(accountDAO.loadAccount(toAcc1.getId()).getBalance(), 120.0, 0.1);

        /**
         * Check that remove works
         */
        transferTransactionDAO.remove(tr);
        assertEquals(0, transferTransactionDAO.getTransactionsCount(fromAcc));
        assertEquals(0, transferTransactionDAO.getTransactionsCount(toAcc));

        assertEquals(accountDAO.loadAccount(fromAcc.getId()).getBalance(), 1000.0, 0.1);
        assertEquals(accountDAO.loadAccount(toAcc.getId()).getBalance(), 10.0, 0.1);

        assertEquals(accountDAO.loadAccount(fromAcc1.getId()).getBalance(), 890.0, 0.1);
        assertEquals(accountDAO.loadAccount(toAcc1.getId()).getBalance(), 120.0, 0.1);
    }

    @After
    public void after(){
        transferTransactionDAO.remove(tr1);
//        transferTransactionDAO.remove(tr);
        accountDAO.remove(fromAcc);
        accountDAO.remove(fromAcc1);
        accountDAO.remove(toAcc);
        accountDAO.remove(toAcc1);
        userDAO.remove(user);
        userDAO.remove(user1);

    }
}
