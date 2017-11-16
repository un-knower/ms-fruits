package wowjoy.fruits.ms.module.plan;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import wowjoy.fruits.ms.module.user.FruitUserDao;
import wowjoy.fruits.ms.module.util.entity.FruitDict;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by wangziwen on 2017/9/15.
 */
public class FruitPlanDao extends FruitPlan {

    public FruitPlanDao() {
        setUuid(null);
    }

    private Date startDateDao;
    private Date endDateDao;
    private volatile List<FruitUserDao> users;
    private Map<FruitDict.Systems, List<String>> userRelation;
    private Map<FruitDict.Systems, List<String>> projectRelation;
    private volatile List<FruitPlanDao> weeks;
    private String projectId;
    private String desc;
    private String asc;
    private List<String> parentIds;

    public List<String> getParentIds() {
        return parentIds;
    }

    public void setParentIds(List<String> parentIds) {
        this.parentIds = parentIds;
    }

    public Integer getDays() {
        return days;
    }

    public void setDays(Integer days) {
        this.days = days;
    }

    private String taskId;
    private Integer days;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getAsc() {
        return asc;
    }

    public void setAsc(String asc) {
        this.asc = asc;
    }

    public List<FruitPlanDao> getWeeks() {
        return weeks;
    }

    public void setWeeks(List<FruitPlanDao> weeks) {
        this.weeks = weeks;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public List<String> getUserRelation(FruitDict.Systems type) {
        return userRelation != null && userRelation.containsKey(type) ? userRelation.get(type) : Lists.newLinkedList();
    }

    public List<String> getProjectRelation(FruitDict.Systems type) {
        return projectRelation != null && projectRelation.containsKey(type) ? projectRelation.get(type) : Lists.newLinkedList();
    }

    public void setUserRelation(Map<FruitDict.Systems, List<String>> userRelation) {
        this.userRelation = userRelation;
    }

    public void setProjectRelation(Map<FruitDict.Systems, List<String>> projectRelation) {
        this.projectRelation = projectRelation;
    }

    public void setProjectRelation(FruitDict.Systems parents, List<String> value) {
        if (projectRelation == null)
            projectRelation = Maps.newHashMap();

        projectRelation.put(parents, value);
    }

    /**
     * 是否延期
     * 延期天数
     */
    public FruitPlanDao computeDays() {
        if (this.getEstimatedEndDate() == null) {
            this.setDays(999999999);
        }
        LocalDateTime predictEndTime = LocalDateTime.parse(new SimpleDateFormat(DateTimeFormat).format(this.getEstimatedEndDate()));
        LocalDateTime currentTime = LocalDateTime.parse(new SimpleDateFormat(DateTimeFormat).format(new Date()));
        Duration between = Duration.between(currentTime, predictEndTime);
        this.setDays((int) between.toDays());
        return this;
    }

    public List<FruitUserDao> getUsers() {
        return users;
    }

    public void setUsers(List<FruitUserDao> users) {
        this.users = users;
    }

    public Date getStartDateDao() {
        return startDateDao;
    }

    public void setStartDateDao(Date startDateDao) {
        this.startDateDao = startDateDao;
    }

    public Date getEndDateDao() {
        return endDateDao;
    }

    public void setEndDateDao(Date endDateDao) {
        this.endDateDao = endDateDao;
    }
}
