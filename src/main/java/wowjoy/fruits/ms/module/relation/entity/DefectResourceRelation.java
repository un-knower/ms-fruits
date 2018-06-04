package wowjoy.fruits.ms.module.relation.entity;

import wowjoy.fruits.ms.module.AbstractEntity;
import wowjoy.fruits.ms.module.util.entity.FruitDict;
import wowjoy.fruits.ms.module.util.entity.FruitDict.DefectDict;

public class DefectResourceRelation extends AbstractEntity {
    private String defectId;
    private String resourceId;
    private FruitDict.Resource drType;

    public static class Update extends DefectResourceRelation {
        public Update() {
            setUuid(null);
        }
    }

    public String getDefectId() {
        return defectId;
    }

    public void setDefectId(String defectId) {
        this.defectId = defectId;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public FruitDict.Resource getDrType() {
        return drType;
    }

    public void setDrType(FruitDict.Resource drType) {
        this.drType = drType;
    }
}