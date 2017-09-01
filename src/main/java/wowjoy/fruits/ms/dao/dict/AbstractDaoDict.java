package wowjoy.fruits.ms.dao.dict;

import wowjoy.fruits.ms.dao.InterfaceDao;
import wowjoy.fruits.ms.module.util.entity.FruitDict;

import java.util.List;

/**
 * Created by wangziwen on 2017/8/24.
 */
public abstract class AbstractDaoDict implements InterfaceDao {
    abstract List<FruitDict> find();
}
