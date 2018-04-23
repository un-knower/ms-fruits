package wowjoy.fruits.ms.dao.task;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import org.apache.commons.lang.StringUtils;
import wowjoy.fruits.ms.dao.InterfaceDao;
import wowjoy.fruits.ms.exception.CheckException;
import wowjoy.fruits.ms.exception.ExceptionSupport;
import wowjoy.fruits.ms.exception.ServiceException;
import wowjoy.fruits.ms.module.AbstractEntity;
import wowjoy.fruits.ms.module.list.FruitList;
import wowjoy.fruits.ms.module.list.FruitListExample;
import wowjoy.fruits.ms.module.logs.FruitLogs;
import wowjoy.fruits.ms.module.logs.transfer.FruitTransferLogs;
import wowjoy.fruits.ms.module.relation.entity.*;
import wowjoy.fruits.ms.module.task.*;
import wowjoy.fruits.ms.module.task.FruitTaskVo.TaskTransferVo;
import wowjoy.fruits.ms.module.util.entity.FruitDict;
import wowjoy.fruits.ms.module.util.entity.FruitDict.Systems;
import wowjoy.fruits.ms.module.util.entity.FruitDict.TransferDict;
import wowjoy.fruits.ms.util.ApplicationContextUtils;
import wowjoy.fruits.ms.util.GsonUtils;

