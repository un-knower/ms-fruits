package wowjoy.fruits.ms.dao.plan;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import wowjoy.fruits.ms.dao.InterfaceDao;
import wowjoy.fruits.ms.exception.CheckException;
import wowjoy.fruits.ms.exception.ExceptionSupport;
import wowjoy.fruits.ms.exception.ServiceException;
import wowjoy.fruits.ms.module.plan.*;
import wowjoy.fruits.ms.module.user.FruitUserDao;
import wowjoy.fruits.ms.module.util.entity.FruitDict;
import wowjoy.fruits.ms.util.DateUtils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by wangziwen on 2017/8/25.
 */
public abstract class AbstractDaoPlan implements InterfaceDao {
    /*********************************************************************************
     * 抽象接口，私有，因为对外的公共接口用来书写业务层，发布api必须在自己的控制范围内，不发布无用的接口。*
     *********************************************************************************/

    protected abstract List<FruitPlanDao> findProject(FruitPlanDao dao, Integer pageNum, Integer pageSize, boolean isPage);

    protected abstract List<FruitPlanDao> findUserByPlanIds(List<String> planIds);

    protected abstract FruitPlan find(FruitPlanDao dao);

    protected abstract FruitPlan findByUUID(String uuid);

    protected abstract void insert(FruitPlanDao dao);

    protected abstract void update(FruitPlanDao dao);

    protected abstract void delete(String uuid);

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
     * 项目月-周计划
     *
     * @param vo
     * @return
     */
    public final List<FruitPlanDao> findMonthWeek(FruitPlanVo vo, boolean isPage) {
        List<FruitPlanDao> result = Lists.newLinkedList();
        List<FruitPlanDao> planDaoList = findProject(this.findMonthWeekTemplate(vo), vo.getPageNum(), vo.getPageSize(), isPage);
        if (planDaoList.isEmpty()) return Lists.newLinkedList();
        DaoThread planThread = DaoThread.getInstance();
        List<String> ids = toIds(planDaoList);
        /*查询用户信息*/
        planThread.execute(() -> {
            List<FruitPlanDao> users = this.findUserByPlanIds(ids);
            LinkedHashMap<String, List<FruitUserDao>> userMaps = Maps.newLinkedHashMap();
            users.forEach((i) -> userMaps.put(i.getUuid(), i.getUsers()));
            planDaoList.forEach((i) -> i.setUsers(userMaps.get(i.getUuid())));
            return true;
        });
        /*计算过期时间*/
        planThread.execute(() -> {
            planDaoList.forEach((i) -> i.computeDays());
            return true;
        });
        /*只递归两层，相当于月、周*/
        if (StringUtils.isNotBlank(vo.getParentId())) {
            planThread.get();
            return planDaoList;
        }
        planDaoList.forEach((plan) -> {
            plan.setWeeks(Lists.newLinkedList());
            planThread.execute(() -> {
                FruitPlanVo childVo = FruitPlan.getVo();
                childVo.setParentId(plan.getUuid());
                childVo.setDesc(vo.getDesc());
                childVo.setAsc(vo.getAsc());
                childVo.setPlanStatus(vo.getPlanStatus());
                plan.getWeeks().addAll(this.findMonthWeek(childVo, false));
                if (StringUtils.isNotBlank(vo.getPlanStatus()) && !plan.getPlanStatus().equals(vo.getPlanStatus()) && plan.getWeeks().isEmpty()) return false;
                result.add(plan);
                return true;
            });
        });
        planThread.get();
        return result;
    }

    private FruitPlanDao findMonthWeekTemplate(FruitPlanVo vo) {
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

    /**
     * PRIVATE/PROTECTED
     */

    private final void modifyStatus(FruitPlanVo vo) {
        FruitPlan project = this.findByUUID(vo.getUuidVo());
        if (!project.isNotEmpty()) throw new CheckException("计划不存在，无法完成");
        if (FruitDict.PlanDict.END.name().equals(project.getPlanStatus()) || FruitDict.PlanDict.COMPLETE.name().equals(project.getPlanStatus()))
            throw new CheckException("计划状态已终止或已完成");
        FruitPlanDao dao = FruitPlan.getDao();
        dao.setUuid(vo.getUuidVo());
        dao.setPlanStatus(vo.getPlanStatus());
        dao.setEndDate(LocalDate.now());
        dao.setStatusDescription(vo.getStatusDescription());
        this.update(dao);
    }

    /**********
     * 内部类  *
     **********/

//    private static class PlanThread {
//        private final ExecutorService service = Executors.newFixedThreadPool(processorCount);
//        private List<Future> futures = Lists.newLinkedList();
//
//        public PlanThread submit(Callable callable) {
//            futures.add(service.submit(callable));
//            return this;
//        }
//
//        public List<Future> get() {
//            try {
//                for (Future week : futures) week.get(1, TimeUnit.MINUTES);
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//                throw new CheckException("主线程中断");
//            } catch (ExecutionException e) {
//                e.printStackTrace();
//                throw new CheckException("线程异常中断");
//            } catch (TimeoutException e) {
//                throw new CheckException("线程超时");
//            } finally {
//                service.shutdownNow();
//            }
//            return futures;
//        }
//
//        public static PlanThread newInstance() {
//            return new PlanThread();
//        }
//
//    }

}
