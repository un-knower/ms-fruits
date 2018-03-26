package wowjoy.fruits.ms.dao.plan;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.lang.StringUtils;
import wowjoy.fruits.ms.dao.InterfaceDao;
import wowjoy.fruits.ms.exception.CheckException;
import wowjoy.fruits.ms.exception.ExceptionSupport;
import wowjoy.fruits.ms.exception.MessageException;
import wowjoy.fruits.ms.exception.ServiceException;
import wowjoy.fruits.ms.module.logs.FruitLogs;
import wowjoy.fruits.ms.module.logs.FruitLogsVo;
import wowjoy.fruits.ms.module.plan.*;
import wowjoy.fruits.ms.module.plan.example.FruitPlanExample;
import wowjoy.fruits.ms.module.task.FruitTaskExample;
import wowjoy.fruits.ms.module.task.FruitTaskUser;
import wowjoy.fruits.ms.module.user.FruitUser;
import wowjoy.fruits.ms.module.util.entity.FruitDict;
import wowjoy.fruits.ms.util.ApplicationContextUtils;
import wowjoy.fruits.ms.util.DateUtils;
import wowjoy.fruits.ms.util.GsonUtils;

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

import static java.util.stream.Collectors.*;

public abstract class AbstractDaoPlan implements InterfaceDao {
    /*********************************************************************************
     * 抽象接口，私有，因为对外的公共接口用来书写业务层，发布api必须在自己的控制范围内，不发布无用的接口。*
     *********************************************************************************/

    protected abstract ArrayList<FruitPlan> findByProjectId(Consumer<FruitPlanExample> exampleConsumer, String projectId, Integer pageNum, Integer pageSize, boolean isPage);

    protected abstract List<FruitPlan> findByExample(Consumer<FruitPlanExample> exampleConsumer);

    protected abstract List<FruitPlanUser> findUserByPlanIds(List<String> planIds, String currentUserId);

    protected abstract Optional<Map<String, ArrayList<FruitPlanTask>>> findTaskByTaskExampleAndPlanIds(Consumer<FruitTaskExample> taskConsumer, List<String> planIds);

    protected abstract Map<String, LinkedList<FruitLogs.Info>> findLogsByPlanIds(List<String> planIds);

    protected abstract void insertLogs(Consumer<FruitLogsVo> vo);

    protected abstract Optional<FruitPlan> findByUUID(String uuid);

    protected abstract void insert(FruitPlan.Insert dao);

    protected abstract void update(Consumer<FruitPlan.Update> planDaoConsumer, Consumer<FruitPlanExample> exampleConsumer);

    protected abstract Optional<List<FruitPlan>> batchUpdateStatusAndReturnResult(Consumer<FruitPlan.Update> planDaoConsumer, Consumer<FruitPlanExample> fruitPlanExampleConsumer);

    protected abstract void delete(String uuid);

    protected abstract void deleteSummarys(FruitPlanSummaryDao dao);

    /*******************************
     * PUBLIC 函数，公共接口         *
     * 尽量保证规范，不直接调用dao接口 *
     *******************************/

