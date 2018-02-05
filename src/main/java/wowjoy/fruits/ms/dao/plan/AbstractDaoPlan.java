package wowjoy.fruits.ms.dao.plan;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import wowjoy.fruits.ms.dao.InterfaceDao;
import wowjoy.fruits.ms.exception.CheckException;
import wowjoy.fruits.ms.exception.ExceptionSupport;
import wowjoy.fruits.ms.exception.ServiceException;
import wowjoy.fruits.ms.module.logs.FruitLogsDao;
import wowjoy.fruits.ms.module.plan.*;
import wowjoy.fruits.ms.module.plan.example.FruitPlanExample;
import wowjoy.fruits.ms.module.task.FruitTaskDao;
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
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;

/**
 * Created by wangziwen on 2017/8/25.
 */
public abstract class AbstractDaoPlan implements InterfaceDao {
    /*********************************************************************************
     * 抽象接口，私有，因为对外的公共接口用来书写业务层，发布api必须在自己的控制范围内，不发布无用的接口。*
     *********************************************************************************/

    protected abstract List<FruitPlanDao> findByProjectId(Consumer<FruitPlanExample> exampleConsumer, String projectId, Integer pageNum, Integer pageSize, boolean isPage);

    protected abstract List<FruitPlanDao> findByProjectId(FruitPlanDao dao);

    protected abstract List<FruitPlanDao> findUserByPlanIds(List<String> planIds, String currentUserId);

    protected abstract List<FruitPlanDao> findTaskByPlanIds(List<String> planIds);

    protected abstract Map<String, LinkedList<FruitLogsDao>> findLogsByPlanIds(List<String> planIds);

    protected abstract FruitPlan find(FruitPlanDao dao);

    protected abstract FruitPlan findByUUID(String uuid);

    protected abstract void insert(FruitPlanDao dao);

    protected abstract void update(Consumer<FruitPlanDao> planDaoConsumer, Consumer<FruitPlanExample> exampleConsumer);

    protected abstract void delete(String uuid);

    protected abstract FruitPlanSummary find(FruitPlanSummaryDao dao);

    protected abstract void insertSummary(FruitPlanSummaryDao dao);

    protected abstract void deleteSummarys(FruitPlanSummaryDao dao);

    /*******************************
     * PUBLIC 函数，公共接口         *
     * 尽量保证规范，不直接调用dao接口 *
     *******************************/

    public final void delete(FruitPlanVo vo) {
        if (!this.findByUUID(vo.getUuidVo()).isNotEmpty()) throw new CheckException("计划不存在，删除失败");
        delete(vo.getUuidVo());
    }

