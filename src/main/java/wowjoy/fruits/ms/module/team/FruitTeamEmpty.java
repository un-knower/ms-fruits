package wowjoy.fruits.ms.module.team;

/**
 * Created by wangziwen on 2017/9/13.
 */
public class FruitTeamEmpty extends FruitTeam {
    protected FruitTeamEmpty() {
        this(null);
    }

    protected FruitTeamEmpty(String msg) {
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
