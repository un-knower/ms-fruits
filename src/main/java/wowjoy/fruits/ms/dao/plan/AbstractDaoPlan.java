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
import wowjoy.fruits.ms.module.user.FruitUser;
import wowjoy.fruits.ms.module.user.FruitUserDao;
import wowjoy.fruits.ms.module.util.entity.FruitDict;
import wowjoy.fruits.ms.util.ApplicationContextUtils;
import wowjoy.fruits.ms.util.DateUtils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

/**
 * Created by wangziwen on 2017/8/25.
 */
public abstract class AbstractDaoPlan implements InterfaceDao {
    /*********************************************************************************
     * 抽象接口，私有，因为对外的公共接口用来书写业务层，发布api必须在自己的控制范围内，不发布无用的接口。*
     *********************************************************************************/

    protected abstract List<FruitPlanDao> findByProjectId(FruitPlanDao dao, Integer pageNum, Integer pageSize, boolean isPage);

    protected abstract List<FruitPlanDao> findByProjectId(FruitPlanDao dao);

    protected abstract List<FruitPlanDao> findUserByPlanIds(List<String> planIds, String currentUserId);

    protected abstract List<FruitPlanDao> findLogsByPlanIds(List<String> planIds);

    protected abstract FruitPlan find(FruitPlanDao dao);

    protected abstract FruitPlan findByUUID(String uuid);

    protected abstract void insert(FruitPlanDao dao);

