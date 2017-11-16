package wowjoy.fruits.ms.module.task;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import wowjoy.fruits.ms.module.plan.FruitPlanDao;
import wowjoy.fruits.ms.module.relation.entity.TaskListRelation;
import wowjoy.fruits.ms.module.relation.entity.TaskPlanRelation;
import wowjoy.fruits.ms.module.relation.entity.TaskProjectRelation;
import wowjoy.fruits.ms.module.relation.entity.TaskUserRelation;
import wowjoy.fruits.ms.module.user.FruitUserDao;
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

    private Map<FruitDict.Systems, List<TaskPlanRelation>> taskPlanRelation;
    private Map<FruitDict.Systems, List<TaskProjectRelation>> taskProjectRelation;
    private Map<FruitDict.Systems, List<TaskUserRelation>> taskUserRelation;
    private Map<FruitDict.Systems, List<TaskListRelation>> taskListRelation;
    private List<String> projectIds;
    private transient Integer pageNum = 1;
    private transient Integer pageSize = 10;

    private List<FruitUserDao> users;
    private FruitPlanDao plan;
    private Integer days;

    private transient String listId;
    private transient String planId;

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public FruitPlanDao getPlan() {
        return plan;
    }

    public void setPlan(FruitPlanDao plan) {
        this.plan = plan;
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
        LocalDateTime estimatedEndDate = LocalDateTime.parse(new SimpleDateFormat("yyyy-MM-dd'T'23:59:59").format(this.getEstimatedEndDate()));
        LocalDateTime toDay = LocalDateTime.now();
        this.days = (int) Duration.between(toDay, estimatedEndDate).toDays();

    }

    public List<FruitUserDao> getUsers() {
        if (users == null || users.isEmpty())
            users = Lists.newLinkedList();
        return users;
    }

    public void setUsers(List<FruitUserDao> users) {
        this.users = users;
    }

    public FruitTaskDao sortUsers() {
        Collections.sort(users, Comparator.comparing(o -> o.getCreateDateTime().toInstant()));
        return this;
    }

    public List<TaskPlanRelation> getTaskPlanRelation(FruitDict.Systems parents) {
        return taskPlanRelation != null && taskPlanRelation.containsKey(parents) ? taskPlanRelation.get(parents) : Lists.newLinkedList();
    }

    public Map<FruitDict.Systems, List<TaskPlanRelation>> getTaskPlanRelation() {
        return taskPlanRelation;
    }

    public boolean checkTaskPlanRelation() {
        if (taskPlanRelation == null || taskPlanRelation.isEmpty())
            return false;
        return true;
    }

    public boolean checkTaskProjectRelation() {
        if (taskProjectRelation == null || taskProjectRelation.isEmpty())
            return false;
        return true;
    }

    public void setTaskPlanRelation(Map<FruitDict.Systems, List<TaskPlanRelation>> taskPlanRelation) {
        this.taskPlanRelation = taskPlanRelation;
    }

    public void setTaskPlanRelation(FruitDict.Systems parents, List<TaskPlanRelation> value) {
        if (this.taskPlanRelation == null)
            this.taskPlanRelation = Maps.newLinkedHashMap();
        this.taskPlanRelation.put(parents, value);
    }

    public List<TaskProjectRelation> getTaskProjectRelation(FruitDict.Systems parents) {
        return taskProjectRelation != null && taskProjectRelation.containsKey(parents) ? taskProjectRelation.get(parents) : Lists.newLinkedList();
    }

    public Map<FruitDict.Systems, List<TaskProjectRelation>> getTaskProjectRelation() {
        return taskProjectRelation;
    }

    public void setTaskProjectRelation(Map<FruitDict.Systems, List<TaskProjectRelation>> taskProjectRelation) {
        this.taskProjectRelation = taskProjectRelation;
    }

    public void setTaskProjectRelation(FruitDict.Systems parents, List<TaskProjectRelation> value) {
        if (this.taskProjectRelation == null)
            this.taskProjectRelation = Maps.newLinkedHashMap();
        this.taskProjectRelation.put(parents, value);
    }

    public List<TaskUserRelation> getTaskUserRelation(FruitDict.Systems parents) {
        return taskUserRelation != null && taskUserRelation.containsKey(parents) ? taskUserRelation.get(parents) : Lists.newLinkedList();
    }

    public void setTaskUserRelation(Map<FruitDict.Systems, List<TaskUserRelation>> taskUserRelation) {
        this.taskUserRelation = taskUserRelation;
    }

    public List<TaskListRelation> getTaskListRelation(FruitDict.Systems parents) {
        return taskListRelation != null && taskListRelation.containsKey(parents) ? taskListRelation.get(parents) : Lists.newLinkedList();
    }

    public void setTaskListRelation(Map<FruitDict.Systems, List<TaskListRelation>> taskListRelation) {
        this.taskListRelation = taskListRelation;
    }

    public void setTaskListRelation(FruitDict.Systems parents, List<TaskListRelation> value) {
        if (taskListRelation == null)
            taskListRelation = Maps.newLinkedHashMap();
        this.taskListRelation.put(parents, value);
    }


}
