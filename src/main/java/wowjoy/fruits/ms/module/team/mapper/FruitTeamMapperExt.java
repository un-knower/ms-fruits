package wowjoy.fruits.ms.module.team.mapper;

import java.util.List;
import wowjoy.fruits.ms.module.team.FruitTeam;
import wowjoy.fruits.ms.module.team.FruitTeamExample;

/**
 * Created by wangziwen on 2017/9/11.
 */
public interface FruitTeamMapperExt {

    List<FruitTeam> selectUserRelationByExample(FruitTeamExample example);
}