    protected abstract void update(FruitPlanDao dao);

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
     * 计划查询：
     * 1、数据用树型结构展示
     * 2、目前只递归两层，若多层递归需要考虑更换实现方式，目前的多层递归效率比较低
     *
     * @param vo
     * @return
     */
    private final List<FruitPlanDao> findTree(FruitPlanVo vo, FruitUser currentUser, boolean isPage) {
        List<FruitPlanDao> planDaoListSource = findByProjectId(this.findTemplate(vo), vo.getPageNum(), vo.getPageSize(), isPage);
        List<FruitPlanDao> planDaoListCopy = new ArrayList<>(Arrays.asList(new FruitPlanDao[planDaoListSource.size()]));
        Collections.copy(planDaoListCopy, planDaoListSource);
        if (planDaoListSource.isEmpty()) return Lists.newLinkedList();
        DaoThread planThread = DaoThread.getFixed();
        List<String> ids = toIds(planDaoListSource);
        /*查询用户信息*/
        planThread.execute(() -> {
            List<FruitPlanDao> users = this.findUserByPlanIds(ids, currentUser.getUserId());
            LinkedHashMap<String, List<FruitUserDao>> userMaps = Maps.newLinkedHashMap();
            users.forEach((i) -> userMaps.put(i.getUuid(), i.getUsers()));
            planDaoListSource.forEach((i) -> i.setUsers(userMaps.get(i.getUuid())));
            return true;
        });
        planThread.execute(() -> {
            List<FruitPlanDao> logs = this.findLogsByPlanIds(ids);
            LinkedHashMap<String, List<FruitLogsDao>> logsMaps = Maps.newLinkedHashMap();
            logs.forEach((i) -> logsMaps.put(i.getUuid(), i.getLogs()));
            planDaoListCopy.forEach((i) -> i.setLogs(logsMaps.get(i.getUuid())));
            return true;
        });
        /*计算过期时间*/
        planThread.execute(() -> {
            planDaoListSource.forEach((i) -> i.computeDays());
            return true;
        });
        /*只递归两层*/
        if (StringUtils.isNotBlank(vo.getParentId())) {
            planThread.get();
            return planDaoListSource;
        }
        planDaoListSource.forEach((plan) -> {
            plan.setWeeks(Lists.newLinkedList());
            planThread.execute(() -> {
                FruitPlanVo childVo = FruitPlan.getVo();
                childVo.setParentId(plan.getUuid());
                childVo.setDesc(vo.getDesc());
                childVo.setAsc(vo.getAsc());
                childVo.setPlanStatus(vo.getPlanStatus());
                plan.getWeeks().addAll(this.findTree(childVo, currentUser, false));
                if (StringUtils.isNotBlank(vo.getPlanStatus()) && !plan.getPlanStatus().equals(vo.getPlanStatus()) && plan.getWeeks().isEmpty())
                    planDaoListCopy.remove(plan);
                return true;
            });
        });
        planThread.get();
        planThread.shutdown();
        return planDaoListCopy;
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

    private List<String> toIds(List<FruitPlanDao> plans) {
        List<String> result = Lists.newLinkedList();
        plans.forEach((i) -> result.add(i.getUuid()));
        return result;
    }

    public final FruitPlan findByUUID(FruitPlanVo vo) {
        return this.findByUUID(vo.getUuidVo());
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
            dao.setPlanStatus(FruitDict.PlanDict.PENDING.name());
            dao.setEstimatedStartDate(vo.getEstimatedStartDate() != null ? vo.getEstimatedStartDate() : new Date());
            dao.setEstimatedEndDate(vo.getEstimatedEndDate());
            dao.setTitle(vo.getTitle());
            dao.setParentId(vo.getParentId());
            dao.setDescription(vo.getDescription());
            dao.setUserRelation(vo.getUserRelation());
            dao.setProjectRelation(vo.getProjectRelation());
            this.addCheckJoinProject(dao);
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
            FruitPlanDao dao = FruitPlan.getDao();
            dao.setUuid(vo.getUuidVo());
            dao.setTitle(vo.getTitle());
            dao.setDescription(vo.getDescription());
            dao.setEstimatedStartDate(vo.getEstimatedStartDate());
            dao.setEstimatedEndDate(vo.getEstimatedEndDate());
            dao.setPercent(vo.getPercent());
            dao.setUserRelation(vo.getUserRelation());
            this.update(dao);
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
        checkPlan(vo);
        try {
            vo.setPlanStatus(FruitDict.PlanDict.END.name());
            this.modifyStatus(vo);
        } catch (CheckException ex) {
            throw ex;
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            throw new ServiceException("计划终止错误");
        }
    }


    /**
     * 完成计划
     *
     * @param vo
     */
    public final void complete(FruitPlanVo vo) {
        FruitPlan project = checkPlan(vo);
        /*是否延期*/
        if (((FruitPlanDao) project).computeDays().getDays() < 0) {
            /*必须填写延期说明*/
            if (StringUtils.isBlank(vo.getStatusDescription()))
                throw new CheckException("计划延期完成，需要填写延期说明");
            /*允许不改变预计结束时间*/
            if (vo.getEstimatedEndDate() != null)
                vo.setEstimatedEndDate(vo.getEstimatedEndDate());
        }
        try {
            vo.setPlanStatus(FruitDict.PlanDict.COMPLETE.name());
            this.modifyStatus(vo);
        } catch (ExceptionSupport ex) {
            throw ex;
        } catch (RuntimeException ex) {
            ex.printStackTrace();
            throw new ServiceException("计划状态转为完成时出现错误");
        }
    }

    private FruitPlan checkPlan(FruitPlanVo vo) {
        FruitPlan project = this.findByUUID(vo.getUuidVo());
        if (!project.isNotEmpty()) throw new CheckException("计划不存在");
        if (!FruitDict.PlanDict.PENDING.name().equals(project.getPlanStatus()))
            throw new CheckException("计划生命周期已结束，无法更改");
        return project;
    }

    /**
     * PRIVATE/PROTECTED
     */

    private final void modifyStatus(FruitPlanVo vo) {
        FruitPlanDao dao = FruitPlan.getDao();
        dao.setUuid(vo.getUuidVo());
        dao.setPlanStatus(vo.getPlanStatus());
        dao.setEndDate(LocalDate.now());
        dao.setStatusDescription(vo.getStatusDescription());
        this.update(dao);
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
