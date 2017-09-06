package wowjoy.fruits.ms.dao.task;

import wowjoy.fruits.ms.dao.InterfaceDao;
import wowjoy.fruits.ms.module.task.FruitTask;

import java.util.List;

/**
 * Created by wangziwen on 2017/8/30.
 */
public abstract class AbstractDaoTask implements InterfaceDao {
    public abstract void insert();

    public abstract List<FruitTask> findByUser();

    private FruitTask task;
    private String userId;

    public FruitTask getTask() {
        return task;
    }

    public AbstractDaoTask setTask(FruitTask task) {
        this.task = task;
        return this;
    }

    public AbstractDaoTask setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public String getUserId() {
        return userId;
    }

}
