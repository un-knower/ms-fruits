package wowjoy.fruits.ms.dao.plan;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.lang.StringUtils;
import wowjoy.fruits.ms.dao.InterfaceDao;
import wowjoy.fruits.ms.exception.CheckException;
import wowjoy.fruits.ms.exception.ExceptionSupport;
import wowjoy.fruits.ms.exception.MessageException;
import wowjoy.fruits.ms.exception.ServiceException;
import wowjoy.fruits.ms.module.logs.FruitLogsDao;
import wowjoy.fruits.ms.module.logs.FruitLogsVo;
import wowjoy.fruits.ms.module.plan.*;
import wowjoy.fruits.ms.module.plan.example.FruitPlanExample;
import wowjoy.fruits.ms.module.task.FruitTaskExample;
import wowjoy.fruits.ms.module.user.FruitUser;
import wowjoy.fruits.ms.module.user.FruitUserDao;
import wowjoy.fruits.ms.module.util.entity.FruitDict;
import wowjoy.fruits.ms.util.ApplicationContextUtils;
import wowjoy.fruits.ms.util.DateUtils;

import java.text.MessageFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;

public abstract class AbstractDaoPlan implements InterfaceDao {
    /*********************************************************************************
     * 抽象接口，私有，因为对外的公共接口用来书写业务层，发布api必须在自己的控制范围内，不发布无用的接口。*
     *********************************************************************************/

    protected abstract List<FruitPlanDao> findByProjectId(Consumer<FruitPlanExample> exampleConsumer, String projectId, Integer pageNum, Integer pageSize, boolean isPage);

    protected abstract List<FruitPlanDao> findByExample(Consumer<FruitPlanExample> exampleConsumer);

    protected abstract List<FruitPlanDao> findUserByPlanIds(List<String> planIds, String currentUserId);

    protected abstract Optional<Map<String, ArrayList<FruitPlanTask>>> findTaskByTaskExampleAndPlanIds(Consumer<FruitTaskExample> taskConsumer, List<String> planIds);

    protected abstract Map<String, LinkedList<FruitLogsDao>> findLogsByPlanIds(List<String> planIds);

    protected abstract void insertLogs(Consumer<FruitLogsVo> vo);

    protected abstract Optional<FruitPlanDao> findByUUID(String uuid);

    protected abstract void insert(FruitPlan.Insert dao);

    protected abstract void update(Consumer<FruitPlan.Update> planDaoConsumer, Consumer<FruitPlanExample> exampleConsumer);

    protected abstract Optional<List<FruitPlanDao>> batchUpdateStatusAndReturnResult(Consumer<FruitPlan.Update> planDaoConsumer, Consumer<FruitPlanExample> fruitPlanExampleConsumer);

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
    private List<FruitPlanDao> findTree(FruitPlanVo vo, FruitUser currentUser) {
        this.getStartTimeAndEndTime(vo);
        DaoThread planThread = DaoThread.getFixed();
        List<FruitPlanDao> planDaoListSource = this.findByProjectId(this.findParent(vo), vo.getProjectId(), vo.getPageNum(), vo.getPageSize(), false);
        if (planDaoListSource.isEmpty()) return planDaoListSource;

        planThread.execute(this.plugUser(planDaoListSource, currentUser));
        Future<List<FruitPlanDao>> weekFuture = planThread.executeFuture(() -> this.findByProjectId(
                example -> {
                    FruitPlanExample.Criteria criteria = example.createCriteria();
                    if (StringUtils.isNotBlank(vo.getTitle()))
                        criteria.andTitleLike(MessageFormat.format("%{0}%", vo.getTitle()));
                    if (StringUtils.isNotBlank(vo.getPlanStatus()))
                        criteria.andPlanStatusEqualTo(vo.getPlanStatus());
                    criteria.andParentIdIsNotNull();
                    criteria.andIsDeletedEqualTo(FruitDict.Systems.N.name());
                    criteria.andParentIdIn(planDaoListSource.parallelStream().filter(Objects::nonNull).map(FruitPlanDao::getUuid).collect(toList()));
                    example.setOrderByClause("create_date_time desc");
                    if (StringUtils.isNotBlank(vo.sortConstrue()))
                        example.setOrderByClause(vo.sortConstrue());
                },
                vo.getProjectId(),
                vo.getPageNum(),
                vo.getPageSize(),
                false)
        );
        try {
            planThread.execute(this.plugUser(weekFuture.get(), currentUser));
            Map<String, List<FruitPlanDao>> nodePlanDaoList = weekFuture.get().parallelStream().collect(groupingBy(FruitPlanDao::getParentId));
            planDaoListSource.parallelStream().forEach(plan -> plan.setWeeks(nodePlanDaoList.get(plan.getUuid())));
        } catch (InterruptedException e) {
            throw new CheckException("执行终止");
        } catch (ExecutionException e) {
            e.printStackTrace();
            throw new CheckException("执行异常");
        }

        planThread.get();
        planThread.shutdown();
        return wherePlan(planDaoListSource, vo);
    }

