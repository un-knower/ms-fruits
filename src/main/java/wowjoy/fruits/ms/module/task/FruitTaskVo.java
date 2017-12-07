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
    private Map<FruitDict.Systems, List<TaskPlanRelation>> planRelation;
    private Map<FruitDict.Systems, List<TaskProjectRelation>> projectRelation;
    private Map<FruitDict.Systems, List<TaskUserRelation>> userRelation;
    private Map<FruitDict.Systems, List<TaskListRelation>> listRelation;
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

    public Map<FruitDict.Systems, List<TaskPlanRelation>> getPlanRelation() {
        return planRelation;
    }

    public void setPlanRelation(Map<FruitDict.Systems, List<TaskPlanRelation>> planRelation) {
        this.planRelation = planRelation;
    }

    public Map<FruitDict.Systems, List<TaskProjectRelation>> getProjectRelation() {
        return projectRelation;
    }

    public List<TaskProjectRelation> getProjectRelation(FruitDict.Systems parents) {
        return projectRelation != null && projectRelation.containsKey(parents) ? projectRelation.get(parents) : Lists.newLinkedList();
    }

    public List<TaskPlanRelation> getPlanRelation(FruitDict.Systems parents) {
        return planRelation != null && planRelation.containsKey(parents) ? planRelation.get(parents) : Lists.newLinkedList();
    }

    public void setProjectRelation(Map<FruitDict.Systems, List<TaskProjectRelation>> projectRelation) {
        this.projectRelation = projectRelation;
    }

    public Map<FruitDict.Systems, List<TaskUserRelation>> getUserRelation() {
        return userRelation;
    }

    public void setUserRelation(Map<FruitDict.Systems, List<TaskUserRelation>> userRelation) {
        this.userRelation = userRelation;
    }

    public Map<FruitDict.Systems, List<TaskListRelation>> getListRelation() {
        return listRelation;
    }

    public void setListRelation(Map<FruitDict.Systems, List<TaskListRelation>> listRelation) {
        this.listRelation = listRelation;
    }
}
