package wowjoy.fruits.ms.module.relation.entity;


import wowjoy.fruits.ms.module.AbstractEntity;

/**
 * Created by wangziwen on 2017/8/29.
 * 任务-计划关联
 */
public class TaskPlanRelation extends AbstractEntity {
    public TaskPlanRelation() {
    }

    public TaskPlanRelation(String taskId, String planId) {
        this.taskId = taskId;
        this.planId = planId;
    }

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
}
