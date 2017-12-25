package wowjoy.fruits.ms.module.logs;

import wowjoy.fruits.ms.module.user.FruitUser;

/**
 * Created by wangziwen on 2017/11/23.
 */
public class FruitLogsDao extends FruitLogs {
    public FruitLogsDao() {
        setUuid(null);
    }

    private String msg;

    private FruitUser user;

    public FruitUser getUser() {
        return user;
    }

    public void setUser(FruitUser user) {
        this.user = user;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