    public final void delete(FruitPlanVo vo) {
        this.findByUUID(vo.getUuidVo()).orElseThrow(() -> new CheckException("计划不存在，删除失败"));
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
            if (StringUtils.isNotBlank(query.getPlanStatus()))
                criteria.andPlanStatusIn(Arrays.asList(query.getPlanStatus().split(",")));
            if (StringUtils.isNotBlank(query.getTitle()))
                criteria.andTitleLike(MessageFormat.format("%{0}%", query.getTitle()));
            criteria.andIsDeletedEqualTo(FruitDict.Systems.N.name());
            example.setOrderByClause("create_date_time desc");
            if (StringUtils.isNotBlank(query.sortConstrue()))
                example.setOrderByClause(query.sortConstrue());
        }, query.getProjectId(), 1, 10, false);
        if (planMonthList.isEmpty()) return Lists.newArrayList();
        /*plan -> planInfo*/
        final List<FruitPlan.Info> planInfoMonth = planMonthList.parallelStream().map(FruitPlan::toInfo).collect(toList());
        /*month - user*/
        CompletableFuture.allOf(CompletableFuture.supplyAsync(
                this.plugUser(planInfoMonth.parallelStream().map(FruitPlan::getUuid).collect(toList()), currentUser.getUserId()))
                        .thenAccept(planUsers -> planInfoMonth.parallelStream()
                                .filter(info -> planUsers.containsKey(info.getUuid()))
                                .forEach(info -> info.setUsers(planUsers.get(info.getUuid())))),
                /*plan - week*/
                CompletableFuture.supplyAsync(() -> this.findByProjectId(
                        example -> {
                            FruitPlanExample.Criteria criteria = example.createCriteria();
                            if (StringUtils.isNotBlank(query.getTitle()))
                                criteria.andTitleLike(MessageFormat.format("%{0}%", query.getTitle()));
                            if (StringUtils.isNotBlank(query.getPlanStatus()))
                                criteria.andPlanStatusEqualTo(query.getPlanStatus());
                            criteria.andParentIdIsNotNull();
                            criteria.andIsDeletedEqualTo(FruitDict.Systems.N.name());
                            criteria.andParentIdIn(planInfoMonth.parallelStream().filter(Objects::nonNull).map(FruitPlan::getUuid).collect(toList()));
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
                            final Map<String, LinkedList<FruitPlanUser>> planUsers = this.plugUser(weekPlan.parallelStream().map(FruitPlan::getUuid).collect(toList()), currentUser.getUserId()).get();
                            return weekPlanInfo.parallelStream().filter(plan -> planUsers.containsKey(plan.getUuid())).map(plan -> {
                                plan.setUsers(planUsers.get(plan.getUuid()));
                                return plan;
                            }).collect(toList());
                        })
                ).thenAccept(weekPlanInfo -> {
                    Map<String, ArrayList<FruitPlan.Info>> parentPlans = weekPlanInfo.stream().collect(groupingBy(FruitPlan.Info::getParentId, toCollection(ArrayList::new)));
                    planInfoMonth.parallelStream().filter(info -> parentPlans.containsKey(info.getUuid())).forEach(info -> info.setWeeks(parentPlans.get(info.getUuid())));
                })
        ).join();
        return wherePlan(planInfoMonth, query);
    }

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

    private Supplier<Map<String, LinkedList<FruitPlanUser>>> plugUser(List<String> planIds, String userId) {
        return () -> Optional.ofNullable(planIds)
                .filter(ids -> !ids.isEmpty())
                .map(ids -> this.findUserByPlanIds(ids, userId))
                .map(users -> users
                        .stream()
                        .collect(groupingBy(FruitPlanUser::getPlanId, toCollection(LinkedList::new)))
                ).orElseGet(HashMap::new);
    }

    private Supplier<Map<String, LinkedList<FruitLogs.Info>>> plugLogs(List<String> planIds) {
        return () -> Optional.ofNullable(planIds)
                .filter(ids -> !ids.isEmpty())
                .map(this::findLogsByPlanIds)
                .orElseGet(HashMap::new);
    }

    private Supplier<Map<String, ArrayList<FruitPlanTask>>> plugTask(List<String> planIds) {
        return () -> Optional.ofNullable(planIds)
                .filter(ids -> !ids.isEmpty())
                .flatMap(ids -> this.findTaskByTaskExampleAndPlanIds(example -> {
                }, ids))
                .orElseGet(HashMap::new);
    }

    /**
     * 统计当前不同状态下数目
     * tips:
     * 进行中 and 未延期的不统计
     */
    private void dataCount(List<FruitPlan.Info> plans, Result result) {
        /*状态等于进行中的 and 待执行的*/
        Predicate<FruitPlan.Info> delay = plan -> FruitDict.PlanDict.PENDING.name().equals(plan.getPlanStatus())
                || FruitDict.PlanDict.STAY_PENDING.name().equals(plan.getPlanStatus());
        /*状态等于终止*/
        Predicate<FruitPlan.Info> end = plan -> FruitDict.PlanDict.END.name().equals(plan.getPlanStatus());
        /*状态等于完成*/
        Predicate<FruitPlan.Info> complete = plan -> FruitDict.PlanDict.COMPLETE.name().equals(plan.getPlanStatus());
        /*延期*/
        Predicate<FruitPlan.Info> day = plan -> plan.getDays() < 0;
        plans.forEach(plan -> {
            if (plan.getWeeks() != null && !plan.getWeeks().isEmpty()) dataCount(plan.getWeeks(), result);
            plan.computeDays();
            if (end.test(plan)) result.addStateType(FruitDict.PlanDict.END);
            else if (delay.and(day).test(plan)) result.addStateType(FruitDict.PlanDict.DELAY);
            else if (complete.test(plan))
                if (day.test(plan)) {
                    result.addStateType(FruitDict.PlanDict.DELAY_COMPLETE);
                } else {
                    result.addStateType(FruitDict.PlanDict.COMPLETE);
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
            throw new CheckException("必须填写项目id");
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
        FruitPlan.Info planInfo = this.findByUUID(planId).map(FruitPlan::toInfo).orElseThrow(() -> new CheckException("计划不存在"));
        CompletableFuture.allOf(
                CompletableFuture.supplyAsync(this.plugUser(Lists.newArrayList(planInfo.getUuid()), currentUser.getUserId())) //查询用户信息
                        .thenAccept(userMap -> Optional.ofNullable(userMap)
                                .filter(map -> map.containsKey(planInfo.getUuid()))
                                .map(map -> map.get(planInfo.getUuid()))
                                .ifPresent(planInfo::setUsers)
                        ),
                CompletableFuture.supplyAsync(this.plugLogs(Lists.newArrayList(planInfo.getUuid()))) //查询日志信息
                        .thenAccept(logsMap -> Optional.ofNullable(logsMap)
                                .filter(map -> map.containsKey(planInfo.getUuid()))
                                .map(map -> map.get(planInfo.getUuid()))
                                .ifPresent(planInfo::setLogs)),
                CompletableFuture.supplyAsync(this.plugTask(Lists.newArrayList(planInfo.getUuid()))) //查询任务信息
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
        try {
            Optional<FruitPlan.Insert> optionalVo = Optional.ofNullable(insert);
            optionalVo.filter(plan -> StringUtils.isNotBlank(plan.getTitle())).orElseThrow(() -> new CheckException("标题不能为空"));
            optionalVo.filter(plan -> plan.getEstimatedEndDate() != null).orElseThrow(() -> new CheckException("必须填写预计结束时间"));
            optionalVo.filter(plan -> plan.getUserRelation().containsKey(FruitDict.Systems.ADD))
                    .filter(plan -> !plan.getUserRelation().get(FruitDict.Systems.ADD).isEmpty())
                    .orElseThrow(() -> new CheckException("必须添加至少一个关联用户"));
            optionalVo.filter(plan -> plan.getProjectRelation().containsKey(FruitDict.Systems.ADD))
                    .filter(plan -> !plan.getProjectRelation().get(FruitDict.Systems.ADD).isEmpty())
                    .orElseThrow(() -> new CheckException("一个计划只能关联一个项目"));
            insert.setPlanStatus(FruitDict.PlanDict.STAY_PENDING.name());
            insert.setEstimatedStartDate(insert.getEstimatedStartDate() != null ? insert.getEstimatedStartDate() : new Date());
            this.insert(insert);
        } catch (ExceptionSupport ex) {
            throw ex;
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            throw new ServiceException("添加计划出错");
        }
    }

    /**
     * 修改计划
     */
    public final void modify(FruitPlanVo vo) {
        try {
            this.findByUUID(vo.getUuidVo()).orElseThrow(() -> new CheckException("计划不存在，拒绝修改"));
            Optional.of(vo).map(FruitPlanVo::getTitle).filter(StringUtils::isNotBlank).orElseThrow(() -> new CheckException("标题不能为空名"));
            this.update(update -> {
                update.setUuid(vo.getUuidVo());
                update.setTitle(vo.getTitle());
                update.setDescription(vo.getDescription());
                update.setEstimatedStartDate(vo.getEstimatedStartDate());
                update.setEstimatedEndDate(vo.getEstimatedEndDate());
                update.setPercent(vo.getPercent());
                update.setUserRelation(vo.getUserRelation());
            }, fruitPlanExample -> fruitPlanExample.createCriteria().andUuidEqualTo(vo.getUuidVo()));
        } catch (ExceptionSupport ex) {
            throw ex;
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            throw new ServiceException("修改计划错误");
        }
    }

    /**
     * 终止计划
     */
    public final void end(FruitPlanVo vo) {
        Optional<FruitPlanVo> optionalPlanVo = Optional.ofNullable(vo);
        Optional<FruitPlan> optionalPlan = this.findByUUID(vo.getUuidVo());
        optionalPlan.orElseThrow(() -> new CheckException("计划不存在"));
        optionalPlan.map(FruitPlan::getPlanStatus)
                .filter(status -> !FruitDict.PlanDict.END.name().equals(status))
                .filter(status -> !FruitDict.PlanDict.COMPLETE.name().equals(status))
                .orElseThrow(() -> new CheckException("生命周期已结束，无法继续变更状态"));
        optionalPlanVo.filter(planVo -> StringUtils.isNotBlank(planVo.getStatusDescription())).orElseThrow(() -> new CheckException("状态描述不能为空"));
        /*校验是否是批量终止*/
        final FruitUser currentUser = ApplicationContextUtils.getCurrentUser();
        final LocalDateTime endDate = LocalDateTime.now();
        final Gson gson = new Gson();
        List<FruitPlan> planList = this.batchUpdateStatusAndReturnResult(update -> {
                    update.setPlanStatus(FruitDict.PlanDict.END.name());
                    update.setEndDate(endDate);
                    update.setStatusDescription(vo.getStatusDescription());
                }, fruitPlanExample -> fruitPlanExample.createCriteria()
                        .andParentIdEqualTo(vo.getUuidVo())
                        .andIsDeletedEqualTo(FruitDict.Systems.N.name())
                        .andPlanStatusIn(Lists.newArrayList(FruitDict.PlanDict.STAY_PENDING.name(), FruitDict.PlanDict.PENDING.name())) //查询出所有待完成、进行中的计划
        ).orElseGet(ArrayList::new);

        this.checkThePlanInToBeCompleteTasks(Optional.ofNullable(planList
                .stream()
                .map(FruitPlan::getUuid).collect(toCollection(ArrayList::new)))
                .filter(plans -> !plans.isEmpty())
                .map(plans -> {
                    plans.add(vo.getUuidVo());
                    return plans;
                }).orElseGet(ArrayList::new)
        ); //检查计划中是否有未完成或未终止的任务
        ArrayList<CompletableFuture<Boolean>> endPlanFutures = planList.stream().map(plan -> CompletableFuture.supplyAsync(() -> {
            this.insertLogs(logsVo -> {
                logsVo.setUserId(currentUser.getUserId());
                logsVo.setFruitUuid(plan.getUuid());
                logsVo.setFruitType(FruitDict.Parents.PLAN);
                logsVo.setOperateType(FruitDict.LogsDict.END);
                plan.setStatusDescription(vo.getStatusDescription());
                plan.setEndDate(endDate);
                logsVo.setJsonObject(gson.toJsonTree(plan).toString());
                logsVo.setVoObject(Optional.ofNullable(gson.toJsonTree(vo))
                        .filter(json -> !json.isJsonNull())
                        .map(JsonElement::getAsJsonObject)
                        .map(json -> {
                            json.addProperty("uuidVo", plan.getUuid());
                            return json;
                        }).orElseGet(JsonObject::new).toString()
                );
            });
            return true;
        })).collect(toCollection(ArrayList::new));
        /*主计划终止函数加入到线程队列中*/
        endPlanFutures.add(CompletableFuture.supplyAsync(() -> {
            this.update(update -> {
                update.setPlanStatus(FruitDict.PlanDict.END.name());
                update.setEndDate(endDate);
                update.setStatusDescription(vo.getStatusDescription());
            }, fruitPlanExample -> fruitPlanExample.createCriteria()
                    .andUuidEqualTo(vo.getUuidVo())
                    .andPlanStatusIn(Lists.newArrayList(FruitDict.PlanDict.STAY_PENDING.name(), FruitDict.PlanDict.PENDING.name())));
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
    public final void complete(FruitPlanVo vo) {
        Optional<FruitPlanVo> optionalPlanVo = Optional.of(vo);
        Optional<FruitPlan> optionalPlanDao = this.findByUUID(vo.getUuidVo());
        optionalPlanDao.orElseThrow(() -> new CheckException("计划不存在"));
        /*若结束时间不为空，那么结束时间必须小于等于当前*/
        optionalPlanVo.filter(planVo -> !(planVo.getEndDate() != null && Duration.between(LocalDate.now().atTime(0, 0, 0), LocalDateTime.ofInstant(planVo.getEndDate().toInstant(), ZoneId.systemDefault()).withHour(0).withMinute(0).withSecond(0)).toDays() > 0))
                .orElseThrow(() -> new CheckException("实际结束时间不能大于今天"));
        /*等于已终止、已完成、待执行都拒绝执行*/
        optionalPlanDao.map(FruitPlan::getPlanStatus)
                .filter(status -> !FruitDict.PlanDict.END.name().equals(status))
                .filter(status -> !FruitDict.PlanDict.COMPLETE.name().equals(status))
                .filter(status -> !FruitDict.PlanDict.STAY_PENDING.name().equals(status))
                .orElseThrow(() -> new CheckException("当前目标状态无法切换到已完成"));
        /*如果延期，必须填写延期说明*/
        final FruitPlan.Info planInfo = optionalPlanDao.map(FruitPlan::toInfo)
                .filter(plan -> !(plan.computeDays().getDays() < 0 && StringUtils.isBlank(vo.getStatusDescription())))
                .orElseThrow(() -> new CheckException("计划延期完成，需要填写延期说明"));

        this.checkThePlanInToBeCompleteTasks(Lists.newArrayList(planInfo.getUuid())); //检查计划中是否有未完成或未终止的任务
        this.update(update -> {
            update.setUuid(vo.getUuidVo());
            update.setPlanStatus(FruitDict.PlanDict.COMPLETE.name());
            update.setEndDate(vo.getEndDate() == null ? LocalDateTime.now() : LocalDateTime.ofInstant(vo.getEndDate().toInstant(), ZoneId.systemDefault()));
            update.setStatusDescription(vo.getStatusDescription());
        }, fruitPlanExample -> fruitPlanExample.createCriteria().andUuidEqualTo(vo.getUuidVo()));
    }

    /*检查计划中待完成的任务*/
    private void checkThePlanInToBeCompleteTasks(ArrayList<String> planIds) {
        Optional.ofNullable(planIds)
                .filter(ids -> !ids.isEmpty())
                .flatMap(ids -> this.findTaskByTaskExampleAndPlanIds(example -> example.createCriteria().andTaskStatusIn(Lists.newArrayList(FruitDict.TaskDict.START.name())), ids))
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
    public final void pending(FruitPlanVo vo) {
        Optional<FruitPlan> optionalPlanDao = this.findByUUID(vo.getUuidVo());
        optionalPlanDao.orElseThrow(() -> new CheckException("计划不存在"));
        /*不等于待执行的状态都拒绝*/
        optionalPlanDao.map(FruitPlan::getPlanStatus)
                .filter(status -> FruitDict.PlanDict.STAY_PENDING.name().equals(status))
                .orElseThrow(() -> new CheckException("只能种待执行变更到进行中"));
        this.update(update -> {
            update.setUuid(vo.getUuidVo());
            update.setPlanStatus(FruitDict.PlanDict.PENDING.name());
            update.setStartDate(new Date());
        }, fruitPlanExample -> fruitPlanExample.createCriteria().andUuidEqualTo(vo.getUuidVo()));
    }

    public static class Result {
        private final List<FruitPlan.Info> plans = Lists.newLinkedList();
        private final Map<FruitDict.PlanDict, Integer> dataCount = Maps.newLinkedHashMap();

        public Map<FruitDict.PlanDict, Integer> getDataCount() {
            return dataCount;
        }

        public void setDataCount(Map<FruitDict.PlanDict, Integer> dataCount) {
            this.dataCount.putAll(dataCount);
        }

        public List<FruitPlan.Info> getPlans() {
            return plans;
        }

        public void setPlans(List<FruitPlan.Info> plans) {
            this.plans.addAll(plans);
        }

        void addStateType(FruitDict.PlanDict planDict) {
            if (!dataCount.containsKey(planDict))
                dataCount.put(planDict, 1);
            else
                dataCount.put(planDict, dataCount.get(planDict) + (Integer) 1);
        }


        public static Result getInstance() {
            return new Result();
        }

        public Result deepCopy() {
            return GsonUtils.newGson().fromJson(GsonUtils.newGson().toJsonTree(this), TypeToken.of(this.getClass()).getType());
        }
    }

}
