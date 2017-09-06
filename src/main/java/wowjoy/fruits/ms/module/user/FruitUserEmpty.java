package wowjoy.fruits.ms.module.user;

/**
 * Created by wangziwen on 2017/9/6.
 */
public class FruitUserEmpty extends FruitUser {

    public FruitUserEmpty(String msg) {
        this.setMsg(msg);
    }

    private String msg;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public boolean isNotEmpty() {
        return false;
    }

    public static FruitUserEmpty getInstance(String msg) {
        return new FruitUserEmpty(msg);
    }
}
