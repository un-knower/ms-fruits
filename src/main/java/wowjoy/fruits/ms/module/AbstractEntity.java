package wowjoy.fruits.ms.module;

import com.google.common.reflect.TypeToken;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import wowjoy.fruits.ms.dao.InterfaceArgument;
import wowjoy.fruits.ms.module.util.entity.FruitDict;

import java.util.Date;
import java.util.UUID;

/**
 * 实体类基类
 */
public abstract class AbstractEntity extends InterfaceArgument {
    protected AbstractEntity() {
        this.setUuid(UUID.randomUUID().toString().replace("-", ""));
        this.setIsDeleted(FruitDict.Dict.N.name());
    }
    private String uuid;
    private Date modifyDateTime;
    private Date createDateTime;
    private String isDeleted;
    private String description;

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setIsDeleted(String isDeleted) {
        this.isDeleted = isDeleted;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getUuid() {
        return uuid;
    }

    public Date getModifyDateTime() {
        return modifyDateTime;
    }

    public Date getCreateDateTime() {
        return createDateTime;
    }

    public String getIsDeleted() {
        return isDeleted;
    }

}
