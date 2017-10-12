package wowjoy.fruits.ms.dao.plan;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import wowjoy.fruits.ms.dao.InterfaceDao;
import wowjoy.fruits.ms.exception.CheckException;
import wowjoy.fruits.ms.module.plan.*;
import wowjoy.fruits.ms.module.util.entity.FruitDict;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Created by wangziwen on 2017/8/25.
 */
public abstract class AbstractDaoPlan implements InterfaceDao {
    /*********************************************************************************
     * 抽象接口，私有，因为对外的公共接口用来书写业务层，发布api必须在自己的控制范围内，不发布无用的接口。*
     *********************************************************************************/

    protected abstract List<FruitPlanDao> findRelationMonth(FruitPlanDao dao, Integer pageNum, Integer pageSize);

    protected abstract List<FruitPlanDao> findRelationWeek(FruitPlanDao dao, Integer pageNum, Integer pageSize);

    protected abstract List<FruitPlanDao> findMonth(FruitPlanDao dao, Integer pageNum, Integer pageSize);

    protected abstract List<FruitPlanDao> findWeek(FruitPlanDao dao, Integer pageNum, Integer pageSize);

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
        if (!this.findByUUID(vo.getUuidVo()).isNotEmpty()) throw new CheckPlanException("计划不存在，删除失败");
        delete(vo.getUuidVo());
    }

    /**
     * 月计划
     *
     * @param vo
     * @return
     */
    public final List<FruitPlanDao> findRelationMonth(FruitPlanVo vo) {
        final FruitPlanDao dao = FruitPlan.getDao();
        dao.setTitle(vo.getTitle());
        dao.setPlanStatus(vo.getPlanStatus());
        dao.setStartDateDao(vo.getStartDateVo());
        dao.setEndDateDao(vo.getEndDateVo());
        return this.findRelationMonth(dao, vo.getPageNum(), vo.getPageSize());
    }

    /**
     * 周计划
     *
     * @param vo
     * @return
     */
    public final List<FruitPlanDao> findRelationWeek(FruitPlanVo vo) {
        FruitPlanDao dao = FruitPlan.getDao();
        dao.setTitle(vo.getTitle());
        dao.setParentId(vo.getParentId());
        dao.setPlanStatus(vo.getPlanStatus());
        dao.setStartDateDao(vo.getStartDateVo());
        dao.setEndDateDao(vo.getEndDateVo());
        return this.findRelationWeek(dao, vo.getPageNum(), vo.getPageSize());
    }

    /**
     * 月计划
     *
     * @param vo
     * @return
     */
    public final List<FruitPlanDao> findMonth(FruitPlanVo vo) {
        final FruitPlanDao dao = FruitPlan.getDao();
        dao.setTitle(vo.getTitle());
        dao.setPlanStatus(vo.getPlanStatus());
        dao.setStartDateDao(vo.getStartDateVo());
        dao.setEndDateDao(vo.getEndDateVo());
        return this.findMonth(dao, vo.getPageNum(), vo.getPageSize());
    }

    /**
     * 周计划
     *
     * @param vo
     * @return
     */
    public final List<FruitPlanDao> findWeek(FruitPlanVo vo) {
        FruitPlanDao dao = FruitPlan.getDao();
        dao.setTitle(vo.getTitle());
        dao.setParentId(vo.getParentId());
        dao.setPlanStatus(vo.getPlanStatus());
        dao.setStartDateDao(vo.getStartDateVo());
        dao.setEndDateDao(vo.getEndDateVo());
        return this.findWeek(dao, vo.getPageNum(), vo.getPageSize());
    }

    public final FruitPlan findByUUID(FruitPlanVo vo) {
        return this.findByUUID(vo.getUuidVo());
    }

    public final void insert(FruitPlanVo vo) {
        FruitPlanDao dao = FruitPlan.getDao();
        dao.setUuid(vo.getUuid());
        dao.setPlanStatus(FruitDict.PlanDict.PENDING.name());
        dao.setEstimatedEndDate(vo.getEstimatedEndDate());
        dao.setTitle(vo.getTitle());
        dao.setParentId(vo.getParentId());
        dao.setDescription(vo.getDescription());
        dao.setUserRelation(vo.getUserRelation());
        dao.setProjectRelation(vo.getProjectRelation());
        this.insert(dao);
    }

    public final void insertSummary(FruitPlanSummaryVo vo) {
        if (!this.findByUUID(vo.getPlanId()).isNotEmpty()) throw new CheckPlanException("计划不存在，修改失败");
        FruitPlanSummaryDao dao = FruitPlanSummary.getDao();
        dao.setUuid(vo.getUuid());
        dao.setPlanId(vo.getPlanId());
        dao.setPercent(vo.getPercent());
        dao.setDescription(vo.getDescription());
        this.insertSummary(dao);
    }

    public final void modify(FruitPlanVo vo) {
        if (!this.findByUUID(vo.getUuidVo()).isNotEmpty()) throw new CheckPlanException("计划不存在，修改失败");
        FruitPlanDao dao = FruitPlan.getDao();
        dao.setUuid(vo.getUuidVo());
        dao.setTitle(vo.getTitle());
        dao.setUserRelation(vo.getUserRelation());
        dao.setProjectRelation(vo.getProjectRelation());
        dao.setDescription(vo.getDescription());
        dao.setEstimatedEndDate(vo.getEstimatedEndDate());
        dao.setPercent(vo.getPercent());
        this.update(dao);
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
     * 提供一年相对星期数
     *
     * @param year
     * @return
     */
    public final Map<Integer, List<DateUtils.Week.WeekChinese>> yearEachWeek(int year) {
        return DateUtils.getInstance(year).weeks().toChinese();
    }

    /****************
     * 计划自定义异常 *
     ****************/
    public class CheckPlanException extends CheckException {
        public CheckPlanException(String message) {
            super(message);
        }
    }

    /**
     * PRIVATE/PROTECTED
     */

    private final void modifyStatus(FruitPlanVo vo) {
        FruitPlan project = this.findByUUID(vo.getUuidVo());
        if (!project.isNotEmpty()) throw new CheckPlanException("计划不存在，无法完成");
        if (FruitDict.PlanDict.END.name().equals(project.getPlanStatus()) || FruitDict.PlanDict.COMPLETE.name().equals(project.getPlanStatus()))
            throw new CheckPlanException("计划状态已终止或已完成");
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

    /**
     * 提供年月日计算工具
     * 目前专为计划提供支持，后期如有需要，可以扩展为顶级类
     */
    private static class DateUtils {
        private final Integer Year;
        private final Map<Integer, List<Week>> Months = Maps.newLinkedHashMap();


        private DateUtils(Integer year) {
            Year = year;
        }

        public static DateUtils getInstance(Integer year) {
            return new DateUtils(year);
        }

        /**
         * 根据月份得出天数
         * 闰年2月29天，平年2月28天
         *
         * @param month
         * @return
         */
        private int monthDays(Integer month) {
            int dom = 31;
            switch (month) {
                case 2:
                    dom = (this.calendar() == 366 ? 29 : 28);
                    break;
                case 4:
                case 6:
                case 9:
                case 11:
                    dom = 30;
                    break;
            }
            return dom;
        }

        /**
         * 计算平年、闰年
         * 规则：
         * 能被100整除的是世纪年，又能被400整除的是世纪闰年。
         * 不能被100整除的是普通年，能被4整除的普通年是闰年
         */
        private int calendar() {
            if ((Year % 100 == 0 && Year % 400 == 0) || (Year % 100 != 0 && Year % 4 == 0)) {
                return 366;
            } else {
                return 365;
            }
        }

        /**
         * 计算闰年数
         * 世纪数前两位除4向下取整
         *
         * @return
         */
        private int leapYearNumber() {
            final String result = String.valueOf(Year).substring(String.valueOf(Year).length() - 2, String.valueOf(Year).length());
            return (int) Math.floor(Double.valueOf(result) / 4);
        }

        /**
         * 计算世纪星期数
         * 第零天指的是1988年12月31日
         * 第一天指的是1999年1月1日
         * 计算世纪的第一个星期数，如果世纪年是闰年，则世纪数相对的星期数是第一天不是第零天
         * 例如：
         * 2000年1月1日计算的出来的数字是6，由于2000年是世纪闰年，所以就是星期六。
         * 1900年1月1日计算出来的数字是0，需要加一，才能得出1900年1月1日的星期数。
         */
        private int centoryWeekNumber() {
            final Integer centoryNummber = Integer.valueOf(String.valueOf(Year).substring(0, 2));
            final int surplus = ((centoryNummber % 4) - 3) * 2;
            return surplus < 0 ? -surplus : surplus;
        }


        /**
         * 计算普通年星期数
         * 规则：
         * 得出闰年数目
         * 得出世纪年到普通年年数
         * 得出世纪年相对星期数
         * （计算闰年数目+普通年年数+世纪年相对星期数）同余7后的结果
         * 结果在根据当年是否是平年来选择是否加一。
         *
         * @return
         */
        private int yearWeekNumber() {
            final Integer yearNumber = Integer.valueOf(String.valueOf(Year).substring(String.valueOf(Year).length() - 2, String.valueOf(Year).length()));
            final int leapYearNumber = leapYearNumber();
            final int centoryWeekNumber = centoryWeekNumber();
            final int number = (centoryWeekNumber + yearNumber + leapYearNumber) % 7;
            return calendar() == 365 ? number + 1 : number;
        }

        /**
         * 根据「yearWeekNumber」函数计算出的星期数，再往后推算出当年一年的星期数。
         * 计算方式有待优化，当前计算方式：
         * 获取普通年相对星期数，在根据每月的天数向后推算
         * <p>
         * 找到计算月相对星期数的算法，替代掉if (day > days && Math.ceil(days / 7) <= Math.floor(day / 7))这句判断语句，
         * 能知道每月相对星期数就可以考虑使用多线程并发执行加快处理速度。
         *
         * @return
         */
        public DateUtils weeks() {
            final int firstDay = yearWeekNumber();
            int year = Year;
            int month = 1;
            Months.put(month, Lists.newLinkedList());
            int day = 0;
            day += (firstDay >= 2 ? (7 - (firstDay - 1)) : 0) + 1;
            int days = this.monthDays(month);
            while (true) {
                if (day > days && Math.ceil(days / 7) <= Math.floor(day / 7)) {
                    month++;
                    if (month > 12) break;
                    Months.put(month, Lists.newLinkedList());
                    day = day - days;
                    days = this.monthDays(month);
                }
                final boolean isNextMonth = (month + 1) > 12;
                final int nextMonth = isNextMonth ? 1 : month + 1;
                final int startDay = day;
                final int endDay = day + 6;
                final boolean startDayBigDays = startDay > days;
                final boolean endDayBigDays = endDay > days;
                final int startNextYear;
                final int startNextMonth;
                final int startNextDay;
                final int endNextYear;
                final int endNextMonth;
                final int endNextDay;
                if (startDayBigDays) {
                    startNextYear = isNextMonth ? year + 1 : year;
                    startNextMonth = nextMonth;
                    startNextDay = startDay - days;
                } else {
                    startNextYear = year;
                    startNextMonth = month;
                    startNextDay = startDay;
                }
                if (endDayBigDays) {
                    endNextYear = isNextMonth ? year + 1 : year;
                    endNextMonth = nextMonth;
                    endNextDay = endDay - days;
                } else {
                    endNextYear = year;
                    endNextMonth = month;
                    endNextDay = endDay;
                }
                Months.get(month).add(new Week(
                        Months.get(month).size() + 1, LocalDate.of(startNextYear, startNextMonth, startNextDay),
                        LocalDate.of(endNextYear, endNextMonth, endNextDay)
                ));
                day += 7;
            }
            return this;
        }

        public Map<Integer, List<Week.WeekChinese>> toChinese() {
            Map<Integer, List<Week.WeekChinese>> result = Maps.newLinkedHashMap();
            Months.forEach((k, weeks) -> {
                result.put(k, Lists.newLinkedList());
                weeks.forEach((i) -> {
                    result.get(k).add(i.toChinese());
                });
            });
            return result;
        }

        /**
         * 尽量使用不可变对象，防止参数逃逸
         */
        public static class Week {
            private final Integer weekMonth;
            private final LocalDate startDate;
            private final LocalDate endDate;

            public Week(Integer weekMonth, LocalDate startDate, LocalDate endDate) {
                this.weekMonth = weekMonth;
                this.startDate = startDate;
                this.endDate = endDate;
            }

            public WeekChinese toChinese() {
                return new WeekChinese(weekMonth, startDate, endDate);
            }

            public static class WeekChinese extends Week {
                private final String weekChinese;

                public WeekChinese(Integer weekMonth, LocalDate startDate, LocalDate endDate) {
                    super(weekMonth, startDate, endDate);
                    switch (weekMonth) {
                        case 1:
                            weekChinese = "第一周";
                            break;
                        case 2:
                            weekChinese = "第二周";
                            break;
                        case 3:
                            weekChinese = "第三周";
                            break;
                        case 4:
                            weekChinese = "第四周";
                            break;
                        case 5:
                            weekChinese = "第五周";
                            break;
                        case 6:
                            weekChinese = "第六周";
                            break;
                        case 7:
                            weekChinese = "第七周";
                            break;
                        case 8:
                            weekChinese = "第八周";
                            break;
                        case 9:
                            weekChinese = "第九周";
                            break;
                        case 10:
                            weekChinese = "第十周";
                            break;
                        default:
                            weekChinese = "";
                            break;
                    }
                }
            }

        }

    }
}
