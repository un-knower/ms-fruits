package wowjoy.fruits.ms.module.relation.entity;

import wowjoy.fruits.ms.module.AbstractEntity;
import wowjoy.fruits.ms.module.util.entity.FruitDict;

public class TaskUserRelation extends AbstractEntity {

    private String taskId;

    private String userId;

    private String userRole;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public static TaskUserRelation newInstance(String taskId, String userId) {
        TaskUserRelation result = new TaskUserRelation();
        result.setTaskId(taskId);
        result.setUserId(userId);
        return result;
    }

    public static TaskUserRelation newInstance(String taskId, String userId, FruitDict.TaskUserDict userRole) {
        TaskUserRelation result = new TaskUserRelation();
        result.setTaskId(taskId);
        result.setUserId(userId);
        result.setUserRole(userRole.name());
        return result;
    }

}