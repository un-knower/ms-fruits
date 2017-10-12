package wowjoy.fruits.ms.module.task;

import com.google.common.collect.Lists;
import wowjoy.fruits.ms.module.relation.entity.TaskPlanRelation;
import wowjoy.fruits.ms.module.relation.entity.TaskProjectRelation;
import wowjoy.fruits.ms.module.relation.entity.TaskUserRelation;
import wowjoy.fruits.ms.module.util.entity.FruitDict;

import java.util.List;
import java.util.Map;

/**
 * Created by wangziwen on 2017/10/9.
 */
public class FruitTaskDao extends FruitTask {
    public FruitTaskDao() {
        this.setUuid(null);
    }

    private Map<FruitDict.Dict, List<TaskPlanRelation>> taskPlanRelation;
    private Map<FruitDict.Dict, List<TaskProjectRelation>> taskProjectRelation;
    private Map<FruitDict.Dict, List<TaskUserRelation>> taskUserRelation;


    public List<TaskPlanRelation> getTaskPlanRelation(FruitDict.Dict dict) {
        return taskPlanRelation != null && taskPlanRelation.containsKey(dict) ? taskPlanRelation.get(dict) : Lists.newLinkedList();
    }

    public void setTaskPlanRelation(Map<FruitDict.Dict, List<TaskPlanRelation>> taskPlanRelation) {
        this.taskPlanRelation = taskPlanRelation;
    }

    public List<TaskProjectRelation> getTaskProjectRelation(FruitDict.Dict dict) {
        return taskProjectRelation != null && taskProjectRelation.containsKey(dict) ? taskProjectRelation.get(dict) : Lists.newLinkedList();
    }

    public void setTaskProjectRelation(Map<FruitDict.Dict, List<TaskProjectRelation>> taskProjectRelation) {
        this.taskProjectRelation = taskProjectRelation;
    }

    public List<TaskUserRelation> getTaskUserRelation(FruitDict.Dict dict) {
        return taskUserRelation != null && taskUserRelation.containsKey(dict) ? taskUserRelation.get(dict) : Lists.newLinkedList();
    }

    public void setTaskUserRelation(Map<FruitDict.Dict, List<TaskUserRelation>> taskUserRelation) {
        this.taskUserRelation = taskUserRelation;
    }

}
