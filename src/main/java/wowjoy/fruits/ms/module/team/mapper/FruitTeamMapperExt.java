package wowjoy.fruits.ms.module.team.mapper;

import org.apache.ibatis.annotations.Param;
import wowjoy.fruits.ms.module.team.FruitTeamDao;
import wowjoy.fruits.ms.module.team.FruitTeamExample;
import wowjoy.fruits.ms.module.user.example.FruitUserExample;

import java.util.List;

/**
 * Created by wangziwen on 2017/9/11.
 */
public interface FruitTeamMapperExt {

    List<FruitTeamDao> selectRelationByExample(@Param("example") FruitTeamExample example, @Param("exampleUser") FruitUserExample exampleUser);
}
