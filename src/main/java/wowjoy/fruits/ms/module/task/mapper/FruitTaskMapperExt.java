package wowjoy.fruits.ms.module.task.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import wowjoy.fruits.ms.module.task.FruitTask;

/**
 * Created by wangziwen on 2017/8/31.
 */
@Mapper
public interface FruitTaskMapperExt {
    List<FruitTask> selectByUser();
}
