package wowjoy.fruits.ms.module;

import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import wowjoy.fruits.ms.module.util.entity.FruitDict;

import java.text.MessageFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 实体类基类
 */
public abstract class AbstractEntity implements InterfaceEntity {
    protected transient final String DateTimeFormat = "yyyy-MM-dd'T'23:59:59";

    protected AbstractEntity() {
        /*利大于弊，我选择保留，如果有更好的方法，可以尝试*/
        /*2018年03月09日11:17:30：内心惭愧，扎根太深一次无法拔出。目前打算使用内部类的方式代替全局UUID，以后的所有功能维护尽量不依赖全局UUID*/
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

    private transient String desc;
    private transient String asc;

    public void setCreateDateTime(Date createDateTime) {
        this.createDateTime = createDateTime;
    }

    private transient Supplier<FruitDict.LogsDict> operateTypeSupplier;

    public Supplier<FruitDict.LogsDict> getOperateTypeSupplier() {
        return operateTypeSupplier;
    }

    public void setOperateTypeSupplier(Supplier<FruitDict.LogsDict> operateTypeSupplier) {
        this.operateTypeSupplier = operateTypeSupplier;
    }

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

    public String sortConstrue(String prefix) {
        LinkedList<String> sorts = Lists.newLinkedList();
        if (StringUtils.isNotBlank(this.getDesc())) for (String desc : this.getDesc().split(","))
            sorts.add(MessageFormat.format("{0}{1} desc", prefix, toMysqlField(desc)));
        if (StringUtils.isNotBlank(this.getAsc())) for (String asc : this.getAsc().split(","))
            sorts.add(MessageFormat.format("{0}{1} asc", prefix, toMysqlField(asc)));
        if (sorts.isEmpty()) return null;
        return StringUtils.join(sorts, ",");
    }

    public String sortConstrue() {
        return sortConstrue("");
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
