package com.moneyManagement.api.interfaces;

import com.moneyManagement.data.*;

import java.util.*;

public interface IPlanItemDAO
{
    CPlanItem create(final CPlanItem planToCreate);

    void update(CPlanItem plan);

    void remove(CPlanItem plan);

    List<CPlanItem> loadPlans(CUser user);

    List<CPlanItem> loadPlans(CUser user, Date startDate, Date endDate);

    List<CPlanItemResult> loadPlanResults(CUser user, Date startDate, Date endDate);

    List<CPlanItem> loadAll();
}
