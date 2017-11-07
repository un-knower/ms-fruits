package wowjoy.fruits.ms.module.team;

import com.google.common.collect.Lists;
import wowjoy.fruits.ms.module.relation.entity.UserTeamRelation;
import wowjoy.fruits.ms.module.user.FruitUser;
import wowjoy.fruits.ms.module.util.entity.FruitDict;

import java.util.List;
import java.util.Map;

/**
 * Created by wangziwen on 2017/9/14.
 */
public class FruitTeamDao extends FruitTeam {
    protected FruitTeamDao() {
    }

    private List<FruitUser> users;

    private Map<FruitDict.Dict,List<UserTeamRelation>> inUsers;

    public List<UserTeamRelation> getInUsers(FruitDict.Dict dict) {
        return inUsers!=null && inUsers.containsKey(dict) ? inUsers.get(dict): Lists.newLinkedList();
    }

    public void setInUsers(Map<FruitDict.Dict, List<UserTeamRelation>> inUsers) {
        this.inUsers = inUsers;
    }

    public List<FruitUser> getUsers() {
        return users;
    }

    public void setUsers(List<FruitUser> users) {
        this.users = users;
    }
}
