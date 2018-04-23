package wowjoy.fruits.ms.module.task;


import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import wowjoy.fruits.ms.exception.CheckException;
import wowjoy.fruits.ms.module.AbstractEntity;
import wowjoy.fruits.ms.module.EntityUtils;
import wowjoy.fruits.ms.module.logs.FruitLogs;
import wowjoy.fruits.ms.module.relation.entity.TaskListRelation;
import wowjoy.fruits.ms.module.relation.entity.TaskPlanRelation;
import wowjoy.fruits.ms.module.relation.entity.TaskProjectRelation;
import wowjoy.fruits.ms.module.relation.entity.TaskUserRelation;
import wowjoy.fruits.ms.module.util.entity.FruitDict;
import wowjoy.fruits.ms.util.GsonUtils;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by wangziwen on 2017/8/24.
 */
public class FruitTask extends AbstractEntity {

    private String taskLevel;
    private String title;
    private String taskStatus;
    private String statusDescription;
    private Date estimatedEndDate;
    private Date endDate;

    public Date getEstimatedEndDate() {
        return estimatedEndDate;
    }

    public void setEstimatedEndDate(Date estimatedEndDate) {
        this.estimatedEndDate = estimatedEndDate;
    }

    public String getStatusDescription() {
        return statusDescription;
    }

    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setTaskStatus(String taskStatus) {
        try {
            if (StringUtils.isNotBlank(taskStatus))
                this.taskStatus = FruitDict.TaskDict.valueOf(taskStatus).name();
        } catch (IllegalArgumentException ex) {
            throw new CheckException("无效的任务状态：" + ex.getMessage());
        }
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.setEndDate(Date.from(endDate.atZone(ZoneId.systemDefault()).toInstant()));
    }

    public void setTaskLevel(String taskLevel) {
        this.taskLevel = taskLevel;
    }

    public String getTitle() {
        return title;
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public String getTaskLevel() {
        return taskLevel;
    }

    public Date getEndDate() {
        return endDate;
    }

    public static FruitTaskDao getDao() {
        return new FruitTaskDao();
    }

    public static FruitTaskVo getVo() {
        return new FruitTaskVo();
    }

    public static FruitTaskEmpty getEmpty() {
        return new FruitTaskEmpty();
    }

    public static class Insert extends FruitTask implements EntityUtils {
        public Insert() {
            super.setUuid(obtainUUID());
        }

        private Map<FruitDict.Systems, List<TaskPlanRelation>> planRelation;
        private Map<FruitDict.Systems, List<TaskProjectRelation>> projectRelation;
        private Map<FruitDict.Systems, List<TaskUserRelation>> userRelation;
        private Map<FruitDict.Systems, List<TaskListRelation>> listRelation;

        public Map<FruitDict.Systems, List<TaskPlanRelation>> getPlanRelation() {
            return planRelation;
        }

        public void setPlanRelation(FruitDict.Systems parents, List<TaskPlanRelation> value) {
            if (this.planRelation == null)
                this.planRelation = Maps.newLinkedHashMap();
            this.planRelation.put(parents, value);
        }

        public Map<FruitDict.Systems, List<TaskProjectRelation>> getProjectRelation() {
            return projectRelation;
        }

        public void setProjectRelation(FruitDict.Systems systems, List<TaskProjectRelation> projects) {
            if (projectRelation == null)
                projectRelation = Maps.newLinkedHashMap();
            this.projectRelation.put(systems, projects);
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

    public static class Update extends FruitTask {
        public Update() {
            super.setUuid(null);
        }

        private Map<FruitDict.Systems, List<TaskPlanRelation>> planRelation;
        private Map<FruitDict.Systems, List<TaskProjectRelation>> projectRelation;
        private Map<FruitDict.Systems, List<TaskUserRelation>> userRelation;
        private Map<FruitDict.Systems, List<TaskListRelation>> listRelation;

        public Map<FruitDict.Systems, List<TaskPlanRelation>> getPlanRelation() {
            return planRelation;
        }

        public void setPlanRelation(FruitDict.Systems parents, List<TaskPlanRelation> value) {
            if (this.planRelation == null)
                this.planRelation = Maps.newLinkedHashMap();
            this.planRelation.put(parents, value);
        }

        public Map<FruitDict.Systems, List<TaskProjectRelation>> getProjectRelation() {
            return projectRelation;
        }

        public void setProjectRelation(FruitDict.Systems systems, List<TaskProjectRelation> projects) {
            if (projectRelation == null)
                projectRelation = Maps.newLinkedHashMap();
            this.projectRelation.put(systems, projects);
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

    public static class Info extends FruitTask {
        public Info() {
            setUuid(null);
            setIsDeleted(null);
        }

        private int days;

        private List<FruitLogs.Info> logs;
        private List<FruitTaskUser> users;
        private FruitTaskPlan plan;
        private FruitTaskProject project;
        private FruitTaskList list;

        public int getDays() {
            return days;
        }

        public void setDays(int days) {
            this.days = days;
        }

        public FruitTaskList getList() {
            return list;
        }

        public void setList(FruitTaskList list) {
            this.list = list;
        }

        public FruitTaskProject getProject() {
            return project;
        }

        public void setProject(FruitTaskProject project) {
            this.project = project;
        }

        public FruitTaskPlan getPlan() {
            return plan;
        }

        public void setPlan(FruitTaskPlan plan) {
            this.plan = plan;
        }

        public List<FruitLogs.Info> getLogs() {
            return logs;
        }

        public void setLogs(List<FruitLogs.Info> logs) {
            this.logs = logs;
        }

        public List<FruitTaskUser> getUsers() {
            return users;
        }

        public void setUsers(List<FruitTaskUser> users) {
            this.users = users;
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
    }

    public static class Search extends FruitTask implements EntityUtils {
        public Search() {
            setUuid(null);
            setIsDeleted(null);
        }

        /*列表名称查询*/
        private String listTitle;
        /*日期范围查询*/
        private Date beginDateTime;   //开始时间
        private Date endDateTime;     //结束时间
        /*根据列表ID查询，支持多列表查询*/
        private String lists;   //多个逗号隔开
        /*分页*/
        private int pageNum;    //页码
        private int pageSize;   //行数

        public Date getBeginDateTime() {
            return beginDateTime;
        }

        public void setBeginDateTime(Date beginDateTime) {
            this.beginDateTime = beginDateTime;
        }

        public Date getEndDateTime() {
            return endDateTime;
        }

        public void setEndDateTime(Date endDateTime) {
            this.endDateTime = endDateTime;
        }

        public int getPageNum() {
            return pageNum;
        }

        public void setPageNum(int pageNum) {
            this.pageNum = pageNum;
        }

        public int getPageSize() {
            return pageSize;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }

        public String getLists() {
            return lists;
        }

        public void setLists(String lists) {
            this.lists = lists;
        }

        public String getListTitle() {
            return listTitle;
        }

        public void setListTitle(String listTitle) {
            this.listTitle = listTitle;
        }
    }

    public Info toInfo() {
        return GsonUtils.toT(this, Info.class);
    }

}
