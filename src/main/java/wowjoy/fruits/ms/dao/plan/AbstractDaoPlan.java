package wowjoy.fruits.ms.dao.plan;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import wowjoy.fruits.ms.dao.InterfaceDao;
import wowjoy.fruits.ms.exception.CheckException;
import wowjoy.fruits.ms.module.plan.FruitPlan;
import wowjoy.fruits.ms.module.plan.FruitPlanDao;
import wowjoy.fruits.ms.module.plan.FruitPlanVo;
import wowjoy.fruits.ms.module.util.entity.FruitDict;

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

    /*******************************
     * PUBLIC 函数，公共接口         *
     * 尽量保证规范，不直接调用dao接口 *
     *******************************/

    /**
     * 月计划
     *
     * @param vo
     * @return
     */
    public List<FruitPlanDao> findRelationMonth(FruitPlanVo vo) {
        final FruitPlanDao dao = FruitPlan.getFruitPlanDao();
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
    public List<FruitPlanDao> findRelationWeek(FruitPlanVo vo) {
        FruitPlanDao dao = FruitPlan.getFruitPlanDao();
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
    public List<FruitPlanDao> findMonth(FruitPlanVo vo) {
        final FruitPlanDao dao = FruitPlan.getFruitPlanDao();
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
    public List<FruitPlanDao> findWeek(FruitPlanVo vo) {
        FruitPlanDao dao = FruitPlan.getFruitPlanDao();
        dao.setTitle(vo.getTitle());
        dao.setParentId(vo.getParentId());
        dao.setPlanStatus(vo.getPlanStatus());
        dao.setStartDateDao(vo.getStartDateVo());
        dao.setEndDateDao(vo.getEndDateVo());
        return this.findWeek(dao, vo.getPageNum(), vo.getPageSize());
    }

    public FruitPlan findByUUID(FruitPlanVo vo) {
        return this.findByUUID(vo.getUuidVo());
    }

    public void insert(FruitPlanVo vo) {
        FruitPlanDao dao = FruitPlan.getFruitPlanDao();
        dao.setUuid(vo.getUuid());
        dao.setPlanStatus(FruitDict.PlanDict.PENDING.name());
        dao.setEndDate(vo.getEndDate());
        dao.setTitle(vo.getTitle());
        dao.setParentId(vo.getParentId());
        dao.setDescription(vo.getDescription());
        dao.setUserRelation(vo.getUserRelation());
        this.insert(dao);
    }

    public void update(FruitPlanVo vo) {
        if (!this.findByUUID(vo.getUuidVo()).isNotEmpty()) throw new CheckPlanException("计划不存在，修改失败");
        FruitPlanDao dao = FruitPlan.getFruitPlanDao();
        dao.setUuid(vo.getUuidVo());
        dao.setTitle(vo.getTitle());
        dao.setPlanStatus(vo.getPlanStatus());
        dao.setUserRelation(vo.getUserRelation());
        dao.setDescription(vo.getDescription());
        dao.setEndDate(vo.getEndDate());
        dao.setPercent(vo.getPercent());
        this.update(dao);
    }

    /**
     * 提供一年相对星期数
     *
     * @param year
     * @return
     */
    public Map<Integer, List<Week>> yearEachWeek(int year) {
        return DateUtils.getInstance(year).weeks();
    }

    /****************
     * 计划自定义异常 *
     ****************/
    public class CheckPlanException extends CheckException {
        public CheckPlanException(String message) {
            super(message);
        }
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
        public Map<Integer, List<Week>> weeks() {
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
                        LocalDate.of(startNextYear, startNextMonth, startNextDay),
                        LocalDate.of(endNextYear, endNextMonth, endNextDay)
                ));
                day += 7;
            }
            return Months;
        }
    }

    /**
     * 尽量使用不可变对象，防止参数逃逸
     */
    public static class Week {
        private final LocalDate startDate;
        private final LocalDate endDate;

        public Week(LocalDate startDate, LocalDate endDate) {
            this.startDate = startDate;
            this.endDate = endDate;
        }
    }
}
