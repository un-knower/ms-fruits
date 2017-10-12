package wowjoy.fruits.ms.dao.task;

import org.apache.commons.lang.StringUtils;
import wowjoy.fruits.ms.dao.InterfaceDao;
import wowjoy.fruits.ms.exception.CheckException;
import wowjoy.fruits.ms.module.relation.entity.TaskPlanRelation;
import wowjoy.fruits.ms.module.relation.entity.TaskProjectRelation;
import wowjoy.fruits.ms.module.task.FruitTask;
import wowjoy.fruits.ms.module.task.FruitTaskDao;
import wowjoy.fruits.ms.module.task.FruitTaskVo;
import wowjoy.fruits.ms.module.util.entity.FruitDict;

import java.time.LocalDate;
import java.util.List;

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

    protected abstract List<FruitTaskDao> finds(FruitTaskDao dao);

    protected abstract void update(FruitTaskDao dao);

    protected abstract void delete(FruitTaskDao dao);

    /**
     * 查询关联计划信息
     *
     * @return
     */
    protected abstract List<TaskPlanRelation> findJoinPlan(TaskPlanRelation relation);

    /*******************************
     /**
     * 查询关联项目信息
     *
     * @return
     */
    protected abstract List<TaskProjectRelation> findJoinProject(TaskProjectRelation relation);

    /*******************************
     * PUBLIC 函数，公共接口         *
     * 尽量保证规范，不直接调用dao接口 *
     *******************************/

    public final void addJoinProject(FruitTaskVo vo) {
        final FruitTaskDao dao = this.addTemplate(vo);
        dao.setTaskProjectRelation(vo.getTaskProjectRelation());
        if (dao.getTaskProjectRelation(FruitDict.Dict.ADD).isEmpty() || dao.getTaskProjectRelation(FruitDict.Dict.ADD).size() > 1)
            throw new CheckException("添加任务，必须关联计划，且只能关联一个计划");
        this.insert(dao);
    }

    public final void addJoinPlan(FruitTaskVo vo) {
        final FruitTaskDao dao = this.addTemplate(vo);
        dao.setTaskPlanRelation(vo.getTaskPlanRelation());
        if (dao.getTaskPlanRelation(FruitDict.Dict.ADD).isEmpty() || dao.getTaskPlanRelation(FruitDict.Dict.ADD).size() > 1)
            throw new CheckException("添加任务，必须关联计划，且只能关联一个计划");
        this.insert(dao);
    }

    private final FruitTaskDao addTemplate(FruitTaskVo vo) {
        final FruitTaskDao dao = FruitTask.getDao();
        dao.setUuid(vo.getUuid());
        dao.setTaskStatus(FruitDict.TaskDict.START.name());
        dao.setDescription(vo.getDescription());
        dao.setEstimatedEndDate(vo.getEstimatedEndDate());
        dao.setTitle(vo.getTitle());
        dao.setTaskLevel(vo.getTaskLevel());
        dao.setTaskUserRelation(vo.getTaskUserRelation());
        return dao;
    }

    public final void modifyJoinPlan(FruitTaskVo vo) {
        final FruitTaskDao dao = modifyTemplate(vo);
        dao.setTaskPlanRelation(vo.getTaskPlanRelation());
        if (!dao.getTaskPlanRelation(FruitDict.Dict.DELETE).isEmpty()) {
            if (this.findJoinPlan(TaskPlanRelation.newInstance(dao.getUuid(), dao.getTaskPlanRelation(FruitDict.Dict.DELETE).get(0).getPlanId())).isEmpty()) {
                throw new CheckException("不存在的关联计划，修改中断");
            }
            if (dao.getTaskPlanRelation(FruitDict.Dict.ADD).isEmpty())
                throw new CheckException("若删除旧的关联计划，必须添加新关联计划");
        } else if (dao.getTaskPlanRelation(FruitDict.Dict.ADD).isEmpty()) {
            throw new CheckException("友情提示，不能添加多个关联计划");
        } else
            dao.setTaskPlanRelation(null);

        this.update(dao);
    }

    public final void modifyJoinProject(FruitTaskVo vo) {
        final FruitTaskDao dao = modifyTemplate(vo);
        dao.setTaskProjectRelation(vo.getTaskProjectRelation());
        if (!dao.getTaskProjectRelation(FruitDict.Dict.DELETE).isEmpty()) {
            if (this.findJoinProject(TaskProjectRelation.newInstance(dao.getUuid(), dao.getTaskProjectRelation(FruitDict.Dict.DELETE).get(0).getProjectId())).isEmpty()) {
                throw new CheckException("不存在的关联项目，修改中断");
            }
            if (dao.getTaskProjectRelation(FruitDict.Dict.ADD).isEmpty())
                throw new CheckException("若删除旧的关联项目，必须添加新关联项目");
        } else if (!dao.getTaskProjectRelation(FruitDict.Dict.ADD).isEmpty()) {
            throw new CheckException("友情提示，不能添加多个关联项目");
        } else
            dao.setTaskPlanRelation(null);
        this.update(dao);
    }

    private final FruitTaskDao modifyTemplate(FruitTaskVo vo) {
        if (!this.findByUUID(vo).isNotEmpty())
            throw new CheckException("任务不存在，不予修改。");
        final FruitTaskDao dao = FruitTask.getDao();
        dao.setUuid(vo.getUuidVo());
        dao.setTitle(vo.getTitle());
        dao.setTaskLevel(vo.getTaskLevel());
        dao.setDescription(vo.getDescription());
        dao.setTaskUserRelation(vo.getTaskUserRelation());
        return dao;
    }

    public final void changeStatusToEnd(FruitTaskVo vo) {
        final FruitTaskDao dao = changeTemplate(vo);
        if (FruitDict.TaskDict.END.name().equals(dao.getTaskStatus()))
            throw new CheckException("任务已结束");
        dao.setTaskStatus(FruitDict.TaskDict.END.name());
        update(dao);
    }

    public final void changeStatusToStart(FruitTaskVo vo) {
        final FruitTaskDao dao = changeTemplate(vo);
        if (FruitDict.TaskDict.START.name().equals(dao.getTaskStatus()))
            throw new CheckException("");
        dao.setTaskStatus(FruitDict.TaskDict.START.name());
        update(dao);
    }

    private final FruitTaskDao changeTemplate(FruitTaskVo vo) {
        final FruitTask task = this.findByUUID(vo);
        if (!task.isNotEmpty())
            throw new CheckException("任务不存在，不予修改。");
        final FruitTaskDao dao = FruitTask.getDao();
        dao.setUuid(vo.getUuidVo());
        dao.setStatusDescription(vo.getStatusDescription());
        dao.setEndDate(LocalDate.now());
        dao.setTaskStatus(task.getTaskStatus());
        return dao;
    }

    public final void delete(FruitTaskVo vo) {
        final FruitTaskDao dao = FruitTask.getDao();
        dao.setUuid(vo.getUuidVo());
        this.delete(dao);
    }

    /**
     * 操作任务前的先决条件
     *
     * @param vo
     * @return
     */
    private final FruitTask findByUUID(FruitTaskVo vo) {
        if (StringUtils.isBlank(vo.getUuidVo()))
            throw new CheckException("任务不存在");
        final FruitTaskDao dao = FruitTask.getDao();
        dao.setUuid(vo.getUuidVo());
        List<FruitTaskDao> data = this.finds(dao);
        if (data.isEmpty() || data.size() > 1)
            return FruitTask.getEmpty();
        return data.get(0);
    }

}

