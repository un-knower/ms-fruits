package wowjoy.fruits.ms.module.task.relation;


import wowjoy.fruits.ms.dao.InterfaceDao;
import wowjoy.fruits.ms.module.AbstractEntity;

/**
 * Created by wangziwen on 2017/8/29.
 * 任务-用户关联表
 */
public class TaskUserRelation extends AbstractEntity {
    public TaskUserRelation() {
    }

    public TaskUserRelation(String taskId, String userId, String userRole) {
        this.taskId = taskId;
        this.userId = userId;
        this.userRole = userRole;
    }

    private String taskId;
    private String userId;
    private String userRole;

    public static TaskUserRelation getInstance(String taskId, String userId, String userRole) {
        return new TaskUserRelation(taskId, userId, userRole);
    }

    public static TaskUserRelation getInstance(String taskId) {
        return new TaskUserRelation(taskId, null, null);

    }

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
}
