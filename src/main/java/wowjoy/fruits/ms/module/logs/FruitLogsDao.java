package wowjoy.fruits.ms.module.logs;

/**
 * Created by wangziwen on 2017/11/23.
 */
public class FruitLogsDao extends FruitLogs {
    public FruitLogsDao() {
        setUuid(null);
    }

    private String msg;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
