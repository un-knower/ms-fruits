package wowjoy.fruits.ms.dao.plan;

import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import wowjoy.fruits.ms.dao.InterfaceDao;
import wowjoy.fruits.ms.exception.CheckException;
import wowjoy.fruits.ms.module.plan.*;
import wowjoy.fruits.ms.module.relation.entity.PlanProjectRelation;
import wowjoy.fruits.ms.module.util.entity.FruitDict;
import wowjoy.fruits.ms.util.DateUtils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created by wangziwen on 2017/8/25.
 */
public abstract class AbstractDaoPlan implements InterfaceDao {
    /*********************************************************************************
     * 抽象接口，私有，因为对外的公共接口用来书写业务层，发布api必须在自己的控制范围内，不发布无用的接口。*
     *********************************************************************************/

    protected abstract List<FruitPlanDao> findProject(FruitPlanDao dao, Integer pageNum, Integer pageSize, boolean isPage);

    protected abstract List<FruitPlanDao> find(FruitPlanDao dao, Integer pageNum, Integer pageSize);

    protected abstract FruitPlan findByUUID(String uuid);

    protected abstract void insert(FruitPlanDao dao);

    protected abstract void update(FruitPlanDao dao);

    protected abstract void delete(String uuid);

    protected abstract void insertSummary(FruitPlanSummaryDao dao);

    protected abstract void deleteSummarys(FruitPlanSummaryDao dao);

    protected abstract List<PlanProjectRelation> findJoin(PlanProjectRelation relation);

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
    public final List<FruitPlanDao> findMonthWeek(FruitPlanVo vo) {
        this.getStarttimeAndEndtime(vo);
        final FruitPlanDao dao = FruitPlan.getDao();
        dao.setTitle(vo.getTitle());
        dao.setPlanStatus(vo.getPlanStatus());
        dao.setStartDateDao(vo.getStartDateVo());
        dao.setEndDateDao(vo.getEndDateVo());
        dao.setProjectId(vo.getProjectId());
        dao.setDesc(vo.getDesc());
        dao.setAsc(vo.getAsc());
        return this.fillPlanWeek(findProject(dao, vo.getPageNum(), vo.getPageSize(), true), IExecutor.newInstance());
    }

