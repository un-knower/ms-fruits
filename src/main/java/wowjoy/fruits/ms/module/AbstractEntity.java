package wowjoy.fruits.ms.module;

import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import wowjoy.fruits.ms.exception.CheckException;
import wowjoy.fruits.ms.exception.NullException;
import wowjoy.fruits.ms.module.plan.FruitPlanDao;
import wowjoy.fruits.ms.module.util.entity.FruitDict;

import java.text.MessageFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 实体类基类
 */
public abstract class AbstractEntity implements InterfaceEntity {
    protected transient final String DateTimeFormat = "yyyy-MM-dd'T'23:59:59";

    protected AbstractEntity() {
        /*利大于弊，我选 择保留，如果有更好的方法，可以尝试*/
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

    private String desc;
    private String asc;

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getAsc() {
        return asc;
    }

    public void setAsc(String asc) {
        this.asc = asc;
    }

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

    /**/
    public String sortConstrue() {
        LinkedList<String> sorts = Lists.newLinkedList();
        if (StringUtils.isNotBlank(this.getDesc())) for (String desc : this.getDesc().split(","))
            sorts.add(MessageFormat.format("{0} desc", toMysqlField(desc)));
        if (StringUtils.isNotBlank(this.getAsc())) for (String asc : this.getAsc().split(","))
            sorts.add(MessageFormat.format("{0} asc", toMysqlField(asc)));
        if (sorts.isEmpty()) return null;
        return StringUtils.join(sorts, ",");
    }

    private String toMysqlField(String field) {
        String replace = "\\.*[A-Z]";
        Pattern compile = Pattern.compile(replace);
        Matcher matcher = compile.matcher(field);
        StringBuffer result = new StringBuffer();
        Integer lastEnd = 0;
        while (matcher.find()) {
            result.append(field.substring(result.length() > 0 ? result.length() - 1 : 0, matcher.start()) + "_").append(field.substring(matcher.start(), matcher.end()));
            lastEnd = matcher.end();
        }
        result.append(field.substring(lastEnd, field.length()));
        return result.toString();
    }
}
