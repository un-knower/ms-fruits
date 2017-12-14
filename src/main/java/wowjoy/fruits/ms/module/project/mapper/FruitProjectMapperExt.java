package wowjoy.fruits.ms.module.project.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import wowjoy.fruits.ms.module.project.FruitProjectDao;
import wowjoy.fruits.ms.module.project.FruitProjectExample;

import java.util.List;

/**
 * Created by wangziwen on 2017/9/12.
 */
@Mapper
public interface FruitProjectMapperExt {
    @Deprecated
    List<FruitProjectDao> selectUserRelationByExample(@Param("example") FruitProjectExample example);

    List<FruitProjectDao> selectUserByProjectId(@Param("projectIds") String... ids);

    List<FruitProjectDao> selectTeamByProjectId(@Param("projectIds") String... ids);

    List<FruitProjectDao> selectCurrentUserByExample(@Param("example") FruitProjectExample example, @Param("userId") String userId);
}
