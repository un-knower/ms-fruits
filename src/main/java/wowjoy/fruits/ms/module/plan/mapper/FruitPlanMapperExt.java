package wowjoy.fruits.ms.module.plan.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import wowjoy.fruits.ms.module.plan.FruitPlan;
import wowjoy.fruits.ms.module.plan.FruitPlanDao;
import wowjoy.fruits.ms.module.plan.example.FruitPlanExample;

/**
 * Created by wangziwen on 2017/9/13.
 */
@Mapper
public interface FruitPlanMapperExt {

    List<FruitPlanDao> selectUserByExampleWithBLOBs(FruitPlanExample example);
}
