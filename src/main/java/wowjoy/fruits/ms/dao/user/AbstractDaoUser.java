package wowjoy.fruits.ms.dao.user;

import wowjoy.fruits.ms.dao.InterfaceDao;
import wowjoy.fruits.ms.module.user.FruitUser;
import wowjoy.fruits.ms.module.user.FruitUserEmpty;
import wowjoy.fruits.ms.util.ApplicationContextUtils;

/**
 * Created by wangziwen on 2017/8/25.
 */
public abstract class AbstractDaoUser implements InterfaceDao {

    /*********************************************************************************
     * 抽象接口，私有，因为对外的公共接口用来书写业务层，如果真的不需要业务，也可以开放为公共接口 *
     *********************************************************************************/

    protected abstract void insert(FruitUser... user);

    protected abstract FruitUser findByUser();

    /**********************************************
     *  FIELD 变量 ，尽量使用final变量、私有，编程习惯 *
     **********************************************/

    private FruitUser fruitUser;
    private ExtraDataParset dataParset;

    protected FruitUser getFruitUser() {
        return fruitUser != null ? fruitUser : FruitUserEmpty.getInstance("");
    }

    private AbstractDaoUser setFruitUser(FruitUser fruitUser) {
        this.fruitUser = fruitUser;
        return this;
    }

    /***********************
     * PUBLIC 函数，公共接口 *
     ***********************/

    public ExtraDataParset getDataParset() {
        return dataParset = ApplicationContextUtils.getContext().getBean(ExtraDataParset.class);
    }

    /**
     * 加载用户信息
     */
    public void build() {
        this.insert(this.getDataParset().build().getList().toArray(new FruitUser[this.getDataParset().getList().size()]));
    }

    /**
     * 根据用户id查询
     *
     * @param user
     * @return
     */
    public FruitUser findByUser(FruitUser user) {
        return this.setFruitUser(user).findByUser();
    }


}
