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
import wowjoy.fruits.ms.module.logs.FruitLogs;
import wowjoy.fruits.ms.module.logs.transfer.FruitTransferLogs;
import wowjoy.fruits.ms.module.plan.FruitPlanDao;
import wowjoy.fruits.ms.module.relation.entity.*;
import wowjoy.fruits.ms.module.task.*;
import wowjoy.fruits.ms.module.task.FruitTaskVo.TaskTransferVo;
import wowjoy.fruits.ms.module.util.entity.FruitDict;
import wowjoy.fruits.ms.module.util.entity.FruitDict.Systems;
import wowjoy.fruits.ms.module.util.entity.FruitDict.TransferDict;

import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static java.util.stream.Collectors.*;

public abstract class AbstractDaoTask implements InterfaceDao {
    /**
     * 添加任务
     * 1、添加任务
     * 2、添加关联计划
     * 3、添加关联项目
     */
    protected abstract void insert(FruitTask.Insert insert);

    protected abstract List<FruitTaskDao> findByExample(Consumer<FruitTaskExample> consumerExample);

    protected abstract void update(FruitTask.Update update, Consumer<FruitTaskExample> taskExampleConsumer);

    protected abstract void delete(FruitTaskDao dao);

    /**
     * 查询关联项目的任务列表
     */
    protected abstract List<FruitTaskDao> findByListExampleAndProjectId(Consumer<FruitTaskExample> exampleUnaryOperator, Consumer<FruitListExample> listExampleConsumer, String projectId);

    protected abstract List<FruitListDao> findProjectList(String projectId, Consumer<FruitListExample> listExampleConsumer);

    protected abstract List<FruitTaskUser> findJoinUserByTaskIds(List<String> taskIds);

    protected abstract List<FruitTaskDao> findPlanByTaskIds(List<String> taskIds);

    protected abstract List<FruitTaskProject> findProjectByTask(List<String> taskIds);

    protected abstract List<FruitTaskDao> findListByTask(List<String> taskIds);

    protected abstract Map<String, LinkedList<FruitLogs.Info>> findJoinLogsByTask(List<String> taskIds);

    protected abstract List<FruitTaskDao> myTask(Consumer<FruitTaskExample> taskExampleConsumer, String projectId);

    protected abstract List<FruitTaskDao> myCreateTask(Consumer<FruitTaskExample> taskExampleConsumer, String projectId);

    protected abstract List<FruitTaskUser> findUserByProjectIdAndUserIdAndTaskExample(Consumer<FruitTaskExample> exampleConsumer, List<String> userIds, String projectId);

    protected abstract void insertTransfer(Consumer<FruitTransferLogs.Insert> insertConsumer);

    /**
     * 任务转交操作
     * 1、获取旧用户
     * 2、获取新用户
     * 3、将所有旧用户改为新用户
     * 4、保存转交操作到转交日志中
     * 5、将转交日志uuid设置到vo中，日志记录时可以定位到当前转交操作数据。
     */
    public void transfer(TaskTransferVo transferVo) {
        transferVo.checkTransferUser();
        /*保存转交记录*/
        this.insertTransfer(transferInsert -> {
            Map<TransferDict, ArrayList<TransferUserRelation>> transferUserRelation = Maps.newLinkedHashMap();
            transferUserRelation.put(TransferDict.NEW, new ArrayList<>(transferVo.getTransferUser()));
            transferUserRelation.put(TransferDict.OLD, this.findJoinUserByTaskIds(Lists.newArrayList(transferVo.getUuidVo()))/*获取当前任务的用户列表*/
                    .parallelStream()/*并行处理*/
                    .map(user -> TransferUserRelation.newInstanceSetUserId(user.getUserId()))/*获取所有用户id*/
                    .collect(toCollection(ArrayList::new)));
            transferInsert.setReason(transferVo.getReason());
            transferInsert.setTransferUserRelation(transferUserRelation);
            transferVo.setTransferId(transferInsert.getUuid());
        });

        /*删除所有旧用户，添加新用户*/
        FruitTask.Update update = new FruitTask.Update();
        update.setUuid(transferVo.getUuidVo());
        Map<Systems, List<TaskUserRelation>> userRelation = Maps.newLinkedHashMap();
        userRelation.put(Systems.DELETE, Lists.newArrayList(TaskUserRelation.newInstance(null, null)));
        userRelation.put(Systems.ADD, transferVo.getTransferUser()
                .parallelStream()
                .map(transferUserRelation -> TaskUserRelation.newInstance(transferVo.getUuidVo(), transferUserRelation.getUserId()))
                .collect(toList()));
        update.setUserRelation(userRelation);
        this.update(update, example -> {
        });
    }

