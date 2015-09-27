package com.moneyManagement.data;

import com.moneyManagement.utils.*;

import java.util.*;

public class CTransferTransaction extends CTransaction
{
    public static class CBuilder
    {
        private Integer id;
        private Date trDate;
        private Integer fromUserId;
        private Integer fromAccountId;
        private Integer toUserId;
        private Integer toAccountId;
        private Double transferAmount;
        private String transferReason;

        public CBuilder(){
        }

        public CBuilder(CTransferTransaction prototype){
            this.id = prototype.getId();
            this.trDate = prototype.getTrDate();
            this.fromUserId = prototype.getFromUserId();
            this.fromAccountId = prototype.getFromAccountId();
            this.toUserId = prototype.getToUserId();
            this.toAccountId = prototype.getToAccountId();
            this.transferAmount = prototype.getTransferAmount();
            this.transferReason = prototype.getTransferReason();
        }

        public CBuilder setTrDate(Date trDate)
        {
            this.trDate = trDate;
            return this;
        }

        public CBuilder setFromUserId(Integer fromUserId)
        {
            this.fromUserId = fromUserId;
            return this;
        }

        public CBuilder setFromAccountId(int fromAccountId)
        {
            this.fromAccountId = fromAccountId;
            return this;
        }

        public CBuilder setTransferAmount(double transferAmount)
        {
            this.transferAmount = transferAmount;
            return this;
        }

        public CBuilder setTransferReason(String transferReason)
        {
            this.transferReason = transferReason;
            return this;
        }

        public CBuilder setToUserId(int toUserId)
        {
            this.toUserId = toUserId;
            return this;
        }

        public CBuilder setToAccountId(Integer toAccountId) {
            this.toAccountId = toAccountId;
            return this;
        }

        public CBuilder setId(Integer id) {
            this.id = id;
            return this;
        }

        public CTransferTransaction build()
        {
            if (trDate == null || fromUserId == null || fromAccountId == null ||
                transferAmount == null || transferReason == null || toUserId == null)
            {
                throw new RuntimeException("Some fields of transfer transaction external initialized incorrectly:\n"
                        + "(trDate = "+ trDate + ",\n"
                        + "fromUserId = "+ fromUserId + ",\n"
                        + "fromAccountId = "+ fromAccountId + ",\n"
                        + "transferAmount = "+ transferAmount + ",\n"
                        + "transferReason = "+ transferReason + ",\n"
                        + "toUserId = "+ toUserId + ",\n"
                        + "toAccountId = "+ toAccountId + ")"
                );
            }

            return new CTransferTransaction(id,
                    trDate, fromUserId, fromAccountId,
                    transferAmount, transferReason, toUserId, toAccountId);
        }
    }

    private Integer id;
    private final Date trDate;
    private final Integer fromUserId;
    private final Integer fromAccountId;
    private final Integer toUserId;
    private final Integer toAccountId;
    private final Double transferAmount;
    private final String transferReason;

    private CTransferTransaction(Integer id, Date trDate, Integer fromUserId,
                                         Integer fromAccountId, Double transferAmount, String transferReason,
                                         Integer toUserId, Integer toAccountId)
    {
        super(id, trDate, fromUserId);

        this.id = id;
        this.trDate = trDate;
        this.fromUserId = fromUserId;
        this.fromAccountId = fromAccountId;
        this.transferAmount = transferAmount;
        this.transferReason = transferReason;
        this.toUserId = toUserId;
        this.toAccountId = toAccountId;
    }

    public Integer getToUserId()
    {
        return toUserId;
    }

    public Date getTrDate() {
        return trDate;
    }

    public Integer getFromUserId() {
        return fromUserId;
    }

    public Integer getFromAccountId() {
        return fromAccountId;
    }

    public Integer getToAccountId() {
        return toAccountId;
    }

    public Double getTransferAmount() {
        return transferAmount;
    }

    public String getTransferReason() {
        return transferReason;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String getTransactionName() {
        return "Transfer transaction";
    }

    public Integer getId() {
        return id;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }

        if (!(obj instanceof CTransferTransaction))
        {
            return false;
        }

        CTransferTransaction other = (CTransferTransaction) obj;

        return CDataUtils.equalsNullSafe(id, other.id) &&
                CDataUtils.equalsNullSafe(trDate, other.trDate) &&
                CDataUtils.equalsNullSafe(fromUserId, other.fromUserId) &&
                CDataUtils.equalsNullSafe(fromAccountId, other.fromAccountId) &&
                CDataUtils.equalsNullSafe(toUserId, other.toUserId) &&
                CDataUtils.equalsNullSafe(toAccountId, other.toAccountId) &&
                CDataUtils.equalsNullSafe(transferAmount, other.transferAmount) &&
                CDataUtils.equalsNullSafe(transferReason, other.transferReason);
    }

    @Override
    public int hashCode() {
        return CDataUtils.calculateHashCode(id, trDate, fromUserId, fromAccountId, toUserId, toAccountId, transferAmount, transferReason);
    }
}
