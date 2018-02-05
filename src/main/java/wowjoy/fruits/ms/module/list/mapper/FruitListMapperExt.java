package wowjoy.fruits.ms.module.list.mapper;

import org.apache.ibatis.annotations.Param;
import wowjoy.fruits.ms.module.list.FruitListDao;
import wowjoy.fruits.ms.module.list.FruitListExample;

import java.util.List;

public interface FruitListMapperExt {
    List<FruitListDao> selectByProjectId(@Param("example") FruitListExample example, @Param("projectId") String projectId);

}