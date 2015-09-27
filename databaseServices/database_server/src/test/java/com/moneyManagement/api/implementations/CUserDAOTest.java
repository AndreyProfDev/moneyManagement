package com.moneyManagement.api.implementations;

import com.moneyManagement.data.*;
import org.junit.*;
import org.springframework.beans.factory.annotation.*;

import java.util.*;

import static junit.framework.Assert.*;

public class CUserDAOTest extends CAbstractDAOTest
{
    @Autowired
    private CUserDAO userDAO;

    private CUser user1;
    private CUser user2;

    @BeforeClass
    public static void beforeClass(){
        System.setProperty("app.env", "test");
    }

    @Before
    public void before(){
        user1 = userDAO.create(new CUser.CBuilder()
                                .setLogin(UUID.randomUUID().toString())
                                .setPassword("Password1^")
                                .setBaseCurrency("EUR")
                                .build());

        user2 = userDAO.create(new CUser.CBuilder()
                                .setLogin(UUID.randomUUID().toString())
                                .setPassword("Password2^")
                                .setBaseCurrency("EUR")
                                .build());
    }

    @Test
    public void test()
    {
        /**
         * Check that users are loaded correctly
         */
        CUser user3 = userDAO.loadById(user1.getId());
        assertTrue(user3.equals(user1));
        assertEquals(user3.getId(), user1.getId());

        CUser user4 = userDAO.loadByLogin(user2.getLogin());
        assertTrue(user4.equals(user2));
        assertEquals(user4.getId(), user2.getId());

        CUser user5 = new CUser.CBuilder(user4)
                .setLogin(UUID.randomUUID().toString())
                .build();

        userDAO.update(user5);

        CUser user6 = userDAO.loadById(user2.getId());
        assertEquals(user5, user6);
        assertEquals(user2.getId(), user6.getId());

        List<CUser> users = userDAO.loadAll();
        assertEquals(users.size(), 2);
        assertTrue(users.contains(user1));
        assertTrue(users.contains(user6));

        users = userDAO.loadAll(Arrays.asList(user1));
        assertEquals(users.size(), 1);
        assertTrue(users.get(0).equals(user6));
        assertEquals(users.get(0).getId(), user6.getId());
    }

    @After
    public void after(){
        userDAO.remove(user1);
        userDAO.remove(user2);
    }
}
