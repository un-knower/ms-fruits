package wowjoy.fruits.ms.module.relation.entity;

import wowjoy.fruits.ms.module.AbstractEntity;

public class TransferUserRelation extends AbstractEntity {
    private String transferId;

    private String userId;

    private String status;

    public static class Update extends TransferUserRelation {
        public Update() {
            setUuid(null);
        }
    }

    public String getTransferId() {
        return transferId;
    }

    public TransferUserRelation setTransferId(String transferId) {
        this.transferId = transferId;
        return this;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public TransferUserRelation setStatus(String status) {
        this.status = status;
        return this;
    }

    public static TransferUserRelation newInstanceSetUserId(String userId) {
        TransferUserRelation transferUserRelation = new TransferUserRelation();
        transferUserRelation.setUserId(userId);
        return transferUserRelation;
    }

}