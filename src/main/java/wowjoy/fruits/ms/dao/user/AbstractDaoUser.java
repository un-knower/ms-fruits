package wowjoy.fruits.ms.dao.user;

import wowjoy.fruits.ms.dao.InterfaceDao;
import wowjoy.fruits.ms.module.user.FruitUser;
import wowjoy.fruits.ms.module.user.FruitUserDao;
import wowjoy.fruits.ms.module.user.FruitUserVo;

import java.util.List;

/**
 * Created by wangziwen on 2017/8/25.
 */
public abstract class AbstractDaoUser implements InterfaceDao {

    /*********************************************************************************
     * 抽象接口，私有，因为对外的公共接口用来书写业务层，如果真的不需要业务，也可以开放为公共接口 *
     *********************************************************************************/

    protected abstract void insert(FruitUser... user);

    protected abstract List<FruitUser> finds(FruitUserDao dao);

    /***********************
     * PUBLIC 函数，公共接口 *
     ***********************/

    public List<FruitUser> finds(FruitUserVo vo) {
        final FruitUserDao dao = FruitUser.getFruitUserDao();
        dao.setUserEmail(vo.getUserEmail());
        dao.setUserName(vo.getUserName());
        return this.finds(dao);
    }


}
