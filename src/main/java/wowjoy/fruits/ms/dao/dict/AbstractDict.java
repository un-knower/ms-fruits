package wowjoy.fruits.ms.dao.dict;

import wowjoy.fruits.ms.dao.InterfaceDao;
import wowjoy.fruits.ms.module.dict.entity.FruitDict;

import java.util.List;

/**
 * Created by wangziwen on 2017/8/24.
 */
public abstract class AbstractDict implements InterfaceDao {
    abstract List<FruitDict> find();
}
