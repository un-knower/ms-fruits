import com.google.common.collect.Queues;
import org.apache.commons.lang.math.RandomUtils;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;
import java.util.Queue;
import java.util.TimeZone;

/**
 * Created by wangziwen on 2017/9/15.
 */
public class LocalDateTime {
    @Test
    public void localDate() throws Exception {
        final LocalDate of = LocalDate.of(2017, 1, 1);
//        System.out.println(of.toEpochDay());
    }

    /**
     * 计算平年、闰年
     * 规则：
     * 能被100整除的是世纪年，又能被400整除的是世纪闰年。
     * 不能被100整除的是普通年，能被4整除的普通年是闰年
     *
     * @throws Exception
     */
    public int calendar(Integer year) {
        if ((year % 100 == 0 && year % 400 == 0) || (year % 100 != 0 && year % 4 == 0)) {
            return 366;
        } else {
            return 365;
        }
    }

    /**
     * 计算闰年数
     * 世纪数前两位除4向下取整
     *
     * @param year
     * @return
     */
    public int leapYearNumber(Integer year) {
        final String result = String.valueOf(year).substring(String.valueOf(year).length() - 2, String.valueOf(year).length());
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
    public int centoryWeekNumber(Integer year) {
        final Integer centoryNummber = Integer.valueOf(String.valueOf(year).substring(0, 2));
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
     * @param year
     * @return
     */
    public int yearWeekNumber(Integer year) {
        final Integer yearNumber = Integer.valueOf(String.valueOf(year).substring(String.valueOf(year).length() - 2, String.valueOf(year).length()));
        final int leapYearNumber = leapYearNumber(year);
        final int centoryWeekNumber = centoryWeekNumber(year);
        final int number = (centoryWeekNumber + yearNumber + leapYearNumber) % 7;
        return calendar(year) == 365 ? number + 1 : number;
    }

    @Test
    public void yearWeekNumber() {
        Integer year = 2006;
        System.out.println(31 / 7);
        System.out.println(37 / 7);
    }

    @Test
    public void test() throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("''yyyy-MM-dd''", Locale.US);
        java.sql.Date date = new java.sql.Date(1508083200000L);
        dateFormat.setTimeZone(TimeZone.getDefault());
        String format = dateFormat.format(date);
        System.out.println(format);
    }
}
