package wowjoy.fruits.ms.module.logs;

import wowjoy.fruits.ms.module.AbstractEntity;
import wowjoy.fruits.ms.module.util.entity.FruitDict;

public class FruitLogs extends AbstractEntity {

    private transient String userId;

    private transient String fruitUuid;

    private transient String fruitType;

    private transient String operateType;

    private transient String jsonObject;

    public String getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(String jsonObject) {
        this.jsonObject = jsonObject;
    }

    public FruitDict.LogsDict getOperateType() {
        return FruitDict.LogsDict.valueOf(operateType);
    }

    public void setOperateType(FruitDict.LogsDict operateType) {
        this.operateType = operateType.name();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFruitUuid() {
        return fruitUuid;
    }

    public void setFruitUuid(String fruitUuid) {
        this.fruitUuid = fruitUuid;
    }

    public FruitDict.Parents getFruitType() {
        return FruitDict.Parents.valueOf(fruitType);
    }

    public void setFruitType(FruitDict.Parents fruitType) {
        this.fruitType = fruitType.name();
    }

    public static FruitLogsVo getVo() {
        return new FruitLogsVo();
    }

    public static FruitLogsDao getDao() {
        return new FruitLogsDao();
    }
}