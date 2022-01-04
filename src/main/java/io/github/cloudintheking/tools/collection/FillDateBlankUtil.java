package io.github.cloudintheking.tools.collection;

import io.github.cloudintheking.tools.date.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotNull;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.stream.Collectors;

//填充空白日期
public class FillDateBlankUtil {

    private final Logger log = LoggerFactory.getLogger(FillDateBlankUtil.class);

    //填充基类
    public static abstract class FillBlankBase<T> {
        private int year;
        private int month;
        private int day;
        private int hour;
        private int minute;

        public int getYear() {
            return year;
        }

        public void setYear(int year) {
            this.year = year;
        }

        public int getMonth() {
            return month;
        }

        public void setMonth(int month) {
            this.month = month;
        }

        public int getDay() {
            return day;
        }

        public void setDay(int day) {
            this.day = day;
        }

        public int getHour() {
            return hour;
        }

        public void setHour(int hour) {
            this.hour = hour;
        }

        public int getMinute() {
            return minute;
        }

        public void setMinute(int minute) {
            this.minute = minute;
        }

        public final String getDateStr() {
            return String.format("%04d-%02d-%02d %02d:%02d", this.year, this.month, this.day, this.hour, this.minute);
        }

        public final Class<T> getActualType() {
            ParameterizedType parameterizedType = (ParameterizedType) this.getClass().getGenericSuperclass();
            return (Class<T>) parameterizedType.getActualTypeArguments()[0];
        }
    }

    public enum FillBlankTypeEnum {
        HOUR(DateUtil.PATTERN_DEFAULT_ON_HOUR, Calendar.HOUR_OF_DAY),   //按时填充
        DAY(DateUtil.PATTERN_DEFAULT_ON_DAY, Calendar.DAY_OF_MONTH),     //按天填充
        MONTH(DateUtil.PATTERN_DEFAULT_ON_MONTH, Calendar.MONTH),       //按月填充
        YEAR(DateUtil.PATTERN_DEFAULT_ON_YEAR, Calendar.YEAR);          //按年填充


        private String format;
        private Integer calendarRule;


        FillBlankTypeEnum(String format, Integer calendarRule) {
            this.format = format;
            this.calendarRule = calendarRule;
        }

        private String getFormat() {
            return format;
        }

        public void setFormat(String format) {
            this.format = format;
        }

        public Integer getCalendarRule() {
            return calendarRule;
        }

        public void setCalendarRule(Integer calendarRule) {
            this.calendarRule = calendarRule;
        }
    }

    @FunctionalInterface
    public interface FillBlankGenFunc {
        void fill(FillBlankBase fillBlankBase);
    }

    /**
     * 空白填充
     *
     * @param fillBlankOrigin  待填充原始列表
     * @param fillBlankType    填充类型
     * @param startDate        填充时间起点
     * @param endDate          填充时间终点
     * @param fillBlankGenFunc 填充生成函数
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T extends FillBlankBase> List fillBlank(List<T> fillBlankOrigin, @NotNull FillBlankTypeEnum fillBlankType, @NotNull Date startDate, @NotNull Date endDate, @NotNull FillBlankGenFunc fillBlankGenFunc) throws IllegalAccessException, InstantiationException {
        if (fillBlankOrigin == null || fillBlankOrigin.size() == 0) {
            throw new IllegalArgumentException("fillBlankOrigin is empty!");
        }
        if (fillBlankType == null) {
            throw new IllegalArgumentException("fillBlankType is null!");
        }
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("startDate or endDate is null!");
        }
        if (startDate.after(endDate)) {
            throw new IllegalArgumentException("startDate should be before endDate!");
        }
        if (fillBlankGenFunc == null) {
            throw new IllegalArgumentException("fillBlankGenFunc is null!");
        }

        Class elementClass = fillBlankOrigin.get(0).getActualType();
        //根据填充类型、填充起始时间获取完整时间点列表
        List<Date> dates = findDates(startDate, endDate, fillBlankType);
        //遍历完整时间列表
        for (Date date : dates) {
            String dateStr = DateUtil.format(date, fillBlankType.getFormat());
            //  遍历待填充原始列表
            if (!fillBlankOrigin.stream().anyMatch(fillBlankBase -> {
                Calendar calendar = Calendar.getInstance();
                calendar.set(fillBlankBase.getYear(), fillBlankBase.getMonth() - 1, fillBlankBase.getDay(), fillBlankBase.getHour(), fillBlankBase.getMinute());
                Date originDate = calendar.getTime();
                return dateStr.equals(DateUtil.format(originDate, fillBlankType.getFormat()));
            })) {
                // 若不存在完整时间列表中,则调用填充生成函数生成填充数据,插入到待填充原始列表
                FillBlankBase fillBlankBase = (FillBlankBase) elementClass.newInstance();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                fillBlankBase.setYear(calendar.get(Calendar.YEAR));
                fillBlankBase.setMonth(calendar.get(Calendar.MONTH) + 1);
                fillBlankBase.setDay(calendar.get(Calendar.DAY_OF_MONTH));
                fillBlankBase.setHour(calendar.get(Calendar.HOUR_OF_DAY));
                //调用填充生成方法
                fillBlankGenFunc.fill(fillBlankBase);
                fillBlankOrigin.add((T) fillBlankBase);
            }
        }
        //重新排序
        fillBlankOrigin = fillBlankOrigin.stream()
                .sorted(Comparator.comparing(FillBlankBase::getDateStr))
                .collect(Collectors.toList());
        return fillBlankOrigin;
    }


    /**
     * 获取时间段内时间点列表
     *
     * @param startDate
     * @param endDate
     * @param fillBlankType
     * @return
     */
    public static List<Date> findDates(@NotNull Date startDate, @NotNull Date endDate, @NotNull FillBlankTypeEnum fillBlankType) {
        List<Date> lDate = new ArrayList<Date>();
        lDate.add(startDate);
        Calendar calBegin = Calendar.getInstance();
        // 使用给定的 Date 设置此 Calendar 的时间
        calBegin.setTime(startDate);
        Calendar calEnd = Calendar.getInstance();
        // 使用给定的 Date 设置此 Calendar 的时间
        calEnd.setTime(endDate);
        // 测试此日期是否在指定日期之后
        while (endDate.after(calBegin.getTime())) {
            // 根据日历的规则，为给定的日历字段添加或减去指定的时间量
            calBegin.add(fillBlankType.getCalendarRule(), 1);
            lDate.add(calBegin.getTime());
        }
        return lDate;
    }
}
