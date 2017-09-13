package wowjoy.fruits.ms.module.plan;

/**
 * Created by wangziwen on 2017/9/13.
 */
public class FruitPlanEmpty extends FruitPlan{
    protected FruitPlanEmpty() {
        this(null);
    }

    protected FruitPlanEmpty(String msg) {
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
