package wowjoy.fruits.ms.dao.task;

import wowjoy.fruits.ms.dao.InterfaceDao;
import wowjoy.fruits.ms.dao.relation.RelationDirector;
import wowjoy.fruits.ms.dao.relation.TaskUserRelationBuilder;
import wowjoy.fruits.ms.module.task.FruitTask;
import wowjoy.fruits.ms.module.task.relation.TaskUserRelation;
import wowjoy.fruits.ms.module.util.entity.FruitDict;

import java.util.List;

/**
 * Created by wangziwen on 2017/8/30.
 */
public abstract class AbstractDaoTask implements InterfaceDao {
    public abstract void insert(FruitTask task);

    public abstract List<FruitTask> findByUser();

    private FruitTask task;
    private TaskUserRelation userRelation;

    public FruitTask getTask() {
        return task;
    }

    public AbstractDaoTask setTask(FruitTask task) {
        this.task = task;
        return this;
    }

    public TaskUserRelation getUserRelation() {
        return userRelation == null ? (userRelation = TaskUserRelation.getInstance(this.getTask().getUuid())) : userRelation;
    }

    /**
     * 创建任务必须有创建用户
     */
    public void createTask() {
        this.insert(this.getTask());
        /*创建者关联*/
        this.createTaskUserRelation(FruitDict.TaskUserDict.PRINCIPAL);
    }

    /**
     * 创建用户关联信息
     */
    private void createTaskUserRelation(FruitDict.TaskUserDict dict) {
        final TaskUserRelationBuilder builder = new TaskUserRelationBuilder();
        this.getUserRelation().setUserRole(dict.name());
        builder.setAbstractEntity(this.getUserRelation());
        RelationDirector.getInstance(builder).insert();
    }

}
