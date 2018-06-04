package wowjoy.fruits.ms.dao.plan;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.lang.StringUtils;
import wowjoy.fruits.ms.dao.InterfaceDao;
import wowjoy.fruits.ms.exception.CheckException;
import wowjoy.fruits.ms.exception.MessageException;
import wowjoy.fruits.ms.module.logs.FruitLogs;
import wowjoy.fruits.ms.module.logs.FruitLogsVo;
import wowjoy.fruits.ms.module.plan.FruitPlan;
import wowjoy.fruits.ms.module.plan.FruitPlanSummaryDao;
import wowjoy.fruits.ms.module.plan.FruitPlanUser;
import wowjoy.fruits.ms.module.plan.FruitPlanVo;
import wowjoy.fruits.ms.module.plan.example.FruitPlanExample;
import wowjoy.fruits.ms.module.task.FruitTask;
import wowjoy.fruits.ms.module.task.FruitTaskExample;
import wowjoy.fruits.ms.module.task.FruitTaskUser;
import wowjoy.fruits.ms.module.user.FruitUser;
import wowjoy.fruits.ms.module.util.entity.FruitDict;
import wowjoy.fruits.ms.module.util.entity.FruitDict.PlanDict;
import wowjoy.fruits.ms.util.ApplicationContextUtils;
import wowjoy.fruits.ms.util.DateUtils;

import java.text.MessageFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.*;

public abstract class AbstractDaoPlan implements InterfaceDao {
    /*********************************************************************************
     * 抽象接口，私有，因为对外的公共接口用来书写业务层，发布api必须在自己的控制范围内，不发布无用的接口。*
     *********************************************************************************/

    protected abstract ArrayList<FruitPlan> findByProjectId(Consumer<FruitPlanExample> exampleConsumer, String projectId, Integer pageNum, Integer pageSize, boolean isPage);

    protected abstract List<FruitPlan> findByExample(Consumer<FruitPlanExample> exampleConsumer);

    protected abstract List<FruitPlanUser> findUserByPlanIds(List<String> planIds, String currentUserId);

    protected abstract Optional<Map<String, ArrayList<FruitTask.Info>>> findTaskByTaskExampleAndPlanIds(Consumer<FruitTaskExample> taskConsumer, List<String> planIds, String userId);

    protected abstract Map<String, ArrayList<FruitLogs.Info>> findLogsByPlanIds(List<String> planIds);

    protected abstract void insertLogs(Consumer<FruitLogsVo> vo);

    protected abstract Optional<FruitPlan> findByUUID(String uuid);

    protected abstract void insert(FruitPlan.Insert dao);

    protected abstract void update(Consumer<FruitPlan.Update> planDaoConsumer, Consumer<FruitPlanExample> exampleConsumer);

    protected abstract List<FruitPlan> batchUpdateStatusAndReturnResult(Consumer<FruitPlan.Update> planDaoConsumer, Consumer<FruitPlanExample> fruitPlanExampleConsumer);

    protected abstract void delete(String uuid);

    protected abstract void deleteSummarys(FruitPlanSummaryDao dao);

    /*******************************
     * PUBLIC 函数，公共接口         *
     * 尽量保证规范，不直接调用dao接口 *
     *******************************/

    public final void delete(FruitPlanVo vo) {
        this.findByUUID(vo.getUuidVo()).orElseThrow(() -> new CheckException(FruitDict.Exception.Check.SYSTEM_NOT_EXISTS.name()));
        delete(vo.getUuidVo());
    }