    /**
     * 筛选出符合条件的添加函数
     */
    public final void insertBefore(FruitTask.Insert insert) {
        try {
            Optional.of(insert)
                    .map(FruitTask.Insert::getTitle)
                    .filter(StringUtils::isNotBlank)
                    .orElseThrow(() -> new CheckException("title can't null"));
            Optional.of(insert)
                    .map(FruitTask.Insert::getEstimatedEndDate)
                    .filter(Objects::nonNull)
                    .orElseThrow(() -> new CheckException("estimatedEndDate can't null"));
            Optional.ofNullable(insert.getListRelation())
                    .filter(listMap -> listMap.containsKey(Systems.ADD))
                    .map(listMap -> listMap.get(Systems.ADD))
                    .filter(list -> list.size() == 1)
                    .orElseThrow(() -> new CheckException("must specify join list. and can only join one list"));

            Optional.ofNullable(insert.getPlanRelation())
                    .filter(planMap -> planMap.containsKey(Systems.ADD))
                    .map(planMap -> planMap.get(Systems.ADD))
                    .filter(plans -> !plans.isEmpty())
                    .ifPresent(plans -> {
                        if (plans.size() > 1) throw new CheckException("You can only add one join plan.");
                    });
            Optional.ofNullable(insert.getProjectRelation())
                    .filter(projectMap -> projectMap.containsKey(Systems.ADD))
                    .map(projectMap -> projectMap.get(Systems.ADD))
                    .filter(projects -> !projects.isEmpty())
                    .ifPresent(projects -> {
                        if (Optional.ofNullable(insert.getPlanRelation())
                                .filter(planMap -> planMap.containsKey(Systems.ADD))
                                .filter(planMap -> !planMap.get(Systems.ADD).isEmpty())
                                .isPresent())   //如果目标已存在，删除关联项目
                            insert.setProjectRelation(Systems.ADD, Lists.newArrayList());
                        else if (projects.size() > 1)
                            throw new CheckException("You can only add one join project.");   //关联项目数量大于1
                    });
            Optional.of(insert).filter(task -> !(!Optional.ofNullable(task.getPlanRelation())
                    .filter(planMap -> planMap.containsKey(Systems.ADD))
                    .map(planMap -> planMap.get(Systems.ADD))
                    .filter(plans -> !plans.isEmpty())
                    .isPresent() &&
                    !Optional.ofNullable(task.getProjectRelation())
                            .filter(projectMap -> projectMap.containsKey(Systems.ADD))
                            .map(projectMap -> projectMap.get(Systems.ADD))
                            .filter(projects -> !projects.isEmpty())
                            .isPresent()))
                    .orElseThrow(() -> new CheckException("project and plan must choose between one")); //项目和目标之间必须选择一个
            insert.setTaskStatus(FruitDict.TaskDict.START.name());
            insert.setTaskLevel(FruitDict.TaskDict.LOW.name());
            this.insert(insert);
        } catch (ExceptionSupport ex) {
            throw ex;
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            throw new CheckException("插入任务时，发生未处理异常");
        }

    }

