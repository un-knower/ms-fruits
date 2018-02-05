package wowjoy.fruits.ms.module.logs.mapper;

import wowjoy.fruits.ms.module.logs.FruitLogsDao;
import wowjoy.fruits.ms.module.logs.FruitLogsExample;

import java.util.List;

/**
 * Created by wangziwen on 2017/12/25.
 */
public interface FruitLogsMapperExt {
    List<FruitLogsDao> joinUserByExample(FruitLogsExample example);
}
