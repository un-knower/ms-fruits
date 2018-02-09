package wowjoy.fruits.ms.dao.task;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import wowjoy.fruits.ms.dao.InterfaceDao;
import wowjoy.fruits.ms.exception.CheckException;
import wowjoy.fruits.ms.exception.ExceptionSupport;
import wowjoy.fruits.ms.exception.ServiceException;
import wowjoy.fruits.ms.module.list.FruitListDao;
import wowjoy.fruits.ms.module.list.FruitListExample;
import wowjoy.fruits.ms.module.logs.FruitLogsDao;
import wowjoy.fruits.ms.module.plan.FruitPlanDao;
import wowjoy.fruits.ms.module.project.FruitProjectDao;
import wowjoy.fruits.ms.module.relation.entity.TaskListRelation;
import wowjoy.fruits.ms.module.relation.entity.TaskPlanRelation;
import wowjoy.fruits.ms.module.relation.entity.TaskProjectRelation;
import wowjoy.fruits.ms.module.task.FruitTask;
import wowjoy.fruits.ms.module.task.FruitTaskDao;
import wowjoy.fruits.ms.module.task.FruitTaskExample;
import wowjoy.fruits.ms.module.task.FruitTaskVo;
import wowjoy.fruits.ms.module.user.FruitUserDao;
import wowjoy.fruits.ms.module.util.entity.FruitDict;
import wowjoy.fruits.ms.util.DateUtils;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

import static java.util.stream.Collectors.*;

public abstract class AbstractDaoTask implements InterfaceDao {
    /*********************************************************************************
     * 抽象接口，私有，因为对外的公共接口用来书写业务层，发布api必须在自己的控制范围内，不发布无用的接口。*
     *********************************************************************************/

    /**
     * 添加任务
     * 1、添加任务
     * 2、添加关联计划
     * 3、添加关联项目
     */
    protected abstract void insert(FruitTaskDao dao);

    protected abstract List<FruitTaskDao> findByExample(Consumer<FruitTaskExample> consumerExample);

    protected abstract void update(Consumer<FruitTaskDao> daoConsumer, Consumer<FruitTaskExample> taskExampleConsumer);

    protected abstract void delete(FruitTaskDao dao);

    /**
     * 查询关联项目的任务列表
     */
    protected abstract List<FruitTaskDao> findByListId(Consumer<FruitTaskExample> exampleUnaryOperator, List<String> listId);

    protected abstract List<FruitListDao> findProjectList(String projectId, Consumer<FruitListExample> listExampleConsumer);

    protected abstract List<FruitTaskDao> findJoinUserByTaskIds(List<String> taskIds);

    protected abstract List<FruitTaskDao> findPlanByTaskIds(List<String> taskIds);

    protected abstract List<FruitTaskDao> findProjectByTask(List<String> taskIds);

    protected abstract List<FruitTaskDao> findPlanJoinProjectByTask(List<String> taskIds);

    protected abstract List<FruitTaskDao> findListByTask(List<String> taskIds);

