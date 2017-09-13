package wowjoy.fruits.ms.dao.team;

import wowjoy.fruits.ms.dao.InterfaceDao;
import wowjoy.fruits.ms.module.team.FruitTeam;

/**
 * Created by wangziwen on 2017/9/5.
 */
public abstract class AbstractDaoTeam<T extends FruitTeam> implements InterfaceDao {
    /*********************************************************************************
     * 抽象接口，私有，因为对外的公共接口用来书写业务层，发布api必须在自己的控制范围内，不发布无用的接口。*
     *********************************************************************************/

    protected abstract void finds();

    /**********************************************
     *  FIELD 变量 ，尽量使用final变量、私有，编程习惯 *
     **********************************************/
    private T fruitTeam;

    public T getFruitTeam() {
        return fruitTeam != null ? fruitTeam : (T) FruitTeam.newEmpty(null);
    }

    public void setFruitTeam(T fruitTeam) {
        this.fruitTeam = fruitTeam;
    }

    /*******************************
     * PUBLIC 函数，公共接口         *
     * 尽量保证规范，不直接调用dao接口 *
     *******************************/

}
