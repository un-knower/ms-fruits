package wowjoy.fruits.ms.util;

import java.util.Date;
import java.util.UUID;

/**
 * 实体类基类
 */
public abstract class AbstractEntity {
    protected AbstractEntity() {
        this.setUuid(UUID.randomUUID().toString().replace("-", ""));
        this.setIsDeleted(CommonEnum.N.name());
    }

    private String uuid;
    private Date modifyDateTime;
    private Date createDateTime;
    private String isDeleted;
    private String description;

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setModifyDateTime(Date modifyDateTime) {
        this.modifyDateTime = modifyDateTime;
    }

    public void setCreateDateTime(Date createDateTime) {
        this.createDateTime = createDateTime;
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
