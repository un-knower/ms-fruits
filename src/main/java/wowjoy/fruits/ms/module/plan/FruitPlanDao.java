package wowjoy.fruits.ms.module.plan;

import wowjoy.fruits.ms.module.relation.entity.PlanUserRelation;

import java.util.Date;
import java.util.List;

/**
 * Created by wangziwen on 2017/9/15.
 */
public class FruitPlanDao extends FruitPlan {

    public FruitPlanDao() {
        setUuid(null);
    }

    private Date startDateDao;
    private Date endDateDao;
    private List<PlanUserRelation> userRelation;

    public List<PlanUserRelation> getUserRelation() {
        return userRelation;
    }

    public void setUserRelation(List<PlanUserRelation> userRelation) {
        this.userRelation = userRelation;
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
