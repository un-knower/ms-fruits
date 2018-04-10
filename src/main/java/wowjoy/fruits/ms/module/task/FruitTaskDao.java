package wowjoy.fruits.ms.module.task;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import wowjoy.fruits.ms.module.list.FruitListDao;
import wowjoy.fruits.ms.module.logs.FruitLogs;
import wowjoy.fruits.ms.module.plan.FruitPlanDao;
import wowjoy.fruits.ms.module.relation.entity.TaskListRelation;
import wowjoy.fruits.ms.module.relation.entity.TaskPlanRelation;
import wowjoy.fruits.ms.module.relation.entity.TaskProjectRelation;
import wowjoy.fruits.ms.module.relation.entity.TaskUserRelation;
import wowjoy.fruits.ms.module.util.entity.FruitDict;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Created by wangziwen on 2017/10/9.
 */
public class FruitTaskDao extends FruitTask {
    public FruitTaskDao() {
        this.setUuid(null);
    }

    private Map<FruitDict.Systems, List<TaskPlanRelation>> planRelation;
    private Map<FruitDict.Systems, List<TaskProjectRelation>> projectRelation;
    private Map<FruitDict.Systems, List<TaskUserRelation>> userRelation;
    private Map<FruitDict.Systems, List<TaskListRelation>> listRelation;
    private List<String> projectIds;
    private transient Integer pageNum = 1;
    private transient Integer pageSize = 10;

    private List<FruitLogs.Info> logs;
    private List<FruitTaskUser> users;
    private FruitTaskPlan plan;
    private FruitTaskProject project;
    private FruitTaskList list;

    private Integer days;

    private transient String listId;

    public FruitTaskPlan getPlan() {
        return plan;
    }

    public void setPlan(FruitTaskPlan plan) {
        this.plan = plan;
    }

    public FruitTaskList getList() {
        return list;
    }

    public void setList(FruitTaskList list) {
        this.list = list;
    }

    public List<FruitTaskUser> getUsers() {
        return users;
    }

    public void setUsers(List<FruitTaskUser> users) {
        this.users = users;
    }

    public List<FruitLogs.Info> getLogs() {
        return logs;
    }

    public void setLogs(List<FruitLogs.Info> logs) {
        this.logs = logs;
    }

    private transient String planId;

    public FruitTaskProject getProject() {
        return project;
    }

    public void setProject(FruitTaskProject project) {
        this.project = project;
    }

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

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

    public String getListId() {
        return listId;
    }

    public void setListId(String listId) {
        this.listId = listId;
    }

    public List<String> getProjectIds() {
        return projectIds;
    }

    public void setProjectIds(List<String> projectIds) {
        this.projectIds = projectIds;
    }

    public Integer getDays() {
        return days;
    }

    public void setDays(Integer days) {
        this.days = days;
    }

    public void computeDays() {
        LocalDateTime estimatedEndDate = LocalDateTime.parse(new SimpleDateFormat(DateTimeFormat).format(this.getEstimatedEndDate()));
        LocalDateTime endDate;
        if (!FruitDict.TaskDict.START.name().equals(this.getTaskStatus()))
            endDate = LocalDateTime.parse(new SimpleDateFormat(DateTimeFormat).format(this.getEndDate()));
        else
            endDate = LocalDateTime.now().toLocalDate().atTime(23, 59, 59);
        this.days = (int) Duration.between(endDate, estimatedEndDate).toDays();

    }

    public FruitTaskDao sortUsers() {
        Collections.sort(users, Comparator.comparing(o -> o.getCreateDateTime().toInstant()));
        return this;
    }

    public List<TaskPlanRelation> getPlanRelation(FruitDict.Systems parents) {
        return planRelation != null && planRelation.containsKey(parents) ? planRelation.get(parents) : Lists.newLinkedList();
    }

    public Map<FruitDict.Systems, List<TaskPlanRelation>> getPlanRelation() {
        return planRelation;
    }

    public boolean checkPlanRelation() {
        if (planRelation == null || planRelation.isEmpty())
            return false;
        return true;
    }

    public boolean checkProjectRelation() {
        if (projectRelation == null || projectRelation.isEmpty())
            return false;
        return true;
    }

    public void setPlanRelation(Map<FruitDict.Systems, List<TaskPlanRelation>> planRelation) {
        this.planRelation = planRelation;
    }

    public void setPlanRelation(FruitDict.Systems parents, List<TaskPlanRelation> value) {
        if (this.planRelation == null)
            this.planRelation = Maps.newLinkedHashMap();
        this.planRelation.put(parents, value);
    }

    public List<TaskProjectRelation> getProjectRelation(FruitDict.Systems parents) {
        return projectRelation != null && projectRelation.containsKey(parents) ? projectRelation.get(parents) : Lists.newLinkedList();
    }

    public Map<FruitDict.Systems, List<TaskProjectRelation>> getProjectRelation() {
        return projectRelation;
    }

    public void setProjectRelation(FruitDict.Systems systems, List<TaskProjectRelation> projects) {
        if (projectRelation == null)
            projectRelation = Maps.newLinkedHashMap();
        this.projectRelation.put(systems, projects);
    }

    public void setTaskProjectRelation(FruitDict.Systems parents, List<TaskProjectRelation> value) {
        if (this.projectRelation == null)
            this.projectRelation = Maps.newLinkedHashMap();
        this.projectRelation.put(parents, value);
    }

    public List<TaskUserRelation> getUserRelation(FruitDict.Systems parents) {
        return userRelation != null && userRelation.containsKey(parents) ? userRelation.get(parents) : Lists.newLinkedList();
    }

    public void setUserRelation(Map<FruitDict.Systems, List<TaskUserRelation>> userRelation) {
        this.userRelation = userRelation;
    }

    public List<TaskListRelation> getListRelation(FruitDict.Systems parents) {
        return listRelation != null && listRelation.containsKey(parents) ? listRelation.get(parents) : Lists.newLinkedList();
    }

    public void setListRelation(Map<FruitDict.Systems, List<TaskListRelation>> listRelation) {
        this.listRelation = listRelation;
    }

    public void setListRelation(FruitDict.Systems parents, List<TaskListRelation> value) {
        if (listRelation == null)
            listRelation = Maps.newLinkedHashMap();
        this.listRelation.put(parents, value);
    }


}
