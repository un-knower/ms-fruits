package wowjoy.fruits.ms.module.logs.transfer;

import wowjoy.fruits.ms.module.user.FruitUser;

/**
 * Created by wangziwen on 2018/3/12.
 */
public class FruitTransferUser extends FruitUser {
    private String status;
    private String transferId;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTransferId() {
        return transferId;
    }

    public void setTransferId(String transferId) {
        this.transferId = transferId;
    }
}