    public final void modify(FruitTask.Update update) {
        try {
            this.findTask(update.getUuid());
            Optional.of(update)
                    .map(FruitTask.Update::getTitle).
                    filter(StringUtils::isNotBlank)
                    .orElseThrow(() -> new CheckException("title can't null"));
            Optional.ofNullable(update.getListRelation())
                    .filter(listMap -> listMap.containsKey(Systems.ADD))
                    .map(listMap -> listMap.get(Systems.ADD))
                    .filter(lists -> !lists.isEmpty())
                    .ifPresent(lists -> {
                        if (lists.size() > 1) throw new CheckException("You can only add one related list");
                        update.getListRelation().put(Systems.DELETE, Lists.newArrayList(TaskListRelation.newInstance(null, null)));
                    });
            Optional.ofNullable(update.getPlanRelation())
                    .filter(planMap -> planMap.containsKey(Systems.ADD))
                    .map(planMap -> planMap.get(Systems.ADD))
                    .filter(plans -> !plans.isEmpty())
                    .ifPresent(plans -> {
                        if (plans.size() > 1) throw new CheckException("You can only add one related plan.");
                        update.setPlanRelation(Systems.DELETE, Lists.newArrayList(TaskPlanRelation.newInstance(null, null)));
                        update.setProjectRelation(Systems.DELETE, Lists.newArrayList(TaskProjectRelation.newInstance(null, null)));
                    });
            Optional.ofNullable(update.getProjectRelation())
                    .filter(projectMap -> projectMap.containsKey(Systems.ADD))
                    .map(projectMap -> projectMap.get(Systems.ADD))
                    .filter(projects -> !projects.isEmpty())
                    .ifPresent(projects -> {
                        if (Optional.of(update.getPlanRelation())
                                .filter(planMap -> planMap.containsKey(Systems.ADD))
                                .filter(planMap -> !planMap.get(Systems.ADD).isEmpty())
                                .isPresent())   //如果目标已存在，删除关联项目
                            update.setProjectRelation(Systems.ADD, Lists.newArrayList());
                        else if (projects.size() > 1)
                            throw new CheckException("You can only add one join project.");   //关联项目数量大于1
                        update.setPlanRelation(Systems.DELETE, Lists.newArrayList(TaskPlanRelation.newInstance(null, null)));
                        update.setProjectRelation(Systems.DELETE, Lists.newArrayList(TaskProjectRelation.newInstance(null, null)));
                    });
            update.setTitle(update.getTitle());
            update.setTaskLevel(update.getTaskLevel());
            update.setDescription(update.getDescription());
            update.setUserRelation(update.getUserRelation());
            this.update(update, taskExample -> taskExample.createCriteria().andUuidEqualTo(update.getUuid()));
        } catch (ExceptionSupport ex) {
            throw ex;
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            throw new CheckException("修改任务时，发生未处理异常");
        }

    }

    public final void changeStatusToComplete(FruitTaskVo vo) {
        try {
            vo.checkUuid();
            FruitTask.Update update = new FruitTask.Update();
            update.setTaskStatus(FruitDict.TaskDict.COMPLETE.name());
            update.setStatusDescription(vo.getStatusDescription());
            update.setEndDate(new Date());
            update(update, taskExample -> taskExample.createCriteria().andUuidEqualTo(vo.getUuidVo()));
        } catch (ExceptionSupport ex) {
            throw ex;
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            throw new ServiceException("更改任务状态为已结束时发生错误");
        }
    }

    public final void changeStatusToStart(FruitTaskVo vo) {
        try {
            vo.checkUuid();
            FruitTask.Update update = new FruitTask.Update();
            update.setTaskStatus(FruitDict.TaskDict.START.name());
            update.setStatusDescription(vo.getStatusDescription());
            update(update, taskExample -> taskExample.createCriteria().andUuidEqualTo(vo.getUuidVo()));
        } catch (ExceptionSupport ex) {
            throw ex;
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            throw new ServiceException("更改任务状态为开始时发生错误");
        }
    }

    public final void changeStatusToEnd(FruitTaskVo vo) {
        try {
            vo.checkUuid();
            if (StringUtils.isBlank(vo.getStatusDescription()))
                throw new CheckException("必须填写关闭描述");
            FruitTask.Update update = new FruitTask.Update();
            update.setTaskStatus(FruitDict.TaskDict.END.name());
            update.setStatusDescription(vo.getStatusDescription());
            update.setEndDate(new Date());
            update(update, taskExample -> taskExample.createCriteria().andUuidEqualTo(vo.getUuidVo()));
        } catch (ExceptionSupport ex) {
            throw ex;
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            throw new ServiceException("关闭任务时发生错误");
        }
    }

