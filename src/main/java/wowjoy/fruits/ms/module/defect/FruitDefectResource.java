package wowjoy.fruits.ms.module.defect;

import wowjoy.fruits.ms.module.resource.FruitResource;

/**
 * Created by ${汪梓文} on ${2018年03月20日15:45:02}.
 */
public class FruitDefectResource extends FruitResource {
    private String drType;
    private String defectId;

    public String getDrType() {
        return drType;
    }

    public void setDrType(String drType) {
        this.drType = drType;
    }

    public String getDefectId() {
        return defectId;
    }

    public void setDefectId(String defectId) {
        this.defectId = defectId;
    }
}
