package wowjoy.fruits.ms.module.task;

import com.github.pagehelper.PageInfo;
import wowjoy.fruits.ms.module.list.FruitList;

/**
 * Created by ${汪梓文} on ${2018年03月20日15:45:02}.
 */
public class FruitTaskList extends FruitList {
    public FruitTaskList() {
        setUuid(null);
        setIsDeleted(null);
    }

    private String taskId;

    private PageInfo<FruitTask.Info> tasks;

    public PageInfo<FruitTask.Info> getTasks() {
        return tasks;
    }

    public void setTasks(PageInfo<? extends FruitTask.Info> tasks) {
        this.tasks = (PageInfo<FruitTask.Info>) tasks;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
}
