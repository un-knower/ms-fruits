package wowjoy.fruits.ms.module.task.mapper;

import org.apache.ibatis.annotations.Mapper;
import wowjoy.fruits.ms.module.task.FruitTaskDao;
import wowjoy.fruits.ms.module.task.FruitTaskExample;

import java.util.List;

/**
 * Created by wangziwen on 2017/8/31.
 */
@Mapper
public interface FruitTaskMapperExt {
    List<FruitTaskDao> relationProjectByExample(FruitTaskExample example);
    List<FruitTaskDao> relationPlanByExample(FruitTaskExample example);
}
