package wowjoy.fruits.ms.module.relation.entity;

import wowjoy.fruits.ms.module.AbstractEntity;

public class PlanProjectRelation extends AbstractEntity {

    private String projectId;

    private String planId;

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public static PlanProjectRelation newInstance(String planId) {
        return newInstance(planId, null);
    }

    public static PlanProjectRelation newInstance(String planId, String projectId) {
        PlanProjectRelation result = new PlanProjectRelation();
        result.setPlanId(planId);
        result.setProjectId(projectId);
        return result;
    }
}