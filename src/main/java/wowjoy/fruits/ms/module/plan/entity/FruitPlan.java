package wowjoy.fruits.ms.module.plan.entity;


import wowjoy.fruits.ms.module.AbstractEntity;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * Created by wangziwen on 2017/8/24.
 */
public class FruitPlan extends AbstractEntity {
    private String title;
    private String planContent;
    private Date startDateTime;
    private Date endDateTime;
    private String planStatus;

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPlanContent(String planContent) {
        this.planContent = planContent;
    }

    public void setPlanStatus(String planStatus) {
        this.planStatus = planStatus;
    }

    public void setStartDateTime(Date startDateTime) {
        this.startDateTime = startDateTime;
    }

    public void setStartDateTime(LocalDate startDateTime) {
        this.setStartDateTime(Date.from(startDateTime.atStartOfDay(ZoneId.systemDefault()).toInstant()));
    }

    public void setEndDateTime(Date endDateTime) {
        this.endDateTime = endDateTime;
    }

    public Date getStartDateTime() {
        return startDateTime;
    }

    public Date getEndDateTime() {
        return endDateTime;
    }

    public String getTitle() {
        return title;
    }

    public String getPlanContent() {
        return planContent;
    }

    public String getPlanStatus() {
        return planStatus;
    }

    public void setEndDateTime(LocalDate endDateTime) {
        this.setEndDateTime(Date.from(endDateTime.atStartOfDay(ZoneId.systemDefault()).toInstant()));
    }
}