import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
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

    protected abstract List<FruitTask> findByExample(Consumer<FruitTaskExample> consumerExample);

    protected abstract void update(FruitTask.Update update, Consumer<FruitTaskExample> taskExampleConsumer);

    protected abstract void delete(FruitTask.Update update);

    /**
     * 查询关联项目的任务列表
     */
    protected abstract List<FruitTaskInfo> findByListExampleAndProjectId(Consumer<FruitTaskExample> exampleUnaryOperator, Consumer<FruitListExample> listExampleConsumer, String projectId, int pageNum, int pageSize);

    protected abstract List<FruitList> findProjectList(String projectId, Consumer<FruitListExample> listExampleConsumer);

    protected abstract List<FruitTaskUser> findJoinUserByTaskIds(List<String> taskIds);

    protected abstract List<FruitTaskPlan> findPlanByTaskIds(List<String> taskIds);

    protected abstract List<FruitTaskProject> findProjectByTask(List<String> taskIds);

    protected abstract List<FruitTaskList> findListByTask(List<String> taskIds);

    protected abstract Map<String, ArrayList<FruitLogs.Info>> findJoinLogsByTask(List<String> taskIds);

    protected abstract List<FruitTaskInfo> myTask(Consumer<FruitTaskExample> taskExampleConsumer, String projectId);

    protected abstract List<FruitTaskInfo> myCreateTask(Consumer<FruitTaskExample> taskExampleConsumer, String projectId);

    protected abstract List<FruitTaskUser> findUserByTaskExampleAndUserIdOrProjectId(Consumer<FruitTaskExample> exampleConsumer, List<String> userIds, String projectId);

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
            Optional.ofNullable(update.getListRelation())
                    .filter(listMap -> listMap.containsKey(Systems.ADD))
                    .map(listMap -> listMap.get(Systems.ADD))
                    .filter(lists -> !lists.isEmpty())
                    .ifPresent(lists -> {
                        if (lists.size() > 1) throw new CheckException("You can only add one related list");
                        Optional.ofNullable(this.findListByTask(Lists.newArrayList(update.getUuid())))
                                .flatMap(taskList -> taskList.stream().findAny())
                                .ifPresent(list -> update.getListRelation().put(Systems.BEFORE, Lists.newArrayList(TaskListRelation.newInstance(update.getUuid(), list.getUuid()))));
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
                        if (Optional.ofNullable(update.getPlanRelation())
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

    public final void changeStatusToComplete(String uuid) {
        try {
            Optional.ofNullable(uuid).filter(StringUtils::isNotBlank).orElseThrow(() -> new CheckException("taskId can't null"));
            FruitTask.Update update = new FruitTask.Update();
            update.setUuid(uuid);
            update.setTaskStatus(FruitDict.TaskDict.COMPLETE.name());
            update.setEndDate(new Date());
            update(update, taskExample -> taskExample.createCriteria().andUuidEqualTo(uuid));
        } catch (ExceptionSupport ex) {
            throw ex;
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            throw new ServiceException("更改任务状态为已结束时发生错误");
        }
    }

    public final void changeStatusToStart(String uuid) {
        try {
            Optional.ofNullable(uuid).filter(StringUtils::isNotBlank).orElseThrow(() -> new CheckException("taskId can't null"));
            FruitTask.Update update = new FruitTask.Update();
            update.setTaskStatus(FruitDict.TaskDict.START.name());
            update(update, taskExample -> taskExample.createCriteria().andUuidEqualTo(uuid));
        } catch (ExceptionSupport ex) {
            throw ex;
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            throw new ServiceException("更改任务状态为开始时发生错误");
        }
    }

    public final void changeStatusToEnd(FruitTask.Update update) {
        try {
            Optional<FruitTask.Update> optionalUpdate = Optional.ofNullable(update);
            optionalUpdate.map(FruitTask.Update::getUuid)
                    .filter(StringUtils::isNotBlank)
                    .orElseThrow(() -> new CheckException("taskId can't null"));
            optionalUpdate.map(FruitTask.Update::getStatusDescription)
                    .filter(StringUtils::isNotBlank)
                    .orElseThrow(() -> new CheckException("statusDescription can't null"));
            update.setTaskStatus(FruitDict.TaskDict.END.name());
            update.setEndDate(new Date());
            update(update, taskExample -> taskExample.createCriteria().andUuidEqualTo(update.getUuid()));
        } catch (ExceptionSupport ex) {
            throw ex;
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            throw new ServiceException("关闭任务时发生错误");
        }
    }

    public final void changeList(FruitTask.Update update) {
        try {
            Optional.ofNullable(update)
                    .map(FruitTask.Update::getUuid)
                    .filter(StringUtils::isNotBlank)
                    .orElseThrow(() -> new CheckException("taskId can't null"));
            Optional.ofNullable(update.getListRelation())
                    .filter(task -> task.containsKey(Systems.ADD))
                    .filter(task -> !task.isEmpty())
                    .orElseThrow(() -> new CheckException("没有目标源，无法切换列表"));
            /*每次切换列表时，都删除旧的关联列表*/
            update.getListRelation().put(Systems.DELETE, Lists.newArrayList(TaskListRelation.newInstance(null, null)));
            this.update(update, taskExample -> taskExample.createCriteria().andUuidEqualTo(update.getUuid()));
        } catch (ExceptionSupport ex) {
            throw ex;
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            throw new ServiceException("更改任务所属列表时发生错误");
        }
    }

    public final void delete(String uuid) {
        try {
            FruitTask.Update update = new FruitTask.Update();
            update.setUuid(uuid);
            this.delete(update);
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
    private FruitTask findTask(String uuid) {
        if (StringUtils.isBlank(uuid))
            throw new CheckException("查询标识不存在");
        Optional<FruitTask> data = this.findByExample(example -> example.createCriteria()
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
     * <p>
     * v3.0.0新增功能
     * 1、增加日期范围查询
     * 2、增加指定列表ID查询
     */
    public ArrayList<FruitTaskList> findJoinProjects(String projectId, final FruitTask.Search search) {
        /*当前用户userId*/
        final String userId = ApplicationContextUtils.getCurrentUser().getUserId();
        /*结果汇总*/
        final ArrayList<FruitTaskList> exportLists = Lists.newArrayList();
        /*列表查询条件*/
        Consumer<FruitListExample> listExampleConsumer = listExample -> {
            FruitListExample.Criteria criteria = listExample.createCriteria();
            /*列表标题查询*/
            Optional.ofNullable(search)
                    .filter(task -> StringUtils.isNotBlank(task.getListTitle()))
                    .ifPresent(task -> criteria.andTitleLike(MessageFormat.format("%{0}%", search.getListTitle())));
            /*列表id查询，支持编号查询*/
            Optional.ofNullable(search)
                    .filter(task -> StringUtils.isNotBlank(task.getLists()))
                    .map(task -> task.getLists().split(","))
                    .filter(lists -> lists.length > 0)
                    .ifPresent(lists -> criteria.andUuidIn(Lists.newArrayList(lists)));
            criteria.andIsDeletedEqualTo(Systems.N.name());
        };

        /*获取项目下所有列表*/
        List<FruitList> listList = this.findProjectList(projectId, listExampleConsumer);
        /*获取每一个列表下的任务*/
        ArrayList<CompletableFuture<Void>> futures = Optional.ofNullable(listList)
                .filter(lists -> !lists.isEmpty())
                .map(lists -> {
                    /*设定线程池大小，封顶一百*/
                    Executor executor = obtainExecutor.apply(lists.size());
                    return lists.stream()
                            .map(intoList -> (FruitTaskList) GsonUtils.newGson().fromJson(GsonUtils.newGson().toJsonTree(intoList), TypeToken.of(FruitTaskList.class).getType()))
                            .map(intoList -> CompletableFuture.supplyAsync(() -> this.findByListExampleAndProjectId(  //根据列表Example 和 项目ID 查询所有任务
                                    (taskExample) -> {  //查询列表关联任务
                                        FruitTaskExample.Criteria criteria = taskExample.createCriteria();
                                        Optional.ofNullable(search.getTitle())
                                                .filter(StringUtils::isNotBlank)
                                                .ifPresent(title -> criteria.andTitleLike(MessageFormat.format("%{0}%", title)));
                                        Optional.ofNullable(search)
                                                .filter(task -> Objects.nonNull(task.getBeginDateTime()))
                                                .filter(task -> Objects.nonNull(task.getEndDateTime()))
                                                .map(task -> criteria.andEndDateBetween(task.getBeginDateTime(), task.getEndDateTime()));
                                    }, listExample -> listExample.createCriteria().andUuidEqualTo(intoList.getUuid()), projectId, search.getPageNum(), search.getPageSize()), executor)
                                    .thenApply(tasks -> {   //查询关联信息
                                        CompletableFuture.allOf(
                                                CompletableFuture.supplyAsync(plugUtilSupplier(tasks)),
                                                CompletableFuture.supplyAsync(plugUserSupplier(tasks.stream().map(FruitTaskInfo::getUuid).collect(toList())))
                                                        .thenAccept(userMap -> tasks.parallelStream().forEach(task -> Optional.ofNullable(userMap.get(task.getUuid())).map(users -> {
                                                            users.sort((l, r) -> l.getUserId().equals(userId) ? -1 : 1);
                                                            return users;
                                                        }).ifPresent(task::setUsers)))).join();
                                        return tasks;
                                    })
                                    .thenAccept(tasks -> {
                                        intoList.setTasks(Optional.ofNullable(tasks)
                                                .map(this::sortDuet)
                                                .orElse(null));
                                        exportLists.add(intoList);
                                    })).collect(toCollection(Lists::newArrayList));
                }).orElseGet(ArrayList::new);
        /*等待结果完成*/
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()])).join();
        /*列表排序*/
        exportLists.sort(Comparator.comparing(AbstractEntity::getCreateDateTime).reversed());
        return Optional.ofNullable(search.getTitle()).filter(StringUtils::isNotBlank).map(title -> exportLists.stream().filter(list -> list.getTasks() != null && !list.getTasks().isEmpty()).collect(toCollection(ArrayList::new))).orElse(exportLists);
    }

    /*排序二重奏*/
    public ArrayList<FruitTask.Info> sortDuet(List<? extends FruitTask.Info> infos) {
        /*一奏*/
        Map<String, LinkedList<FruitTask.Info>> oneConcert = infos.parallelStream().collect(groupingBy(FruitTask.Info::getTaskStatus, toCollection(Lists::newLinkedList)));
        /*二奏*/
        ArrayList<FruitTask.Info> twoConcert = Optional.ofNullable(oneConcert.get(FruitDict.TaskDict.START.name())).map(starts -> {
            ArrayList<FruitTask.Info> copyStarts = new ArrayList<>(starts);
            copyStarts.sort(Comparator.comparing(FruitTask::getEstimatedEndDate));
            return copyStarts;
        }).orElseGet(Lists::newArrayList);
        twoConcert.addAll(Optional.ofNullable(oneConcert.get(FruitDict.TaskDict.COMPLETE.name())).map(completes -> {
            ArrayList<FruitTask.Info> copyCompletes = new ArrayList<>(completes);
            copyCompletes.sort((l, r) -> l.getEndDate() == null || r.getEndDate() == null ? 1 : r.getEndDate().compareTo(l.getEndDate()));
            return copyCompletes;
        }).orElseGet(Lists::newArrayList));
        twoConcert.addAll(Optional.ofNullable(oneConcert.get(FruitDict.TaskDict.END.name())).map(ends -> {
            ArrayList<FruitTask.Info> copyEnds = new ArrayList<>(ends);
            copyEnds.sort((l, r) -> l.getEndDate() == null || r.getEndDate() == null ? 1 : r.getEndDate().compareTo(l.getEndDate()));
            return copyEnds;
        }).orElseGet(Lists::newArrayList));
        return twoConcert;
    }

    public FruitTask.Info findTaskInfo(String uuid) {
        String userId = ApplicationContextUtils.getCurrentUser().getUserId();
        FruitTask.Info taskInfo = this.findTask(uuid).toInfo();
        Executor maxExecutor = obtainExecutor.apply(5);
        CompletableFuture.allOf(CompletableFuture.supplyAsync(plugLogsSupplier(Lists.newArrayList(taskInfo.getUuid())), maxExecutor)
                        .thenAccept(logsMap -> Optional.ofNullable(logsMap.get(taskInfo.getUuid())).ifPresent(taskInfo::setLogs)),
                CompletableFuture.supplyAsync(plugPlanSupplier(Lists.newArrayList(taskInfo.getUuid())), maxExecutor)
                        .thenAccept(planMap -> Optional.ofNullable(planMap.get(taskInfo.getUuid())).flatMap(plans -> plans.stream().findAny()).ifPresent(taskInfo::setPlan)),
                CompletableFuture.supplyAsync(plugUserSupplier(Lists.newArrayList(taskInfo.getUuid())), maxExecutor)
                        .thenAccept(userMap -> Optional.ofNullable(userMap.get(taskInfo.getUuid())).map(users -> {
                            users.sort((l, r) -> l.getUserId().equals(userId) ? -1 : 1);
                            return users;
                        }).ifPresent(taskInfo::setUsers)),
                CompletableFuture.supplyAsync(plugProjectSupplier(Lists.newArrayList(taskInfo.getUuid())), maxExecutor)
                        .thenAccept(projectMap -> Optional.ofNullable(projectMap.get(taskInfo.getUuid())).ifPresent(projectList -> taskInfo.setProject(projectList.stream().findAny().orElse(null)))),
                CompletableFuture.supplyAsync(plugListSupplier(Lists.newArrayList(taskInfo.getUuid())), maxExecutor)
                        .thenAccept(listMap -> Optional.ofNullable(listMap.get(taskInfo.getUuid())).flatMap(lists -> lists.stream().findAny()).ifPresent(taskInfo::setList))).join();
        return taskInfo;
    }

    public List<FruitTaskUser> findContainUserListByUserIdByProjectId(Consumer<FruitTaskExample> exampleConsumer, List<String> userIds, String projectId) {
        return this.findUserByTaskExampleAndUserIdOrProjectId(exampleConsumer, userIds, projectId);
    }

    /************************************************************************************************
     *                                       个人中心专供                                            *
     ************************************************************************************************/
    public List<FruitTask.Info> myTask(FruitTaskVo vo) {
        String userId = ApplicationContextUtils.getCurrentUser().getUserId();
        return Optional.of(this.myTask(example -> {
            FruitTaskExample.Criteria criteria = example.createCriteria();
            if (StringUtils.isNotBlank(vo.getTitle()))
                criteria.andTitleLike(MessageFormat.format("%{0}%", vo.getTitle()));
            if (StringUtils.isNotBlank(vo.getTaskStatus()))
                criteria.andTaskStatusIn(Lists.newArrayList(StringUtils.split(vo.getTaskStatus(), ",")));
        }, vo.getProjectId())).filter(tasks -> !tasks.isEmpty()).map(tasks -> {
            List<FruitTask.Info> sortAfterTasks = sortDuet(tasks);
            CompletableFuture.allOf(CompletableFuture.supplyAsync(plugUtilSupplier(sortAfterTasks)),
                    CompletableFuture.supplyAsync(plugUserSupplier(sortAfterTasks.stream().map(FruitTask.Info::getUuid).collect(toList())))
                            .thenAccept(userMap -> sortAfterTasks.parallelStream().forEach(task -> Optional.ofNullable(userMap.get(task.getUuid())).map(users -> {
                                users.sort((l, r) -> l.getUserId().equals(userId) ? -1 : 1);
                                return users;
                            }).ifPresent(task::setUsers))),
                    CompletableFuture.supplyAsync(plugListSupplier(sortAfterTasks.stream().map(FruitTask.Info::getUuid).collect(toList())))
                            .thenAccept(listMap -> sortAfterTasks.parallelStream().forEach(task -> Optional.ofNullable(listMap.get(task.getUuid())).flatMap(lists -> lists.stream().findAny()).ifPresent(task::setList))),
                    CompletableFuture.supplyAsync(this.plugProjectSupplier(sortAfterTasks.stream().map(FruitTask.Info::getUuid).collect(toList())))
                            .thenAccept(projectMap -> sortAfterTasks.parallelStream()
                                    .forEach(task -> Optional.ofNullable(projectMap.get(task.getUuid()))
                                            .flatMap(projects -> projects.stream().findAny())
                                            .ifPresent(task::setProject)))).join();
            return sortAfterTasks;
        }).orElseGet(ArrayList::new);
    }

    public List<FruitTask.Info> myCreateTask(FruitTaskVo vo) {
        String userId = ApplicationContextUtils.getCurrentUser().getUserId();
        return Optional.of(this.myCreateTask(example -> {
            FruitTaskExample.Criteria criteria = example.createCriteria();
            if (StringUtils.isNotBlank(vo.getTitle()))
                criteria.andTitleLike(MessageFormat.format("%{0}%", vo.getTitle()));
            if (StringUtils.isNotBlank(vo.getTaskStatus()))
                criteria.andTaskStatusEqualTo(vo.getTaskStatus());
            example.setOrderByClause(Optional.ofNullable(vo.sortConstrue("task")).filter(StringUtils::isNotBlank).orElse("task.create_date_time desc"));
        }, vo.getProjectId())).filter(tasks -> !tasks.isEmpty()).map(tasks -> {
            ArrayList<FruitTask.Info> sortAfterTasks = this.sortDuet(tasks);
            CompletableFuture.allOf(CompletableFuture.supplyAsync(plugUtilSupplier(sortAfterTasks)),
                    CompletableFuture.supplyAsync(plugUserSupplier(sortAfterTasks.stream().map(FruitTask.Info::getUuid).collect(toList())))
                            .thenAccept(userMap -> sortAfterTasks.parallelStream().forEach(task -> Optional.ofNullable(userMap.get(task.getUuid())).map(users -> {
                                users.sort((l, r) -> l.getUserId().equals(userId) ? -1 : 1);
                                return users;
                            }).ifPresent(task::setUsers))),
                    CompletableFuture.supplyAsync(plugListSupplier(sortAfterTasks.stream().map(FruitTask.Info::getUuid).collect(toList())))
                            .thenAccept(listMap -> sortAfterTasks.parallelStream().forEach(task -> Optional.ofNullable(listMap.get(task.getUuid())).flatMap(lists -> lists.stream().findAny()).ifPresent(task::setList))),
                    CompletableFuture.supplyAsync(this.plugProjectSupplier(sortAfterTasks.stream().map(FruitTask.Info::getUuid).collect(toList())))
                            .thenAccept(projectMap -> sortAfterTasks.parallelStream()
                                    .forEach(task -> Optional.ofNullable(projectMap.get(task.getUuid()))
                                            .flatMap(projects -> projects.stream().findAny())
                                            .ifPresent(task::setProject)))).join();
            return sortAfterTasks;
        }).orElseGet(ArrayList::new);
    }

    private Supplier<Map<String, ArrayList<FruitLogs.Info>>> plugLogsSupplier(final List<String> taskId) {
        return () -> Optional.ofNullable(taskId).filter(ids -> !ids.isEmpty()).map(this::findJoinLogsByTask).orElseGet(Maps::newHashMap);
    }

    private Supplier<Map<String, List<FruitTaskPlan>>> plugPlanSupplier(final List<String> taskId) {
        return () -> Optional.ofNullable(taskId).filter(ids -> !ids.isEmpty()).map(this::findPlanByTaskIds).map(plans -> plans.stream().collect(groupingBy(FruitTaskPlan::getTaskId))).orElseGet(Maps::newHashMap);
    }

    private Supplier<Map<String, List<FruitTaskProject>>> plugProjectSupplier(final List<String> tasks) {
        return () -> Optional.ofNullable(tasks).filter(ids -> !ids.isEmpty()).map(this::findProjectByTask).map(projectList -> projectList.stream().collect(groupingBy(FruitTaskProject::getTaskId))).orElseGet(Maps::newHashMap);
    }

    private Supplier<Map<String, List<FruitTaskList>>> plugListSupplier(final List<String> tasks) {
        return () -> Optional.ofNullable(tasks).filter(ids -> !ids.isEmpty()).map(this::findListByTask).map(lists -> lists.stream().collect(groupingBy(FruitTaskList::getTaskId))).orElseGet(Maps::newHashMap);
    }

    public Supplier<Map<String, LinkedList<FruitTaskUser>>> plugUserSupplier(final List<String> tasks) {
        return () -> Optional.ofNullable(tasks).filter(ids -> !ids.isEmpty()).map(this::findJoinUserByTaskIds).map(users -> users.stream()
                .collect(groupingBy(FruitTaskUser::getTaskId, toCollection(LinkedList::new)))).orElseGet(Maps::newHashMap);
    }

    private Supplier<Void> plugUtilSupplier(List<? extends FruitTask.Info> tasks) {
        return () -> {
            Optional.ofNullable(tasks).filter(infos -> !infos.isEmpty()).ifPresent(infos -> infos.forEach(FruitTask.Info::computeDays));
            return null;
        };
    }

}