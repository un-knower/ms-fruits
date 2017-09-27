package wowjoy.fruits.ms.module.team;


import wowjoy.fruits.ms.module.AbstractEntity;

/**
 * Created by wangziwen on 2017/8/24.
 */
public class FruitTeam extends AbstractEntity {
    private String title;

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public static FruitTeam newEmpty(String msg) {
        return new FruitTeamEmpty(msg);
    }

    public static FruitTeamDao getFruitTeamDao() {
        return new FruitTeamDao();
    }

    public static FruitTeamVo getFruitTeamVo() {
        return new FruitTeamVo();
    }

}
