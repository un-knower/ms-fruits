package wowjoy.fruits.ms.module.task;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
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

    private Map<FruitDict.Dict, List<TaskPlanRelation>> taskPlanRelation;
    private Map<FruitDict.Dict, List<TaskProjectRelation>> taskProjectRelation;
    private Map<FruitDict.Dict, List<TaskUserRelation>> taskUserRelation;
    private Map<FruitDict.Dict, TaskListRelation> taskListRelation;

    private List<FruitUserDao> users;
    private Integer days;

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
        return users;
    }

    public void setUsers(List<FruitUserDao> users) {
        this.users = users;
    }

    public FruitTaskDao sortUsers() {
        Collections.sort(users, Comparator.comparing(o -> o.getCreateDateTime().toInstant()));
        return this;
    }

    public List<TaskPlanRelation> getTaskPlanRelation(FruitDict.Dict dict) {
        return taskPlanRelation != null && taskPlanRelation.containsKey(dict) ? taskPlanRelation.get(dict) : Lists.newLinkedList();
    }

    public void setTaskPlanRelation(Map<FruitDict.Dict, List<TaskPlanRelation>> taskPlanRelation) {
        this.taskPlanRelation = taskPlanRelation;
    }

    public void setTaskPlanRelation(FruitDict.Dict dict, List<TaskPlanRelation> value) {
        if (this.taskPlanRelation == null)
            this.taskPlanRelation = Maps.newLinkedHashMap();
        this.taskPlanRelation.put(dict, value);
    }

    public List<TaskProjectRelation> getTaskProjectRelation(FruitDict.Dict dict) {
        return taskProjectRelation != null && taskProjectRelation.containsKey(dict) ? taskProjectRelation.get(dict) : Lists.newLinkedList();
    }

    public void setTaskProjectRelation(Map<FruitDict.Dict, List<TaskProjectRelation>> taskProjectRelation) {
        this.taskProjectRelation = taskProjectRelation;
    }

    public void setTaskProjectRelation(FruitDict.Dict dict, List<TaskProjectRelation> value) {
        if (this.taskProjectRelation == null)
            this.taskProjectRelation = Maps.newLinkedHashMap();
        this.taskProjectRelation.put(dict, value);
    }

    public List<TaskUserRelation> getTaskUserRelation(FruitDict.Dict dict) {
        return taskUserRelation != null && taskUserRelation.containsKey(dict) ? taskUserRelation.get(dict) : Lists.newLinkedList();
    }

    public void setTaskUserRelation(Map<FruitDict.Dict, List<TaskUserRelation>> taskUserRelation) {
        this.taskUserRelation = taskUserRelation;
    }

    public TaskListRelation getTaskListRelation(FruitDict.Dict dict) {
        return taskListRelation != null && taskListRelation.containsKey(dict) ? taskListRelation.get(dict) : TaskListRelation.getEmpty();
    }

    public void setTaskListRelation(Map<FruitDict.Dict, TaskListRelation> taskListRelation) {
        this.taskListRelation = taskListRelation;
    }

    public void setTaskListRelation(FruitDict.Dict dict, TaskListRelation value) {
        if (taskListRelation == null)
            taskListRelation = Maps.newLinkedHashMap();
        this.taskListRelation.put(dict, value);
    }


}
