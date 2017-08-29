package wowjoy.fruits.ms.dao.plan;

import java.util.List;

import wowjoy.fruits.ms.dao.InterfaceDao;
import wowjoy.fruits.ms.module.plan.entity.FruitPlan;

/**
 * Created by wangziwen on 2017/8/25.
 */
public abstract class AbstractPlan implements InterfaceDao {
    public abstract List<FruitPlan> finds();
}
