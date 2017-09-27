package wowjoy.fruits.ms.module.team;

import wowjoy.fruits.ms.module.user.FruitUser;

import java.util.List;

/**
 * Created by wangziwen on 2017/9/14.
 */
public class FruitTeamDao extends FruitTeam {
    protected FruitTeamDao() {
    }

    private List<FruitUser> users;

    public List<FruitUser> getUsers() {
        return users;
    }

    public void setUsers(List<FruitUser> users) {
        this.users = users;
    }
}
