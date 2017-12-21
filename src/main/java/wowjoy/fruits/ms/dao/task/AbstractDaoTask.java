package wowjoy.fruits.ms.dao.task;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import wowjoy.fruits.ms.dao.InterfaceDao;
import wowjoy.fruits.ms.exception.CheckException;
import wowjoy.fruits.ms.exception.ExceptionSupport;
import wowjoy.fruits.ms.exception.ServiceException;
import wowjoy.fruits.ms.module.list.FruitListDao;
import wowjoy.fruits.ms.module.plan.FruitPlanDao;
import wowjoy.fruits.ms.module.project.FruitProjectDao;
import wowjoy.fruits.ms.module.relation.entity.TaskListRelation;
import wowjoy.fruits.ms.module.relation.entity.TaskPlanRelation;
import wowjoy.fruits.ms.module.relation.entity.TaskProjectRelation;
import wowjoy.fruits.ms.module.task.FruitTask;
import wowjoy.fruits.ms.module.task.FruitTaskDao;
import wowjoy.fruits.ms.module.task.FruitTaskVo;
import wowjoy.fruits.ms.module.user.FruitUserDao;
import wowjoy.fruits.ms.module.util.entity.FruitDict;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

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

    protected abstract FruitTask find(FruitTaskDao dao);

    protected abstract void update(FruitTaskDao dao);

    protected abstract void delete(FruitTaskDao dao);

    /**
     * 查询关联项目的任务列表
     */
    protected abstract List<FruitTaskDao> findByPlanId(FruitTaskDao dao);

    protected abstract List<FruitTaskDao> findByListId(FruitTaskDao dao);

    protected abstract List<FruitListDao> findProjectList(List<String> projectId);

    protected abstract List<FruitTaskDao> findUserByTaskIds(List<String> taskIds);

    protected abstract List<FruitTaskDao> findPlanByTaskIds(List<String> taskIds);

    protected abstract List<FruitTaskDao> findProjectByTask(List<String> taskIds);

    protected abstract List<FruitTaskDao> findPlanJoinProjectByTask(List<String> taskIds);

    protected abstract List<FruitTaskDao> findListByTask(List<String> taskIds);

    protected abstract List<FruitTaskDao> myTask(FruitTaskDao dao);

    protected abstract List<FruitTaskDao> myCreateTask(FruitTaskDao dao);

    /*******************************
     * PUBLIC 函数，公共接口         *
     * 尽量保证规范，不直接调用dao接口 *
     *******************************/

    /**
     * 筛选出符合条件的添加函数
     */
    public final void insert(FruitTaskVo vo) {
        try {
            this.insert(TaskTemplate.newInstance(vo)
                    .insertTemplate()
                    .insertJoinPlan()
                    .insertJoinProject()
                    .checkInsert()
                    .queryAddResult());
        } catch (ExceptionSupport ex) {
            throw ex;
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            throw new CheckException("插入任务时，发生未处理异常");
        }

    }

    public final void modify(FruitTaskVo vo) {
        try {
            if (!this.findByUUID(vo).isNotEmpty())
                throw new CheckException("任务不存在，不允许修改。");
            this.update(TaskTemplate.newInstance(vo)
                    .modifyTemplate()
                    .modifyCheckJoinPlan()
                    .modifyCheckJoinProject()
                    .checkModify()
                    .queryModifyResult());
        } catch (ExceptionSupport ex) {
            throw ex;
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            throw new CheckException("修改任务时，发生未处理异常，联系 Boss 严");
        }

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
            dao.setListRelation(vo.getListRelation());
            this.checkChangeList(dao);
            this.update(dao);
        } catch (ExceptionSupport ex) {
            throw ex;
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            throw new ServiceException("更改任务所属列表时发生错误");
        }
    }

    private void checkChangeList(FruitTaskDao dao) {
        if (dao.getListRelation(FruitDict.Systems.ADD).isEmpty())
            throw new CheckException("没有目标源，无法切换列表");
        /*每次切换列表时，都删除旧的关联列表*/
        dao.setListRelation(FruitDict.Systems.DELETE, Lists.newArrayList(TaskListRelation.newInstance(dao.getUuid(), null)));

    }

    private FruitTaskDao changeTemplate(FruitTaskVo vo) {
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
     */
    private FruitTask findByUUID(FruitTaskVo vo) {
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
     * 查询指定计划下的所有任务
     */
    public List<FruitTaskDao> findByPlanId(FruitTaskVo vo) {
        if (StringUtils.isBlank(vo.getPlanId()))
            throw new CheckException("需要指定要查询的计划");
        FruitTaskDao dao = FruitTask.getDao();
        dao.setPlanId(vo.getPlanId());
        List<FruitTaskDao> tasks = this.findByPlanId(dao);
        List<String> ids = toIds(tasks);
        DaoThread taskThread = DaoThread.getFixed();
        taskThread.execute(this.plugUser(ids, tasks))
                .execute(this.plugList(ids,tasks))
                .execute(this.plugUtil(tasks))
                .get();
        taskThread.shutdown();
        return tasks;
    }

    /**
     * 查询指定列表的任务信息
     */
    public List<FruitTaskDao> findByListId(FruitTaskVo vo) {
        if (StringUtils.isBlank(vo.getListId()))
            throw new CheckException("需要指定要查询的列表");
        FruitTaskDao dao = TaskTemplate.newInstance(vo).findTemplate();
        dao.setListId(vo.getListId());
        List<FruitTaskDao> tasks = this.findByListId(dao);
        List<String> ids = toIds(tasks);
        DaoThread taskThread = DaoThread.getFixed();
        taskThread.execute(this.plugPlan(ids, tasks))
                .execute(this.plugUser(ids, tasks))
                .execute(this.plugUtil(tasks))
                .get();
        taskThread.shutdown();
        return tasks;
    }


    /**
     * 查询指定项目下所有关联任务
     * 若任务有关联计划则需要查询出关联的计划
     * 函数思路：
     * 1、查询项目的任务列表集合
     * 2、查询每个列表对应的任务集合
     * 3、组合每个任务的详细信息，例如计划信息、用户信息
     */
    public List<FruitListDao> findJoinProjects(FruitTaskVo vo) {
        if (vo.getProjectIds() == null || vo.getProjectIds().isEmpty())
            throw new CheckException("项目id不能为空！");
        FruitTaskDao dao = TaskTemplate.newInstance(vo).findTemplate();
        dao.setProjectIds(vo.getProjectIds());
        List<FruitListDao> lists = this.findProjectList(dao.getProjectIds());
        DaoThread listThread = DaoThread.getFixed();
        DaoThread taskThread = DaoThread.getFixed();
        long start = System.currentTimeMillis();
        lists.forEach((list) -> listThread.execute(() -> {
            FruitTaskDao taskDao = FruitTask.getDao();
            taskDao.setListId(list.getUuid());
            List<FruitTaskDao> tasks = this.findByListId(taskDao);
            List<String> ids = toIds(tasks);
            taskThread
                    .execute(this.plugPlan(ids, tasks))
                    .execute(this.plugUser(ids, tasks))
                    .execute(this.plugUtil(tasks));
            list.setTasks(tasks);
            return true;
        }));
        long end = System.currentTimeMillis();
        logger.info("---------" + (end - start));
        listThread.get();
        taskThread.get();
        listThread.shutdown();
        taskThread.shutdown();
        return lists;
    }

    /************************************************************************************************
     *                                       个人中心专供                                            *
     ************************************************************************************************/
    public List<FruitTaskDao> myTask(FruitTaskVo vo) {
        List<FruitTaskDao> tasks = this.myTask(TaskTemplate.newInstance(vo).findTemplate());
        myTaskPlug(tasks);
        return tasks;
    }

    public List<FruitTaskDao> myCreateTask(FruitTaskVo vo) {
        List<FruitTaskDao> task = this.myCreateTask(TaskTemplate.newInstance(vo).findTemplate());
        myTaskPlug(task);
        return task;
    }

    private void myTaskPlug(List<FruitTaskDao> tasks) {
        try {
            DaoThread thread = DaoThread.getFixed();
            List<String> ids = toIds(tasks);
            thread
                    .execute(this.plugProject(ids, tasks))
                    .execute(this.plugPlanJoinProject(ids, tasks))
                    .execute(this.plugUser(ids, tasks))
                    .execute(this.plugList(ids, tasks))
                    .execute(this.plugPlan(ids, tasks))
                    .execute(this.plugUtil(tasks)).get();
            thread.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<String> toIds(List<FruitTaskDao> tasks) {
        List<String> taskIds = Lists.newLinkedList();
        tasks.forEach((i) -> taskIds.add(i.getUuid()));
        return taskIds;
    }

    private Callable plugPlan(List<String> taskIds, List<FruitTaskDao> tasks) {
        return () -> {
            Map<String, FruitPlanDao> planDaoMap = Maps.newLinkedHashMap();
            this.findPlanByTaskIds(taskIds).forEach((i) -> planDaoMap.put(i.getUuid(), i.getPlan()));
            tasks.forEach((task) -> task.setPlan(planDaoMap.get(task.getUuid())));
            return true;
        };
    }

    private Callable plugProject(List<String> taskIds, List<FruitTaskDao> tasks) {
        return () -> {
            Map<String, FruitProjectDao> projectDaoMap = Maps.newLinkedHashMap();
            this.findProjectByTask(taskIds).forEach((i) -> projectDaoMap.put(i.getUuid(), i.getProject()));
            tasks.forEach((task) -> task.setProject(projectDaoMap.get(task.getUuid()) != null ? projectDaoMap.get(task.getUuid()) : task.getProject()));
            return true;
        };
    }

    private Callable plugPlanJoinProject(List<String> taskIds, List<FruitTaskDao> tasks) {
        return () -> {
            Map<String, FruitProjectDao> projectDaoMap = Maps.newLinkedHashMap();
            this.findPlanJoinProjectByTask(taskIds).forEach((i) -> projectDaoMap.put(i.getUuid(), i.getProject()));
            tasks.forEach((task) -> task.setProject(projectDaoMap.get(task.getUuid()) != null ? projectDaoMap.get(task.getUuid()) : task.getProject()));
            return true;
        };
    }

    private Callable plugList(List<String> taskIds, List<FruitTaskDao> tasks) {
        return () -> {
            Map<String, FruitListDao> listDaoMap = Maps.newLinkedHashMap();
            this.findListByTask(taskIds).forEach((i) -> listDaoMap.put(i.getUuid(), i.getList()));
            tasks.forEach((task) -> task.setList(listDaoMap.get(task.getUuid())));
            return true;
        };
    }

    private Callable plugUser(List<String> taskIds, List<FruitTaskDao> tasks) {
        return () -> {
            Map<String, List<FruitUserDao>> userDaoMap = Maps.newLinkedHashMap();
            this.findUserByTaskIds(taskIds).forEach((i) -> userDaoMap.put(i.getUuid(), i.getUsers()));
            tasks.forEach((task) -> task.setUsers(userDaoMap.get(task.getUuid())));
            return true;
        };
    }

    private Callable plugUtil(List<FruitTaskDao> tasks) {
        return () -> {
            tasks.forEach((i) -> i.computeDays());
            return true;
        };
    }


    /**
     * 检查添加信息合法性
     * 返回处理后的添加信息
     */
    private static class TaskTemplate {
        private final FruitTaskVo vo;
        private final FruitTaskDao dao = new FruitTaskDao();

        private TaskTemplate(final FruitTaskVo vo) {
            this.vo = vo;
        }

        public static TaskTemplate newInstance(FruitTaskVo vo) {
            return new TaskTemplate(vo);
        }

        /**
         * 获取添加结果
         */
        private FruitTaskDao queryAddResult() {
            return dao;
        }

        /**
         * 添加前限制
         */
        private TaskTemplate checkInsert() {
            if (dao.getPlanRelation(FruitDict.Systems.ADD).isEmpty() && dao.getProjectRelation(FruitDict.Systems.ADD).isEmpty())
                throw new CheckException("未检测到任务所关联的元素");
            if (!dao.getPlanRelation(FruitDict.Systems.ADD).isEmpty() && !dao.getProjectRelation(FruitDict.Systems.ADD).isEmpty())
                throw new CheckException("检测到关联多个元素，这是不合法的");
            return this;
        }

        /**
         * 检查是否是项目关联
         */
        private TaskTemplate insertJoinProject() {
            try {
                if (!vo.getProjectRelation(FruitDict.Systems.ADD).isEmpty()) {
                    if (vo.getProjectRelation(FruitDict.Systems.ADD).size() > 1)
                        throw new CheckException("必须关联项目，且只能关联一个项目");
                    dao.setProjectRelation(vo.getProjectRelation());
                }
                return this;
            } catch (ExceptionSupport ex) {
                throw ex;
            } catch (RuntimeException ex) {
                ex.printStackTrace();
                throw new ServiceException("添加项目任务失败");
            }

        }

        /**
         * 检查是否是计划关联
         */
        private TaskTemplate insertJoinPlan() {
            try {
                if (!vo.getPlanRelation(FruitDict.Systems.ADD).isEmpty()) {
                    if (vo.getPlanRelation(FruitDict.Systems.ADD).size() > 1)
                        throw new CheckException("必须关联计划，且只能关联一个计划");
                    dao.setPlanRelation(vo.getPlanRelation());
                }
                return this;
            } catch (ExceptionSupport ex) {
                throw ex;
            } catch (RuntimeException ex) {
                ex.printStackTrace();
                throw new ServiceException("添加计划任务失败");
            }
        }

        /**
         * 任务添加统一模板
         */
        private TaskTemplate insertTemplate() {
            dao.setUuid(vo.getUuid());
            dao.setTaskStatus(FruitDict.TaskDict.START.name());
            dao.setDescription(vo.getDescription());
            dao.setEstimatedEndDate(vo.getEstimatedEndDate());
            dao.setTitle(vo.getTitle());
            dao.setTaskLevel(FruitDict.TaskDict.LOW.name());
            dao.setUserRelation(vo.getUserRelation());
            dao.setListRelation(vo.getListRelation());
            if (StringUtils.isBlank(dao.getTitle()))
                throw new CheckException("标题不能为空（前端难道没有校验吗！！）");
            if (dao.getListRelation(FruitDict.Systems.ADD).isEmpty())
                throw new CheckException("必须关联列表");
            if (dao.getListRelation(FruitDict.Systems.ADD).size() > 1)
                throw new CheckException("一次只能关联一个列表");
            return this;
        }

        /**
         * 获取修改结果
         */
        private FruitTaskDao queryModifyResult() {
            return dao;
        }

        /**
         * 添加前限制
         */
        private TaskTemplate checkModify() {
            if (!dao.getPlanRelation(FruitDict.Systems.ADD).isEmpty() && !dao.getProjectRelation(FruitDict.Systems.ADD).isEmpty())
                throw new CheckException("检测到关联多个元素，这是不合法的");
            return this;
        }

        private TaskTemplate modifyCheckJoinPlan() {
            if (vo.getPlanRelation(FruitDict.Systems.DELETE).isEmpty()) return this;
            if (vo.getPlanRelation(FruitDict.Systems.ADD).isEmpty()) return this;
            this.insertJoinPlan();
            dao.setPlanRelation(FruitDict.Systems.DELETE, Lists.newArrayList(TaskPlanRelation.newInstance(dao.getUuid(), null)));
            return this;
        }

        /**
         * 检测是否需要删除当前关联
         * 检查必须有
         */
        private TaskTemplate modifyCheckJoinProject() {
            if (vo.getProjectRelation(FruitDict.Systems.DELETE).isEmpty()) return this;
            if (vo.getProjectRelation(FruitDict.Systems.ADD).isEmpty()) return this;
            insertJoinProject();
            dao.setTaskProjectRelation(FruitDict.Systems.DELETE, Lists.newArrayList(TaskProjectRelation.newInstance(dao.getUuid(), null)));
            return this;
        }

        private TaskTemplate modifyTemplate() {
            dao.setUuid(vo.getUuidVo());
            dao.setTitle(vo.getTitle());
            dao.setTaskLevel(vo.getTaskLevel());
            dao.setDescription(vo.getDescription());
            dao.setUserRelation(vo.getUserRelation());
            return this;
        }

        public FruitTaskDao findTemplate() {
            dao.setTitle(vo.getTitle());
            dao.setTaskStatus(vo.getTaskStatus());
            dao.setProjectIds(vo.getProjectIds());
            dao.setAsc(vo.getAsc());
            dao.setDesc(vo.getDesc());
            return dao;
        }

    }

}