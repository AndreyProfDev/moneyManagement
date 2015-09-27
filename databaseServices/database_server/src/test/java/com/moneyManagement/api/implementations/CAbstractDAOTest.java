package com.moneyManagement.api.implementations;

import org.junit.*;
import org.junit.runner.*;
import org.springframework.test.context.*;
import org.springframework.test.context.junit4.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/spring/dao.xml"})
public abstract class CAbstractDAOTest {

    @BeforeClass
    public static void beforeClass(){
        System.setProperty("app.env", "test");
    }
}
