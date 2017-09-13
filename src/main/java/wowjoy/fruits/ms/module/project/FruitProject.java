package wowjoy.fruits.ms.module.project;


import wowjoy.fruits.ms.module.AbstractEntity;
import wowjoy.fruits.ms.module.team.FruitTeam;
import wowjoy.fruits.ms.module.user.FruitUser;

import java.util.Date;
import java.util.List;

/**
 * Created by wangziwen on 2017/8/24.
 */
public class FruitProject extends AbstractEntity {
    private String title;
    private Date predictEndDate;
    private Date endDateTime;
    private String projectStatus;
    private List<FruitUser> users;
    private List<FruitTeam> teams;

    public List<FruitTeam> getTeams() {
        return teams;
    }

    public void setTeams(List<FruitTeam> teams) {
        this.teams = teams;
    }

    public List<FruitUser> getUsers() {
        return users;
    }

    public void setUsers(List<FruitUser> users) {
        this.users = users;
    }

    public void setPredictEndDate(Date predictEndDate) {
        this.predictEndDate = predictEndDate;
    }

    public void setEndDateTime(Date endDateTime) {
        this.endDateTime = endDateTime;
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

    public Date getEndDateTime() {
        return endDateTime;
    }

    public String getTitle() {
        return title;
    }

    public static FruitProject getInstance() {
        return new FruitProject();
    }

    public static FruitProject newEmpty(String msg) {
        return new FruitProjectEmpty(msg);
    }
}
