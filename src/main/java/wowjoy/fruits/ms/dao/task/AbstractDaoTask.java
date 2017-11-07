package wowjoy.fruits.ms.dao.task;

import org.apache.commons.lang.StringUtils;
import wowjoy.fruits.ms.dao.InterfaceDao;
import wowjoy.fruits.ms.exception.CheckException;
import wowjoy.fruits.ms.exception.ExceptionSupport;
import wowjoy.fruits.ms.exception.ServiceException;
import wowjoy.fruits.ms.module.relation.entity.TaskListRelation;
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
    protected abstract List<TaskPlanRelation> findJoin(TaskPlanRelation relation);

    /**
     * 查询关联项目信息
     *
     * @return
     */
    protected abstract List<TaskProjectRelation> findJoin(TaskProjectRelation relation);


    /**
     * 查询关联列表信息
     *
     * @return
     */
    protected abstract List<TaskListRelation> findJoin(TaskListRelation relation);

    /**
     * 查询关联项目的任务列表
     *
     * @return
     */
    protected abstract List<FruitTaskDao> findJoinProjects(FruitTaskDao dao);

    /**
     * 查询关联计划的任务列表
     *
     * @return
     */
    protected abstract List<FruitTaskDao> findJoinPlans(FruitTaskDao dao);


    /*******************************
     * PUBLIC 函数，公共接口         *
     * 尽量保证规范，不直接调用dao接口 *
     *******************************/

    public final void addJoinProject(FruitTaskVo vo) {
        try {
            final FruitTaskDao dao = this.addTemplate(vo);
            dao.setTaskProjectRelation(vo.getTaskProjectRelation());
            if (dao.getTaskProjectRelation(FruitDict.Dict.ADD).isEmpty() || dao.getTaskProjectRelation(FruitDict.Dict.ADD).size() > 1)
                throw new CheckException("添加任务，必须关联项目，且只能关联一个项目");
            this.insert(dao);
        } catch (ExceptionSupport ex) {
            throw ex;
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            throw new ServiceException("添加项目任务失败");
        }

    }

    public final void addJoinPlan(FruitTaskVo vo) {
        try {
            final FruitTaskDao dao = this.addTemplate(vo);
            dao.setTaskPlanRelation(vo.getTaskPlanRelation());
            if (dao.getTaskPlanRelation(FruitDict.Dict.ADD).isEmpty() || dao.getTaskPlanRelation(FruitDict.Dict.ADD).size() > 1)
                throw new CheckException("添加任务，必须关联计划，且只能关联一个计划");
            this.insert(dao);
        } catch (ExceptionSupport ex) {
            throw ex;
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            throw new ServiceException("添加计划任务失败");
        }

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
        dao.setTaskListRelation(vo.getTaskListRelation());
        if (dao.getTaskListRelation(FruitDict.Dict.ADD).isEmpty())
            throw new CheckException("必须关联列表");
        return dao;
    }

    public final void modifyJoinPlan(FruitTaskVo vo) {
        try {
            final FruitTaskDao dao = modifyTemplate(vo);
            dao.setTaskPlanRelation(vo.getTaskPlanRelation());
            this.modifyCheckJoinPlan(dao);
            this.update(dao);
        } catch (ExceptionSupport ex) {
            throw ex;
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            throw new ServiceException("修改计划任务失败");
        }
    }

    public final void modifyJoinProject(FruitTaskVo vo) {
        try {
            final FruitTaskDao dao = modifyTemplate(vo);
            dao.setTaskProjectRelation(vo.getTaskProjectRelation());
            this.modifyCheckJoinProject(dao);
            this.update(dao);
        } catch (ExceptionSupport ex) {
            throw ex;
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            throw new ServiceException("修改项目任务失败");
        }
    }

    private final void modifyCheckJoinPlan(FruitTaskDao dao) {
        if (dao.getTaskPlanRelation(FruitDict.Dict.ADD).isEmpty()) return;
        if (dao.getTaskPlanRelation(FruitDict.Dict.ADD).size() > 1) throw new CheckException("只能尝试覆盖一个计划");
        List<TaskPlanRelation> relation = this.findJoin(TaskPlanRelation.newInstance(dao.getUuid(), null));
        if (relation.isEmpty()) return;
        List<TaskPlanRelation> delete = dao.getTaskPlanRelation(FruitDict.Dict.DELETE);
        relation.forEach((i) -> delete.add(TaskPlanRelation.newInstance(dao.getUuid(), i.getPlanId())));
        dao.setTaskPlanRelation(FruitDict.Dict.DELETE, delete);
    }

    private final void modifyCheckJoinProject(FruitTaskDao dao) {
        if (dao.getTaskProjectRelation(FruitDict.Dict.ADD).isEmpty()) return;
        if (dao.getTaskProjectRelation(FruitDict.Dict.ADD).size() > 1) throw new CheckException("只能尝试覆盖一个项目");
        List<TaskProjectRelation> relation = this.findJoin(TaskProjectRelation.newInstance(dao.getUuid(), null));
        if (relation.isEmpty()) return;
        List<TaskProjectRelation> delete = dao.getTaskProjectRelation(FruitDict.Dict.DELETE);
        relation.forEach((i) -> delete.add(TaskProjectRelation.newInstance(dao.getUuid(), i.getProjectId())));
        dao.setTaskProjectRelation(FruitDict.Dict.DELETE, delete);
    }

    private final FruitTaskDao modifyTemplate(FruitTaskVo vo) {
        if (!this.findByUUID(vo).isNotEmpty())
            throw new CheckException("任务不存在，不允许修改。");
        final FruitTaskDao dao = FruitTask.getDao();
        dao.setUuid(vo.getUuidVo());
        dao.setTitle(vo.getTitle());
        dao.setTaskLevel(vo.getTaskLevel());
        dao.setDescription(vo.getDescription());
        dao.setTaskUserRelation(vo.getTaskUserRelation());
        dao.setTaskPlanRelation(null);
        return dao;
    }

    public final void changeStatusToEnd(FruitTaskVo vo) {
        try {
            final FruitTaskDao dao = changeTemplate(vo);
            if (FruitDict.TaskDict.END.name().equals(dao.getTaskStatus()))
                throw new CheckException("任务已结束");
            dao.setTaskStatus(FruitDict.TaskDict.END.name());
            update(dao);
        } catch (ExceptionSupport ex) {
            throw ex;
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            throw new ServiceException("更改任务状态为已结束时发生错误");
        }
    }

    public final void changeStatusToStart(FruitTaskVo vo) {
        try {
            final FruitTaskDao dao = changeTemplate(vo);
            if (FruitDict.TaskDict.START.name().equals(dao.getTaskStatus()))
                throw new CheckException("任务已开始");
            dao.setTaskStatus(FruitDict.TaskDict.START.name());
            update(dao);
        } catch (ExceptionSupport ex) {
            throw ex;
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            throw new ServiceException("更改任务状态为开始时发生错误");
        }
    }

    public final void changeList(FruitTaskVo vo) {
        try {
            if (!this.findByUUID(vo).isNotEmpty())
                throw new CheckException("任务不存在，不允许操作。");
            FruitTaskDao dao = FruitTask.getDao();
            dao.setUuid(vo.getUuidVo());
            dao.setTaskListRelation(vo.getTaskListRelation());
            this.changeCheckList(dao);
            this.update(dao);
        } catch (ExceptionSupport ex) {
            throw ex;
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            throw new ServiceException("更改任务所属列表时发生错误");
        }
    }

    private final void changeCheckList(FruitTaskDao dao) {
        if (dao.getTaskListRelation(FruitDict.Dict.ADD).isEmpty())
            throw new CheckException("没有目标源，无法切换列表");
        List<TaskListRelation> relations = this.findJoin(TaskListRelation.newInstance(dao.getUuid(), null));
        if (relations.isEmpty()) return;
        if (relations.size() > 1) throw new CheckException("关联列表数据出现一对多情况，请联系开发人员");
        if (relations.get(0).getListId().equals(dao.getTaskListRelation(FruitDict.Dict.ADD).get(0).getListId()))
            throw new ServiceException("和当前列表相同，操作终止。");
        List<TaskListRelation> delete = dao.getTaskListRelation(FruitDict.Dict.DELETE);
        /*每次切换列表时，都删除旧的关联列表*/
        delete.add(TaskListRelation.newInstance(null, relations.get(0).getListId()));
        dao.setTaskListRelation(FruitDict.Dict.DELETE, delete);

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
        try {
            final FruitTaskDao dao = FruitTask.getDao();
            dao.setUuid(vo.getUuidVo());
            this.delete(dao);
        } catch (ExceptionSupport ex) {
            throw ex;
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            throw new ServiceException("删除任务失败");
        }
    }

    /**
     * 操作任务前的任务检查
     *
     * @param vo
     * @return
     */
    private final FruitTask findByUUID(FruitTaskVo vo) {
        if (StringUtils.isBlank(vo.getUuidVo()))
            throw new CheckException("查询标识不存在");
        final FruitTaskDao dao = FruitTask.getDao();
        dao.setUuid(vo.getUuidVo());
        List<FruitTaskDao> data = this.finds(dao);
        if (data.isEmpty() || data.size() > 1)
            return FruitTask.getEmpty();
        return data.get(0);
    }

    /**
     * 查询关联项目任务列表
     *
     * @param vo
     * @return
     */
    public List<FruitTaskDao> findJoinProjects(FruitTaskVo vo) {
        FruitTaskDao dao = findJoinDao(vo);
        dao.setProjectIds(vo.getId());
        return this.sortList(this.findJoinProjects(dao));
    }

    /**
     * 查询关联计划的任务列表
     *
     * @param vo
     * @return
     */
    public List<FruitTaskDao> findJoinPlans(FruitTaskVo vo) {
        FruitTaskDao dao = this.findJoinDao(vo);
        dao.setPlanIds(vo.getId());
        return this.sortList(this.findJoinPlans(dao));
    }

    private FruitTaskDao findJoinDao(FruitTaskVo vo) {
        FruitTaskDao dao = FruitTaskVo.getDao();
        dao.setTitle(vo.getTitle());
        dao.setListIds(vo.getListId());
        return dao;
    }

    private List<FruitTaskDao> sortList(List<FruitTaskDao> list) {
        list.forEach((i) -> i.sortUsers().computeDays());
        return list;
    }
}

