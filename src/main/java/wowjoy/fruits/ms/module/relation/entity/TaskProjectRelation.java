package wowjoy.fruits.ms.module.relation.entity;

import wowjoy.fruits.ms.module.AbstractEntity;

public class TaskProjectRelation extends AbstractEntity {
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

    public static TaskProjectRelation getInstance() {
        return new TaskProjectRelation();
    }

    public static TaskProjectRelation newInstance(String taskId, String projectId) {
        TaskProjectRelation result = new TaskProjectRelation();
        result.setTaskId(taskId);
        result.setProjectId(projectId);
        return result;
    }
}