    /**
     * 获取计划，分级展示
     */
    private List<FruitPlan.Info> findTree(FruitPlan.Query query, FruitUser currentUser) {
        this.getStartTimeAndEndTime(query);
        final List<FruitPlan> planMonthList = this.findByProjectId(example -> {
            FruitPlanExample.Criteria criteria = example.createCriteria();
            criteria.andParentIdIsNull();
            if (Objects.nonNull(query.getStartDate()) && Objects.nonNull(query.getEndDate()))
                criteria.andEstimatedEndDateBetween(query.getStartDate(), query.getEndDate());
            if (StringUtils.isNotBlank(query.getTitle()))
                criteria.andTitleLike(MessageFormat.format("%{0}%", query.getTitle()));
            criteria.andIsDeletedEqualTo(FruitDict.Systems.N.name());
            example.setOrderByClause("create_date_time desc");
            if (StringUtils.isNotBlank(query.sortConstrue()))
                example.setOrderByClause(query.sortConstrue());
        }, query.getProjectId(), 1, 10, false);
        if (planMonthList.isEmpty()) return Lists.newArrayList();
        /*plan -> planInfo*/
        final List<FruitPlan.Info> planInfoMonthList = planMonthList.parallelStream().map(FruitPlan::toInfo).collect(toList());
        /*SYSTEM Default Or Mysql Order*/
        List<FruitPlan.Info> sortAfterPlanMonth = Optional.ofNullable(query.sortConstrue()).filter(StringUtils::isNotBlank).map(construe -> planInfoMonthList).orElseGet(() -> this.sortDuet(planInfoMonthList));
        /*month - user*/
        CompletableFuture.allOf(CompletableFuture.supplyAsync(
                this.plugUser(sortAfterPlanMonth.parallelStream().map(FruitPlan::getUuid).collect(toList()), currentUser.getUserId()))
                        .thenAccept(planUsers -> sortAfterPlanMonth.parallelStream()
                                .forEach(info -> Optional.ofNullable(planUsers.get(info.getUuid())).map(users -> {
                                    users.sort((l, r) -> l.getUserId().equals(currentUser.getUserId()) ? -1 : 1);
                                    return users;
                                }).ifPresent(info::setUsers))),
                /*plan - week*/
                CompletableFuture.supplyAsync(() -> this.findByProjectId(
                        example -> {
                            FruitPlanExample.Criteria criteria = example.createCriteria();
                            if (StringUtils.isNotBlank(query.getTitle()))
                                criteria.andTitleLike(MessageFormat.format("%{0}%", query.getTitle()));
                            if (StringUtils.isNotBlank(query.getPlanStatus()))
                                criteria.andPlanStatusIn(Arrays.asList(query.getPlanStatus().split(",")));
                            criteria.andParentIdIsNotNull();
                            criteria.andIsDeletedEqualTo(FruitDict.Systems.N.name());
                            criteria.andParentIdIn(sortAfterPlanMonth.parallelStream().filter(Objects::nonNull).map(FruitPlan::getUuid).collect(toList()));
                            example.setOrderByClause("create_date_time desc");
                            if (StringUtils.isNotBlank(query.sortConstrue()))
                                example.setOrderByClause(query.sortConstrue());
                        },
                        query.getProjectId(),
                        1,
                        10,
                        false)
                ).thenCompose(weekPlan ->
                        /*week - user*/
                        CompletableFuture.supplyAsync(() -> {
                            final List<FruitPlan.Info> weekPlanInfo = weekPlan.parallelStream().map(FruitPlan::toInfo).collect(toList());
                            Map<String, LinkedList<FruitPlanUser>> userMap = this.plugUser(weekPlan.parallelStream().map(FruitPlan::getUuid).collect(toList()), currentUser.getUserId()).get();
                            /*sort + composite user info*/
                            return weekPlanInfo.parallelStream().map(plan -> {
                                Optional.ofNullable(userMap.get(plan.getUuid())).map(users -> {
                                    users.sort((l, r) -> l.getUserId().equals(currentUser.getUserId()) ? -1 : 1);
                                    return users;
                                }).ifPresent(plan::setUsers);
                                return plan;
                            }).collect(toList());
                        }).thenAccept(weekPlanInfo -> {
                            Map<String, ArrayList<FruitPlan.Info>> parentPlans = weekPlanInfo.stream().collect(groupingBy(FruitPlan.Info::getParentId, toCollection(ArrayList::new)));
                            sortAfterPlanMonth.parallelStream()
                                    .filter(info -> parentPlans.containsKey(info.getUuid()))
                                    .forEach(info -> info.setWeeks(Optional.ofNullable(query.sortConstrue()).filter(StringUtils::isNotBlank).map(construe -> parentPlans.get(info.getUuid())).orElseGet(() -> this.sortDuet(parentPlans.get(info.getUuid())))));
                        })
                )).join();
        return wherePlan(sortAfterPlanMonth, query);
    }

