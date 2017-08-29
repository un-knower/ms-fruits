package wowjoy.fruits.ms.module.plan.entity;

import wowjoy.fruits.ms.module.dict.entity.FruitDict;
import wowjoy.fruits.ms.util.AbstractEntity;

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

    /**
     * plan 状态管理
     */
    public enum Dict {
        //进行中
        PENDING("进行中"),
        //准时完成
        COMPLETE("准时完成"),
        //延期完成
        OUTTIMECOMPLET("延期完成"),
        //终止
        END("以终止");

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
            this.setParentCode(FruitDict.Dict.PLAN.name());
            this.setValue(value);
        }
    }
}
