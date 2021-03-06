package wowjoy.fruits.ms.module.project;


import wowjoy.fruits.ms.module.AbstractEntity;
import wowjoy.fruits.ms.module.mark.UserMarkProject;
import wowjoy.fruits.ms.module.relation.entity.ProjectTeamRelation;
import wowjoy.fruits.ms.module.relation.entity.UserProjectRelation;
import wowjoy.fruits.ms.module.util.entity.FruitDict;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by wangziwen on 2017/8/24.
 */
public class FruitProject extends AbstractEntity {
    private String title;
    private Date predictStartDate;
    private Date predictEndDate;
    private Date endDate;
    private String projectStatus;
    private String statusDescription;

    public String getStatusDescription() {
        return statusDescription;
    }

    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
    }

    public void setPredictEndDate(Date predictEndDate) {
        this.predictEndDate = predictEndDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void setEndDate(LocalDateTime endDateTime) {
        this.endDate = Date.from(endDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getProjectStatus() {
        return projectStatus;
    }

    public void setProjectStatus(String projectStatus) {
        this.projectStatus = projectStatus;
    }

    public Date getPredictEndDate() {
        return predictEndDate;
    }

    public String getTitle() {
        return title;
    }

    public Date getPredictStartDate() {
        return predictStartDate;
    }

    public void setPredictStartDate(Date predictStartDate) {
        this.predictStartDate = predictStartDate;
    }

    /***********************
     * 静态工厂函数-创建实例  *
     ***********************/
    public static FruitProject getInstance() {
        return new FruitProject();
    }

    public static FruitProjectDao getDao() {
        return new FruitProjectDao();
    }

    public static FruitProjectVo getVo() {
        return new FruitProjectVo();
    }

    public static FruitProject getEmpty() {
        return new FruitProjectEmpty();
    }

    public static class Update extends FruitProject {
        public Update() {
            setUuid(null);
            setIsDeleted(null);
        }

        private Map<FruitDict.Systems, List<ProjectTeamRelation>> teamRelation;
        private Map<FruitDict.Systems, List<UserProjectRelation>> userRelation;

        public Map<FruitDict.Systems, List<ProjectTeamRelation>> getTeamRelation() {
            return teamRelation;
        }

        public void setTeamRelation(Map<FruitDict.Systems, List<ProjectTeamRelation>> teamRelation) {
            this.teamRelation = teamRelation;
        }

        public Map<FruitDict.Systems, List<UserProjectRelation>> getUserRelation() {
            return userRelation;
        }

        public void setUserRelation(Map<FruitDict.Systems, List<UserProjectRelation>> userRelation) {
            this.userRelation = userRelation;
        }
    }

    public static class Info extends FruitProject {

        public Info() {
            setUuid(null);
            setIsDeleted(null);
        }

        private List<FruitProjectUser> users;
        private List<FruitProjectTeam> teams;
        private FruitProjectTeam principalTeam;
        private FruitProjectUser principalUser;
        /*星标项目*/
        private UserMarkProject markProject;
        private boolean mark;

        /**
         * 查找负责人
         */
        public Info seekPrincipalUser() {
            if (this.getUsers().isEmpty()) return this;
            for (FruitProjectUser user : this.getUsers()) {
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
        public Info seekPrincipalTeam() {
            if (this.getTeams().isEmpty()) return this;
            for (FruitProjectTeam teamDao : this.getTeams()) {
                if (FruitDict.ProjectTeamDict.PRINCIPAL.name().equals(teamDao.getProjectRole())) {
                    this.setPrincipalTeam(teamDao);
                    this.getTeams().remove(teamDao);
                    return this;
                }
            }
            return this;
        }

        public UserMarkProject getMarkProject() {
            return markProject;
        }

        public void setMarkProject(UserMarkProject markProject) {
            this.markProject = markProject;
            this.setMark(true);
        }

        public boolean isMark() {
            return mark;
        }

        public void setMark(boolean mark) {
            this.mark = mark;
        }

        public List<FruitProjectUser> getUsers() {
            return users;
        }

        public void setUsers(List<FruitProjectUser> users) {
            this.users = users;
        }

        public List<FruitProjectTeam> getTeams() {
            return teams;
        }

        public void setTeams(List<FruitProjectTeam> teams) {
            this.teams = teams;
        }

        public FruitProjectTeam getPrincipalTeam() {
            return principalTeam;
        }

        public void setPrincipalTeam(FruitProjectTeam principalTeam) {
            this.principalTeam = principalTeam;
        }

        public FruitProjectUser getPrincipalUser() {
            return principalUser;
        }

        public void setPrincipalUser(FruitProjectUser principalUser) {
            this.principalUser = principalUser;
        }
    }

}