    /**
     * 如果目标子集存在数据，则保留子集。若不存在，判断父目标状态是否和查询状态相匹配
     * 如果匹配保留目标，不匹配过滤目标
     *
     * @param planDaoListSource
     * @param query
     * @return
     */
    private List<FruitPlan.Info> wherePlan(List<FruitPlan.Info> planDaoListSource, FruitPlan.Query query) {
        final Predicate<FruitPlan.Info> planWeekFilter = plan -> plan.getWeeks() != null && !plan.getWeeks().isEmpty();
        Predicate<FruitPlan.Info> chain = plan -> !planWeekFilter.test(plan);
        if (StringUtils.isNotBlank(query.getPlanStatus()))
            chain = chain.and(plan -> plan.getPlanStatus().equals(query.getPlanStatus()));
        if (StringUtils.isNotBlank(query.getTitle()))
            chain = chain.and(plan -> plan.getTitle().contains(query.getTitle()));
        final Predicate<FruitPlan.Info> planFilter = chain;
        return planDaoListSource.parallelStream().filter(plan -> planWeekFilter.test(plan) || planFilter.test(plan)).collect(toList());
    }

    private ArrayList<FruitPlan.Info> sortDuet(List<FruitPlan.Info> infos) {
        Map<String, LinkedList<FruitPlan.Info>> oneConcert = infos.parallelStream().collect(groupingBy(FruitPlan.Info::getPlanStatus, toCollection(Lists::newLinkedList)));
        ArrayList<FruitPlan.Info> sortAfterPlans = Optional.ofNullable(oneConcert.get(PlanDict.STAY_PENDING.name()))
                .map(stayPendingList -> {
                    stayPendingList.sort(comparing(FruitPlan::getEstimatedEndDate));
                    return new ArrayList<>(stayPendingList);
                }).orElseGet(Lists::newArrayList);
        sortAfterPlans.addAll(Optional.ofNullable(oneConcert.get(PlanDict.PENDING.name()))
                .map(pendingList -> {
                    pendingList.sort(comparing(FruitPlan::getEstimatedEndDate));
                    return new ArrayList<>(pendingList);
                }).orElseGet(Lists::newArrayList));
        sortAfterPlans.addAll(Optional.ofNullable(oneConcert.get(PlanDict.COMPLETE.name()))
                .map(completeList -> {
                    completeList.sort(comparing(FruitPlan::getEstimatedEndDate));
                    return new ArrayList<>(completeList);
                }).orElseGet(Lists::newArrayList));
        sortAfterPlans.addAll(Optional.ofNullable(oneConcert.get(PlanDict.END.name()))
                .map(endList -> {
                    endList.sort(comparing(FruitPlan::getEstimatedEndDate));
                    return new ArrayList<>(endList);
                }).orElseGet(Lists::newArrayList));
        return sortAfterPlans;
    }

    private Supplier<Map<String, LinkedList<FruitPlanUser>>> plugUser(List<String> planIds, String userId) {
        return () -> Optional.ofNullable(planIds)
                .filter(ids -> !ids.isEmpty())
                .map(ids -> this.findUserByPlanIds(ids, userId))
                .map(users -> users
                        .stream()
                        .collect(groupingBy(FruitPlanUser::getPlanId, toCollection(LinkedList::new)))
                ).orElseGet(HashMap::new);
    }

    private Supplier<Map<String, ArrayList<FruitLogs.Info>>> plugLogs(List<String> planIds) {
        return () -> Optional.ofNullable(planIds)
                .filter(ids -> !ids.isEmpty())
                .map(this::findLogsByPlanIds)
                .orElseGet(HashMap::new);
    }

    private Supplier<Map<String, ArrayList<FruitTask.Info>>> plugTask(List<String> planIds, String userId) {
        return () -> Optional.ofNullable(planIds)
                .filter(ids -> !ids.isEmpty())
                .flatMap(ids -> this.findTaskByTaskExampleAndPlanIds(example -> {
                }, ids, userId))
                .orElseGet(HashMap::new);
    }

