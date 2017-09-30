package wowjoy.fruits.ms.module.plan;

import com.google.common.collect.Lists;
import wowjoy.fruits.ms.module.user.FruitUserDao;
import wowjoy.fruits.ms.module.util.entity.FruitDict;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by wangziwen on 2017/9/15.
 */
public class FruitPlanDao extends FruitPlan {

    public FruitPlanDao() {
        setUuid(null);
    }

    private Date startDateDao;
    private Date endDateDao;
    private List<FruitUserDao> users;
    private Map<String, List<String>> userRelation;
    private Map<String, List<String>> projectRelation;

    public List<String> getUserRelation(FruitDict.Dict type) {
        return userRelation != null && userRelation.containsKey(type.name().toLowerCase()) ? userRelation.get(type.name().toLowerCase()) : Lists.newLinkedList();
    }

    public void setUserRelation(Map<String, List<String>> userRelation) {
        this.userRelation = userRelation;
    }

    public List<String> getProjectRelation(FruitDict.Dict type) {
        return userRelation != null && projectRelation.containsKey(type.name().toLowerCase()) ? projectRelation.get(type.name().toLowerCase()) : Lists.newLinkedList();
    }

    public void setProjectRelation(Map<String, List<String>> projectRelation) {
        this.projectRelation = projectRelation;
    }

    public List<FruitUserDao> getUsers() {
        return users;
    }

    public void setUsers(List<FruitUserDao> users) {
        this.users = users;
    }

    public Date getStartDateDao() {
        return startDateDao;
    }

    public void setStartDateDao(Date startDateDao) {
        this.startDateDao = startDateDao;
    }

    public Date getEndDateDao() {
        return endDateDao;
    }

    public void setEndDateDao(Date endDateDao) {
        this.endDateDao = endDateDao;
    }
}
