package wowjoy.fruits.ms.module.team.mapper;

import org.apache.ibatis.annotations.Param;
import wowjoy.fruits.ms.module.team.FruitTeamUser;
import wowjoy.fruits.ms.module.user.example.FruitUserExample;

import java.util.List;

/**
 * Created by wangziwen on 2017/9/11.
 */
public interface FruitTeamMapperExt {

    List<FruitTeamUser> selectUserByTeamId(@Param("example") FruitUserExample userExample, @Param("teamIds") List<String> teamIds);
}