    private Consumer<FruitPlanExample> findParent(FruitPlanVo vo) {
        return example -> {
            FruitPlanExample.Criteria criteria = example.createCriteria();
            criteria.andParentIdIsNull();
            if (Objects.nonNull(vo.getStartDateVo()) && Objects.nonNull(vo.getEndDateVo()))
                criteria.andEstimatedEndDateBetween(vo.getStartDateVo(), vo.getEndDateVo());
            criteria.andIsDeletedEqualTo(FruitDict.Systems.N.name());
            example.setOrderByClause("create_date_time desc");
            if (StringUtils.isNotBlank(vo.sortConstrue()))
                example.setOrderByClause(vo.sortConstrue());
        };
    }

    private List<FruitPlanDao> wherePlan(List<FruitPlanDao> planDaoListSource, FruitPlanVo vo) {
        Stream<FruitPlanDao> planDaoStream = planDaoListSource.stream();
        final Predicate<FruitPlanDao> planWeekFilter = plan -> plan.getWeeks() != null && !plan.getWeeks().isEmpty();
        Predicate<FruitPlanDao> chain = plan -> !planWeekFilter.test(plan);
        if (StringUtils.isNotBlank(vo.getPlanStatus()))
            chain = chain.and(plan -> plan.getPlanStatus().equals(vo.getPlanStatus()));
        if (StringUtils.isNotBlank(vo.getTitle()))
            chain = chain.and(plan -> plan.getTitle().contains(vo.getTitle()));
        final Predicate<FruitPlanDao> planFilter = chain;
        return planDaoStream.filter(plan -> planWeekFilter.test(plan) || planFilter.test(plan)).collect(toList());
    }

    private Callable plugLogs(List<FruitPlanDao> planDaos) {
        return () -> {
            if (planDaos.isEmpty()) return false;
            Map<String, LinkedList<FruitLogsDao>> logsMap = this.findLogsByPlanIds(planDaos.stream().map(FruitPlanDao::getUuid).collect(toList()));
            planDaos.forEach(plan -> plan.setLogs(logsMap.get(plan.getUuid())));
            return true;
        };
    }

    private Callable plugUser(List<FruitPlanDao> planDaos, FruitUser currentUser) {
        return () -> {
            if (planDaos.isEmpty()) return false;
            Map<String, LinkedList<FruitUserDao>> userMap = this.findUserByPlanIds(planDaos.stream().map(FruitPlanDao::getUuid).collect(toList()), currentUser.getUserId())
                    .stream()
                    .collect(toMap(FruitPlanDao::getUuid, plan -> {
                        LinkedList<FruitUserDao> userList = Lists.newLinkedList();
                        userList.addAll(plan.getUsers());
                        return userList;
                    }, (l, r) -> {
                        r.addAll(l);
                        return r;
                    }));
            planDaos.forEach(plan -> plan.setUsers(userMap.get(plan.getUuid())));
            return true;
        };
    }

    private Callable plugTask(List<FruitPlanDao> planDaos) {
        return () -> {
            if (planDaos.isEmpty()) return false;
            Optional<Map<String, ArrayList<FruitPlanTask>>> optionalTaskMap = this.findTaskByTaskExampleAndPlanIds(example -> {
            }, planDaos.stream().map(FruitPlanDao::getUuid).collect(toList()));
            planDaos.forEach(plan -> plan.setTasks(optionalTaskMap.filter(taskMap -> taskMap.containsKey(plan.getUuid())).map(taskMap -> taskMap.get(plan.getUuid())).orElseGet(ArrayList::new)));
            return true;
        };
    }

