package com.moneyManagement.data;

import com.moneyManagement.exceptions.*;
import com.moneyManagement.utils.*;

public class CPlanItemResult extends CPlanItem
{
    public static class CBuilder
    {
        private CPlanItem source;
        private Double planResult;

        public CBuilder setPlanResult(Double planResult)
        {
            this.planResult = planResult;
            return this;
        }

        public CBuilder setSource(CPlanItem source)
        {
            this.source = source;
            return this;
        }

        public CPlanItemResult build()
        {
            if (source == null || planResult == null)
            {
                throw new RuntimeException("Some fields of plan item result initialized incorrectly:\n"
                        + "(source = "+ source + ",\n"
                        + "planResult = "+ planResult + ")"
                );
            }

            return new CPlanItemResult(source.getId(), source, planResult);
        }
    }

    private double planResult;

    private CPlanItemResult(Integer id, CPlanItem source, double planResult)
    {
        super(id, source.getUserId(), source.getDate(), source.getAmount());
        this.planResult = planResult;
    }

    public double getPlanResult()
    {
        return planResult;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }

        if (!(obj instanceof CPlanItemResult))
        {
            return false;
        }

        CPlanItemResult other = (CPlanItemResult) obj;

        return super.equals(other) &&
               planResult == other.planResult;
    }

    @Override
    public int hashCode()
    {
        return CDataUtils.calculateHashCode(getUserId(), getDate(), getAmount(), planResult);
    }
}
