package wowjoy.fruits.ms.dao.project;

import java.util.List;

import wowjoy.fruits.ms.dao.InterfaceDao;
import wowjoy.fruits.ms.module.project.FruitProject;
import wowjoy.fruits.ms.module.project.FruitProjectEmpty;

/**
 * Created by wangziwen on 2017/9/6.
 */
public abstract class AbstractDaoProject implements InterfaceDao {
    /*********************************************************************************
     * 抽象接口，私有，因为对外的公共接口用来书写业务层，如果真的不需要业务，也可以开放为公共接口 *
     *********************************************************************************/

    protected abstract void insert();

    protected abstract List<FruitProject> findByProject();

    /**********************************************
     *  FIELD 变量 ，尽量使用final变量、私有，编程习惯 *
     **********************************************/

    private FruitProject fruitProject;

    private AbstractDaoProject setFruitProject(FruitProject fruitProject) {
        this.fruitProject = fruitProject;
        return this;
    }

    protected FruitProject getFruitProject() {
        return fruitProject != null ? fruitProject : FruitProjectEmpty.getInstance(null);
    }

    /***********************
     * PUBLIC 函数，公共接口 *
     ***********************/

    /**
     * 尽量保证规范，不直接调用dao接口
     *
     * @param project
     */
    public void insert(FruitProject project) {
        this.setFruitProject(project).insert();
    }

    public List<FruitProject> findByProject(FruitProject project) {
        final List<FruitProject> data = this.setFruitProject(project).findByProject();
        data.forEach((i) -> {

        });
        return data;
    }
}
