package wowjoy.fruits.ms.module.defect;

import wowjoy.fruits.ms.module.AbstractEntity;

public class DefectStatusCount extends AbstractEntity {
    private String defectId;

    private String defectStatus;

    private Integer count;

    public String getDefectId() {
        return defectId;
    }

    public void setDefectId(String defectId) {
        this.defectId = defectId;
    }

    public String getDefectStatus() {
        return defectStatus;
    }

    public void setDefectStatus(String defectStatus) {
        this.defectStatus = defectStatus;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}