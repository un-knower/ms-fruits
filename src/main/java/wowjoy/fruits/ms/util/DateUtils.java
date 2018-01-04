package wowjoy.fruits.ms.util;

import com.google.common.collect.Lists;
import wowjoy.fruits.ms.exception.CheckException;
import wowjoy.fruits.ms.module.InterfaceEntity;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 提供年月日计算工具
 */
public class DateUtils {

    private static final ConcurrentMap<Integer, List<Month<Week.WeekChinese>>> yearMap = new ConcurrentMapCustom(new ConcurrentHashMap());

    public static String dayOfWeekChinese(int dayOfWeek){
        return Week.WeekChinese.WeekOneDay(dayOfWeek);
    }

    public static List<Month<Week.WeekChinese>> getWeekByYear(Integer year) {
        if (yearMap.containsKey(year))
            return yearMap.get(year);
        yearMap.put(year, DateUtils.newInstance(year).weeks().toChinese());
        return yearMap.get(year);
    }

    public static List<Month<Week>> getMonthBetween(Integer year) {
        return DateUtils.newInstance(year).months().Months;
    }

    public static List<Month<Week>> getMonthBetween(String year) {
        checkYear(year);
        return getMonthBetween(Integer.valueOf(year));
    }

    public static List<Month<Week.WeekChinese>> getWeekByYear(String year) {
        checkYear(year);
        return getWeekByYear(Integer.valueOf(year));
    }

    public static Month<Week.WeekChinese> getMonthByYearMonth(Integer year, Integer month) {
        List<Month<Week.WeekChinese>> weekByYear = getWeekByYear(year);
        for (Month<Week.WeekChinese> m : weekByYear) {
            if (m.getMonth().equals(month))
                return m;

        }
        return Month.getEmpty();
    }

    private static void checkYear(String year) {
        try {
            Integer.valueOf(year);
        } catch (Exception ex) {
            throw new CheckException("年份必须是整数");
        }
    }

    private static class ConcurrentMapCustom<K, V> extends ConcurrentHashMap<K, V> {
        private final ConcurrentMap<K, V> map;

        private ConcurrentMapCustom(ConcurrentMap<K, V> map) {
            this.map = map;
        }

        @Override
        public V put(K key, V value) {
            cacheClear();
            return map.put(key, value);
        }

        @Override
        public V get(Object key) {
            cacheClear();
            return map.get(key);
        }

        /*清空缓存*/
        public void cacheClear() {
            if (map.size() >= 10)
                map.clear();
        }
    }


    private final Integer Year;
    private final List<DateUtils.Month<DateUtils.Week>> Months = Lists.newLinkedList();


    private DateUtils(Integer year) {
        Year = year;
    }

