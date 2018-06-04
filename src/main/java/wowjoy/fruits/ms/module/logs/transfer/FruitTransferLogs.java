package wowjoy.fruits.ms.module.logs.transfer;

import wowjoy.fruits.ms.module.AbstractEntity;
import wowjoy.fruits.ms.module.EntityUtils;
import wowjoy.fruits.ms.module.relation.entity.TransferUserRelation;
import wowjoy.fruits.ms.module.util.entity.FruitDict.TransferDict;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

public class FruitTransferLogs extends AbstractEntity {
    public FruitTransferLogs() {
        super.setUuid(null);
    }

    private String reason;
    private Map<TransferDict, ArrayList<FruitTransferUser>> transferUser;

    public void setTransferUser(Map<TransferDict, ArrayList<FruitTransferUser>> transferUser) {
        this.transferUser = transferUser;
    }

    public Optional<Map<TransferDict, ArrayList<FruitTransferUser>>> getTransferUser() {
        return Optional.ofNullable(transferUser);
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public static Insert newInsert() {
        return new Insert();
    }

    /*添加操作*/
    public static class Insert extends FruitTransferLogs implements EntityUtils {
        public Insert() {
            super.setUuid(obtainUUID());
        }

        private Map<TransferDict, ArrayList<TransferUserRelation>> transferUserRelation;

        public Optional<ArrayList<TransferUserRelation>> getTransferUserRelation(TransferDict transfer) {
            return transferUserRelation != null && transferUserRelation.containsKey(transfer) ? Optional.of(transferUserRelation.get(transfer)) : Optional.empty();
        }

        public void setTransferUserRelation(Map<TransferDict, ArrayList<TransferUserRelation>> userRelation) {
            this.transferUserRelation = userRelation;
        }
    }
}