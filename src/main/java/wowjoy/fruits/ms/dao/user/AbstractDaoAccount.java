package wowjoy.fruits.ms.dao.user;

import org.apache.commons.lang.StringUtils;
import wowjoy.fruits.ms.dao.InterfaceDao;
import wowjoy.fruits.ms.exception.CheckException;
import wowjoy.fruits.ms.module.user.FruitAccount;
import wowjoy.fruits.ms.module.user.FruitAccountDao;
import wowjoy.fruits.ms.module.user.FruitAccountVo;

import java.util.List;

/**
 * Created by wangziwen on 2017/10/26.
 */
public abstract class AbstractDaoAccount implements InterfaceDao {
    /*********************************************************************************
     * 抽象接口，私有，因为对外的公共接口用来书写业务层，如果真的不需要业务，也可以开放为公共接口 *
     *********************************************************************************/
    protected abstract void inserts(List<FruitAccountDao> accountDaos);

    protected abstract List<FruitAccountDao> finds(FruitAccountDao dao);

    protected abstract void clearByUserId(String... ids);

    /***********************
     * PUBLIC 函数，公共接口 *
     ***********************/

    /**
     * 查询账号是否存在
     * 返回账号对应的关联用户
     *
     * @param vo
     * @return
     */
    public List<FruitAccountDao> findByAccount(FruitAccountVo vo) {
        if (StringUtils.isBlank(vo.getPrincipal()))
            throw new CheckException("账号密码不存在");
        FruitAccountDao dao = FruitAccount.getDao();
        dao.setPrincipal(vo.getPrincipal());
        return finds(dao);
    }

}
