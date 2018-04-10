package wowjoy.fruits.ms.module.list.mapper;

import org.apache.ibatis.annotations.Param;
import wowjoy.fruits.ms.module.list.FruitList;
import wowjoy.fruits.ms.module.list.FruitListExample;

import java.util.List;

public interface FruitListMapperExt {
    List<FruitList> selectByProjectId(@Param("example") FruitListExample example, @Param("projectId") String projectId);

}