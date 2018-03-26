package wowjoy.fruits.ms.module.task;


import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import wowjoy.fruits.ms.exception.CheckException;
import wowjoy.fruits.ms.module.AbstractEntity;
import wowjoy.fruits.ms.module.EntityUtils;
import wowjoy.fruits.ms.module.relation.entity.TaskListRelation;
import wowjoy.fruits.ms.module.relation.entity.TaskPlanRelation;
import wowjoy.fruits.ms.module.relation.entity.TaskProjectRelation;
import wowjoy.fruits.ms.module.relation.entity.TaskUserRelation;
import wowjoy.fruits.ms.module.util.entity.FruitDict;

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

}
