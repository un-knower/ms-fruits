package wowjoy.fruits.ms.module.task;

import org.apache.commons.lang.StringUtils;
import wowjoy.fruits.ms.module.relation.entity.TaskListRelation;
import wowjoy.fruits.ms.module.relation.entity.TaskPlanRelation;
import wowjoy.fruits.ms.module.relation.entity.TaskProjectRelation;
import wowjoy.fruits.ms.module.relation.entity.TaskUserRelation;
import wowjoy.fruits.ms.module.util.entity.FruitDict;

import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * Created by wangziwen on 2017/10/9.
 */
public class FruitTaskVo extends FruitTask {
    private String uuidVo;
    private Map<FruitDict.Dict, List<TaskPlanRelation>> taskPlanRelation;
    private Map<FruitDict.Dict, List<TaskProjectRelation>> taskProjectRelation;
    private Map<FruitDict.Dict, List<TaskUserRelation>> taskUserRelation;
    private Map<FruitDict.Dict, List<TaskListRelation>> taskListRelation;
    private String id;
    private String listId;

    public List<String> getId() {
        if (StringUtils.isBlank(id))
            return null;
        return Arrays.asList(id.split(","));
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getListId() {
        if (StringUtils.isBlank(listId))
            return null;
        return Arrays.asList(listId.split(","));
    }

    public void setListId(String listId) {
        this.listId = listId;
    }

    public String getUuidVo() {
        return uuidVo;
    }

    public void setUuidVo(String uuidVo) {
        this.uuidVo = uuidVo;
    }

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

    public Map<FruitDict.Dict, List<TaskUserRelation>> getTaskUserRelation() {
        return taskUserRelation;
    }

    public void setTaskUserRelation(Map<FruitDict.Dict, List<TaskUserRelation>> taskUserRelation) {
        this.taskUserRelation = taskUserRelation;
    }

    public Map<FruitDict.Dict, List<TaskListRelation>> getTaskListRelation() {
        return taskListRelation;
    }

    public void setTaskListRelation(Map<FruitDict.Dict, List<TaskListRelation>> taskListRelation) {
        this.taskListRelation = taskListRelation;
    }
}
