package wowjoy.fruits.ms.module.team.mapper;

import wowjoy.fruits.ms.module.team.FruitTeamDao;
import wowjoy.fruits.ms.module.team.FruitTeamExample;

import java.util.List;

/**
 * Created by wangziwen on 2017/9/11.
 */
public interface FruitTeamMapperExt {

    List<FruitTeamDao> selectUserRelationByExample(FruitTeamExample example);
}
