package wowjoy.fruits.ms.module.project.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import wowjoy.fruits.ms.module.project.FruitProjectDao;
import wowjoy.fruits.ms.module.project.FruitProjectExample;
import wowjoy.fruits.ms.module.project.FruitProjectTeam;
import wowjoy.fruits.ms.module.project.FruitProjectUser;
import wowjoy.fruits.ms.module.user.example.FruitUserExample;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangziwen on 2017/9/12.
 */
@Mapper
public interface FruitProjectMapperExt {
    ArrayList<FruitProjectUser> selectUserByProjectId(@Param("example") FruitUserExample example, @Param("projectIds") List<String> ids);

    List<FruitProjectTeam> selectTeamByProjectId(@Param("projectIds") List<String> ids);

    List<FruitProjectDao> selectByUserIdAndExample(@Param("example") FruitProjectExample example, @Param("userId") String userId);

    List<FruitProjectUser> selectAllUserByProjectId(@Param("projectId") String projectId);
}
