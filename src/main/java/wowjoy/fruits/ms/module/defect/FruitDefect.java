package wowjoy.fruits.ms.module.defect;

import wowjoy.fruits.ms.module.AbstractEntity;

import java.util.Date;

public class FruitDefect extends AbstractEntity {
    private Integer number;

    private String projectId;

    private String beforVersionsId;

    private String afterVersionsId;

    private String defectName;

    private String userId;

    private String handlerUserId;

    private String defectType;

    private String defectLevel;

    private String riskIndex;

    private String defectStatus;

    private Date endDateTime;

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getBeforVersionsId() {
        return beforVersionsId;
    }

    public void setBeforVersionsId(String beforVersionsId) {
        this.beforVersionsId = beforVersionsId;
    }

    public String getAfterVersionsId() {
        return afterVersionsId;
    }

    public void setAfterVersionsId(String afterVersionsId) {
        this.afterVersionsId = afterVersionsId;
    }

    public String getDefectName() {
        return defectName;
    }

    public void setDefectName(String defectName) {
        this.defectName = defectName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getHandlerUserId() {
        return handlerUserId;
    }

    public void setHandlerUserId(String handlerUserId) {
        this.handlerUserId = handlerUserId;
    }

    public String getDefectType() {
        return defectType;
    }

    public void setDefectType(String defectType) {
        this.defectType = defectType;
    }

    public String getDefectLevel() {
        return defectLevel;
    }

    public void setDefectLevel(String defectLevel) {
        this.defectLevel = defectLevel;
    }

    public String getRiskIndex() {
        return riskIndex;
    }

    public void setRiskIndex(String riskIndex) {
        this.riskIndex = riskIndex;
    }

    public String getDefectStatus() {
        return defectStatus;
    }

    public void setDefectStatus(String defectStatus) {
        this.defectStatus = defectStatus;
    }

    public Date getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(Date endDateTime) {
        this.endDateTime = endDateTime;
    }
}