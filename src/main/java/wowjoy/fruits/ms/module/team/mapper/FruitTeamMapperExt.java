package wowjoy.fruits.ms.module.team.mapper;

import org.apache.ibatis.annotations.Param;
import wowjoy.fruits.ms.module.team.FruitTeamDao;

import java.util.List;

/**
 * Created by wangziwen on 2017/9/11.
 */
public interface FruitTeamMapperExt {

    List<FruitTeamDao> selectUserByTeamId(@Param("ids") List<String> ids);
}
