package wowjoy.fruits.ms.module.plan.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import wowjoy.fruits.ms.module.plan.FruitPlanDao;
import wowjoy.fruits.ms.module.plan.example.FruitPlanExample;

import java.util.List;

/**
 * Created by wangziwen on 2017/9/13.
 */
@Mapper
public interface FruitPlanMapperExt {

    List<FruitPlanDao> selectUserByProject(@Param("example") FruitPlanExample example, @Param("projectId") String projectId);
}
