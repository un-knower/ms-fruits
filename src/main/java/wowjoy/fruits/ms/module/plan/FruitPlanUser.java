package wowjoy.fruits.ms.module.plan;

import wowjoy.fruits.ms.module.user.FruitUser;

/**
 * Created by ${汪梓文} on ${2018年03月20日15:45:02}.
 */
public class FruitPlanUser extends FruitUser.Info {
    private transient String planId;
    private String planRole;

    public String getPlanRole() {
        return planRole;
    }

    public void setPlanRole(String planRole) {
        this.planRole = planRole;
    }

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }
}
