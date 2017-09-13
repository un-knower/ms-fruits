package wowjoy.fruits.ms.dao.plan;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import wowjoy.fruits.ms.module.plan.FruitPlan;
import wowjoy.fruits.ms.module.plan.example.FruitPlanExample;
import wowjoy.fruits.ms.module.plan.mapper.FruitPlanMapper;

import java.util.List;

/**
 * Created by wangziwen on 2017/8/25.
 */
@Service
public class DataPlanDaoImpl extends AbstractDaoPlan {

    @Autowired
    private FruitPlanMapper mapper;

    @Override
    protected List<FruitPlan> finds() {
        final FruitPlanExample example = new FruitPlanExample();
        final FruitPlanExample.Criteria criteria = example.createCriteria();
        if (this.getFruitPlan().isNotEmpty()) {
            if (StringUtils.isNotBlank(this.getFruitPlan().getParentId()))
                criteria.andParentIdEqualTo(this.getFruitPlan().getParentId());
            if (StringUtils.isNotBlank(this.getFruitPlan().getTitle()))
                criteria.andTitleEqualTo(this.getFruitPlan().getTitle());
            if (StringUtils.isNotBlank(this.getFruitPlan().getPlanStatus()))
                criteria.andPlanStatusEqualTo(this.getFruitPlan().getPlanStatus());
            if (StringUtils.isNotBlank(this.getFruitPlan().getUuid()))
                criteria.andPlanStatusEqualTo(this.getFruitPlan().getUuid());
        }
        return mapper.selectByExampleWithBLOBs(example);
    }
}
