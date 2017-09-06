package wowjoy.fruits.ms.module.relation.entity;


import wowjoy.fruits.ms.module.AbstractEntity;

/**
 * Created by wangziwen on 2017/8/29.
 * 任务-项目关联
 */
public class TaskProjectRelation extends AbstractEntity {
    public TaskProjectRelation() {
    }

    public TaskProjectRelation(String taskId, String projectId) {
        this.taskId = taskId;
        this.projectId = projectId;
    }

    private String taskId;
    private String projectId;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }
}
