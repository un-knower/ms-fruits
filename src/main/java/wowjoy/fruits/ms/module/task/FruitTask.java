package wowjoy.fruits.ms.module.task;


import wowjoy.fruits.ms.exception.CheckException;
import wowjoy.fruits.ms.module.AbstractEntity;
import wowjoy.fruits.ms.module.util.entity.FruitDict;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

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
            this.taskStatus = FruitDict.TaskDict.valueOf(taskStatus).name();
        } catch (IllegalArgumentException ex) {
            throw new CheckException("无效的任务状态：" + ex.getMessage());
        }
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.setEndDate(Date.from(endDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
    }

    public void setTaskLevel(String taskLevel) {
        try {
            this.taskLevel = FruitDict.TaskDict.valueOf(taskLevel).name();
        } catch (IllegalArgumentException ex) {
            throw new CheckException("无效的任务等级：" + ex.getMessage());
        }
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

}