    public final void changeList(FruitTaskVo vo) {
        try {
            vo.checkUuid();
            FruitTask.Update update = new FruitTask.Update();
            update.setUuid(vo.getUuidVo());
            update.setListRelation(vo.getListRelation());
            Optional.ofNullable(update.getListRelation())
                    .filter(task -> task.containsKey(Systems.ADD))
                    .filter(task -> !task.isEmpty())
                    .orElseThrow(() -> new CheckException("没有目标源，无法切换列表"));
            /*每次切换列表时，都删除旧的关联列表*/
            update.getListRelation().put(Systems.DELETE, Lists.newArrayList(TaskListRelation.newInstance(null, null)));
            this.update(update, taskExample -> taskExample.createCriteria().andUuidEqualTo(vo.getUuidVo()));
        } catch (ExceptionSupport ex) {
            throw ex;
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            throw new ServiceException("更改任务所属列表时发生错误");
        }
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
        Optional<FruitTaskDao> data = this.findByExample(example -> example.createCriteria()
                .andIsDeletedEqualTo(Systems.N.name())
                .andUuidEqualTo(uuid)).stream().findAny();
        if (!data.isPresent())
            throw new CheckException("任务不存在");
        return data.get();
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
        final List<FruitListDao> lists = Lists.newLinkedList();
        Consumer<FruitListExample> listExampleConsumer = listExample -> {
            FruitListExample.Criteria criteria = listExample.createCriteria();
            if (StringUtils.isNotBlank(vo.getListTitle()))
                criteria.andTitleLike(MessageFormat.format("%{0}%", vo.getListTitle()));
            criteria.andIsDeletedEqualTo(Systems.N.name());
        };
        List<FruitTaskDao> tasks = this.findByListExampleAndProjectId((taskExample) -> {
            if (StringUtils.isNotBlank(vo.getTitle()))
                taskExample.createCriteria().andTitleLike(MessageFormat.format("%{0}%", vo.getTitle()));
        }, listExampleConsumer, projectId);
        DaoThread taskThread = DaoThread.getFixed();
        taskThread
                .execute(this.plugUser(tasks))
                .execute(this.plugUtil(tasks))
                .execute(() -> lists.addAll(findProjectList(projectId, listExampleConsumer)));
        Map<String, List<FruitTaskDao>> listMap = tasks.parallelStream().collect(groupingBy(FruitTaskDao::getListId));
        taskThread.get().shutdown();
        lists.parallelStream().forEach(list -> list.setTasks(listMap.get(list.getUuid())));
        if (StringUtils.isNotBlank(vo.getTitle()))
            return lists.stream().filter(list -> list.getTasks() != null && !list.getTasks().isEmpty()).collect(toList());
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
                .execute(this.plugProject(Lists.newArrayList(taskInfo)))
                .execute(this.plugList(Lists.newArrayList(taskInfo)))
                .get();
        return taskInfo;
    }

    public List<FruitTaskUser> findContainUserListByUserIdByProjectId(Consumer<FruitTaskExample> exampleConsumer, List<String> userIds, String projectId) {
        return this.findUserByProjectIdAndUserIdAndTaskExample(exampleConsumer, userIds, projectId);
    }

