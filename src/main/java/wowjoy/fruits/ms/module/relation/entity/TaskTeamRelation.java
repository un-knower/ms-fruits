package wowjoy.fruits.ms.module.relation.entity;


import wowjoy.fruits.ms.module.AbstractEntity;

/**
 * Created by wangziwen on 2017/8/29.
 * 任务-团队关联
 */
public class TaskTeamRelation extends AbstractEntity {
    public TaskTeamRelation() {
    }

    public TaskTeamRelation(String taskId, String teamId) {
        this.taskId = taskId;
        this.teamId = teamId;
    }

    private String taskId;
    private String teamId;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }
}
