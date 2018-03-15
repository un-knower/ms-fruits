package wowjoy.fruits.ms.module.team;

import com.google.common.collect.Lists;
import wowjoy.fruits.ms.module.relation.entity.UserTeamRelation;
import wowjoy.fruits.ms.module.user.FruitUserDao;
import wowjoy.fruits.ms.module.util.entity.FruitDict;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    private FruitTeamUser leader;

    /**
     * 关联用户集合
     */
    private List<FruitTeamUser> users;

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
    public void searchLeader() {
        this.setLeader(this.findUsers().orElseGet(LinkedList::new).parallelStream().filter(user -> FruitDict.UserTeamDict.LEADER.name().equals(user.getTeamRole())).findAny().orElse(null));
    }

    public String getProjectRole() {
        return projectRole;
    }

    public void setProjectRole(String projectRole) {
        this.projectRole = projectRole;
    }

    public FruitTeamUser getLeader() {
        return leader;
    }

    public void setLeader(FruitTeamUser leader) {
        this.leader = leader;
    }

    public List<UserTeamRelation> getUserRelation(FruitDict.Systems parents) {
        return userRelation != null && userRelation.containsKey(parents) ? userRelation.get(parents) : Lists.newLinkedList();
    }

    public void setUserRelation(Map<FruitDict.Systems, List<UserTeamRelation>> userRelation) {
        this.userRelation = userRelation;
    }

    public Optional<List<FruitTeamUser>> findUsers() {
        if (users != null)
            return Optional.of(users);
        return Optional.empty();
    }

    public void setUsers(List<FruitTeamUser> users) {
        this.users = users;
    }
}
