package wowjoy.fruits.ms.module.project;

import org.apache.commons.lang3.StringUtils;
import wowjoy.fruits.ms.module.relation.entity.ProjectTeamRelation;
import wowjoy.fruits.ms.module.relation.entity.UserProjectRelation;
import wowjoy.fruits.ms.module.user.FruitUser;
import wowjoy.fruits.ms.module.user.FruitUserDao;
import wowjoy.fruits.ms.module.util.entity.FruitDict;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

/**
 * Dao层实体类
 */
public class FruitProjectDao extends FruitProject {

    protected FruitProjectDao() {
        setUuid(null);
    }

    private List<ProjectTeamRelation> teamRelation;
    private List<UserProjectRelation> userRelation;

    private List<FruitUserDao> users;
    private FruitUserDao principal;

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

    @Override
    public void setProjectStatus(String projectStatus) {
        super.setProjectStatus(projectStatus);
        if (FruitDict.ProjectDict.COMPLETE.equals(projectStatus))
            this.setEndDateTime(java.sql.Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
    }

    public FruitUserDao getPrincipal() {
        return principal;
    }

    public void setPrincipal(FruitUserDao principal) {
        this.principal = principal;
    }

    public List<ProjectTeamRelation> getTeamRelation() {
        return teamRelation;
    }

    public void setTeamRelation(List<ProjectTeamRelation> teamRelation) {
        this.teamRelation = teamRelation;
    }

    public List<UserProjectRelation> getUserRelation() {
        return userRelation;
    }

    public void setUserRelation(List<UserProjectRelation> userRelation) {
        this.userRelation = userRelation;
    }
}
