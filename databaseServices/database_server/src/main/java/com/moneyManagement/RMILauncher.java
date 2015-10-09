package com.moneyManagement;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class RMILauncher {
    public static void main(String[] args) throws Exception
    {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("spring/rmiApplicationContext.xml");
    }
}
