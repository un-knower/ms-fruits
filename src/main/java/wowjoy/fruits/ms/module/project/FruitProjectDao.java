package wowjoy.fruits.ms.module.project;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import wowjoy.fruits.ms.module.relation.entity.ProjectListRelation;
import wowjoy.fruits.ms.module.relation.entity.ProjectTeamRelation;
import wowjoy.fruits.ms.module.relation.entity.UserProjectRelation;
import wowjoy.fruits.ms.module.team.FruitTeamDao;
import wowjoy.fruits.ms.module.user.FruitUserDao;
import wowjoy.fruits.ms.module.util.entity.FruitDict;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Dao层实体类
 */
public class FruitProjectDao extends FruitProject {

    protected FruitProjectDao() {
        setUuid(null);
    }

    private Map<FruitDict.Systems, List<ProjectTeamRelation>> teamRelation;
    private Map<FruitDict.Systems, List<UserProjectRelation>> userRelation;
    private Map<FruitDict.Systems, List<ProjectListRelation>> listRelation;

    private List<FruitUserDao> users;
    private List<FruitTeamDao> teams;
    private FruitTeamDao principalTeam;
    private FruitUserDao principalUser;

    private Integer days;

    public FruitTeamDao getPrincipalTeam() {
        return principalTeam;
    }

    public void setPrincipalTeam(FruitTeamDao principalTeam) {
        this.principalTeam = principalTeam;
    }

    public List<FruitTeamDao> getTeams() {
        return teams;
    }

    public void setTeams(List<FruitTeamDao> teams) {
        this.teams = teams;
    }

    public FruitUserDao getPrincipalUser() {
        return principalUser;
    }

    public void setPrincipalUser(FruitUserDao principalUser) {
        this.principalUser = principalUser;
    }

    public List<ProjectTeamRelation> getTeamRelation(FruitDict.Systems type) {
        return teamRelation != null && teamRelation.containsKey(type) ? teamRelation.get(type) : Lists.newLinkedList();
    }

    public List<UserProjectRelation> getUserRelation(FruitDict.Systems type) {
        return userRelation != null && userRelation.containsKey(type) ? userRelation.get(type) : Lists.newLinkedList();
    }

    public List<ProjectListRelation> getListRelation(FruitDict.Systems type) {
        return listRelation != null && listRelation.containsKey(type) ? listRelation.get(type) : Lists.newLinkedList();
    }

    public void setTeamRelation(Map<FruitDict.Systems, List<ProjectTeamRelation>> teamRelation) {
        this.teamRelation = teamRelation;
    }

    public void setTeamRelation(FruitDict.Systems parents, List<ProjectTeamRelation> value) {
        if (teamRelation == null)
            teamRelation = Maps.newLinkedHashMap();
        teamRelation.put(parents, value);
    }

    public void setUserRelation(Map<FruitDict.Systems, List<UserProjectRelation>> userRelation) {
        this.userRelation = userRelation;
    }

    public void setUserRelation(FruitDict.Systems parents, List<UserProjectRelation> value) {
        if (userRelation == null)
            userRelation = Maps.newLinkedHashMap();
        userRelation.put(parents, value);
    }

    public void setListRelation(Map<FruitDict.Systems, List<ProjectListRelation>> listRelation) {
        this.listRelation = listRelation;
    }

    public void setListRelation(FruitDict.Systems parents, List<ProjectListRelation> value) {
        if (listRelation == null)
            listRelation = Maps.newLinkedHashMap();
        listRelation.put(parents, value);
    }

    public Integer getDays() {
        return days;
    }

    public void setDays(Integer days) {
        this.days = days;
    }

    public List<FruitUserDao> getUsers() {
        return users;
    }

    public void setUsers(List<FruitUserDao> users) {
        this.users = users;
    }

    @Override
    public void setTitle(String title) {
        if (StringUtils.isNotBlank(title))
            super.setTitle(title);
    }

    /***************
     * 项目工具函数  *
     ***************/

    /**
     * 是否延期
     * 延期天数
     * 2018年03月08日15:03:09：暂无需求
     */
    public FruitProjectDao computeDays() {
        Date startDate;
        Date endDate;
        if (this.getPredictEndDate() != null) {
            if (this.getEndDate() != null && FruitDict.ProjectDict.COMPLETE.equals(this.getProjectStatus())) {
                startDate = this.getPredictEndDate();
                endDate = this.getEndDate();
            } else {
                startDate = this.getPredictEndDate();
                endDate = new Date();
            }
        } else {
            this.setDays(999999999);
            return this;
        }
        LocalDateTime predictEndTime = LocalDateTime.parse(new SimpleDateFormat(DateTimeFormat).format(startDate));
        LocalDateTime currentTime = LocalDateTime.parse(new SimpleDateFormat(DateTimeFormat).format(endDate));
        Duration between = Duration.between(currentTime, predictEndTime);
        this.setDays((int) between.toDays());
        return this;
    }

    /**
     * 查找负责人
     */
    public FruitProjectDao seekPrincipalUser() {
        if (this.getUsers().isEmpty()) return this;
        for (FruitUserDao user : this.getUsers()) {
            if (FruitDict.UserProjectDict.PRINCIPAL.name().equals(user.getProjectRole())) {
                this.setPrincipalUser(user);
                this.getUsers().remove(user);
                return this;
            }
        }
        return this;
    }

    /**
     * 查找负责团队
     */
    public FruitProjectDao seekPrincipalTeam() {
        if (this.getTeams().isEmpty()) return this;
        for (FruitTeamDao teamDao : this.getTeams()) {
            if (FruitDict.ProjectTeamDict.PRINCIPAL.name().equals(teamDao.getProjectRole())) {
                this.setPrincipalTeam(teamDao);
                this.getTeams().remove(teamDao);
                return this;
            }
        }
        return this;
    }

}
