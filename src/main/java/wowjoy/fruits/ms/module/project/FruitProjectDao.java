package wowjoy.fruits.ms.module.project;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import wowjoy.fruits.ms.module.relation.entity.ProjectTeamRelation;
import wowjoy.fruits.ms.module.relation.entity.UserProjectRelation;
import wowjoy.fruits.ms.module.user.FruitUserDao;
import wowjoy.fruits.ms.module.util.entity.FruitDict;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Dao层实体类
 */
public class FruitProjectDao extends FruitProject {

    private final String DateTimeFormat = "yyyy-MM-dd'T'23:59:59";

    protected FruitProjectDao() {
        setUuid(null);
    }

    private Map<String, List<ProjectTeamRelation>> teamRelation;
    private Map<String, List<UserProjectRelation>> userRelation;

    private List<FruitUserDao> users;
    private FruitUserDao principal;

    private FruitDict.Dict isPostponed = FruitDict.Dict.N;
    private Integer days;

    public List<ProjectTeamRelation> getTeamRelation(FruitDict.Dict type) {
        return teamRelation != null && teamRelation.containsKey(type.name().toLowerCase()) ? teamRelation.get(type.name().toLowerCase()) : Lists.newLinkedList();
    }

    public void setTeamRelation(Map<String, List<ProjectTeamRelation>> teamRelation) {
        this.teamRelation = teamRelation;
    }

    public List<UserProjectRelation> getUserRelation(FruitDict.Dict type) {
        return userRelation != null && userRelation.containsKey(type.name().toLowerCase()) ? userRelation.get(type.name().toLowerCase()) : Lists.newLinkedList();
    }

    public void setUserRelation(Map<String, List<UserProjectRelation>> userRelation) {
        this.userRelation = userRelation;
    }

    public FruitDict.Dict getIsPostponed() {
        return isPostponed;
    }

    public void setIsPostponed(FruitDict.Dict isPostponed) {
        this.isPostponed = isPostponed;
    }

    public Integer getDays() {
        return days;
    }

    public void setDays(Integer days) {
        this.days = days;
    }

    public List<FruitUserDao> getUsers() {
        return users;
    }

    public void setUsers(List<FruitUserDao> users) {
        this.users = users;
    }

    @Override
    public void setTitle(String title) {
        if (StringUtils.isNotBlank(title))
            super.setTitle(title);
    }

    @Override
    public void setProjectStatus(String projectStatus) {
        super.setProjectStatus(projectStatus);
        if (FruitDict.ProjectDict.COMPLETE.equals(projectStatus))
            this.setEndDateTime(java.sql.Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()));
    }

    public FruitUserDao getPrincipal() {
        return principal;
    }

    public void setPrincipal(FruitUserDao principal) {
        this.principal = principal;
    }

    /**
     * 项目工具函数
     */

    /**
     * 是否延期
     * 延期天数
     */
    public void computeDays() {
        LocalDateTime predictEndTime = LocalDateTime.parse(new SimpleDateFormat(DateTimeFormat).format(this.getPredictEndDate()));
        LocalDateTime currentTime = LocalDateTime.parse(new SimpleDateFormat(DateTimeFormat).format(new Date()));
        Duration between = Duration.between(currentTime, predictEndTime);
        if (between.toDays() > 0) {
            this.setIsPostponed(FruitDict.Dict.N);
            this.setDays(Math.abs((int) between.toDays()));
        } else {
            this.setIsPostponed(FruitDict.Dict.Y);
            this.setDays((int) Math.abs(between.toDays()));
        }
    }

    /**
     * 查找负责人
     */
    public void seekPrincipal() {
        this.getUsers().forEach((user) -> {
            if (FruitDict.UserProjectDict.PRINCIPAL.name().equals(user.getProjectRole()))
                this.setPrincipal(user);
        });
    }
}
