package wowjoy.fruits.ms.dao.plan;

import java.util.List;

import org.springframework.stereotype.Service;
import wowjoy.fruits.ms.module.plan.entity.FruitPlan;

/**
 * Created by wangziwen on 2017/8/25.
 */
@Service
public class DataPlanDaoImpl extends AbstractPlan {
    private List<FruitPlan> data;

    public List<FruitPlan> finds() {
        return data;
    }
}
