package wowjoy.fruits.ms.dao.project;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import wowjoy.fruits.ms.controller.vo.FruitProjectVo;
import wowjoy.fruits.ms.dao.InterfaceDao;
import wowjoy.fruits.ms.exception.CheckException;
import wowjoy.fruits.ms.module.project.FruitProject;
import wowjoy.fruits.ms.module.util.entity.FruitDict;

/**
 * Created by wangziwen on 2017/9/6.
 */
public abstract class AbstractDaoProject<T extends FruitProject> implements InterfaceDao {
    /*********************************************************************************
     * 抽象接口，私有，因为对外的公共接口用来书写业务层，发布api必须在自己的控制范围内，不发布无用的接口。*
     *********************************************************************************/

    /**
     * 1、添加项目信息
     * 2、添加团队关联
     * 3、添加用户关联
     */
    protected abstract void insert();

    /**
     * 查询项目信息，支持查询条件：
     * 1、根据标题查询
     * 2、根据项目状态查询
     */
    protected abstract List<FruitProject> finds();

    protected abstract void update();

    protected abstract void updateStatus();

    /**********************************************
     *  FIELD 变量 ，尽量使用final变量、私有，编程习惯 *
     **********************************************/

    private T fruitProject;

    private AbstractDaoProject setFruitProject(T fruitProject) {
        this.fruitProject = fruitProject;
        return this;
    }

    protected T getFruitProject() {
        return fruitProject != null ? fruitProject : (T) FruitProject.newEmpty(null);
    }

    /*******************************
     * PUBLIC 函数，公共接口         *
     * 尽量保证规范，不直接调用dao接口 *
     *******************************/

    public void insert(T project) {
        project.setProjectStatus(FruitDict.ProjectDict.UNDERWAY.name());
        this.setFruitProject(project).insert();
    }

    public List<FruitProject> finds(T project) {
        final List<FruitProject> data = this.setFruitProject(project).finds();
        return data;
    }

    public FruitProject findByUUID(String uuid) {
        final FruitProject projet = FruitProject.getInstance();
        projet.setUuid(uuid);
        final List data = this.setFruitProject((T) projet).finds();
        if (data.isEmpty())
            throw new CheckProjectException("【findByUUID】无匹配信息");
        return (FruitProject) data.get(0);
    }

    public void update(T fruitProject) {
        this.setFruitProject(fruitProject).update();
        this.finds();
    }

    public void updateStatus(String uuid, FruitDict.ProjectDict projectDict) {
        if (StringUtils.isBlank(uuid))
            throw new CheckProjectException("【uuid】无效。");
        final FruitProject data = FruitProjectVo.getInstance();
        data.setUuid(uuid);
        data.setProjectStatus(projectDict.name());
        this.setFruitProject((T) data).updateStatus();
    }

    /*******************************************************
     * 仅用于实现类。内部类尽量采用static，非静态类不利于垃圾回收。 *
     *******************************************************/
    protected static class CheckProjectException extends CheckException {
        public CheckProjectException(String message) {
            super("【Project exception】" + message);
        }
    }

}
