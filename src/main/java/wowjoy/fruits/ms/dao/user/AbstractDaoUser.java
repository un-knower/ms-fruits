package wowjoy.fruits.ms.dao.user;

import org.apache.commons.lang.StringUtils;
import wowjoy.fruits.ms.dao.InterfaceDao;
import wowjoy.fruits.ms.exception.CheckException;
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

    protected abstract List<FruitUserDao> finds(FruitUserDao dao);

    protected abstract void clearByUserId(String... ids);

    protected abstract List<FruitUserDao> findByAccount(FruitUserDao dao);

    /***********************
     * PUBLIC 函数，公共接口 *
     ***********************/

    public List<FruitUserDao> finds(FruitUserVo vo) {
        final FruitUserDao dao = FruitUser.getDao();
        dao.setUserName(vo.getUserName());
        return this.finds(dao);
    }

    public FruitUser finFdByAccount(FruitUserVo vo) {
        if (StringUtils.isBlank(vo.getPrincipal()))
            throw new CheckException("账户不存在");
        FruitUserDao dao = FruitUser.getDao();
        dao.setPrincipal(vo.getPrincipal());
        List<FruitUserDao> users = this.findByAccount(dao);
        if (users.isEmpty())
            throw new CheckException("账户不存在");
        return users.get(0);
    }

}
