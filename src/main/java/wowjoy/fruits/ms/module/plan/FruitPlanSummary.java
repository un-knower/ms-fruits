package wowjoy.fruits.ms.module.plan;

import wowjoy.fruits.ms.module.AbstractEntity;

/**
 * Created by wangziwen on 2017/9/11.
 */
public class FruitPlanSummary extends AbstractEntity {
    private String planId;
    private int percent;

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public int getPercent() {
        return percent;
    }

    public void setPercent(int percent) {
        this.percent = percent;
    }

    public static FruitPlanSummaryDao getDao() {
        return new FruitPlanSummaryDao();
    }

    public static FruitPlanSummaryDao newDao(String planId) {
        FruitPlanSummaryDao result = new FruitPlanSummaryDao();
        result.setPlanId(planId);
        return result;
    }

    public static FruitPlanSummaryVo getVo() {
        return new FruitPlanSummaryVo();
    }
}
