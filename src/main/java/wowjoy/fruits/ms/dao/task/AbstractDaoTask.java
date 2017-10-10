package wowjoy.fruits.ms.dao.task;

import wowjoy.fruits.ms.dao.InterfaceDao;
import wowjoy.fruits.ms.exception.CheckException;
import wowjoy.fruits.ms.module.task.FruitTask;
import wowjoy.fruits.ms.module.task.FruitTaskDao;
import wowjoy.fruits.ms.module.task.FruitTaskVo;
import wowjoy.fruits.ms.module.util.entity.FruitDict;

/**
 * Created by wangziwen on 2017/8/30.
 */
public abstract class AbstractDaoTask implements InterfaceDao {
    /*********************************************************************************
     * 抽象接口，私有，因为对外的公共接口用来书写业务层，发布api必须在自己的控制范围内，不发布无用的接口。*
     *********************************************************************************/

    /**
     * 添加任务
     * 1、添加任务
     * 2、添加关联计划
     * 3、添加关联项目
     *
     * @param dao
     */
    protected abstract void insert(FruitTaskDao dao);

    /*******************************
     * PUBLIC 函数，公共接口         *
     * 尽量保证规范，不直接调用dao接口 *
     *******************************/

    public final void insert(FruitTaskVo vo) {
        FruitTaskDao dao = FruitTask.getDao();
        dao.setUuid(vo.getUuid());
        dao.setTitle(vo.getTitle());
        dao.setTaskLevel(vo.getTaskLevel());
        dao.setTaskStatus(FruitDict.TaskDict.START.name());
        dao.setEstimatedEndDate(vo.getEstimatedEndDate());
        dao.setDescription(vo.getDescription());
        /*如果无关联计划，默认绑定关联项目*/
        if (vo.getTaskPlanRelation() != null && vo.getTaskPlanRelation().containsKey(FruitDict.Dict.ADD))
            dao.setTaskPlanRelation(vo.getTaskPlanRelation());
        else if (vo.getTaskProjectRelation() != null && vo.getTaskProjectRelation().containsKey(FruitDict.Dict.ADD))
            dao.setTaskProjectRelation(vo.getTaskProjectRelation());
        else
            throw new CheckException("无法确认任务去向，请指明至少一条关联渠道");
        this.insert(dao);
    }

}

