package wowjoy.fruits.ms.module.project;

import wowjoy.fruits.ms.module.user.FruitUser;

/**
 * Created by wangziwen on 2018/3/14.
 */
public class FruitProjectUser extends FruitUser {
    private transient String projectId;
    private String projectRole;

    public String getProjectRole() {
        return projectRole;
    }

    public void setProjectRole(String projectRole) {
        this.projectRole = projectRole;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }
}
