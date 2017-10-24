package wowjoy.fruits.ms.module.task.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import wowjoy.fruits.ms.module.task.FruitTaskDao;
import wowjoy.fruits.ms.module.task.FruitTaskExample;

import java.util.List;

/**
 * Created by wangziwen on 2017/8/31.
 */
@Mapper
public interface FruitTaskMapperExt {
    List<FruitTaskDao> selectProjectByExample(@Param("example") FruitTaskExample example, @Param("listIds") List<String> listIds, @Param("projectIds") List<String> projectIds);

    List<FruitTaskDao> selectPlanByExample(@Param("example") FruitTaskExample example, @Param("listIds") List<String> listIds, @Param("planIds") List<String> planIds);
}
