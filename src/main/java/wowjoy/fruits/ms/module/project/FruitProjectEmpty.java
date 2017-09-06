package wowjoy.fruits.ms.module.project;

/**
 * Created by wangziwen on 2017/9/6.
 */
public class FruitProjectEmpty extends FruitProject {
    public FruitProjectEmpty() {
        this(null);
    }

    public FruitProjectEmpty(String msg) {
        this.msg = msg;
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

    public static FruitProjectEmpty getInstance(String msg) {
        return new FruitProjectEmpty(msg);
    }
}
