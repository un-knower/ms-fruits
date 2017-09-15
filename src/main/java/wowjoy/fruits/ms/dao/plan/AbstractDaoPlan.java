package wowjoy.fruits.ms.dao.plan;

import java.util.List;

import wowjoy.fruits.ms.dao.InterfaceDao;
import wowjoy.fruits.ms.module.plan.FruitPlan;
import wowjoy.fruits.ms.module.plan.FruitPlanDao;
import wowjoy.fruits.ms.module.project.FruitProjectVo;

/**
 * Created by wangziwen on 2017/8/25.
 */
public abstract class AbstractDaoPlan<T extends FruitPlan> implements InterfaceDao {
    /*********************************************************************************
     * 抽象接口，私有，因为对外的公共接口用来书写业务层，发布api必须在自己的控制范围内，不发布无用的接口。*
     *********************************************************************************/

    protected abstract List<FruitPlan> finds(FruitPlanDao dao);

    /*******************************
     * PUBLIC 函数，公共接口         *
     * 尽量保证规范，不直接调用dao接口 *
     *******************************/

    public List<FruitPlan> finds(FruitProjectVo vo){
        final FruitPlanDao dao = FruitPlan.getFruitPlanDao();
        dao.setTitle(vo.getTitle());
        return this.finds(dao);
    }
}
