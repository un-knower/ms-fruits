package wowjoy.fruits.ms.module.relation.entity;

import wowjoy.fruits.ms.exception.CheckException;
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
        try {
            this.userRole = FruitDict.TaskUserDict.valueOf(userRole).name();
        } catch (Exception e) {
            throw new CheckException("用户角色不存在");
        }
    }

    public void setUserRole(FruitDict.TaskUserDict userDict) {
        this.setUserRole(userDict.name());
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
        result.setUserRole(userRole);
        return result;
    }

    public static TaskUserRelation getInstance() {
        return new TaskUserRelation();
    }

}