    /*若年月不为空，则设置开始时间 and 结束时间*/
    protected final void getStarttimeAndEndtime(FruitPlanVo vo) {
        if (StringUtils.isBlank(vo.getYear()) || StringUtils.isBlank(vo.getMonth()))
            return;
        DateUtils.Month<DateUtils.Week.WeekChinese> month = DateUtils.getMonthByYearMonth(Integer.valueOf(vo.getYear()), Integer.valueOf(Integer.valueOf(vo.getMonth())));
        vo.setStartDateVo(Date.from(month.getStartDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
        vo.setEndDateVo(Date.from(month.getEndDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
    }

    private final List<FruitPlanDao> fillPlanWeek(List<FruitPlanDao> plans, IExecutor iExecutor) {
        final List<FruitPlanDao> data = plans;
        List<String> parents = Lists.newLinkedList();
        plans.forEach((i) -> parents.add(i.getUuid()));
        long start = System.nanoTime();
        List<Future> futures = Lists.newLinkedList();
        System.out.println(start);
        plans.forEach((parent) -> {
            futures.add(iExecutor.submit(() -> {
                FruitPlanDao dao = FruitPlan.getDao();
                dao.setParentId(parent.getUuid());
                parent.setWeeks(this.findProject(dao, 0, 0, false));
                return parent;
            }));
        });
        wait(futures, iExecutor);
        System.out.println(System.nanoTime() - start);
        return data;
    }

    public void wait(List<Future> futures, IExecutor iExecutor) {
        for (Future week : futures) {
            try {
                week.get(1, TimeUnit.MINUTES);
            } catch (InterruptedException e) {
                iExecutor.shutdownNow();
                throw new CheckException("获取计划中断");
            } catch (ExecutionException e) {
            } catch (TimeoutException e) {
                iExecutor.shutdownNow();
                throw new CheckException("获取计划超时");
            }
        }
    }

    /**
     * 月计划
     *
     * @param vo
     * @return
     */
    public final List<FruitPlanDao> findProject(FruitPlanVo vo) {
        final FruitPlanDao dao = FruitPlan.getDao();
        dao.setTitle(vo.getTitle());
        dao.setPlanStatus(vo.getPlanStatus());
        dao.setStartDateDao(vo.getStartDateVo());
        dao.setEndDateDao(vo.getEndDateVo());
        dao.setProjectId(vo.getProjectId());
        return this.findProject(dao, vo.getPageNum(), vo.getPageSize(), false);
    }

    /**
     * 周计划
     *
     * @param vo
     * @return
     */
    public final List<FruitPlanDao> findWeekProject(FruitPlanVo vo) {
        if (StringUtils.isNotBlank(vo.getParentId()))
            throw new CheckException("必须填写父id");
        FruitPlanDao dao = FruitPlan.getDao();
        dao.setTitle(vo.getTitle());
        dao.setParentId(vo.getParentId());
        dao.setPlanStatus(vo.getPlanStatus());
        dao.setStartDateDao(vo.getStartDateVo());
        dao.setEndDateDao(vo.getEndDateVo());
        dao.setProjectId(vo.getProjectId());
        return this.findProject(dao, vo.getPageNum(), vo.getPageSize(), false);
    }

    /**
     * 月计划
     *
     * @param vo
     * @return
     */
    public final List<FruitPlanDao> find(FruitPlanVo vo) {
        final FruitPlanDao dao = FruitPlan.getDao();
        dao.setTitle(vo.getTitle());
        dao.setPlanStatus(vo.getPlanStatus());
        dao.setStartDateDao(vo.getStartDateVo());
        dao.setEndDateDao(vo.getEndDateVo());
        return this.find(dao, vo.getPageNum(), vo.getPageSize());
    }

    /**
     * 周计划
     *
     * @param vo
     * @return
     */
    public final List<FruitPlanDao> findWeek(FruitPlanVo vo) {
        if (StringUtils.isNotBlank(vo.getParentId()))
            throw new CheckException("必须填写父id");
        FruitPlanDao dao = FruitPlan.getDao();
        dao.setTitle(vo.getTitle());
        dao.setParentId(vo.getParentId());
        dao.setPlanStatus(vo.getPlanStatus());
        dao.setStartDateDao(vo.getStartDateVo());
        dao.setEndDateDao(vo.getEndDateVo());
        return this.find(dao, vo.getPageNum(), vo.getPageSize());
    }

    public final FruitPlan findByUUID(FruitPlanVo vo) {
        return this.findByUUID(vo.getUuidVo());
    }

    public final void add(FruitPlanVo vo) {
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
    }

    private final void addCheckJoinProject(FruitPlanDao dao) {
        if (dao.getProjectRelation(FruitDict.Dict.ADD).isEmpty() || dao.getProjectRelation(FruitDict.Dict.ADD).size() != 1)
            throw new CheckException("限制添加计划只能关联一个项目");
        if (dao.getUserRelation(FruitDict.Dict.ADD).isEmpty())
            throw new CheckException("必须添加至少一个关联用户");
        if (dao.getEstimatedEndDate() == null)
            throw new CheckException("必须填写预计结束时间");
    }

    public final void modify(FruitPlanVo vo) {
        if (!this.findByUUID(vo.getUuidVo()).isNotEmpty()) throw new CheckException("计划不存在，修改失败");
        FruitPlanDao dao = FruitPlan.getDao();
        dao.setUuid(vo.getUuidVo());
        dao.setTitle(vo.getTitle());
        dao.setDescription(vo.getDescription());
        dao.setEstimatedStartDate(vo.getEstimatedStartDate());
        dao.setEstimatedEndDate(vo.getEstimatedEndDate());
        dao.setPercent(vo.getPercent());
        dao.setUserRelation(vo.getUserRelation());
        dao.setProjectRelation(vo.getProjectRelation());
        this.modifyCheckJoinProject(dao);
        this.update(dao);
    }

    private final void modifyCheckJoinProject(FruitPlanDao dao) {
        if (dao.getProjectRelation(FruitDict.Dict.ADD).isEmpty()) return;
        if (StringUtils.isBlank(dao.getProjectRelation(FruitDict.Dict.ADD).get(0))) return;
        dao.setProjectRelation(FruitDict.Dict.DELETE, Lists.newArrayList(this.findJoin(PlanProjectRelation.newInstance(dao.getUuid())).get(0).getProjectId()));
    }

    public final void insertSummary(FruitPlanSummaryVo vo) {
        if (!this.findByUUID(vo.getPlanId()).isNotEmpty()) throw new CheckException("计划不存在，修改失败");
        FruitPlanSummaryDao dao = FruitPlanSummary.getDao();
        dao.setUuid(vo.getUuid());
        dao.setPlanId(vo.getPlanId());
        dao.setPercent(vo.getPercent());
        dao.setDescription(vo.getDescription());
        this.insertSummary(dao);
    }

    /**
     * 终止计划
     *
     * @param vo
     */
    public final void end(FruitPlanVo vo) {
        vo.setPlanStatus(FruitDict.PlanDict.END.name());
        this.modifyStatus(vo);
    }

    /**
     * 完成计划
     *
     * @param vo
     */
    public final void complete(FruitPlanVo vo) {
        vo.setPlanStatus(FruitDict.PlanDict.COMPLETE.name());
        this.modifyStatus(vo);
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

    private static class IExecutor {
        private final ExecutorService service = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1);

        public <T> Future<T> submit(Callable callable) {
            return service.submit(callable);
        }

        public static IExecutor newInstance() {
            return new IExecutor();
        }

        public void shutdownNow() {
            service.shutdownNow();
        }

    }

}
