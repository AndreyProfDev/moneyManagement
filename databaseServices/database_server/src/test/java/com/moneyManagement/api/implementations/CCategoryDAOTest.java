package com.moneyManagement.api.implementations;

import com.moneyManagement.api.implementations.*;
import com.moneyManagement.data.*;
import org.junit.*;
import org.springframework.beans.factory.annotation.*;

import java.util.*;

import static junit.framework.Assert.*;

public class CCategoryDAOTest extends CAbstractDAOTest
{
    @Autowired
    private CUserDAO userDAO;

    @Autowired
    private CCategoryDAO categoryDAO;

    private CUser user;
    private CUser user1;
    private CCategory category1;
    private CCategory category2;

    @Before
    public void before(){

        user = userDAO.create(new CUser.CBuilder()
                                .setLogin(UUID.randomUUID().toString())
                                .setPassword("Password1^")
                                .setBaseCurrency("EUR")
                                .build());

        user1 = userDAO.create(new CUser.CBuilder()
                                .setLogin(UUID.randomUUID().toString())
                                .setPassword("Password2^")
                                .setBaseCurrency("USD")
                                .build());

        category1 = categoryDAO.create(new CCategory.CBuilder()
                                    .setName(UUID.randomUUID().toString())
                                    .setType(ECategoryType.INCOME)
                                    .setUserId(user.getId())
                                    .build());

        category2 = categoryDAO.create(new CCategory.CBuilder()
                                    .setName(UUID.randomUUID().toString())
                                    .setType(ECategoryType.EXPENSE)
                                    .setUserId(user.getId())
                                    .build());
    }

    @Test
    public void test()
    {
        /**
         * Check, that all categories are loaded correctly
         */
        CCategory category3 = categoryDAO.loadById(category1.getId());
        assertEquals(category1, category3);
        assertEquals(category1.getId(), category3.getId());

        List<CCategory> categories = categoryDAO.loadCategories(ECategoryType.INCOME, user1);
        assertTrue(categories.isEmpty());

        categories = categoryDAO.loadCategories(ECategoryType.EXPENSE, user1);
        assertTrue(categories.isEmpty());

        categories = categoryDAO.loadCategories(ECategoryType.INCOME, user);
        assertEquals(1, categories.size());
        assertEquals(category1, categories.get(0));
        assertEquals(category1.getId(), categories.get(0).getId());

        categories = categoryDAO.loadCategories(ECategoryType.EXPENSE, user);
        assertEquals(1, categories.size());
        assertEquals(category2, categories.get(0));
        assertEquals(category2.getId(), categories.get(0).getId());

        categories = categoryDAO.loadCategories(user);
        assertEquals(2, categories.size());
        assertTrue(categories.contains(category1));
        assertTrue(categories.contains(category2));

        assertTrue(categoryDAO.checkCategoriesExists(user));

        /**
         * Check, that category update works
         */
        category3 = new CCategory.CBuilder(category1)
                .setName(UUID.randomUUID().toString())
                .setType(ECategoryType.EXPENSE)
                .setUserId(user1.getId())
                .build();
        categoryDAO.update(category3);

        CCategory category4 = categoryDAO.loadById(category1.getId());
        assertEquals(category3.getName(), category4.getName());
        assertEquals(category1.getType(), category4.getType());
        assertEquals(category1.getUserId(), category4.getUserId());

        category3 = new CCategory.CBuilder(category4)
                .setName(UUID.randomUUID().toString())
                .build();
        categoryDAO.update(category3);

        category4 = categoryDAO.loadById(category1.getId());
        assertEquals(category3, category4);

        /**
         * Check, that category remove works
         */
        categoryDAO.remove(category4);
        categoryDAO.remove(category2);
        assertFalse(categoryDAO.checkCategoriesExists(user));
    }

    @After
    public void after(){
        userDAO.remove(user);
        userDAO.remove(user1);
        categoryDAO.remove(category1);
        categoryDAO.remove(category2);
    }
}
