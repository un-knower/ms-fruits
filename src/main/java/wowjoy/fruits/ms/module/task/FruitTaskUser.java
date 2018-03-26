package wowjoy.fruits.ms.module.task;

import wowjoy.fruits.ms.module.user.FruitUser;

/**
 * Created by ${汪梓文} on ${2018年03月20日15:45:02}.
 */
public class FruitTaskUser extends FruitUser.Info {
    private String taskId;
    private String userRole;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }
}
