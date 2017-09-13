package wowjoy.fruits.ms.module.project;

/**
 * Created by wangziwen on 2017/9/6.
 */
public class FruitProjectEmpty extends FruitProject {
    protected FruitProjectEmpty() {
        this(null);
    }

    protected FruitProjectEmpty(String msg) {
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

}
