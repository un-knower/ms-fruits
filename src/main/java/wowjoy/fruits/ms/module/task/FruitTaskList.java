package wowjoy.fruits.ms.module.task;

import wowjoy.fruits.ms.module.list.FruitList;

import java.util.ArrayList;

/**
 * Created by ${汪梓文} on ${2018年03月20日15:45:02}.
 */
public class FruitTaskList extends FruitList {
    public FruitTaskList() {
        setUuid(null);
        setIsDeleted(null);
    }

    private String taskId;

    private ArrayList<FruitTask.Info> tasks;

    public ArrayList<FruitTask.Info> getTasks() {
        return tasks;
    }

    public void setTasks(ArrayList<? extends FruitTask.Info> tasks) {
        this.tasks = (ArrayList<FruitTask.Info>) tasks;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
}
