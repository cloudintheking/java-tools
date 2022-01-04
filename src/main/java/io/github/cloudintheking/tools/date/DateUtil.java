package io.github.cloudintheking.tools.date;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.FastDateFormat;

import javax.validation.constraints.NotNull;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

    public static final String SYMBOL_DOT = "\\.";

    public static final String DATE_REGEX_YYYYMM = "^\\d{4}-\\d{1,2}$";//日期正则yyyy-MM
    public static final String DATE_REGEX_YYYYMMDD = "^\\d{4}-\\d{1,2}-\\d{1,2}$";//日期正则yyyy-MM-dd
    public static final String DATE_REGEX_YYYYMMDDHHMM = "^\\d{4}-\\d{1,2}-\\d{1,2} {1}\\d{1,2}:\\d{1,2}$";//日期正则yyyy-MM-dd hh:mm
    public static final String DATE_REGEX_YYYYMMDDHHMMSS = "^\\d{4}-\\d{1,2}-\\d{1,2} {1}\\d{1,2}:\\d{1,2}:\\d{1,2}$";//日期正则yyyy-MM-dd hh:mm:ss
    public static final String DATE_REGEX_SECOND_DOT_NANOSECOND = "^[0-9]{1,}\\.[0-9]{1,9}$";//Instant日期秒+纳秒
    public static final String DATE_REGEX_YYYYMMDD_T_HHMMSS_Z = "^\\d{4}-\\d{1,2}-\\d{1,2}T\\d{1,2}:\\d{1,2}:\\d{1,2}Z$";//日期正则yyyy-MM-dd'T'HH:mm:ssZ
    public static final String DATE_REGEX_YYYYMMDD_T_HHMMSS_SSS_Z = "^\\d{4}-\\d{1,2}-\\d{1,2}T\\d{1,2}:\\d{1,2}:\\d{1,2}\\.\\d{1,3}Z$";//日期正则yyyy-MM-dd'T'HH:mm:ss.SSSZ


    // 以T分隔日期和时间，并带时区信息，符合ISO8601规范
    public static final String PATTERN_ISO = "yyyy-MM-dd'T'HH:mm:ss.SSSZZ";
    public static final String PATTERN_ISO_ON_SECOND = "yyyy-MM-dd'T'HH:mm:ssZZ";
    public static final String PATTERN_ISO_ON_DATE = "yyyy-MM-dd";
    public static final String PATTERN_ISO_ON_MONTH = "yyyy-MM";

    // 以空格分隔日期和时间，不带时区信息
    public static final String PATTERN_DEFAULT = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String PATTERN_DEFAULT_ON_SECOND = "yyyy-MM-dd HH:mm:ss";
    public static final String PATTERN_DEFAULT_ON_MINUTE = "yyyy-MM-dd HH:mm";
    public static final String PATTERN_DEFAULT_ON_HOUR = "yyyy-MM-dd HH";
    public static final String PATTERN_DEFAULT_ON_DAY = "yyyy-MM-dd";
    public static final String PATTERN_DEFAULT_ON_MONTH = "yyyy-MM";
    public static final String PATTERN_DEFAULT_ON_YEAR = "yyyy";

    // 使用工厂方法FastDateFormat.getInstance(), 从缓存中获取实例

    // 以T分隔日期和时间，并带时区信息，符合ISO8601规范
    public static final FastDateFormat ISO_FORMAT = FastDateFormat.getInstance(PATTERN_ISO);  //FastDateFormat.getInstance(PATTERN_ISO, TimeZone.getDefault());
    public static final FastDateFormat ISO_ON_SECOND_FORMAT = FastDateFormat.getInstance(PATTERN_ISO_ON_SECOND);
    public static final FastDateFormat ISO_ON_DATE_FORMAT = FastDateFormat.getInstance(PATTERN_ISO_ON_DATE);
    public static final FastDateFormat ISO_ON_MONTH_FORMAT = FastDateFormat.getInstance(PATTERN_ISO_ON_MONTH);

    // 以空格分隔日期和时间，不带时区信息
    public static final FastDateFormat DEFAULT_FORMAT = FastDateFormat.getInstance(PATTERN_DEFAULT);
    public static final FastDateFormat DEFAULT_ON_SECOND_FORMAT = FastDateFormat.getInstance(PATTERN_DEFAULT_ON_SECOND);
    public static final FastDateFormat DEFAULT_ON_MINUTE_FORMAT = FastDateFormat.getInstance(PATTERN_DEFAULT_ON_MINUTE);


    public static String format(@NotNull Date date, @NotNull String pattern) {
        return DateFormatUtils.format(date, pattern);
    }

    /**
     * 将日期格式的字符串转换成指定格式的日期
     *
     * @param pattern    日期格式
     * @param dateString 日期字符串
     * @return
     * @throws ParseException
     */
    public static Date pareDate(@NotNull String dateString, @NotNull String pattern) throws ParseException {
        return FastDateFormat.getInstance(pattern).parse(dateString);
    }

    /**
     * 将日期格式的字符串根据正则转换成相应格式的日期
     *
     * @param dateString 日期字符串
     * @return
     * @throws ParseException
     */
    public static Date pareDate(@NotNull String dateString) throws ParseException {
        String source = dateString.trim();
        if (StringUtils.isNotBlank(source)) {
            if (source.matches(DATE_REGEX_YYYYMM)) {
                return ISO_ON_MONTH_FORMAT.parse(source);
            } else if (source.matches(DATE_REGEX_YYYYMMDD)) {
                return ISO_ON_DATE_FORMAT.parse(source);
            } else if (source.matches(DATE_REGEX_YYYYMMDDHHMM)) {
                return DEFAULT_ON_MINUTE_FORMAT.parse(source);
            } else if (source.matches(DATE_REGEX_YYYYMMDDHHMMSS)) {
                return DEFAULT_ON_SECOND_FORMAT.parse(source);
            } else if (source.matches(DATE_REGEX_YYYYMMDD_T_HHMMSS_Z)) {
                return ISO_ON_SECOND_FORMAT.parse(source);
            } else if (source.matches(DATE_REGEX_YYYYMMDD_T_HHMMSS_SSS_Z)) {
                return ISO_FORMAT.parse(source);
            } else if (source.matches(DATE_REGEX_SECOND_DOT_NANOSECOND)) {
                String[] split = source.split(SYMBOL_DOT);
                return Date.from(Instant.ofEpochSecond(Long.parseLong(split[0]), Long.parseLong(split[1])));
            } else {
                throw new IllegalArgumentException("Invalid date value '" + source + "'");
            }
        }
        return null;
    }

    /**
     * 一天起始时间
     *
     * @param date
     * @return
     */
    public static Date getStartOfDay(@NotNull Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        setBeginTime(calendar);
        return calendar.getTime();
    }

    /**
     * 一天结束时间
     *
     * @param date
     * @return
     */
    public static Date getEndOfDay(@NotNull Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        setEndTime(calendar);
        return calendar.getTime();
    }

    /**
     * 获取周数
     *
     * @param date
     * @return
     */
    public static int getWeekOfYear(@NotNull Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        //默认每周从周日开始,改为从周一开始
        calendar.setFirstDayOfWeek(Calendar.MONDAY);

        return calendar.get(Calendar.WEEK_OF_YEAR);
    }

    /**
     * 增加/减少时间
     *
     * @param date
     * @param field
     * @param amount
     * @return
     */
    public static Date addDate(@NotNull Date date, @NotNull int field, @NotNull int amount) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(field, amount);
        return calendar.getTime();
    }

    /**
     * 相隔天数
     *
     * @param start
     * @param end
     * @return
     */
    public static long betweenDay(@NotNull Date start, @NotNull Date end) throws ParseException {
        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        Date nstart = sf.parse(sf.format(start));
        Date nend = sf.parse(sf.format(end));
        return (nend.getTime() - nstart.getTime()) / (60 * 60 * 24 * 1000);
    }

    /**
     * 获取传入时间的那一周任意一天时间
     *
     * @param date
     * @param day  周几
     * @return
     */
    public static Date getDayOfWeekBySpecialDay(@NotNull Date date, @NotNull Integer day) {
        if (day < 1 || day > 7) {
            throw new RuntimeException("请传入1到7之间的整数");
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek() + day - 1);
        return calendar.getTime();
    }

    /**
     * 获取传入时间属于周几
     *
     * @param date
     * @return
     */
    public static int getDayOfWeek(@NotNull Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * 比较两个时间是否是同一天
     *
     * @param date1
     * @param date2
     * @return
     */
    public static boolean isSameDay(@NotNull Date date1, @NotNull Date date2) {
        Calendar calDateA = Calendar.getInstance();
        calDateA.setTime(date1);
        Calendar calDateB = Calendar.getInstance();
        calDateB.setTime(date2);
        return calDateA.get(Calendar.YEAR) == calDateB.get(Calendar.YEAR)
                && calDateA.get(Calendar.MONTH) == calDateB.get(Calendar.MONTH)
                && calDateA.get(Calendar.DAY_OF_MONTH) == calDateB
                .get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 获取传入时间所属月份的第一天时间
     *
     * @param date
     * @return
     */
    public static Date getFirstDayOfMonth(@NotNull Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        setBeginTime(calendar);
        return calendar.getTime();
    }

    /**
     * 获取传入时间所属月份的最后一天时间
     *
     * @param date
     * @return
     */
    public static Date getLastDayOfMonth(@NotNull Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int last = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        calendar.set(Calendar.DAY_OF_MONTH, last);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MINUTE, 59);
        return calendar.getTime();
    }

    /**
     * 获取传入时间所属周的第一天开始时间
     *
     * @param date
     * @return
     */
    public static Date getFirstDayOfWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        setBeginTime(calendar);
        return calendar.getTime();
    }


    /**
     * 获取传入时间所属周的最后一天时间
     *
     * @param date
     * @return
     */
    public static Date getLastDayOfWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        setEndTime(calendar);
        return calendar.getTime();
    }

    /**
     * 获取传入时间所属年的第一天开始时间
     *
     * @param date
     * @return
     */
    public static Date getFirstDayOfYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int first = calendar.getActualMinimum(Calendar.DAY_OF_YEAR);
        calendar.set(Calendar.DAY_OF_YEAR, first);
        setBeginTime(calendar);
        return calendar.getTime();
    }


    /**
     * 获取传入时间所属年的最后一天时间
     *
     * @param date
     * @return
     */
    public static Date getLastDayOfYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int last = calendar.getActualMaximum(Calendar.DAY_OF_YEAR);
        calendar.set(Calendar.DAY_OF_YEAR, last);
        setEndTime(calendar);
        return calendar.getTime();
    }

    /**
     * 获取明天零点
     *
     * @return
     */
    public static Date getTomorrow() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.add(Calendar.DATE, 1);
        return calendar.getTime();
    }

    private static void setEndTime(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    private static void setBeginTime(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

    }
}
