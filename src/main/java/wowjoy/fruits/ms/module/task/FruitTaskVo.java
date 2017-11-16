package wowjoy.fruits.ms.module.task;

import com.google.common.collect.Lists;
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
    private Integer pageNum = 1;
    private Integer pageSize = 10;
    private Map<FruitDict.Systems, List<TaskPlanRelation>> taskPlanRelation;
    private Map<FruitDict.Systems, List<TaskProjectRelation>> taskProjectRelation;
    private Map<FruitDict.Systems, List<TaskUserRelation>> taskUserRelation;
    private Map<FruitDict.Systems, List<TaskListRelation>> taskListRelation;
    private String projectIds;
    private String planId;
    private String listId;

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public List<String> getProjectIds() {
        return split(projectIds);
    }

    public void setProjectIds(String projectIds) {
        this.projectIds = projectIds;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public String getPlanId() {
        return planId;
    }

    private List<String> split(String ids) {
        try {
            return Arrays.asList(ids.split(","));
        } catch (NullPointerException ex) {
            return null;
        }
    }

    public String getListId() {
        return listId;
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

    public Map<FruitDict.Systems, List<TaskPlanRelation>> getTaskPlanRelation() {
        return taskPlanRelation;
    }

    public void setTaskPlanRelation(Map<FruitDict.Systems, List<TaskPlanRelation>> taskPlanRelation) {
        this.taskPlanRelation = taskPlanRelation;
    }

    public Map<FruitDict.Systems, List<TaskProjectRelation>> getTaskProjectRelation() {
        return taskProjectRelation;
    }

    public List<TaskProjectRelation> getTaskProjectRelation(FruitDict.Systems parents) {
        return taskProjectRelation != null && taskProjectRelation.containsKey(parents) ? taskProjectRelation.get(parents) : Lists.newLinkedList();
    }

    public List<TaskPlanRelation> getTaskPlanRelation(FruitDict.Systems parents) {
        return taskPlanRelation != null && taskPlanRelation.containsKey(parents) ? taskPlanRelation.get(parents) : Lists.newLinkedList();
    }

    public void setTaskProjectRelation(Map<FruitDict.Systems, List<TaskProjectRelation>> taskProjectRelation) {
        this.taskProjectRelation = taskProjectRelation;
    }

    public Map<FruitDict.Systems, List<TaskUserRelation>> getTaskUserRelation() {
        return taskUserRelation;
    }

    public void setTaskUserRelation(Map<FruitDict.Systems, List<TaskUserRelation>> taskUserRelation) {
        this.taskUserRelation = taskUserRelation;
    }

    public Map<FruitDict.Systems, List<TaskListRelation>> getTaskListRelation() {
        return taskListRelation;
    }

    public void setTaskListRelation(Map<FruitDict.Systems, List<TaskListRelation>> taskListRelation) {
        this.taskListRelation = taskListRelation;
    }
}
