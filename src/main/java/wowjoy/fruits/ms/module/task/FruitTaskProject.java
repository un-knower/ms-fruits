package wowjoy.fruits.ms.module.task;

import wowjoy.fruits.ms.module.project.FruitProject;

/**
 * Created by wangziwen on 2018/3/19.
 */
public class FruitTaskProject extends FruitProject {
    private transient String taskId;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
}
