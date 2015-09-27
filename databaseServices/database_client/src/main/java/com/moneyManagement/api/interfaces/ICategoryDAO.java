package com.moneyManagement.api.interfaces;

import com.moneyManagement.data.*;

import java.util.*;

public interface ICategoryDAO
{
    CCategory create(final CCategory newCategory);

    void update(CCategory category);

    void remove(CCategory category);

    List<CCategory> loadCategories(ECategoryType catType, CUser user);

    List<CCategory> loadCategories(CUser user);

    CCategory loadById(final int categoryId);

    boolean checkCategoriesExists(CUser user);
}
