package wowjoy.fruits.ms.module.team;

import com.google.common.collect.Lists;
import wowjoy.fruits.ms.module.relation.entity.UserTeamRelation;
import wowjoy.fruits.ms.module.user.FruitUserDao;
import wowjoy.fruits.ms.module.util.entity.FruitDict;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by wangziwen on 2017/9/14.
 */
public class FruitTeamDao extends FruitTeam {
    protected FruitTeamDao() {
        this.setUuid(null);
    }

    /**
     * 筛选出领导
     */
    private FruitUserDao leader;

    /**
     * 关联用户集合
     */
    private List<FruitUserDao> users;

    /**
     * 增删关联用户
     */
    private Map<FruitDict.Systems, List<UserTeamRelation>> userRelation;

    private String projectRole;

    /**
     * 检索团队leader
     *
     * @return
     */
    public boolean searchLeader() {
        if (Objects.isNull(users) && users.isEmpty())
            return false;
        for (FruitUserDao user : this.getUsers()) {
            if (FruitDict.UserTeamDict.LEADER.name().equals(user.getTeamRole())) {
                this.getUsers().remove(user);
                leader = user;
                return true;
            }
        }
        return false;
    }

    public String getProjectRole() {
        return projectRole;
    }

    public void setProjectRole(String projectRole) {
        this.projectRole = projectRole;
    }

    public FruitUserDao getLeader() {
        return leader;
    }

    public void setLeader(FruitUserDao leader) {
        this.leader = leader;
    }

    public List<UserTeamRelation> getUserRelation(FruitDict.Systems parents) {
        return userRelation != null && userRelation.containsKey(parents) ? userRelation.get(parents) : Lists.newLinkedList();
    }

    public void setUserRelation(Map<FruitDict.Systems, List<UserTeamRelation>> userRelation) {
        this.userRelation = userRelation;
    }

    public List<FruitUserDao> getUsers() {
        return users;
    }

    public void setUsers(List<FruitUserDao> users) {
        this.users = users;
    }
}
