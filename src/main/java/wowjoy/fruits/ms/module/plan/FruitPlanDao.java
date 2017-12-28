package wowjoy.fruits.ms.module.plan;

import com.google.common.collect.Lists;
import wowjoy.fruits.ms.module.logs.FruitLogsDao;
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

    private List<FruitUserDao> users;
    private List<FruitLogsDao> logs;

    private Map<FruitDict.Systems, List<String>> userRelation;
    private Map<FruitDict.Systems, List<String>> projectRelation;
    private volatile List<FruitPlanDao> weeks;
    private String projectId;
    private List<String> parentIds;
    private String taskId;
    private Integer days;
    /*因为原days的值不符合日志模板需求，特加一个字段用来存储正整数*/
    private transient Integer daysTemplate;

    public List<FruitLogsDao> getLogs() {
        return logs;
    }

    public void setLogs(List<FruitLogsDao> logs) {
        this.logs = logs;
    }

    public List<String> getParentIds() {
        return parentIds;
    }

    public void setDays(Integer days) {
        this.days = days;
    }

    public Integer getDays() {
        return days;
    }

    public Integer getDaysTemplate() {
        daysTemplate = Math.abs(days);
        return daysTemplate;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
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

    /**
     * 是否延期
     * 延期天数
     */
    public FruitPlanDao computeDays() {
        Date endDate;
        if (this.getEstimatedEndDate() == null) {
            this.setDays(999999999);
            return this;
        }
        if (FruitDict.PlanDict.PENDING.name().equals(this.getPlanStatus()) && this.getEndDate() == null) {
            endDate = new Date();
        } else {
            endDate = this.getEndDate();
        }
        LocalDateTime predictEndTime = LocalDateTime.parse(new SimpleDateFormat(DateTimeFormat).format(this.getEstimatedEndDate()));
        LocalDateTime currentTime = LocalDateTime.parse(new SimpleDateFormat(DateTimeFormat).format(endDate));
        Duration between = Duration.between(currentTime, predictEndTime);
        this.setDays((int) between.toDays());
//        checkPlanStatus();
        return this;
    }

    private void checkPlanStatus() {
        if (FruitDict.PlanDict.PENDING.name().equals(this.getPlanStatus()))
            return;
        if (FruitDict.PlanDict.COMPLETE.name().equals(this.getPlanStatus()))
            if (this.getDays() < 0)
                setPlanStatus(FruitDict.PlanDict.DELAY_COMPLETE.name());
    }

}
