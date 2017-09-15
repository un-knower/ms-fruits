package wowjoy.fruits.ms.module.project;

import org.apache.commons.lang3.StringUtils;
import wowjoy.fruits.ms.module.relation.entity.ProjectTeamRelation;
import wowjoy.fruits.ms.module.relation.entity.UserProjectRelation;
import wowjoy.fruits.ms.module.util.entity.FruitDict;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

/**
 * Dao层实体类
 */
public class FruitProjectDao extends FruitProject {

    private List<ProjectTeamRelation> teamDao;
    private List<UserProjectRelation> userDao;

    protected FruitProjectDao() {
        setUuid(null);
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

    public List<ProjectTeamRelation> getTeamDao() {
        return teamDao;
    }

    public void setTeamDao(List<ProjectTeamRelation> teamDao) {
        this.teamDao = teamDao;
    }

    public List<UserProjectRelation> getUserDao() {
        return userDao;
    }

    public void setUserDao(List<UserProjectRelation> userDao) {
        this.userDao = userDao;
    }
}