    /**
     * 统计当前不同状态下数目
     * tips:
     * 进行中 and 未延期的不统计
     */
    private void dataCount(List<FruitPlanDao> plans, Result result) {
        /*状态等于进行中的 and 待执行的*/
        Predicate<FruitPlanDao> delay = plan -> FruitDict.PlanDict.PENDING.name().equals(plan.getPlanStatus())
                || FruitDict.PlanDict.STAY_PENDING.name().equals(plan.getPlanStatus());
        /*状态等于终止*/
        Predicate<FruitPlanDao> end = plan -> FruitDict.PlanDict.END.name().equals(plan.getPlanStatus());
        /*状态等于完成*/
        Predicate<FruitPlanDao> complete = plan -> FruitDict.PlanDict.COMPLETE.name().equals(plan.getPlanStatus());
        /*延期*/
        Predicate<FruitPlanDao> day = plan -> plan.getDays() < 0;
        plans.forEach(plan -> {
            if (plan.getWeeks() != null && !plan.getWeeks().isEmpty()) dataCount(plan.getWeeks(), result);
            plan.computeDays();
            if (end.test(plan)) result.addStateType(FruitDict.PlanDict.END, 1);
            else if (delay.and(day).test(plan)) result.addStateType(FruitDict.PlanDict.DELAY, 1);
            else if (complete.test(plan))
                if (day.test(plan)) {
                    result.addStateType(FruitDict.PlanDict.DELAY_COMPLETE, 1);
                } else {
                    result.addStateType(FruitDict.PlanDict.COMPLETE, 1);
                }
        });
    }

    public Result compositeQuery(FruitPlanVo vo) {
        Result result = Result.getInstance();
        result.setPlans(findTree(vo, ApplicationContextUtils.getCurrentUser()));
        long start = System.currentTimeMillis();
        dataCount(result.getPlans(), result);
        long end = System.currentTimeMillis();
        logger.info("--" + (end - start));
        return result;
    }

    public List<FruitPlanDao> findList(FruitPlanVo vo) {
        if (StringUtils.isBlank(vo.getProjectId()))
            throw new CheckException("必须填写项目id");
        this.getStartTimeAndEndTime(vo);
        return this.findByProjectId(example -> {
            FruitPlanExample.Criteria criteria = example.createCriteria();
            if (StringUtils.isNotBlank(vo.getPlanStatus()))
                criteria.andPlanStatusIn(Arrays.asList(vo.getPlanStatus().split(",")));
            if (StringUtils.isNotBlank(vo.getTitle()))
                criteria.andTitleLike(MessageFormat.format("%{0}%", vo.getTitle()));
            criteria.andIsDeletedEqualTo(FruitDict.Systems.N.name());
            example.setOrderByClause("create_date_time desc");
            if (StringUtils.isNotBlank(vo.sortConstrue()))
                example.setOrderByClause(vo.sortConstrue());
        }, vo.getProjectId(), vo.getPageNum(), vo.getPageSize(), false);
    }