    /************************************************************************************************
     *                                       个人中心专供                                            *
     ************************************************************************************************/
    public List<FruitTaskDao> myTask(FruitTaskVo vo) {
        return Optional.of(this.myTask(example -> {
            FruitTaskExample.Criteria criteria = example.createCriteria();
            if (StringUtils.isNotBlank(vo.getTitle()))
                criteria.andTitleLike(MessageFormat.format("%{0}%", vo.getTitle()));
            if (StringUtils.isNotBlank(vo.getTaskStatus()))
                criteria.andTaskStatusIn(Lists.newArrayList(StringUtils.split(vo.getTaskStatus(), ",")));
            example.setOrderByClause(Optional.ofNullable(vo.sortConstrue("task")).filter(StringUtils::isNotBlank).orElse("task.create_date_time desc"));
        }, vo.getProjectId())).filter(tasks -> !tasks.isEmpty()).map(tasks -> {
            DaoThread thread = DaoThread.getFixed();
            thread.execute(this.plugUtil(tasks))
                    .execute(this.plugUser(tasks))
                    .execute(this.plugList(tasks))
                    .execute(this.plugProject(tasks)).get().shutdown();
            return tasks;
        }).orElseGet(ArrayList::new);
    }

    public List<FruitTaskDao> myCreateTask(FruitTaskVo vo) {
        return Optional.of(this.myCreateTask(example -> {
            FruitTaskExample.Criteria criteria = example.createCriteria();
            if (StringUtils.isNotBlank(vo.getTitle()))
                criteria.andTitleLike(MessageFormat.format("%{0}%", vo.getTitle()));
            if (StringUtils.isNotBlank(vo.getTaskStatus()))
                criteria.andTaskStatusEqualTo(vo.getTaskStatus());
            example.setOrderByClause(Optional.ofNullable(vo.sortConstrue("task")).filter(StringUtils::isNotBlank).orElse("task.create_date_time desc"));
        }, vo.getProjectId())).filter(tasks -> !tasks.isEmpty()).map(tasks -> {
            DaoThread thread = DaoThread.getFixed();
            thread.execute(this.plugUtil(tasks))
                    .execute(this.plugUser(tasks))
                    .execute(this.plugList(tasks))
                    .execute(this.plugProject(tasks)).get().shutdown();
            return tasks;
        }).orElseGet(ArrayList::new);
    }

    private Callable plugLogs(final List<FruitTaskDao> tasks) {
        return () -> {
            long start = System.currentTimeMillis();
            if (tasks == null || tasks.isEmpty()) return false;
            Map<String, LinkedList<FruitLogs.Info>> logs = this.findJoinLogsByTask(tasks.parallelStream().map(FruitTaskDao::getUuid).collect(toList()));
            tasks.forEach(task -> {
                if (logs.containsKey(task.getUuid()))
                    task.setLogs(logs.get(task.getUuid()));
                else
                    task.setLogs(Lists.newLinkedList());
            });
            logger.info("日志耗时：" + (start - System.currentTimeMillis()));
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
            Map<String, List<FruitTaskProject>> projectMap = this.findProjectByTask(tasks.parallelStream().map(FruitTaskDao::getUuid).collect(toList()))
                    .stream().collect(groupingBy(FruitTaskProject::getTaskId));
            tasks.forEach((task) -> task.setProject(projectMap.containsKey(task.getUuid()) ? projectMap.get(task.getUuid()).stream().findAny().orElse(null) : task.getProject()));
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

    private Callable plugUser(final List<FruitTaskDao> tasks) {
        return () -> {
            if (tasks == null || tasks.isEmpty()) return false;
            this.plugUserSupplier(tasks).get().ifPresent(userMap -> tasks.forEach((task) -> task.setUsers(userMap.get(task.getUuid()))));
            return true;
        };
    }

    public Supplier<Optional<Map<String, LinkedList<FruitTaskUser>>>> plugUserSupplier(final List<? extends FruitTask> tasks) {
        return () -> {
            if (tasks == null || tasks.isEmpty()) return Optional.empty();
            return Optional.of(this.findJoinUserByTaskIds(tasks.parallelStream().map(FruitTask::getUuid).collect(toList()))
                    .stream()
                    .collect(groupingBy(FruitTaskUser::getTaskId, toCollection(LinkedList::new))));
        };
    }

    private Callable plugUtil(List<FruitTaskDao> tasks) {
        return () -> {
            if (tasks == null || tasks.isEmpty()) return false;
            tasks.forEach(FruitTaskDao::computeDays);
            return true;
        };
    }

}