package wowjoy.fruits.ms.module.project;

import wowjoy.fruits.ms.module.team.FruitTeam;
import wowjoy.fruits.ms.module.team.FruitTeamUser;

import java.util.ArrayList;

/**
 * Created by ${汪梓文} on ${2018年03月20日15:45:02}.
 */
public class FruitProjectTeam extends FruitTeam {
    private String projectId;
    private String projectRole;

    private ArrayList<FruitTeamUser> users;

    public ArrayList<FruitTeamUser> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<FruitTeamUser> users) {
        this.users = users;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getProjectRole() {
        return projectRole;
    }

    public void setProjectRole(String projectRole) {
        this.projectRole = projectRole;
    }
}
