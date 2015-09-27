package com.moneyManagement.api.implementations;

import com.moneyManagement.api.interfaces.*;

public class CPing implements IPing {

    @Override
    public boolean ping(){
        return true;
    }
}
