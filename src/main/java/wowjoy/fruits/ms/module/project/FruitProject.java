package wowjoy.fruits.ms.module.project;


import wowjoy.fruits.ms.module.AbstractEntity;

import java.util.Date;

/**
 * Created by wangziwen on 2017/8/24.
 */
public class FruitProject extends AbstractEntity {
    private String title;
    private Date predictEndDate;
    private Date endDateTime;
    private String projectStatus;

    public void setPredictEndDate(Date predictEndDate) {
        this.predictEndDate = predictEndDate;
    }

    public void setEndDateTime(Date endDateTime) {
        this.endDateTime = endDateTime;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getProjectStatus() {
        return projectStatus;
    }

    public void setProjectStatus(String projectStatus) {
        this.projectStatus = projectStatus;
    }

    public Date getPredictEndDate() {
        return predictEndDate;
    }

    public Date getEndDateTime() {
        return endDateTime;
    }

    public String getTitle() {
        return title;
    }

}