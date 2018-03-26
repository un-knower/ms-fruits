package wowjoy.fruits.ms.module.plan;


import com.google.common.reflect.TypeToken;
import org.apache.commons.lang.StringUtils;
import wowjoy.fruits.ms.module.AbstractEntity;
import wowjoy.fruits.ms.module.EntityUtils;
import wowjoy.fruits.ms.module.logs.FruitLogs;
import wowjoy.fruits.ms.module.util.entity.FruitDict;
import wowjoy.fruits.ms.util.GsonUtils;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

/**
 * Created by wangziwen on 2017/8/24.
 */
public class FruitPlan extends AbstractEntity {
    private String title;
    private Integer percent;
    private Date estimatedStartDate;
    private Date estimatedEndDate;
    private Date startDate;
    private Date endDate;
    private String planStatus;
    private String statusDescription;
    private String parentId;

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEstimatedStartDate() {
        return estimatedStartDate;
    }

    public void setEstimatedStartDate(Date estimatedStartDate) {
        this.estimatedStartDate = estimatedStartDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getStatusDescription() {
        return statusDescription;
    }

    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
    }

    public Date getEstimatedEndDate() {
        return estimatedEndDate;
    }

    public void setEstimatedEndDate(Date estimatedEndDate) {
        this.estimatedEndDate = estimatedEndDate;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = StringUtils.isNotBlank(parentId) ? parentId : null;
    }

    public Integer getPercent() {
        return percent;
    }

    public void setPercent(Integer percent) {
        this.percent = percent;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public void setPlanStatus(String planStatus) {
        this.planStatus = planStatus;
    }

    public String getTitle() {
        return title;
    }

    public String getPlanStatus() {
        return planStatus;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.setEndDate(Date.from(endDate.atZone(ZoneId.systemDefault()).toInstant()));
    }

    /*添加*/
    public static class Insert extends FruitPlan implements EntityUtils {
        public Insert() {
            this.setUuid(obtainUUID());
        }

        private Map<FruitDict.Systems, List<String>> userRelation;
        private Map<FruitDict.Systems, List<String>> projectRelation;

        public Map<FruitDict.Systems, List<String>> getUserRelation() {
            return userRelation;
        }

        public Map<FruitDict.Systems, List<String>> getProjectRelation() {
            return projectRelation;
        }

        public void setUserRelation(Map<FruitDict.Systems, List<String>> userRelation) {
            this.userRelation = userRelation;
        }

        public void setProjectRelation(Map<FruitDict.Systems, List<String>> projectRelation) {
            this.projectRelation = projectRelation;
        }

    }

    /*修改*/
    public static class Update extends FruitPlan {
        Update() {
            this.setUuid(null);
        }

        private Map<FruitDict.Systems, List<String>> userRelation;

        public void setUserRelation(Map<FruitDict.Systems, List<String>> userRelation) {
            this.userRelation = userRelation;
        }

        public Map<FruitDict.Systems, List<String>> getUserRelation() {
            return userRelation;
        }
    }

    /*查询*/
    public static class Query extends FruitPlan {
        public Query() {
            setUuid(null);
        }
        private String projectId;
        /*提供年份*/
        private String year;
        /*提供月份*/
        private String month;

        public String getYear() {
            return year;
        }

        public void setYear(String year) {
            this.year = year;
        }

        public String getMonth() {
            return month;
        }

        public void setMonth(String month) {
            this.month = month;
        }

        public String getProjectId() {
            return projectId;
        }

        public void setProjectId(String projectId) {
            this.projectId = projectId;
        }
    }

    /*出参*/
    public static class Info extends FruitPlan {
        public Info() {
            setIsDeleted(null);
            setUuid(null);
        }

        private LinkedList<FruitPlanUser> users;
        private LinkedList<FruitLogs.Info> logs;
        private ArrayList<FruitPlanTask> tasks;
        private ArrayList<FruitPlan.Info> weeks;
        private Integer days;

        /**
         * 延期天数
         */
        public FruitPlan.Info computeDays() {
            Date endDate;
            if (this.getEstimatedEndDate() == null) {
                this.setDays(999999999);
                return this;
            }
            if (this.getEndDate() != null && !FruitDict.PlanDict.PENDING.name().equals(this.getPlanStatus())) {
                endDate = this.getEndDate();
            } else {
                endDate = new Date();
            }
            LocalDateTime predictEndTime = LocalDateTime.parse(new SimpleDateFormat(DateTimeFormat).format(this.getEstimatedEndDate()));
            LocalDateTime currentTime = LocalDateTime.parse(new SimpleDateFormat(DateTimeFormat).format(endDate));
            Duration between = Duration.between(currentTime, predictEndTime);
            this.setDays((int) between.toDays());
            return this;
        }

        public ArrayList<Info> getWeeks() {
            return weeks;
        }

        public void setWeeks(ArrayList<Info> weeks) {
            this.weeks = weeks;
        }

        public LinkedList<FruitLogs.Info> getLogs() {
            return logs;
        }

        public void setLogs(LinkedList<FruitLogs.Info> logs) {
            this.logs = logs;
        }

        public Integer getDays() {
            return days;
        }

        public void setDays(Integer days) {
            this.days = days;
        }

        public LinkedList<FruitPlanUser> getUsers() {
            return users;
        }

        public void setUsers(LinkedList<FruitPlanUser> users) {
            this.users = users;
        }

        public ArrayList<FruitPlanTask> getTasks() {
            return tasks;
        }

        public void setTasks(ArrayList<FruitPlanTask> tasks) {
            this.tasks = tasks;
        }

        public Info deepCopy() {
            return GsonUtils.newGson().fromJson(GsonUtils.newGson().toJsonTree(this), TypeToken.of(this.getClass()).getType());
        }
    }

    /********
     * 实例  *
     ********/
    public static FruitPlan.Update newUpdate() {
        return new FruitPlan.Update();
    }

    public static FruitPlan.Info newInfo() {
        return new FruitPlan.Info();
    }

    public FruitPlan.Info toInfo() {
        return GsonUtils.newGson().fromJson(GsonUtils.newGson().toJsonTree(this), TypeToken.of(FruitPlan.Info.class).getType());
    }

    public static FruitPlan getInstance() {
        return new FruitPlan();
    }

    public static FruitPlanDao getDao() {
        return new FruitPlanDao();
    }

    public static FruitPlanVo getVo() {
        return new FruitPlanVo();
    }
}
