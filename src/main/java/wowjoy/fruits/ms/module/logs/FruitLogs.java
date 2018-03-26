package wowjoy.fruits.ms.module.logs;

import com.google.common.reflect.TypeToken;
import wowjoy.fruits.ms.module.AbstractEntity;
import wowjoy.fruits.ms.module.util.entity.FruitDict;
import wowjoy.fruits.ms.util.GsonUtils;

public class FruitLogs extends AbstractEntity {

    private transient String userId;

    private transient String fruitUuid;

    private transient String fruitType;

    private transient String operateType;

    private transient String jsonObject;

    private transient String voObject;

    public String getVoObject() {
        return voObject;
    }

    public void setVoObject(String voObject) {
        this.voObject = voObject;
    }

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

    public static class Info extends FruitLogs {
        public Info() {
            setUuid(null);
            setIsDeleted(null);
        }

        private String msg;

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }
    }

    public Info toInfo() {
        Info info = GsonUtils.newGson().fromJson(GsonUtils.newGson().toJsonTree(this), TypeToken.of(Info.class).getType());
        info.setFruitUuid(this.getFruitUuid());
        return info;
    }

    public static Info newInfo() {
        return new Info();
    }
}