    /**
     * 统计当前不同状态下数目
     * tips:
     * 进行中 and 未延期的不统计
     */
    private void dataCount(List<FruitPlan.Info> plans, Result result) {
        /*状态等于进行中的 and 待执行的*/
        Predicate<FruitPlan.Info> delay = plan -> PlanDict.PENDING.name().equals(plan.getPlanStatus())
                || PlanDict.STAY_PENDING.name().equals(plan.getPlanStatus());
        /*状态等于终止*/
        Predicate<FruitPlan.Info> end = plan -> PlanDict.END.name().equals(plan.getPlanStatus());
        /*状态等于完成*/
        Predicate<FruitPlan.Info> complete = plan -> PlanDict.COMPLETE.name().equals(plan.getPlanStatus());
        /*延期*/
        Predicate<FruitPlan.Info> day = plan -> plan.getDays() < 0;
        plans.forEach(plan -> {
            if (plan.getWeeks() != null && !plan.getWeeks().isEmpty()) dataCount(plan.getWeeks(), result);
            plan.computeDays();
            if (end.test(plan)) result.addStateType(PlanDict.END);
            else if (delay.and(day).test(plan)) result.addStateType(PlanDict.DELAY);
            else if (complete.test(plan))
                if (day.test(plan)) {
                    result.addStateType(PlanDict.DELAY_COMPLETE);
                } else {
                    result.addStateType(PlanDict.COMPLETE);
                }
        });
    }

    public Result compositeQuery(FruitPlan.Query query) {
        Result result = Result.getInstance();
        result.setPlans(findTree(query, ApplicationContextUtils.getCurrentUser()));
        dataCount(result.getPlans(), result);
        return result;
    }

    public ArrayList<FruitPlan> findList(FruitPlan.Query query) {
        if (StringUtils.isBlank(query.getProjectId()))
            throw new CheckException(FruitDict.Exception.Check.PLAN_PROJECT_NULL.name());
        this.getStartTimeAndEndTime(query);
        return this.findByProjectId(example -> {
            FruitPlanExample.Criteria criteria = example.createCriteria();
            if (Objects.nonNull(query.getStartDate()) && Objects.nonNull(query.getEndDate()))
                criteria.andEstimatedEndDateBetween(query.getStartDate(), query.getEndDate());
            if (StringUtils.isNotBlank(query.getPlanStatus()))
                criteria.andPlanStatusIn(Arrays.asList(query.getPlanStatus().split(",")));
            if (StringUtils.isNotBlank(query.getTitle()))
                criteria.andTitleLike(MessageFormat.format("%{0}%", query.getTitle()));
            criteria.andIsDeletedEqualTo(FruitDict.Systems.N.name());
            example.setOrderByClause("create_date_time desc");
            if (StringUtils.isNotBlank(query.sortConstrue()))
                example.setOrderByClause(query.sortConstrue());
        }, query.getProjectId(), 1, 10, false);
    }

