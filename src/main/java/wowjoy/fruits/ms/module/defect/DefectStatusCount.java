package wowjoy.fruits.ms.module.defect;

import wowjoy.fruits.ms.module.AbstractEntity;
import wowjoy.fruits.ms.module.EntityUtils;
import wowjoy.fruits.ms.module.util.entity.FruitDict.DefectDict.Status;

public class DefectStatusCount extends AbstractEntity {
    private String defectId;

    private Status defectStatus;

    private String duplicateId;

    private Integer count;

    public static class Insert extends DefectStatusCount implements EntityUtils {
        public Insert() {
            this.setUuid(obtainUUID());
        }
    }

    public String getDuplicateId() {
        return duplicateId;
    }

    public void setDuplicateId(String duplicateId) {
        this.duplicateId = duplicateId;
    }

    public String getDefectId() {
        return defectId;
    }

    public void setDefectId(String defectId) {
        this.defectId = defectId;
    }

    public Status getDefectStatus() {
        return defectStatus;
    }

    public void setDefectStatus(Status defectStatus) {
        this.defectStatus = defectStatus;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}