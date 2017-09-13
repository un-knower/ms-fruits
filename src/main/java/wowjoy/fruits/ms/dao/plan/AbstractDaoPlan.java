package wowjoy.fruits.ms.dao.plan;

import java.util.List;

import wowjoy.fruits.ms.dao.InterfaceDao;
import wowjoy.fruits.ms.module.plan.FruitPlan;

/**
 * Created by wangziwen on 2017/8/25.
 */
public abstract class AbstractDaoPlan<T extends FruitPlan> implements InterfaceDao {
    /*********************************************************************************
     * 抽象接口，私有，因为对外的公共接口用来书写业务层，发布api必须在自己的控制范围内，不发布无用的接口。*
     *********************************************************************************/

    protected abstract List<FruitPlan> finds();

    /**********************************************
     *  FIELD 变量 ，尽量使用final变量、私有，编程习惯 *
     **********************************************/
    private T fruitPlan;

    public T getFruitPlan() {
        return fruitPlan != null ? fruitPlan : (T) FruitPlan.newEmpty(null);
    }

    public AbstractDaoPlan<T> setFruitPlan(T fruitPlan) {
        this.fruitPlan = fruitPlan;
        return this;
    }

    /*******************************
     * PUBLIC 函数，公共接口         *
     * 尽量保证规范，不直接调用dao接口 *
     *******************************/

    public List<FruitPlan> finds(T fruitPlan){
        return this.setFruitPlan(fruitPlan).finds();
    }
}
