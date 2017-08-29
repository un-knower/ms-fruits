package wowjoy.fruits.ms.dao.user;

import wowjoy.fruits.ms.module.user.entity.FruitUser;

import java.util.List;

/**
 * Created by wangziwen on 2017/8/25.
 */
public abstract class AbstractUser {

    public abstract List<FruitUser> findByUser(FruitUser user);
}