    /*若年月不为空，则设置开始时间 and 结束时间*/
    private void getStartTimeAndEndTime(FruitPlan.Query query) {
        if (StringUtils.isBlank(query.getYear()) || StringUtils.isBlank(query.getMonth()))
            return;
        DateUtils.Month<DateUtils.Week.WeekChinese> month = DateUtils.getMonthByYearMonth(Integer.valueOf(query.getYear()), Integer.valueOf(query.getMonth()));
        query.setStartDate(Date.from(month.getStartDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        query.setEndDate(Date.from(month.getEndDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
    }

    public final FruitPlan findInfo(String planId) {
        final FruitUser currentUser = ApplicationContextUtils.getCurrentUser();
        FruitPlan.Info planInfo = this.findByUUID(planId).map(FruitPlan::toInfo).orElseThrow(() -> new CheckException(FruitDict.Exception.Check.SYSTEM_NOT_EXISTS.name()));
        CompletableFuture.allOf(
                CompletableFuture.supplyAsync(this.plugUser(Lists.newArrayList(planInfo.getUuid()), currentUser.getUserId())) //查询用户信息
                        .thenAccept(userMap -> Optional.ofNullable(userMap.get(planInfo.getUuid()))
                                .map(users -> {
                                    users.sort((l, r) -> l.getUserId().equals(currentUser.getUserId()) ? -1 : 1);
                                    return users;
                                })
                                .ifPresent(planInfo::setUsers)
                        ),
                CompletableFuture.supplyAsync(this.plugLogs(Lists.newArrayList(planInfo.getUuid()))) //查询日志信息
                        .thenAccept(logsMap -> Optional.ofNullable(logsMap)
                                .filter(map -> map.containsKey(planInfo.getUuid()))
                                .map(map -> map.get(planInfo.getUuid()))
                                .ifPresent(planInfo::setLogs)),
                CompletableFuture.supplyAsync(this.plugTask(Lists.newArrayList(planInfo.getUuid()), currentUser.getUserId())) //查询任务信息
                        .thenAccept(taskMap -> Optional.ofNullable(taskMap)
                                .filter(map -> map.containsKey(planInfo.getUuid()))
                                .map(map -> map.get(planInfo.getUuid()))
                                .ifPresent(planInfo::setTasks))
        ).join();
        return planInfo;
    }

    /**
     * 添加【项目】计划
     */
    public final void addJoinProject(FruitPlan.Insert insert) {
        Optional<FruitPlan.Insert> optionalVo = Optional.ofNullable(insert);
        optionalVo.filter(plan -> StringUtils.isNotBlank(plan.getTitle())).orElseThrow(() -> new CheckException(FruitDict.Exception.Check.PLAN_TITLE_NULL.name()));
        optionalVo.filter(plan -> plan.getEstimatedEndDate() != null).orElseThrow(() -> new CheckException(FruitDict.Exception.Check.PLAN_ESTIMATED_END_DATE.name()));
        optionalVo.filter(plan -> plan.getUserRelation().containsKey(FruitDict.Systems.ADD))
                .filter(plan -> !plan.getUserRelation().get(FruitDict.Systems.ADD).isEmpty())
                .orElseThrow(() -> new CheckException(FruitDict.Exception.Check.PLAN_ADD_LEAST_ONE_USER.name()));
        optionalVo.filter(plan -> plan.getProjectRelation().containsKey(FruitDict.Systems.ADD))
                .filter(plan -> !plan.getProjectRelation().get(FruitDict.Systems.ADD).isEmpty())
                .orElseThrow(() -> new CheckException(FruitDict.Exception.Check.PLAN_ADD_CAN_BUT_ONE_PROJECT.name()));
        insert.setPlanStatus(PlanDict.STAY_PENDING.name());
        insert.setEstimatedStartDate(insert.getEstimatedStartDate() != null ? insert.getEstimatedStartDate() : new Date());
        this.insert(insert);
    }

    /**
     * 修改计划
     */
    public final void modify(FruitPlan.Update update) {
        this.findByUUID(update.getUuid()).orElseThrow(() -> new CheckException(FruitDict.Exception.Check.SYSTEM_NOT_EXISTS.name()));
        this.update(dao -> {
            dao.setUuid(update.getUuid());
            dao.setTitle(update.getTitle());
            dao.setDescription(update.getDescription());
            dao.setEstimatedStartDate(update.getEstimatedStartDate());
            dao.setEstimatedEndDate(update.getEstimatedEndDate());
            dao.setPercent(update.getPercent());
            dao.setUserRelation(update.getUserRelation());
        }, fruitPlanExample -> fruitPlanExample.createCriteria().andUuidEqualTo(update.getUuid()));
    }

    /**
     * 终止计划
     */
    public final void end(FruitPlan.Update intoUpdate) {
        Optional<FruitPlan> optionalPlan = Optional.ofNullable(intoUpdate.getUuid())
                .filter(StringUtils::isNotBlank)
                .map(this::findByUUID).orElseThrow(() -> new CheckException(FruitDict.Exception.Check.SYSTEM_NOT_EXISTS.name()));
        optionalPlan.map(FruitPlan::getPlanStatus)
                .filter(status -> !PlanDict.END.name().equals(status))
                .filter(status -> !PlanDict.COMPLETE.name().equals(status))
                .orElseThrow(() -> new CheckException(FruitDict.Exception.Check.PLAN_NO_TO_END.name()));
        Optional.of(intoUpdate).filter(plan -> StringUtils.isNotBlank(plan.getStatusDescription())).orElseThrow(() -> new CheckException(FruitDict.Exception.Check.PLAN_END_STATUS.name()));
        /*校验是否是批量终止*/
        final FruitUser currentUser = ApplicationContextUtils.getCurrentUser();
        final LocalDateTime endDate = LocalDateTime.now();
        final Gson gson = new Gson();
        List<FruitPlan> planList = this.batchUpdateStatusAndReturnResult(update -> {
            update.setPlanStatus(PlanDict.END.name());
            update.setEndDate(endDate);
            update.setStatusDescription(intoUpdate.getStatusDescription());
        }, fruitPlanExample -> fruitPlanExample.createCriteria()
                .andParentIdEqualTo(intoUpdate.getUuid())
                .andIsDeletedEqualTo(FruitDict.Systems.N.name())
                .andPlanStatusIn(Lists.newArrayList(PlanDict.STAY_PENDING.name(), PlanDict.PENDING.name()))); //查询出所有待完成、进行中的计划

        this.checkThePlanInToBeCompleteTasks(Optional.ofNullable(planList.stream()
                .map(FruitPlan::getUuid).collect(toCollection(ArrayList::new)))
                .map(plans -> {
                    plans.add(intoUpdate.getUuid());
                    return plans;
                }).orElseGet(ArrayList::new), currentUser.getUserId()); //检查计划中是否有未完成或未终止的任务
        ArrayList<CompletableFuture<Boolean>> endPlanFutures = planList.stream().map(plan -> CompletableFuture.supplyAsync(() -> {
            this.insertLogs(logsVo -> {
                logsVo.setUserId(currentUser.getUserId());
                logsVo.setFruitUuid(plan.getUuid());
                logsVo.setFruitType(FruitDict.Parents.PLAN);
                logsVo.setOperateType(FruitDict.LogsDict.END);
                plan.setStatusDescription(intoUpdate.getStatusDescription());
                plan.setEndDate(endDate);
                logsVo.setJsonObject(gson.toJsonTree(plan).toString());
                logsVo.setVoObject(Optional.ofNullable(gson.toJsonTree(intoUpdate))
                        .filter(json -> !json.isJsonNull())
                        .map(JsonElement::getAsJsonObject)
                        .map(json -> {
                            json.addProperty("uuid", plan.getUuid());
                            return json;
                        }).orElseGet(JsonObject::new).toString()
                );
            });
            return true;
        })).collect(toCollection(ArrayList::new));
        /*主计划终止函数加入到线程队列中*/
        endPlanFutures.add(CompletableFuture.supplyAsync(() -> {
            this.update(update -> {
                update.setPlanStatus(PlanDict.END.name());
                update.setEndDate(endDate);
                update.setStatusDescription(intoUpdate.getStatusDescription());
            }, fruitPlanExample -> fruitPlanExample.createCriteria()
                    .andUuidEqualTo(intoUpdate.getUuid())
                    .andPlanStatusIn(Lists.newArrayList(PlanDict.STAY_PENDING.name(), PlanDict.PENDING.name())));
            return true;
        }));

        CompletableFuture.allOf(endPlanFutures.stream().toArray((IntFunction<CompletableFuture<?>[]>) CompletableFuture[]::new)).join(); //等待计划全部终止
    }

    /**
     * 完成计划
     * v2.5.0:
     * 1、目标没有待完成的任务才能完成，否则返回提示信息
     * lambda
     */
    public final void complete(FruitPlan.Update intoUpdate) {
        Optional<FruitPlan> optionalPlan = this.findByUUID(intoUpdate.getUuid());
        optionalPlan.orElseThrow(() -> new CheckException(FruitDict.Exception.Check.SYSTEM_NOT_EXISTS.name()));
        /*若结束时间不为空，那么结束时间必须小于等于当前*/
        Optional.of(intoUpdate).filter(plan -> !(plan.getEndDate() != null && Duration.between(LocalDate.now().atTime(0, 0, 0), LocalDateTime.ofInstant(plan.getEndDate().toInstant(), ZoneId.systemDefault()).withHour(0).withMinute(0).withSecond(0)).toDays() > 0))
                .orElseThrow(() -> new CheckException(FruitDict.Exception.Check.PLAN_END_DATE_GREATER_NOW.name()));
        /*等于已终止、已完成、待执行都拒绝执行*/
        optionalPlan.map(FruitPlan::getPlanStatus)
                .filter(status -> !PlanDict.END.name().equals(status))
                .filter(status -> !PlanDict.COMPLETE.name().equals(status))
                .filter(status -> !PlanDict.STAY_PENDING.name().equals(status))
                .orElseThrow(() -> new CheckException(FruitDict.Exception.Check.PLAN_NO_TO_COMPLETE.name()));
        /*如果延期，必须填写延期说明*/
        final FruitPlan.Info planInfo = optionalPlan.map(FruitPlan::toInfo)
                .filter(plan -> !(plan.computeDays().getDays() < 0 && StringUtils.isBlank(intoUpdate.getStatusDescription())))
                .orElseThrow(() -> new CheckException(FruitDict.Exception.Check.PLAN_DELAY_COMPLETE_REASON.name()));

        this.checkThePlanInToBeCompleteTasks(Lists.newArrayList(planInfo.getUuid()), ApplicationContextUtils.getCurrentUser().getUserId()); //检查计划中是否有未完成或未终止的任务
        this.update(update -> {
            update.setPlanStatus(PlanDict.COMPLETE.name());
            update.setEndDate(intoUpdate.getEndDate() == null ? LocalDateTime.now() : LocalDateTime.ofInstant(intoUpdate.getEndDate().toInstant(), ZoneId.systemDefault()));
            update.setStatusDescription(intoUpdate.getStatusDescription());
        }, fruitPlanExample -> fruitPlanExample.createCriteria().andUuidEqualTo(intoUpdate.getUuid()));
    }

    /*检查计划中待完成的任务*/
    private void checkThePlanInToBeCompleteTasks(ArrayList<String> planIds, String userId) {
        Optional.ofNullable(planIds)
                .filter(ids -> !ids.isEmpty())
                .flatMap(ids -> this.findTaskByTaskExampleAndPlanIds(example -> example.createCriteria().andTaskStatusIn(Lists.newArrayList(FruitDict.TaskDict.START.name())), ids, userId))
                .flatMap(planOfTasks -> planIds.stream().filter(planOfTasks::containsKey).map(planOfTasks::get).reduce((l, r) -> {
                    l.addAll(r);
                    return l;
                }))
                .ifPresent(tasks -> {
                    LinkedList<MessageException.RefuseToCompletePlan> refuseToCompletePlans = tasks.stream().map(task -> MessageException.RefuseToCompletePlan.newInstance(task.getTitle(), task.getUsers().stream().map(FruitTaskUser::getUserName).collect(joining("、"))))
                            .collect(toCollection(LinkedList::new));
                    throw new MessageException(new Gson().toJson(refuseToCompletePlans));
                });
    }

    /**
     * 待进行 -> 进行中
     */
    public final void pending(String uuid) {
        Optional<FruitPlan> optionalPlanDao = Optional.ofNullable(uuid).filter(StringUtils::isNotBlank).map(this::findByUUID).orElseThrow(() -> new CheckException(FruitDict.Exception.Check.SYSTEM_NOT_EXISTS.name()));
        /*不等于待执行的状态都拒绝*/
        optionalPlanDao.map(FruitPlan::getPlanStatus)
                .filter(status -> PlanDict.STAY_PENDING.name().equals(status))
                .orElseThrow(() -> new CheckException(FruitDict.Exception.Check.PLAN_STAY_PENDING_TO_PENDING.name()));
        this.update(update -> {
            update.setPlanStatus(PlanDict.PENDING.name());
            update.setStartDate(new Date());
        }, fruitPlanExample -> fruitPlanExample.createCriteria().andUuidEqualTo(uuid));
    }

    public static class Result {
        private final List<FruitPlan.Info> plans = Lists.newLinkedList();
        private final Map<PlanDict, Integer> dataCount = Maps.newLinkedHashMap();

        public Map<PlanDict, Integer> getDataCount() {
            return dataCount;
        }

        public void setDataCount(Map<PlanDict, Integer> dataCount) {
            this.dataCount.putAll(dataCount);
        }

        public List<FruitPlan.Info> getPlans() {
            return plans;
        }

        public void setPlans(List<FruitPlan.Info> plans) {
            this.plans.addAll(plans);
        }

        void addStateType(PlanDict planDict) {
            if (!dataCount.containsKey(planDict))
                dataCount.put(planDict, 1);
            else
                dataCount.put(planDict, dataCount.get(planDict) + (Integer) 1);
        }


        public static Result getInstance() {
            return new Result();
        }
    }

}