    protected abstract Map<String, LinkedList<FruitLogsDao>> findJoinLogsByTask(List<String> taskIds);

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
            this.findTask(vo.getUuidVo());
            this.update(
                    dao -> TaskTemplate.newInstance(vo, dao)
                            .modifyTemplate()
                            .modifyCheckJoinPlan()
                            .modifyCheckJoinProject()
                            .checkModify(), taskExample -> taskExample.createCriteria().andUuidEqualTo(vo.getUuidVo())
            );
        } catch (ExceptionSupport ex) {
            throw ex;
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            throw new CheckException("修改任务时，发生未处理异常");
        }

    }

    public final void changeStatusToEnd(FruitTaskVo vo) {
        try {
            changeTemplate(vo);
            update(dao -> {
                dao.setTaskStatus(FruitDict.TaskDict.END.name());
                dao.setStatusDescription(vo.getStatusDescription());
                dao.setEndDate(new Date());
            }, taskExample -> taskExample.createCriteria().andUuidEqualTo(vo.getUuidVo()));
        } catch (ExceptionSupport ex) {
            throw ex;
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            throw new ServiceException("更改任务状态为已结束时发生错误");
        }
    }

    public final void changeStatusToStart(FruitTaskVo vo) {
        try {
            changeTemplate(vo);
            update(dao -> {
                dao.setTaskStatus(FruitDict.TaskDict.START.name());
                dao.setStatusDescription(vo.getStatusDescription());
            }, taskExample -> taskExample.createCriteria().andUuidEqualTo(vo.getUuidVo()));
        } catch (ExceptionSupport ex) {
            throw ex;
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            throw new ServiceException("更改任务状态为开始时发生错误");
        }
    }

    public final void changeStatusToClose(FruitTaskVo vo) {
        try {
            changeTemplate(vo);
            if (StringUtils.isBlank(vo.getStatusDescription()))
                throw new CheckException("必须填写关闭描述");
            update(dao -> {
                dao.setTaskStatus(FruitDict.TaskDict.CLOSE.name());
                dao.setStatusDescription(vo.getStatusDescription());
                dao.setEndDate(new Date());
            }, taskExample -> taskExample.createCriteria().andUuidEqualTo(vo.getUuidVo()));
        } catch (ExceptionSupport ex) {
            throw ex;
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            throw new ServiceException("关闭任务时发生错误");
        }
    }

    public final void changeList(FruitTaskVo vo) {
        try {
            changeTemplate(vo);
            this.update(dao -> {
                dao.setListRelation(vo.getListRelation());
                checkChangeList(dao);
            }, taskExample -> taskExample.createCriteria().andUuidEqualTo(vo.getUuidVo()));
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

    private void changeTemplate(FruitTaskVo vo) {
        if (StringUtils.isBlank(vo.getUuidVo()))
            throw new CheckException("uuid不能为空");
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
    private FruitTaskDao findTask(String uuid) {
        if (StringUtils.isBlank(uuid))
            throw new CheckException("查询标识不存在");
        final FruitTaskDao dao = FruitTask.getDao();
        dao.setUuid(uuid);
        List<FruitTaskDao> data = this.findByExample(example -> example.createCriteria()
                .andIsDeletedEqualTo(FruitDict.Systems.N.name())
                .andUuidEqualTo(uuid));
        if (data == null || data.isEmpty())
            throw new CheckException("任务不存在");
        return data.get(0);
    }

    /**
     * 查询指定项目下所有关联任务
     * 若任务有关联计划则需要查询出关联的计划
     * 函数思路：
     * 1、查询项目的任务列表集合
     * 2、查询每个列表对应的任务集合
     * 3、组合每个任务的详细信息，例如计划信息、用户信息
     */
    public List<FruitListDao> findJoinProjects(String projectId, final FruitTaskVo vo) {
        if (StringUtils.isBlank(projectId)) throw new CheckException("项目id不能为空");
        List<FruitListDao> lists = this.findProjectList(projectId, listExample -> {
            if (StringUtils.isNotBlank(vo.getListTitle()))
                listExample.createCriteria().andTitleLike(MessageFormat.format("%{0}%", vo.getListTitle()));
        });
        if (lists.isEmpty()) return Lists.newLinkedList();
        List<FruitTaskDao> tasks = this.findByListId((taskExample) -> {
            if (StringUtils.isNotBlank(vo.getTitle()))
                taskExample.createCriteria().andTitleLike(MessageFormat.format("%{0}%", vo.getTitle()));
        }, lists.stream().map(FruitListDao::getUuid).collect(toList())).stream().collect(toList());
        DaoThread taskThread = DaoThread.getFixed();
        if (tasks.isEmpty()) return lists;
        taskThread
                .execute(this.plugPlan(tasks))
                .execute(this.plugUser(tasks))
                .execute(this.plugLogs(tasks))
                .execute(this.plugUtil(tasks));
        Map<String, List<FruitTaskDao>> listMap = tasks.parallelStream().collect(groupingBy(FruitTaskDao::getListId));
        lists.parallelStream().forEach(list -> list.setTasks(listMap.get(list.getUuid())));
        if (StringUtils.isNotBlank(vo.getTitle()))
            lists = lists.stream().filter(list -> list.getTasks() != null && !list.getTasks().isEmpty()).collect(toList());
        taskThread.get().shutdown();
        return lists;
    }

    public FruitTaskDao findTaskInfo(String uuid) {
        FruitTaskDao taskInfo = this.findTask(uuid);
        DaoThread taskThread = DaoThread.getFixed();
        taskThread
                .execute(this.plugLogs(Lists.newArrayList(taskInfo)))
                .execute(this.plugPlan(Lists.newArrayList(taskInfo)))
                .execute(this.plugUtil(Lists.newArrayList(taskInfo)))
                .execute(this.plugUser(Lists.newArrayList(taskInfo)))
                .execute(this.plugList(Lists.newArrayList(taskInfo)))
                .get();
        return taskInfo;

    }

    /************************************************************************************************
     *                                       个人中心专供                                            *
     ************************************************************************************************/
    public List<FruitTaskDao> myTask(FruitTaskVo vo) {
        List<FruitTaskDao> tasks = this.myTask(TaskTemplate.newInstance(vo).findTemplate());
        myTaskPlug(tasks);
        return tasks;
    }

    public LinkedList<TaskTemplate.EndTasks> myTaskByEnd(FruitTaskVo vo) {
        long start = System.currentTimeMillis();
        vo.setTaskStatus(FruitDict.TaskDict.END.name());
        List<FruitTaskDao> fruitTaskDaos = myTask(vo);
        long end = System.currentTimeMillis();
        logger.info(String.valueOf(end - start));
        start = System.currentTimeMillis();
        LinkedList<TaskTemplate.EndTasks> endTasks = TaskTemplate.formatByEndDate(fruitTaskDaos);
        end = System.currentTimeMillis();
        logger.info(String.valueOf(end - start));
        return endTasks;
    }

    public List<FruitTaskDao> myCreateTask(FruitTaskVo vo) {
        List<FruitTaskDao> task = this.myCreateTask(TaskTemplate.newInstance(vo).findTemplate());
        myTaskPlug(task);
        return task;
    }

    private void myTaskPlug(List<FruitTaskDao> tasks) {
        try {
            if (tasks == null || tasks.isEmpty()) return;
            DaoThread thread = DaoThread.getFixed();
            thread
                    .execute(this.plugLogs(tasks))
                    .execute(this.plugProject(tasks))
                    .execute(this.plugPlanJoinProject(tasks))
                    .execute(this.plugUser(tasks))
                    .execute(this.plugList(tasks))
                    .execute(this.plugPlan(tasks))
                    .execute(this.plugUtil(tasks)).get();
            thread.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Callable plugLogs(final List<FruitTaskDao> tasks) {
        return () -> {
            if (tasks == null || tasks.isEmpty()) return false;
            Map<String, LinkedList<FruitLogsDao>> logs = this.findJoinLogsByTask(tasks.parallelStream().map(FruitTaskDao::getUuid).collect(toList()));
            tasks.stream().forEach(task -> {
                if (logs.containsKey(task.getUuid()))
                    task.setLogs(logs.get(task.getUuid()));
                else
                    task.setLogs(Lists.newLinkedList());
            });
            return true;
        };
    }

    private Callable plugPlan(final List<FruitTaskDao> tasks) {
        return () -> {
            if (tasks == null || tasks.isEmpty()) return false;
            Map<String, FruitPlanDao> planMap = this.findPlanByTaskIds(tasks.parallelStream().map(FruitTaskDao::getUuid).collect(toList()))
                    .stream().collect(toMap(FruitTaskDao::getUuid, FruitTaskDao::getPlan));
            tasks.forEach((task) -> task.setPlan(planMap.get(task.getUuid())));
            return true;
        };
    }

    private Callable plugProject(final List<FruitTaskDao> tasks) {
        return () -> {
            if (tasks == null || tasks.isEmpty()) return false;
            Map<String, FruitProjectDao> projectMap = this.findProjectByTask(tasks.parallelStream().map(FruitTaskDao::getUuid).collect(toList()))
                    .stream().collect(toMap(FruitTaskDao::getUuid, FruitTaskDao::getProject));
            tasks.forEach((task) -> task.setProject(projectMap.containsKey(task.getUuid()) ? projectMap.get(task.getUuid()) : task.getProject()));
            return true;
        };
    }

    private Callable plugPlanJoinProject(final List<FruitTaskDao> tasks) {
        return () -> {
            if (tasks == null || tasks.isEmpty()) return false;
            Map<String, FruitProjectDao> projectMap = this.findPlanJoinProjectByTask(tasks.parallelStream().map(FruitTaskDao::getUuid).collect(toList()))
                    .stream()
                    .collect(toMap(FruitTaskDao::getUuid, FruitTaskDao::getProject));
            tasks.forEach((task) -> task.setProject(projectMap.containsKey(task.getUuid()) ? projectMap.get(task.getUuid()) : task.getProject()));
            return true;
        };
    }

    private Callable plugList(final List<FruitTaskDao> tasks) {
        return () -> {
            if (tasks == null || tasks.isEmpty()) return false;
            Map<String, FruitListDao> listDaoMap = this.findListByTask(tasks.parallelStream().map(FruitTaskDao::getUuid).collect(toList())).stream().collect(toMap(FruitTaskDao::getUuid, FruitTaskDao::getList));
            tasks.forEach((task) -> task.setList(listDaoMap.get(task.getUuid())));
            return true;
        };
    }

    public Callable plugUser(final List<FruitTaskDao> tasks) {
        return () -> {
            if (tasks == null || tasks.isEmpty()) return false;
            Map<String, LinkedList<FruitUserDao>> userMap = this.findJoinUserByTaskIds(tasks.parallelStream().map(FruitTaskDao::getUuid).collect(toList()))
                    .stream()
                    .collect(toMap(FruitTaskDao::getUuid, task -> {
                        LinkedList<FruitUserDao> userList = Lists.newLinkedList();
                        userList.addAll(task.getUsers());
                        return userList;
                    }, (l, r) -> {
                        r.addAll(l);
                        return r;
                    }));
            tasks.forEach((task) -> task.setUsers(userMap.get(task.getUuid())));
            return true;
        };
    }

    private Callable plugUtil(List<FruitTaskDao> tasks) {
        return () -> {
            if (tasks == null || tasks.isEmpty()) return false;
            tasks.forEach(FruitTaskDao::computeDays);
            return true;
        };
    }


    /**
     * 检查添加信息合法性
     * 返回处理后的添加信息
     */
    private static class TaskTemplate {
        private final FruitTaskVo vo;
        private final FruitTaskDao dao;

        private TaskTemplate(final FruitTaskVo vo, final FruitTaskDao dao) {
            this.vo = vo;
            this.dao = dao;
        }

        public static TaskTemplate newInstance(FruitTaskVo vo, FruitTaskDao dao) {
            return new TaskTemplate(vo, dao);
        }

        public static TaskTemplate newInstance(FruitTaskVo vo) {
            return new TaskTemplate(vo, FruitTask.getDao());
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
                throw new CheckException("检测到关联多个元素");
            return this;
        }

        /**
         * 检查是否是项目关联
         */
        private TaskTemplate insertJoinProject() {
            try {
                if (!vo.getProjectRelation(FruitDict.Systems.ADD).isEmpty()) {
                    if (vo.getProjectRelation(FruitDict.Systems.ADD).size() > 1)
                        throw new CheckException("只能关联一个项目");
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
                        throw new CheckException("只能关联一个计划");
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
                throw new CheckException("标题不能为空");
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

        FruitTaskDao findTemplate() {
            dao.setTitle(vo.getTitle());
            dao.setTaskStatus(vo.getTaskStatus());
            dao.setProjectIds(vo.getProjectIds());
            dao.setAsc(vo.getAsc());
            dao.setDesc(vo.getDesc());
            return dao;
        }

        static LinkedList<EndTasks> formatByEndDate(List<FruitTaskDao> tasks) {
            LinkedList<EndTasks> endTaskList = Lists.newLinkedList();
            tasksToEndMap(tasks).forEach((endDate, taskList) -> endTaskList.add(new EndTasks(endDate, taskList)));
            endTaskList.sort((o1, o2) -> o2.getEndDate().compareTo(o1.getEndDate()));
            return endTaskList;
        }

        private static LinkedHashMap<LocalDateTime, List<FruitTaskDao>> tasksToEndMap(List<FruitTaskDao> tasks) {
            LinkedHashMap<LocalDateTime, List<FruitTaskDao>> endTaskMap = Maps.newLinkedHashMap();
            LocalDateTime endDate;
            for (FruitTaskDao task : tasks) {
                endDate = LocalDateTime.ofInstant(task.getEndDate().toInstant(), ZoneId.systemDefault());
                if (!endTaskMap.containsKey(endDate)) {
                    LinkedList<FruitTaskDao> lists = Lists.newLinkedList();
                    lists.add(task);
                    endTaskMap.put(endDate, lists);
                } else
                    endTaskMap.get(endDate).add(task);
            }
            return endTaskMap;
        }

        private static class EndTasks {
            private transient final LocalDateTime endDate;
            private final String key;
            private final List<FruitTaskDao> tasks;

            public LocalDateTime getEndDate() {
                return endDate;
            }

            private EndTasks(LocalDateTime endDate, List<FruitTaskDao> tasks) {
                this.endDate = endDate;
                this.key = MessageFormat.format(
                        "{0}（{1}）",
                        endDate.format(DateTimeFormatter.ISO_DATE),
                        DateUtils.dayOfWeekChinese(endDate.getDayOfWeek().getValue())
                );
                this.tasks = tasks;
            }
        }

    }

}