    public static DateUtils newInstance(Integer year) {
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

    public DateUtils months() {
        for (int monthNum = 1; monthNum <= 12; monthNum++) {
            Months.add(new Month<>(monthNum, null));
            Months.get(monthNum - 1).setStartDate(LocalDate.of(this.Year, monthNum, 1));
            Months.get(monthNum - 1).setEndDate(LocalDate.of(this.Year, monthNum, this.monthDays(monthNum)));
        }
        return this;
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
        int day = 0;
        day += (firstDay >= 2 ? (7 - (firstDay - 1)) : 0) + 1;
        int days = this.monthDays(month);
        Months.add(DateUtils.Month.newInstance(month, Lists.newLinkedList()));
        getMonths(month).setStartDate(LocalDate.of(year, month, day));
        while (true) {
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
//                Months.get(Objects.toString(month)).add(new Week(
//                        Months.get(Objects.toString(month)).size() + 1, LocalDate.of(startNextYear, startNextMonth, startNextDay),
//                        LocalDate.of(endNextYear, endNextMonth, endNextDay)
//                ));
            for (DateUtils.Month m : Months) {
                if (m.getMonth() != month) continue;
                m.getWeeks().add(DateUtils.Week.newInstance(m.getWeeks().size() + 1, LocalDate.of(startNextYear, startNextMonth, startNextDay),
                        LocalDate.of(endNextYear, endNextMonth, endNextDay)));
            }
            day += 7;

            if (checkLastWeek(days, day)) {
                getMonths(month).setEndDate(LocalDate.of(endNextYear, endNextMonth, endNextDay));
                month++;
                if (month > 12) break;
                day = day - days;
                days = this.monthDays(month);
                Months.add(DateUtils.Month.newInstance(month, Lists.newLinkedList()));
                getMonths(month).setStartDate(LocalDate.of(endNextYear, month, day));
            }
        }
        return this;
    }

    private boolean checkLastWeek(Integer days, Integer day) {
        return day > days && Math.ceil(days / 7) <= Math.floor(day / 7);
    }

    private DateUtils.Month getMonths(Integer month) {
        for (DateUtils.Month m : Months) {
            if (m.getMonth() == month)
                return m;
        }
        return DateUtils.Month.getEmpty();
    }

    public List<DateUtils.Month<DateUtils.Week.WeekChinese>> toChinese() {
        List<DateUtils.Month<DateUtils.Week.WeekChinese>> result = Lists.newLinkedList();
        Months.forEach((m) -> {
            DateUtils.Month month = DateUtils.Month.newInstance(m.getMonth(), Lists.newLinkedList());
            month.setStartDate(m.getStartDate());
            month.setEndDate(m.getEndDate());
            m.getWeeks().forEach((i) -> month.getWeeks().add(i.toChinese()));
            result.add(month);
        });
        return result;
    }

    private enum Key {
        scope("周期范围Key");
        private String value;

        Key(String value) {
            this.value = value;
        }
    }

    /**
     * 尽量使用不可变对象，防止参数逃逸
     */
    public static class Month<T extends DateUtils.Week> implements InterfaceEntity {
        private final Integer month;
        private final List<T> weeks;
        private LocalDate startDate;
        private LocalDate endDate;

        public LocalDate getStartDate() {
            return startDate;
        }

        public void setStartDate(LocalDate startDate) {
            this.startDate = startDate;
        }

        public LocalDate getEndDate() {
            return endDate;
        }

        public void setEndDate(LocalDate endDate) {
            this.endDate = endDate;
        }

        public Integer getMonth() {
            return month;
        }

        public List<T> getWeeks() {
            return weeks;
        }

        public Month(Integer month, List<T> weeks) {
            this.month = month;
            this.weeks = weeks;
        }

        public static DateUtils.Month newInstance(Integer month, List<? extends DateUtils.Week> weeks) {
            return new DateUtils.Month(month, weeks);
        }

        public static DateUtils.Month.MonthEmpty getEmpty() {
            return new DateUtils.Month.MonthEmpty(null, null);
        }

        public static class MonthEmpty extends DateUtils.Month {

            public MonthEmpty(Integer month, List weeks) {
                super(month, weeks);
            }

            @Override
            public boolean isNotEmpty() {
                return false;
            }
        }
    }

    public static class Week {
        private final Integer weekMonth;
        private final LocalDate startDate;
        private final LocalDate endDate;

        public Integer getWeekMonth() {
            return weekMonth;
        }

        public LocalDate getStartDate() {
            return startDate;
        }

        public LocalDate getEndDate() {
            return endDate;
        }

        public Week(Integer weekMonth, LocalDate startDate, LocalDate endDate) {
            this.weekMonth = weekMonth;
            this.startDate = startDate;
            this.endDate = endDate;
        }

        public static DateUtils.Week newInstance(Integer weekMonth, LocalDate startDate, LocalDate endDate) {
            return new DateUtils.Week(weekMonth, startDate, endDate);
        }

        public DateUtils.Week.WeekChinese toChinese() {
            return new DateUtils.Week.WeekChinese(weekMonth, startDate, endDate);
        }

        public static class WeekChinese extends DateUtils.Week {
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

            public static String WeekOneDay(Integer dayOfWeek) {
                switch (dayOfWeek) {
                    case 1:
                        return "周一";
                    case 2:
                        return "周二";
                    case 3:
                        return "周三";
                    case 4:
                        return "周四";
                    case 5:
                        return "周五";
                    case 6:
                        return "周六";
                    case 7:
                        return "周日";
                    default:
                        return "";
                }
            }

        }

    }

}
