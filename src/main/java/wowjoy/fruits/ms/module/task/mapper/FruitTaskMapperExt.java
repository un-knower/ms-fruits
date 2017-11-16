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
    List<FruitTaskDao> selectByTaskList(@Param("id") String id);

    List<FruitTaskDao> selectByTaskPlan(@Param("id") String id);

    List<FruitTaskDao> selectUserByTask(@Param("example") FruitTaskExample example);

    List<FruitTaskDao> selectPlanByTask(@Param("example") FruitTaskExample example);

    /************************************************************************************************
     *                                       个人中心专供                                            *
     ************************************************************************************************/
    List<FruitTaskDao> userSelectByExample(@Param("example") FruitTaskExample example, @Param("userId") String userId);

}
