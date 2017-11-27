package wowjoy.fruits.ms.dao.plan;

import org.apache.commons.lang.StringUtils;
import wowjoy.fruits.ms.dao.AbstractDaoChain;
import wowjoy.fruits.ms.module.AbstractEntity;
import wowjoy.fruits.ms.module.plan.FruitPlan;
import wowjoy.fruits.ms.module.plan.FruitPlanDao;
import wowjoy.fruits.ms.module.util.entity.FruitDict;
import wowjoy.fruits.ms.util.ApplicationContextUtils;

/**
 * Created by wangziwen on 2017/11/27.
 */
public class PlanDaoNode extends AbstractDaoChain {
    private AbstractDaoPlan planDao = ApplicationContextUtils.getContext().getBean(PlanDaoImpl.class);

    public PlanDaoNode(FruitDict.Parents type) {
        super(type);
    }

    @Override
    public AbstractEntity find(String uuid) {
        if (!super.type.name().equals(FruitDict.Parents.PLAN.name()))
            return super.getNext().find(uuid);
        if (StringUtils.isBlank(uuid))
            return null;
        FruitPlanDao dao = FruitPlan.getDao();
        dao.setUuid(uuid);
        FruitPlan data = planDao.find(dao);
        if (!data.isNotEmpty()) return null;
        return data;
    }
}
