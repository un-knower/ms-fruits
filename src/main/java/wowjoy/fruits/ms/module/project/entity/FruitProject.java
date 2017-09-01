package wowjoy.fruits.ms.module.project.entity;


import wowjoy.fruits.ms.module.AbstractEntity;

import java.util.Date;

/**
 * Created by wangziwen on 2017/8/24.
 */
public class FruitProject extends AbstractEntity {
    private String title;
    private String teamStatus;
    private Date predictEndDateTime;
    private Date endDateTime;

    public void setPredictEndDateTime(Date predictEndDateTime) {
        this.predictEndDateTime = predictEndDateTime;
    }

    public void setEndDateTime(Date endDateTime) {
        this.endDateTime = endDateTime;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setTeamStatus(String teamStatus) {
        this.teamStatus = teamStatus;
    }

    public Date getPredictEndDateTime() {
        return predictEndDateTime;
    }

    public Date getEndDateTime() {
        return endDateTime;
    }

    public String getTitle() {
        return title;
    }

    public String getTeamStatus() {
        return teamStatus;
    }

}
