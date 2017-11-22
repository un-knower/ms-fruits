package wowjoy.fruits.ms.module.team;

import wowjoy.fruits.ms.module.relation.entity.UserTeamRelation;
import wowjoy.fruits.ms.module.util.entity.FruitDict;

import java.util.List;
import java.util.Map;

/**
 * Created by wangziwen on 2017/9/14.
 */
public class FruitTeamVo extends FruitTeam {
    protected FruitTeamVo() {
    }

    private String uuidVo;

    private String userName;

    public String getUserName() {
        return userName;
    }

    private Map<FruitDict.Systems, List<UserTeamRelation>> inUsers;

    public Map<FruitDict.Systems, List<UserTeamRelation>> getInUsers() {
        return inUsers;
    }

    public void setUsers(Map<FruitDict.Systems, List<UserTeamRelation>> users) {
        this.inUsers = users;
    }

    public String getUuidVo() {
        return uuidVo;
    }

    public void setUuidVo(String uuidVo) {
        this.uuidVo = uuidVo;
    }
}
