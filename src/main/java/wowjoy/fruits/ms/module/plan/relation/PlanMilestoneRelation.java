package wowjoy.fruits.ms.module.plan.relation;


import wowjoy.fruits.ms.module.AbstractEntity;

/**
 * Created by wangziwen on 2017/8/29.
 * 计划-里程碑关联表
 */
public class PlanMilestoneRelation extends AbstractEntity {
    private String planId;
    private String milestoneId;

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public String getMilestoneId() {
        return milestoneId;
    }

    public void setMilestoneId(String milestoneId) {
        this.milestoneId = milestoneId;
    }
}
