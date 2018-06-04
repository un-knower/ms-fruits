package wowjoy.fruits.ms.dao.task;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import wowjoy.fruits.ms.dao.InterfaceDao;
import wowjoy.fruits.ms.exception.CheckException;
import wowjoy.fruits.ms.exception.ExceptionSupport;
import wowjoy.fruits.ms.exception.ServiceException;
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

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
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

    protected abstract Page<FruitTaskInfo> findPages(Consumer<FruitTask.Search> searchConsumer);

    protected abstract Page<FruitTaskInfo> myTask(Consumer<FruitTaskExample> taskExampleConsumer, String projectId, Integer pageNum, Integer pageSize);

    protected abstract Page<FruitTaskInfo> myCreateTask(Consumer<FruitTaskExample> taskExampleConsumer, String projectId, Integer pageNum, Integer pageSize);

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
        if (transferVo.getTransferUser() == null || transferVo.getTransferUser().isEmpty())
            throw new CheckException(FruitDict.Exception.Check.TRANSFER_USER_NULL.name());
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
        Optional.of(insert)
                .map(FruitTask.Insert::getTitle)
                .filter(StringUtils::isNotBlank)
                .orElseThrow(() -> new CheckException(FruitDict.Exception.Check.TASK_TITLE_NULL.name()));
        Optional.of(insert)
                .map(FruitTask.Insert::getEstimatedEndDate)
                .filter(Objects::nonNull)
                .orElseThrow(() -> new CheckException(FruitDict.Exception.Check.TASK_ESTIMATED_END_DATE_NULL.name()));
        Optional.ofNullable(insert.getListRelation())
                .filter(listMap -> listMap.containsKey(Systems.ADD))
                .map(listMap -> listMap.get(Systems.ADD))
                .filter(list -> list.size() == 1)
                .orElseThrow(() -> new CheckException(FruitDict.Exception.Check.TASK_JOIN_LIST.name()));

        Optional.ofNullable(insert.getPlanRelation())
                .filter(planMap -> planMap.containsKey(Systems.ADD))
                .map(planMap -> planMap.get(Systems.ADD))
                .filter(plans -> !plans.isEmpty())
                .ifPresent(plans -> {
                    if (plans.size() > 1) throw new CheckException(FruitDict.Exception.Check.TASK_JOIN_PLAN.name());
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
                        throw new CheckException(FruitDict.Exception.Check.TASK_JOIN_PROJECT.name());   //关联项目数量大于1
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
                .orElseThrow(() -> new CheckException(FruitDict.Exception.Check.TASK_PROJECT_OR_PLAN.name())); //项目和目标之间必须选择一个
        insert.setTaskStatus(FruitDict.TaskDict.START.name());
        insert.setTaskLevel(FruitDict.TaskDict.LOW.name());
        this.insert(insert);
    }

    public final void modify(FruitTask.Update update) {
        try {
            this.findTask(update.getUuid());
            Optional.ofNullable(update.getListRelation())
                    .filter(listMap -> listMap.containsKey(Systems.ADD))
                    .map(listMap -> listMap.get(Systems.ADD))
                    .filter(lists -> !lists.isEmpty())
                    .ifPresent(lists -> {
                        if (lists.size() > 1) throw new CheckException(FruitDict.Exception.Check.TASK_JOIN_LIST.name());
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
                        if (plans.size() > 1) throw new CheckException(FruitDict.Exception.Check.TASK_JOIN_PLAN.name());
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
                            throw new CheckException(FruitDict.Exception.Check.TASK_JOIN_PROJECT.name());   //关联项目数量大于1
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
            Optional.ofNullable(uuid).filter(StringUtils::isNotBlank).orElseThrow(() -> new CheckException(FruitDict.Exception.Check.SYSTEM_NULL.name()));
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
            Optional.ofNullable(uuid).filter(StringUtils::isNotBlank).orElseThrow(() -> new CheckException(FruitDict.Exception.Check.SYSTEM_NULL.name()));
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
                    .orElseThrow(() -> new CheckException(FruitDict.Exception.Check.SYSTEM_NULL.name()));
            optionalUpdate.map(FruitTask.Update::getStatusDescription)
                    .filter(StringUtils::isNotBlank)
                    .orElseThrow(() -> new CheckException(FruitDict.Exception.Check.TASK_END_DESCRIPTION_NULL.name()));
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
        Optional.ofNullable(update)
                .map(FruitTask.Update::getUuid)
                .filter(StringUtils::isNotBlank)
                .orElseThrow(() -> new CheckException(FruitDict.Exception.Check.SYSTEM_NULL.name()));
        Optional.ofNullable(update.getListRelation())
                .filter(task -> task.containsKey(Systems.ADD))
                .filter(task -> !task.isEmpty())
                .orElseThrow(() -> new CheckException(FruitDict.Exception.Check.TASK_LIST_NULL.name()));
        /*每次切换列表时，都删除旧的关联列表*/
        update.getListRelation().put(Systems.DELETE, Lists.newArrayList(TaskListRelation.newInstance(null, null)));
        this.update(update, taskExample -> taskExample.createCriteria().andUuidEqualTo(update.getUuid()));
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
            throw new CheckException(FruitDict.Exception.Check.SYSTEM_NULL.name());
        Optional<FruitTask> data = this.findByExample(example -> example.createCriteria()
                .andIsDeletedEqualTo(Systems.N.name())
                .andUuidEqualTo(uuid)).stream().findAny();
        if (!data.isPresent())
            throw new CheckException(FruitDict.Exception.Check.SYSTEM_NOT_EXISTS.name());
        return data.get();
    }

    /**
     * 任务重写：2018年05月21日15:09:14
     *
     * @param search
     * @return
     */
    public ArrayList<FruitTaskList> findByListPages(FruitTask.Search search) {
        String userId = ApplicationContextUtils.getCurrentUser().getUserId();
        return this.findProjectList(search.getProjectId(), listExample -> listExample.setOrderByClause("create_date_time desc")).parallelStream()
                .map(list -> {
                    FruitTaskList taskList = new FruitTaskList();
                    taskList.setTitle(list.getTitle());
                    taskList.setUuid(list.getUuid());
                    taskList.setCreateDateTime(list.getCreateDateTime());
                    return taskList;
                }).peek(taskList -> taskList.setTasks(Optional.ofNullable(findPage(taskList.getUuid(), search, userId)).map(Page::toPageInfo).orElse(null)))
                .collect(toCollection(ArrayList::new));
    }

    public Page<? extends FruitTask.Info> findPage(String listId, FruitTask.Search search, String userId) {
        return Optional.ofNullable(this.findPages(intoSearch -> {
            Optional.ofNullable(search.getTitle())
                    .filter(StringUtils::isNotBlank)
                    .ifPresent(title -> intoSearch.setTitle(title + "%"));
            Optional.of(search)     /*只包含开始时间时，自动填充结束时间为当前时间*/
                    .filter(defect -> defect.getBeginDateTime() != null && defect.getEndDateTime() == null)
                    .ifPresent(defect -> {
                        intoSearch.setBeginDateTime(
                                Date.from(LocalDateTime.ofInstant(defect.getBeginDateTime().toInstant(), ZoneId.systemDefault()).toLocalDate().atTime(0, 0, 0).atZone(ZoneId.systemDefault()).toInstant())
                        );
                        intoSearch.setEndDateTime(new Date());
                    });
            Optional.of(search)     /*只包含结束时间时，只查询小于结束时间*/
                    .filter(defect -> defect.getBeginDateTime() == null && defect.getEndDateTime() != null)
                    .ifPresent(defect -> intoSearch.setEndDateTime(
                            Date.from(LocalDateTime.ofInstant(defect.getEndDateTime().toInstant(), ZoneId.systemDefault()).toLocalDate().atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant())
                    ));
            Optional.of(search)     /*范围查询，包含开始和结束时间*/
                    .filter(defect -> defect.getBeginDateTime() != null && defect.getEndDateTime() != null)
                    .ifPresent(defect -> {
                        intoSearch.setBeginDateTime(
                                Date.from(LocalDateTime.ofInstant(defect.getBeginDateTime().toInstant(), ZoneId.systemDefault()).toLocalDate().atTime(0, 0, 0).atZone(ZoneId.systemDefault()).toInstant())
                        );
                        intoSearch.setEndDateTime(
                                Date.from(LocalDateTime.ofInstant(defect.getEndDateTime().toInstant(), ZoneId.systemDefault()).toLocalDate().atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant())
                        );
                    });
            intoSearch.setStatusIn(Lists.newArrayList(FruitDict.TaskDict.values()));
            intoSearch.setListIn(Lists.newArrayList(listId));
            intoSearch.setPageNum(search.getPageNum());
            intoSearch.setPageSize(search.getPageSize());
        })).map(tasks -> {
            CompletableFuture.allOf(
                    CompletableFuture.supplyAsync(plugUtilSupplier(tasks)),
                    CompletableFuture.supplyAsync(plugUserSupplier(tasks.stream().map(FruitTaskInfo::getUuid).collect(toList())))
                            .thenAccept(userMap -> tasks.parallelStream().forEach(task -> Optional.ofNullable(userMap.get(task.getUuid())).map(users -> {
                                users.sort((l, r) -> l.getUserId().equals(userId) ? -1 : 1);
                                return users;
                            }).ifPresent(task::setUsers)))).join();
            return tasks;
        }).orElseGet(Page::new);
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
    public PageInfo<? extends FruitTask.Info> myTask(FruitTask.Search search) {
        String userId = ApplicationContextUtils.getCurrentUser().getUserId();
        return Optional.of(this.myTask(example -> {
            FruitTaskExample.Criteria criteria = example.createCriteria();
            if (StringUtils.isNotBlank(search.getTitle()))
                criteria.andTitleLike(MessageFormat.format("{0}%", search.getTitle()));
            if (StringUtils.isNotBlank(search.getTaskStatus()))
                criteria.andTaskStatusIn(Lists.newArrayList(StringUtils.split(search.getTaskStatus(), ",")));
        }, Optional.ofNullable(search.getProjectId()).filter(StringUtils::isNotBlank).orElse(null), search.getPageNum(), search.getPageSize())).filter(tasks -> !tasks.isEmpty()).map(tasks -> {
            CompletableFuture.allOf(CompletableFuture.supplyAsync(plugUtilSupplier(tasks)),
                    CompletableFuture.supplyAsync(plugUserSupplier(tasks.stream().map(FruitTask.Info::getUuid).collect(toList())))
                            .thenAccept(userMap -> tasks.parallelStream().forEach(task -> Optional.ofNullable(userMap.get(task.getUuid())).map(users -> {
                                users.sort((l, r) -> l.getUserId().equals(userId) ? -1 : 1);
                                return users;
                            }).ifPresent(task::setUsers))),
                    CompletableFuture.supplyAsync(plugListSupplier(tasks.stream().map(FruitTask.Info::getUuid).collect(toList())))
                            .thenAccept(listMap -> tasks.parallelStream().forEach(task -> Optional.ofNullable(listMap.get(task.getUuid())).flatMap(lists -> lists.stream().findAny()).ifPresent(task::setList))),
                    CompletableFuture.supplyAsync(this.plugProjectSupplier(tasks.stream().map(FruitTask.Info::getUuid).collect(toList())))
                            .thenAccept(projectMap -> tasks.parallelStream()
                                    .forEach(task -> Optional.ofNullable(projectMap.get(task.getUuid()))
                                            .flatMap(projects -> projects.stream().findAny())
                                            .ifPresent(task::setProject)))).join();
            return tasks.toPageInfo();
        }).orElse(null);
    }

    public PageInfo<? extends FruitTask.Info> myCreateTask(FruitTask.Search search) {
        String userId = ApplicationContextUtils.getCurrentUser().getUserId();
        return Optional.of(this.myCreateTask(example -> {
            FruitTaskExample.Criteria criteria = example.createCriteria();
            if (StringUtils.isNotBlank(search.getTitle()))
                criteria.andTitleLike(MessageFormat.format("{0}%", search.getTitle()));
            if (StringUtils.isNotBlank(search.getTaskStatus()))
                criteria.andTaskStatusEqualTo(search.getTaskStatus());
        }, Optional.ofNullable(search.getProjectId()).filter(StringUtils::isNotBlank).orElse(null), search.getPageNum(), search.getPageSize())).filter(tasks -> !tasks.isEmpty()).map(tasks -> {
            CompletableFuture.allOf(CompletableFuture.supplyAsync(plugUtilSupplier(tasks)),
                    CompletableFuture.supplyAsync(plugUserSupplier(tasks.stream().map(FruitTask.Info::getUuid).collect(toList())))
                            .thenAccept(userMap -> tasks.parallelStream().forEach(task -> Optional.ofNullable(userMap.get(task.getUuid())).map(users -> {
                                users.sort((l, r) -> l.getUserId().equals(userId) ? -1 : 1);
                                return users;
                            }).ifPresent(task::setUsers))),
                    CompletableFuture.supplyAsync(plugListSupplier(tasks.stream().map(FruitTask.Info::getUuid).collect(toList())))
                            .thenAccept(listMap -> tasks.parallelStream().forEach(task -> Optional.ofNullable(listMap.get(task.getUuid())).flatMap(lists -> lists.stream().findAny()).ifPresent(task::setList))),
                    CompletableFuture.supplyAsync(this.plugProjectSupplier(tasks.stream().map(FruitTask.Info::getUuid).collect(toList())))
                            .thenAccept(projectMap -> tasks.parallelStream()
                                    .forEach(task -> Optional.ofNullable(projectMap.get(task.getUuid()))
                                            .flatMap(projects -> projects.stream().findAny())
                                            .ifPresent(task::setProject)))).join();
            return tasks.toPageInfo();
        }).orElse(null);
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