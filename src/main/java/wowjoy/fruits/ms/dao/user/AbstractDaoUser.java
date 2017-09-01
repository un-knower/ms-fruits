package wowjoy.fruits.ms.dao.user;

import wowjoy.fruits.ms.dao.InterfaceDao;
import wowjoy.fruits.ms.module.user.FruitUser;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by wangziwen on 2017/8/25.
 */
public abstract class AbstractDaoUser implements InterfaceDao {

    public abstract void insert(FruitUser... user);

    public abstract LinkedList<FruitUser> build();

    public abstract List<FruitUser> findByUser(FruitUser user);

    public abstract FruitUser findByUserId(String userId);
}
