package wowjoy.fruits.ms.module.plan;


import org.apache.commons.lang.StringUtils;
import wowjoy.fruits.ms.module.AbstractEntity;
import wowjoy.fruits.ms.module.EntityUtils;
import wowjoy.fruits.ms.module.util.entity.FruitDict;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by wangziwen on 2017/8/24.
 */
public class FruitPlan extends AbstractEntity {
    private String title;
    private int percent;
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

    public int getPercent() {
        return percent;
    }

    public void setPercent(int percent) {
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
        public Update() {
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

    /********
     * 实例  *
     ********/
    public static FruitPlan.Insert newInsert() {
        return new FruitPlan.Insert();
    }

    public static FruitPlan.Update newUpdate() {
        return new FruitPlan.Update();
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
