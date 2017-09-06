package wowjoy.fruits.ms.module.team;


import wowjoy.fruits.ms.module.AbstractEntity;
import wowjoy.fruits.ms.module.user.FruitUser;

import java.util.List;

/**
 * Created by wangziwen on 2017/8/24.
 */
public class FruitTeam extends AbstractEntity {
    private String title;

    private List<FruitUser> users;

    public List<FruitUser> getUsers() {
        return users;
    }

    public void setUsers(List<FruitUser> users) {
        this.users = users;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

}
