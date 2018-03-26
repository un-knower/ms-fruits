package wowjoy.fruits.ms.module.plan;

import wowjoy.fruits.ms.module.task.FruitTaskDao;

/**
 * Created by wangziwen on 2018/3/16.
 */
public class FruitPlanTask extends FruitTaskDao {
    public FruitPlanTask() {
        setUuid(null);
        setIsDeleted(null);
    }

    private transient String planId;

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }
}
