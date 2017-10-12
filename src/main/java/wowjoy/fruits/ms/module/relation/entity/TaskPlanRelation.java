package wowjoy.fruits.ms.module.relation.entity;

import wowjoy.fruits.ms.module.AbstractEntity;

public class TaskPlanRelation extends AbstractEntity {
    private String taskId;

    private String planId;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public static TaskPlanRelation getInstance() {
        return new TaskPlanRelation();
    }

    public static TaskPlanRelation newInstance(String taskId, String planId) {
        TaskPlanRelation result = new TaskPlanRelation();
        result.setTaskId(taskId);
        result.setPlanId(planId);
        return result;
    }
}