    /**
     * 获取计划，分级展示
     *
     * @param vo
     * @param currentUser
     * @param isPage
     * @return
     */
    private final List<FruitPlanDao> findTree(FruitPlanVo vo, FruitUser currentUser, boolean isPage) {
        DaoThread planThread = DaoThread.getFixed();
        List<FruitPlanDao> planDaoListSource = findByProjectId(example -> {
            FruitPlanExample.Criteria criteria = example.createCriteria();
            criteria.andParentIdIsNull();
            example.setOrderByClause("create_date_time desc");
            if (StringUtils.isNotBlank(vo.sortConstrue()))
                example.setOrderByClause(vo.sortConstrue());
        }, vo.getProjectId(), vo.getPageNum(), vo.getPageSize(), isPage);

        if (planDaoListSource.isEmpty()) return planDaoListSource;

        planThread
                .execute(this.plugUser(planDaoListSource, currentUser));
        Future<List<FruitPlanDao>> weekFuture = planThread.executeFuture(() -> this.findByProjectId(example -> {
            FruitPlanExample.Criteria criteria = example.createCriteria();
            if (StringUtils.isNotBlank(vo.getTitle()))
                criteria.andTitleLike(MessageFormat.format("%{0}%", vo.getTitle()));
            if (StringUtils.isNotBlank(vo.getPlanStatus()))
                criteria.andPlanStatusEqualTo(vo.getPlanStatus());
            criteria.andParentIdIsNotNull();
            criteria.andParentIdIn(planDaoListSource.stream().map(FruitPlanDao::getUuid).collect(toList()));
            example.setOrderByClause("create_date_time desc");
            if (StringUtils.isNotBlank(vo.sortConstrue()))
                example.setOrderByClause(vo.sortConstrue());
        }, vo.getProjectId(), vo.getPageNum(), vo.getPageSize(), false));
        try {
            planThread
                    .execute(this.plugUser(weekFuture.get(), currentUser));
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

    protected List<FruitPlanDao> wherePlan(List<FruitPlanDao> planDaoListSource, FruitPlanVo vo) {
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
            planDaos.stream().forEach(plan -> plan.setLogs(logsMap.get(plan.getUuid())));
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
            planDaos.stream().forEach(plan -> plan.setUsers(userMap.get(plan.getUuid())));
            return true;
        };
    }

    private Callable plugTask(List<FruitPlanDao> planDaos) {
        return () -> {
            if (planDaos.isEmpty()) return false;
            Map<String, LinkedList<FruitTaskDao>> taskMap = this.findTaskByPlanIds(planDaos.stream().map(FruitPlanDao::getUuid).collect(toList()))
                    .stream()
                    .collect(toMap(FruitPlanDao::getUuid, plan -> {
                        LinkedList<FruitTaskDao> taskList = Lists.newLinkedList();
                        taskList.addAll(plan.getTasks());
                        return taskList;
                    }, (l, r) -> {
                        r.addAll(l);
                        return r;
                    }));
            planDaos.stream().forEach(plan -> plan.setTasks(taskMap.get(plan.getUuid())));
            return true;
        };
    }

    /**
     * 统计当前不同状态下数目
     *
     * @param result
     * @return
     */
    private Result dataCount(List<FruitPlanDao> plans, Result result) {
        /*状态匹配*/
        /*进行中的不统计*/
        plans.forEach((plan) -> {
            if (plan.getWeeks() != null && !plan.getWeeks().isEmpty()) dataCount(plan.getWeeks(), result);
            plan.computeDays();
            if (FruitDict.PlanDict.END.name().equals(plan.getPlanStatus()))
                result.addStateType(FruitDict.PlanDict.END, 1);
            else if (FruitDict.PlanDict.PENDING.name().equals(plan.getPlanStatus()) && plan.getDays() < 0)
                result.addStateType(FruitDict.PlanDict.DELAY, 1);
            else if (FruitDict.PlanDict.COMPLETE.name().equals(plan.getPlanStatus()))
                if (plan.getDays() < 0) {
                    result.addStateType(FruitDict.PlanDict.DELAY_COMPLETE, 1);
                } else {
                    result.addStateType(FruitDict.PlanDict.COMPLETE, 1);
                }
        });
        return result;
    }

    public Result compositeQuery(FruitPlanVo vo) {
        Result result = Result.getInstance();
        result.setPlans(findTree(vo, ApplicationContextUtils.getCurrentUser(), false));
        long start = System.currentTimeMillis();
        dataCount(result.getPlans(), result);
        long end = System.currentTimeMillis();
        logger.info("--" + (end - start));
        return result;
    }

    public List<FruitPlanDao> findByProjectId(FruitPlanVo vo) {
        return findByProjectId(findTemplate(vo));
    }

    private FruitPlanDao findTemplate(FruitPlanVo vo) {
        this.getStartTimeAndEndTime(vo);
        final FruitPlanDao dao = FruitPlan.getDao();
        dao.setTitle(vo.getTitle());
        dao.setParentId(vo.getParentId());
        dao.setPlanStatus(StringUtils.isNotBlank(vo.getParentId()) ? vo.getPlanStatus() : null);
        dao.setStartDateDao(vo.getStartDateVo());
        dao.setEndDateDao(vo.getEndDateVo());
        dao.setProjectId(vo.getProjectId());
        dao.setDesc(vo.getDesc());
        dao.setAsc(vo.getAsc());
        return dao;
    }

    /*若年月不为空，则设置开始时间 and 结束时间*/
    protected final void getStartTimeAndEndTime(FruitPlanVo vo) {
        if (StringUtils.isBlank(vo.getYear()) || StringUtils.isBlank(vo.getMonth()))
            return;
        DateUtils.Month<DateUtils.Week.WeekChinese> month = DateUtils.getMonthByYearMonth(Integer.valueOf(vo.getYear()), Integer.valueOf(Integer.valueOf(vo.getMonth())));
        vo.setStartDateVo(Date.from(month.getStartDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        vo.setEndDateVo(Date.from(month.getEndDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
    }

    public final FruitPlan findByUUID(FruitPlanVo vo) {
        FruitUser currentUser = ApplicationContextUtils.getCurrentUser();
        FruitPlan plan = this.findByUUID(vo.getUuidVo());
        if (!plan.isNotEmpty()) throw new CheckException("计划不存在");
        FruitPlanDao planDao = (FruitPlanDao) plan;
        DaoThread.getFixed()
                .execute(plugUser(Lists.newArrayList(planDao), currentUser))
                .execute(this.plugLogs(Lists.newArrayList(planDao)))
                .execute(this.plugTask(Lists.newArrayList(planDao))).get();
        return planDao;
    }

    /**
     * 添加【项目】计划
     *
     * @param vo
     */
    public final void addJoinProject(FruitPlanVo vo) {
        try {
            FruitPlanDao dao = FruitPlan.getDao();
            dao.setUuid(vo.getUuid());
            dao.setPlanStatus(FruitDict.PlanDict.STAY_PENDING.name());
            dao.setEstimatedStartDate(vo.getEstimatedStartDate() != null ? vo.getEstimatedStartDate() : new Date());
            dao.setEstimatedEndDate(vo.getEstimatedEndDate());
            dao.setTitle(vo.getTitle());
            dao.setParentId(vo.getParentId());
            dao.setDescription(vo.getDescription());
            dao.setUserRelation(vo.getUserRelation());
            dao.setProjectRelation(vo.getProjectRelation());
            this.addCheckJoinProject(dao);
            if (this.findByUUID(vo.getUuid()).isNotEmpty()) throw new CheckException("违规的操作，添加不需要传入uuid");
            this.insert(dao);
        } catch (ExceptionSupport ex) {
            throw ex;
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            throw new ServiceException("添加计划出错");
        }
    }

    /*添加【项目】计划前检查参数是否合法*/
    private final void addCheckJoinProject(FruitPlanDao dao) {
        if (StringUtils.isBlank(dao.getTitle()))
            throw new CheckException("标题不能为空");
        if (dao.getProjectRelation(FruitDict.Systems.ADD).isEmpty() || dao.getProjectRelation(FruitDict.Systems.ADD).size() != 1)
            throw new CheckException("限制添加计划只能关联一个项目");
        if (dao.getUserRelation(FruitDict.Systems.ADD).isEmpty())
            throw new CheckException("必须添加至少一个关联用户");
        if (dao.getEstimatedEndDate() == null)
            throw new CheckException("必须填写预计结束时间");
    }

    /**
     * 修改计划
     *
     * @param vo
     */
    public final void modify(FruitPlanVo vo) {
        try {
            if (!this.findByUUID(vo.getUuidVo()).isNotEmpty()) throw new CheckException("计划不存在，修改失败");
            this.update(dao -> {
                dao.setUuid(vo.getUuidVo());
                dao.setTitle(vo.getTitle());
                dao.setDescription(vo.getDescription());
                dao.setEstimatedStartDate(vo.getEstimatedStartDate());
                dao.setEstimatedEndDate(vo.getEstimatedEndDate());
                dao.setPercent(vo.getPercent());
                dao.setUserRelation(vo.getUserRelation());
            }, fruitPlanExample -> fruitPlanExample.createCriteria().andUuidEqualTo(vo.getUuidVo()));
        } catch (ExceptionSupport ex) {
            throw ex;
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            throw new ServiceException("修改计划错误");
        }
    }

    /**
     * 添加计划关联摘要
     *
     * @param vo
     */
    public final void insertSummary(FruitPlanSummaryVo vo) {
        try {
            if (!this.findByUUID(vo.getPlanId()).isNotEmpty()) throw new CheckException("计划不存在，修改失败");
            FruitPlanSummaryDao dao = FruitPlanSummary.getDao();
            dao.setUuid(vo.getUuid());
            dao.setPlanId(vo.getPlanId());
            dao.setPercent(vo.getPercent());
            dao.setDescription(vo.getDescription());
            this.insertSummary(dao);
        } catch (ExceptionSupport ex) {
            throw ex;
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            throw new ServiceException("添加摘要错误");
        }
    }

    /**
     * 终止计划
     *
     * @param vo
     */
    public final void end(FruitPlanVo vo) {
        if (checkPlan(vo))
            throw new CheckException("计划不存在");
        this.update(dao -> {
            dao.setUuid(vo.getUuidVo());
            dao.setPlanStatus(FruitDict.PlanDict.END.name());
            dao.setEndDate(LocalDateTime.now());
            dao.setStatusDescription(vo.getStatusDescription());
        }, fruitPlanExample -> fruitPlanExample.createCriteria().andUuidEqualTo(vo.getUuidVo()));
    }


    /**
     * 完成计划
     *
     * @param vo
     */
    public final void complete(FruitPlanVo vo) {
        if (checkPlan(vo))
            throw new CheckException("计划不存在");
        FruitPlanDao plan = (FruitPlanDao) this.findByUUID(vo.getUuidVo());
        /*要完成计划必须填写实际开始时间*/
        if (FruitDict.PlanDict.STAY_PENDING.name().equals(plan.getPlanStatus())) {
            if (vo.getStartDate() == null)
                throw new CheckException("必须填写实际开始时间");
        } else vo.setStartDate(null);

        /*如果延期，必须填写延期说明*/
        if (plan.computeDays().getDays() < 0) {
            if (StringUtils.isBlank(vo.getStatusDescription()))
                throw new CheckException("计划延期完成，需要填写延期说明");
            if (vo.getEndDate() != null && Duration.between(LocalDate.now(), LocalDateTime.ofInstant(vo.getEndDate().toInstant(), ZoneId.systemDefault())).toDays() > 0)
                throw new CheckException("实际结束时间不能大约今天");
        }

        this.update(dao -> {
            dao.setUuid(vo.getUuidVo());
            dao.setPlanStatus(FruitDict.PlanDict.COMPLETE.name());
            /*未延期默认是当前时间，延期需要填写延期时间*/
            dao.setStartDate(vo.getStartDate());
            dao.setEndDate(vo.getEndDate() == null ? LocalDateTime.now() : LocalDateTime.ofInstant(vo.getEndDate().toInstant(), ZoneId.systemDefault()));
            dao.setStatusDescription(vo.getStatusDescription());
        }, fruitPlanExample -> fruitPlanExample.createCriteria().andUuidEqualTo(vo.getUuidVo()));
    }

    /**
     * 待进行 -> 进行中
     *
     * @param vo
     */
    public final void pending(FruitPlanVo vo) {
        if (checkPlan(vo))
            throw new CheckException("计划不存在");
        FruitPlan plan = this.findByUUID(vo.getUuidVo());
        if (!FruitDict.PlanDict.STAY_PENDING.name().equals(plan.getPlanStatus()))
            throw new CheckException("状态转换条件：待进行 -> 进行中");
        this.update(dao -> {
            dao.setUuid(vo.getUuidVo());
            dao.setPlanStatus(FruitDict.PlanDict.PENDING.name());
            dao.setStartDate(new Date());
        }, fruitPlanExample -> fruitPlanExample.createCriteria().andUuidEqualTo(vo.getUuidVo()));
    }

    private boolean checkPlan(FruitPlanVo vo) {
        FruitPlan plan = this.findByUUID(vo.getUuidVo());
        if (!plan.isNotEmpty())
            return true;
        if (FruitDict.PlanDict.COMPLETE.name().equals(plan.getPlanStatus()) ||
                FruitDict.PlanDict.END.name().equals(plan.getPlanStatus()))
            throw new CheckException("计划生命周期已结束，不可在进行任何操作");
        return false;
    }

    public static class Result {
        private final List<FruitPlanDao> plans = Lists.newLinkedList();
        private final Map<FruitDict.PlanDict, Integer> dataCount = Maps.newLinkedHashMap();

        public List<FruitPlanDao> getPlans() {
            return plans;
        }

        public void setPlans(List<FruitPlanDao> plans) {
            this.plans.addAll(plans);
        }

        public void addStateType(FruitDict.PlanDict planDict, Integer count) {
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
