package wowjoy.fruits.ms.module.relation.entity;

import wowjoy.fruits.ms.module.AbstractEntity;
import wowjoy.fruits.ms.module.util.entity.FruitDict;

public class PlanUserRelation extends AbstractEntity {
    private String userId;

    private String planId;

    private String puRole;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public String getPuRole() {
        return puRole;
    }

    public void setPuRole(String puRole) {
        this.puRole = puRole;
    }

    public static PlanUserRelation getInstance(String planId) {
        PlanUserRelation result = new PlanUserRelation();
        result.setPlanId(planId);
        return getInstance(planId, null, null);
    }

    public static PlanUserRelation getInstance(String planId, String userId, FruitDict.PlanUserDict role) {
        PlanUserRelation result = new PlanUserRelation();
        result.setPlanId(planId);
        result.setUserId(userId);
        result.setPuRole(role != null ? role.name() : null);
        return result;
    }

    public static PlanUserRelation getInstance() {
        return new PlanUserRelation();
    }
}