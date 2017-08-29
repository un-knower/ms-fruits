package wowjoy.fruits.ms.module.task.entity;

import wowjoy.fruits.ms.module.dict.entity.FruitDict;
import wowjoy.fruits.ms.util.AbstractEntity;

import java.util.Date;

/**
 * Created by wangziwen on 2017/8/24.
 */
public class FruitTask extends AbstractEntity {
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

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
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

    public Date getEndDate() {
        return endDate;
    }

    /**
     * task 状态管理
     */
    public enum Dict {
        START("进行中"),
        END("结束");

        private String parentCode;
        private String value;

        private void setParentCode(String parentCode) {
            this.parentCode = parentCode;
        }

        private void setValue(String value) {
            this.value = value;
        }

        public String getParentCode() {
            return parentCode;
        }

        public String getValue() {
            return value;
        }

        Dict(String value) {
            this.setParentCode(FruitDict.Dict.TASK.name());
            this.setValue(value);
        }
    }
}
