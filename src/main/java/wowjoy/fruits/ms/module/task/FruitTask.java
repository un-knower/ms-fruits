package wowjoy.fruits.ms.module.task;


import org.springframework.format.annotation.DateTimeFormat;
import wowjoy.fruits.ms.module.AbstractEntity;
import wowjoy.fruits.ms.module.util.entity.FruitDict;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * Created by wangziwen on 2017/8/24.
 */
public class FruitTask extends AbstractEntity {
    public FruitTask() {
        this.taskLevel = FruitDict.TaskDict.LOW.name();
        this.taskStatus = FruitDict.TaskDict.START.name();
    }

    private String listId;
    private String taskLevel;
    private String title;
    private String taskStatus;
    private Date startDate;
    private Date endDate;

    public void setTitle(String title) {
        this.title = title;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.setStartDate(Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.setEndDate(Date.from(endDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
    }

    public void setListId(String listId) {
        this.listId = listId;
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

    public Date getStartDate() {
        return startDate;
    }

    public String getListId() {
        return listId;
    }

    public String getTaskLevel() {
        return taskLevel;
    }

    public Date getEndDate() {
        return endDate;
    }

}
