package wowjoy.fruits.ms.module;

import wowjoy.fruits.ms.exception.CheckException;
import wowjoy.fruits.ms.exception.NullException;
import wowjoy.fruits.ms.module.util.entity.FruitDict;

import java.util.Date;
import java.util.UUID;

/**
 * 实体类基类
 */
public abstract class AbstractEntity implements InterfaceEntity {
    protected transient final String DateTimeFormat = "yyyy-MM-dd'T'23:59:59";

    protected AbstractEntity() {
        /*利大于弊，我选择保留，如果有更好的方法，可以尝试*/
        this.setUuid(UUID());
        this.setIsDeleted(FruitDict.Systems.N.name());
    }

    public static String UUID() {
        return UUID.randomUUID().toString().replace("-", "");
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

    /****************
     * 实体类异常内部类 *
     ****************/

    protected static class CheckEntityException extends CheckException {
        public CheckEntityException(String message) {
            super("【Entity exception】：" + message);
        }
    }

    protected static class NullEntityException extends NullException {
        public NullEntityException(String message) {
            super("【Entity exception】：" + message);
        }
    }
}
