package wowjoy.fruits.ms.module.user;

/**
 * Created by wangziwen on 2017/9/14.
 */
public class FruitUserDao extends FruitUser {
    protected FruitUserDao() {
    }

    private String projectRole;

    private String planRole;

    public String getPlanRole() {
        return planRole;
    }

    public void setPlanRole(String planRole) {
        this.planRole = planRole;
    }

    public String getProjectRole() {
        return projectRole;
    }

    public void setProjectRole(String projectRole) {
        this.projectRole = projectRole;
    }
}
