package wowjoy.fruits.ms.module.logs;

import wowjoy.fruits.ms.module.AbstractEntity;
import wowjoy.fruits.ms.module.util.entity.FruitDict;

public class FruitLogs extends AbstractEntity {

    private String content;

    private String userId;

    private String fruitUuid;

    private String fruitType;

    private String operateType;

    public FruitDict.Systems getOperateType() {
        return FruitDict.Systems.valueOf(operateType);
    }

    public void setOperateType(FruitDict.Systems operateType) {
        this.operateType = operateType.name();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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