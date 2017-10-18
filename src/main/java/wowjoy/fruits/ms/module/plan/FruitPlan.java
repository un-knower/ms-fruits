package wowjoy.fruits.ms.module.plan;


import org.apache.commons.lang.StringUtils;
import wowjoy.fruits.ms.module.AbstractEntity;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * Created by wangziwen on 2017/8/24.
 */
public class FruitPlan extends AbstractEntity {
    private String title;
    private int percent;
    private Date estimatedEndDate;
    private Date endDate;
    private String planStatus;
    private String statusDescription;
    private String parentId;


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

    public void setEndDate(LocalDate endDate) {
        this.setEstimatedEndDate(Date.from(endDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
    }

    /********
     * 实例  *
     ********/
    public static FruitPlanEmpty newEmpty(String msg) {
        return new FruitPlanEmpty(msg);
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
