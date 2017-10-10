package wowjoy.fruits.ms.module.task;

import wowjoy.fruits.ms.module.relation.entity.TaskPlanRelation;
import wowjoy.fruits.ms.module.relation.entity.TaskProjectRelation;
import wowjoy.fruits.ms.module.util.entity.FruitDict;

import java.util.List;
import java.util.Map;


/**
 * Created by wangziwen on 2017/10/9.
 */
public class FruitTaskVo extends FruitTask {

    private Map<FruitDict.Dict, List<TaskPlanRelation>> taskPlanRelation;
    private Map<FruitDict.Dict, List<TaskProjectRelation>> taskProjectRelation;

    public Map<FruitDict.Dict, List<TaskPlanRelation>> getTaskPlanRelation() {
        return taskPlanRelation;
    }

    public void setTaskPlanRelation(Map<FruitDict.Dict, List<TaskPlanRelation>> taskPlanRelation) {
        this.taskPlanRelation = taskPlanRelation;
    }

    public Map<FruitDict.Dict, List<TaskProjectRelation>> getTaskProjectRelation() {
        return taskProjectRelation;
    }

    public void setTaskProjectRelation(Map<FruitDict.Dict, List<TaskProjectRelation>> taskProjectRelation) {
        this.taskProjectRelation = taskProjectRelation;
    }
}