    /*若年月不为空，则设置开始时间 and 结束时间*/
    private void getStartTimeAndEndTime(FruitPlanVo vo) {
        if (StringUtils.isBlank(vo.getYear()) || StringUtils.isBlank(vo.getMonth()))
            return;
        DateUtils.Month<DateUtils.Week.WeekChinese> month = DateUtils.getMonthByYearMonth(Integer.valueOf(vo.getYear()), Integer.valueOf(vo.getMonth()));
        vo.setStartDateVo(Date.from(month.getStartDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        vo.setEndDateVo(Date.from(month.getEndDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
    }

    public final FruitPlan findByUUID(FruitPlanVo vo) {
        final FruitUser currentUser = ApplicationContextUtils.getCurrentUser();
        Optional<FruitPlanDao> optionalPlan = this.findByUUID(vo.getUuidVo());
        optionalPlan.orElseThrow(() -> new CheckException("计划不存在"));
        DaoThread.getFixed()
                .execute(this.plugUser(Lists.newArrayList(optionalPlan.get()), currentUser))
                .execute(this.plugLogs(Lists.newArrayList(optionalPlan.get())))
                .execute(this.plugTask(Lists.newArrayList(optionalPlan.get()))).get();
        return optionalPlan.get();
    }

    /**
     * 添加【项目】计划
     */
    public final void addJoinProject(FruitPlanVo vo) {
        try {
            Optional<FruitPlanVo> optionalVo = Optional.ofNullable(vo);
            optionalVo.filter(plan -> StringUtils.isNotBlank(plan.getTitle())).orElseThrow(() -> new CheckException("标题不能为空"));
            optionalVo.filter(plan -> plan.getEstimatedEndDate() != null).orElseThrow(() -> new CheckException("必须填写预计结束时间"));
            optionalVo.filter(plan -> plan.getUserRelation().containsKey(FruitDict.Systems.ADD))
                    .filter(plan -> !plan.getUserRelation().get(FruitDict.Systems.ADD).isEmpty())
                    .orElseThrow(() -> new CheckException("必须添加至少一个关联用户"));
            optionalVo.filter(plan -> plan.getProjectRelation().containsKey(FruitDict.Systems.ADD))
                    .filter(plan -> !plan.getProjectRelation().get(FruitDict.Systems.ADD).isEmpty())
                    .orElseThrow(() -> new CheckException("一个计划只能关联一个项目"));
            FruitPlan.Insert insert = FruitPlan.newInsert();
            insert.setPlanStatus(FruitDict.PlanDict.STAY_PENDING.name());
            insert.setEstimatedStartDate(vo.getEstimatedStartDate() != null ? vo.getEstimatedStartDate() : new Date());
            insert.setEstimatedEndDate(vo.getEstimatedEndDate());
            insert.setTitle(vo.getTitle());
            insert.setParentId(vo.getParentId());
            insert.setDescription(vo.getDescription());
            insert.setUserRelation(vo.getUserRelation());
            insert.setProjectRelation(vo.getProjectRelation());
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
        Optional<FruitPlanDao> optionalPlanDao = this.findByUUID(vo.getUuidVo());
        optionalPlanDao.orElseThrow(() -> new CheckException("计划不存在"));
        optionalPlanDao.map(FruitPlanDao::getPlanStatus)
                .filter(status -> !FruitDict.PlanDict.END.name().equals(status))
                .filter(status -> !FruitDict.PlanDict.COMPLETE.name().equals(status))
                .orElseThrow(() -> new CheckException("生命周期已结束，无法继续变更状态"));
        optionalPlanVo.filter(planVo -> StringUtils.isNotBlank(planVo.getStatusDescription())).orElseThrow(() -> new CheckException("状态描述不能为空"));
        /*校验是否是批量终止*/
        final FruitUser currentUser = ApplicationContextUtils.getCurrentUser();
        final LocalDateTime endDate = LocalDateTime.now();
        final Gson gson = new Gson();
        List<FruitPlanDao> planDaoList = this.batchUpdateStatusAndReturnResult(update -> {
                    update.setPlanStatus(FruitDict.PlanDict.END.name());
                    update.setEndDate(endDate);
                    update.setStatusDescription(vo.getStatusDescription());
                }, fruitPlanExample -> fruitPlanExample.createCriteria()
                        .andParentIdEqualTo(vo.getUuidVo())
                        .andIsDeletedEqualTo(FruitDict.Systems.N.name())
                        .andPlanStatusIn(Lists.newArrayList(FruitDict.PlanDict.STAY_PENDING.name(), FruitDict.PlanDict.PENDING.name())) //查询出所有待完成、进行中的计划
        ).orElseGet(ArrayList::new);
        this.checkThePlanInToBeCompleteTasks(planDaoList.stream().map(FruitPlanDao::getUuid).collect(toCollection(ArrayList::new))); //检查计划中是否有未完成或未终止的任务
        ArrayList<CompletableFuture<Boolean>> endPlanFutures = planDaoList.stream().map(plan -> CompletableFuture.supplyAsync(() -> {
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
        Optional<FruitPlanDao> optionalPlanDao = this.findByUUID(vo.getUuidVo());
        optionalPlanDao.orElseThrow(() -> new CheckException("计划不存在"));
        /*若结束时间不为空，那么结束时间必须小于等于当前*/
        optionalPlanVo.filter(planVo -> !(planVo.getEndDate() != null && Duration.between(LocalDate.now().atTime(0, 0, 0), LocalDateTime.ofInstant(planVo.getEndDate().toInstant(), ZoneId.systemDefault()).withHour(0).withMinute(0).withSecond(0)).toDays() > 0))
                .orElseThrow(() -> new CheckException("实际结束时间不能大于今天"));
        /*等于已终止、已完成、待执行都拒绝执行*/
        optionalPlanDao.map(FruitPlanDao::getPlanStatus)
                .filter(status -> !FruitDict.PlanDict.END.name().equals(status))
                .filter(status -> !FruitDict.PlanDict.COMPLETE.name().equals(status))
                .filter(status -> !FruitDict.PlanDict.STAY_PENDING.name().equals(status))
                .orElseThrow(() -> new CheckException("当前目标状态无法切换到已完成"));
        /*如果延期，必须填写延期说明*/
        optionalPlanDao.filter(plan -> !(plan.computeDays().getDays() < 0 && StringUtils.isBlank(vo.getStatusDescription())))
                .orElseThrow(() -> new CheckException("计划延期完成，需要填写延期说明"));

        this.checkThePlanInToBeCompleteTasks(Lists.newArrayList(optionalPlanDao.get().getUuid())); //检查计划中是否有未完成或未终止的任务
        this.update(update -> {
            update.setUuid(vo.getUuidVo());
            update.setPlanStatus(FruitDict.PlanDict.COMPLETE.name());
            update.setEndDate(vo.getEndDate() == null ? LocalDateTime.now() : LocalDateTime.ofInstant(vo.getEndDate().toInstant(), ZoneId.systemDefault()));
            update.setStatusDescription(vo.getStatusDescription());
        }, fruitPlanExample -> fruitPlanExample.createCriteria().andUuidEqualTo(vo.getUuidVo()));
    }

    /*检查计划中待完成的任务*/
    private void checkThePlanInToBeCompleteTasks(ArrayList<String> planIds) {
        Optional<Map<String, ArrayList<FruitPlanTask>>> optionalPlanOfTasks = Optional.ofNullable(planIds)
                .filter(ids -> !ids.isEmpty())
                .flatMap(ids -> this.findTaskByTaskExampleAndPlanIds(example -> example.createCriteria().andTaskStatusIn(Lists.newArrayList(FruitDict.TaskDict.START.name())), ids));
        optionalPlanOfTasks.flatMap(planOfTasks -> planIds.stream().filter(planOfTasks::containsKey).map(planOfTasks::get).reduce((l, r) -> {
            l.addAll(r);
            return l;
        })).ifPresent(tasks -> {
            LinkedList<MessageException.RefuseToCompletePlan> refuseToCompletePlans = tasks.stream().map(task -> MessageException.RefuseToCompletePlan.newInstance(task.getTitle(), task.getUsers().stream().map(FruitUserDao::getUserName).collect(joining("、"))))
                    .collect(toCollection(LinkedList::new));
            throw new MessageException(new Gson().toJson(refuseToCompletePlans));
        });
    }

    /**
     * 待进行 -> 进行中
     */
    public final void pending(FruitPlanVo vo) {
        Optional<FruitPlanDao> optionalPlanDao = this.findByUUID(vo.getUuidVo());
        optionalPlanDao.orElseThrow(() -> new CheckException("计划不存在"));
        /*不等于待执行的状态都拒绝*/
        optionalPlanDao.map(FruitPlanDao::getPlanStatus)
                .filter(status -> FruitDict.PlanDict.STAY_PENDING.name().equals(status))
                .orElseThrow(() -> new CheckException("只能种待执行变更到进行中"));
        this.update(update -> {
            update.setUuid(vo.getUuidVo());
            update.setPlanStatus(FruitDict.PlanDict.PENDING.name());
            update.setStartDate(new Date());
        }, fruitPlanExample -> fruitPlanExample.createCriteria().andUuidEqualTo(vo.getUuidVo()));
    }

    public static class Result {
        private final List<FruitPlanDao> plans = Lists.newLinkedList();
        private final Map<FruitDict.PlanDict, Integer> dataCount = Maps.newLinkedHashMap();

        List<FruitPlanDao> getPlans() {
            return plans;
        }

        void setPlans(List<FruitPlanDao> plans) {
            this.plans.addAll(plans);
        }

        void addStateType(FruitDict.PlanDict planDict, Integer count) {
            if (!dataCount.containsKey(planDict))
                dataCount.put(planDict, 1);
            else
                dataCount.put(planDict, dataCount.get(planDict) + count);
        }


        public static Result getInstance() {
            return new Result();
        }
    }

}
