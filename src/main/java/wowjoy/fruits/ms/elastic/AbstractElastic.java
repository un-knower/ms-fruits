package wowjoy.fruits.ms.elastic;

import wowjoy.fruits.ms.module.util.entity.FruitDict;

/**
 * Created by wangziwen on 2017/9/26.
 */
public abstract class AbstractElastic {
    protected final String Index = FruitDict.Dict.MS_FRUITS.name().toLowerCase();
    protected final String Type = FruitDict.ESType.PROJECT.name().toLowerCase();
}
