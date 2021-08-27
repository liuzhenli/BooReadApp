package com.liuzhenli.common.utils.filepicker.util;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 日期时间工具类
 *
 * @author 李玉江[QQ:1023694760]
 * @since 2015/8/5
 */
public class DateUtils extends android.text.format.DateUtils {
    private static final SimpleDateFormat sd = new SimpleDateFormat("yyyy/MM/dd HH:mm");
    public static final int Second = 0;
    public static final int Minute = 1;
    public static final int Hour = 2;
    public static final int Day = 3;

    @IntDef(value = {Second, Minute, Hour, Day})
    @Retention(RetentionPolicy.SOURCE)
    public @interface DifferenceMode {
    }
    public static String format(Date date){
        return sd.format(date);
    }

    public static long calculateDifferentSecond(Date startDate, Date endDate) {
        return calculateDifference(startDate, endDate, Second);
    }

    public static long calculateDifferentMinute(Date startDate, Date endDate) {
        return calculateDifference(startDate, endDate, Minute);
    }

    public static long calculateDifferentHour(Date startDate, Date endDate) {
        return calculateDifference(startDate, endDate, Hour);
    }

    public static long calculateDifferentDay(Date startDate, Date endDate) {
        return calculateDifference(startDate, endDate, Day);
    }

    public static long calculateDifferentSecond(long startTimeMillis, long endTimeMillis) {
        return calculateDifference(startTimeMillis, endTimeMillis, Second);
    }

    public static long calculateDifferentMinute(long startTimeMillis, long endTimeMillis) {
        return calculateDifference(startTimeMillis, endTimeMillis, Minute);
    }

    public static long calculateDifferentHour(long startTimeMillis, long endTimeMillis) {
        return calculateDifference(startTimeMillis, endTimeMillis, Hour);
    }

    public static long calculateDifferentDay(long startTimeMillis, long endTimeMillis) {
        return calculateDifference(startTimeMillis, endTimeMillis, Day);
    }

    /**
     * 计算两个时间戳之间相差的时间戳数
     */
    public static long calculateDifference(long startTimeMillis, long endTimeMillis, @DifferenceMode int mode) {
        return calculateDifference(new Date(startTimeMillis), new Date(endTimeMillis), mode);
    }

    /**
     * 计算两个日期之间相差的时间戳数
     */
    public static long calculateDifference(Date startDate, Date endDate, @DifferenceMode int mode) {
        long[] different = calculateDifference(startDate, endDate);
        if (mode == Minute) {
            return different[2];
        } else if (mode == Hour) {
            return different[1];
        } else if (mode == Day) {
            return different[0];
        } else {
            return different[3];
        }
    }

    private static long[] calculateDifference(Date startDate, Date endDate) {
        return calculateDifference(endDate.getTime() - startDate.getTime());
    }

    private static long[] calculateDifference(long differentMilliSeconds) {
        long secondsInMilli = 1000;//1s==1000ms
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;
        long elapsedDays = differentMilliSeconds / daysInMilli;
        differentMilliSeconds = differentMilliSeconds % daysInMilli;
        long elapsedHours = differentMilliSeconds / hoursInMilli;
        differentMilliSeconds = differentMilliSeconds % hoursInMilli;
        long elapsedMinutes = differentMilliSeconds / minutesInMilli;
        differentMilliSeconds = differentMilliSeconds % minutesInMilli;
        long elapsedSeconds = differentMilliSeconds / secondsInMilli;
        return new long[]{elapsedDays, elapsedHours, elapsedMinutes, elapsedSeconds};
    }

    /**
     * 计算每月的天数
     */
    public static int calculateDaysInMonth(int month) {
        return calculateDaysInMonth(0, month);
    }

    /**
     * 根据年份及月份计算每月的天数
     */
    public static int calculateDaysInMonth(int year, int month) {
        // 添加大小月月份并将其转换为list,方便之后的判断
        String[] bigMonths = {"1", "3", "5", "7", "8", "10", "12"};
        String[] littleMonths = {"4", "6", "9", "11"};
        List<String> bigList = Arrays.asList(bigMonths);
        List<String> littleList = Arrays.asList(littleMonths);
        // 判断大小月及是否闰年,用来确定"日"的数据
        if (bigList.contains(String.valueOf(month))) {
            return 31;
        } else if (littleList.contains(String.valueOf(month))) {
            return 30;
        } else {
            if (year <= 0) {
                return 29;
            }
            // 是否闰年
            if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) {
                return 29;
            } else {
                return 28;
            }
        }
    }

    /**
     * 月日时分秒，0-9前补0
     */
    @NonNull
    public static String fillZero(int number) {
        return number < 10 ? "0" + number : "" + number;
    }

    /**
     * 截取掉前缀0以便转换为整数
     *
     * @see #fillZero(int)
     */
    public static int trimZero(@NonNull String text) {
        try {
            if (text.startsWith("0")) {
                text = text.substring(1);
            }
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * 功能：判断日期是否和当前date对象在同一天。
     * 参见：http://www.cnblogs.com/myzhijie/p/3330970.html
     *
     * @param date 比较的日期
     * @return boolean 如果在返回true，否则返回false。
     */
    public static boolean isSameDay(Date date) {
        if (date == null) {
            throw new IllegalArgumentException("date is null");
        }
        Calendar nowCalendar = Calendar.getInstance();
        Calendar newCalendar = Calendar.getInstance();
        newCalendar.setTime(date);
        return (nowCalendar.get(Calendar.ERA) == newCalendar.get(Calendar.ERA) &&
                nowCalendar.get(Calendar.YEAR) == newCalendar.get(Calendar.YEAR) &&
                nowCalendar.get(Calendar.DAY_OF_YEAR) == newCalendar.get(Calendar.DAY_OF_YEAR));
    }

    /**
     * 将yyyy-MM-dd HH:mm:ss字符串转换成日期<br/>
     *
     * @param dateStr    时间字符串
     * @param dataFormat 当前时间字符串的格式。
     * @return Date 日期 ,转换异常时返回null。
     */
    public static Date parseDate(String dateStr, String dataFormat) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(dataFormat, Locale.PRC);
            Date date = dateFormat.parse(dateStr);
            return new Date(date.getTime());
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 将yyyy-MM-dd HH:mm:ss字符串转换成日期<br/>
     *
     * @param dateStr yyyy-MM-dd HH:mm:ss字符串
     * @return Date 日期 ,转换异常时返回null。
     */
    public static Date parseDate(String dateStr) {
        return parseDate(dateStr, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 将指定的日期转换为一定格式的字符串
     */
    public static String formatDate(Date date, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.PRC);
        return sdf.format(date);
    }

    /**
     * 将当前日期转换为一定格式的字符串
     */
    public static String formatDate(String format) {
        return formatDate(Calendar.getInstance(Locale.CHINA).getTime(), format);
    }

    /**
     * 格式化更新时间  单位毫秒
     */
    public static String formatUpdateTime(long updateTime) {
        if (updateTime <= 0) {
            return "";
        }
        updateTime /= 1000;
        Calendar cal = Calendar.getInstance();
        // 手机当前时间
        int currentYear = cal.get(Calendar.YEAR);
        int year = 0;
        try {
            year = Integer.parseInt(formatDateInSecond(updateTime).substring(0, 4));
        } catch (Exception e) {
            year = currentYear;
        }
        // 手机时间大于服务器时间 显示服务器时间
        if (currentYear > year) {
            return formatDateInSecond(updateTime);
        }
        long currentTime = System.currentTimeMillis() / 1000;
        long time = currentTime - updateTime;
        if (time < 0) {
            return formatDateInmm(updateTime, true);
        } else if (time < 60) {
            return "刚刚";
        } else if (time < 3600) {
            return time / 60 + "分钟前";
        } else if (time < 3600 * 24) {
            return time / 3600 + "小时前";
        } else if (time < 3600 * 24 * 30) {
            return time / (3600 * 24) + "天前";
        } else if (time < 3600 * 24 * 30 * 11) {
            return formatDateInmmNoYear(updateTime, true);
        } else {
            return formatDateInmm(updateTime, true);
        }

        // 时间与评论显示一样
        // ·1分钟内——“刚刚”
        // ·1分钟<*<1小时——“n分钟前”
        // ·1小时<*<24小时——“n小时前”
        // ·1天<*<30天——“n天前”
        // ·30天<*<11月——“n月n日 10:05”（这是例子）
        // ·超过11月——“n年n月n日 12:25”（这是例子）
    }

    public static String formatDateInSecond(long time) {
        String strDate = "";
        try {
            time *= 1000;
            if (time <= 0) {
                time = System.currentTimeMillis();
            }
            Date date = new Date();
            date.setTime(time);

            strDate = sd.format(time);
        } catch (Exception e) {

            e.printStackTrace();
        }
        return strDate;
    }

    public static String formatDateInmmNoYear(long time, boolean ishavtime) {
        SimpleDateFormat sdf = new SimpleDateFormat(ishavtime ? "MM月dd日 HH:mm" : "MM月dd日");
        String strDate = "";
        try {
            time *= 1000;
            if (time <= 0) {
                time = System.currentTimeMillis();
            }
            Date date = new Date();
            date.setTime(time);
            strDate = sdf.format(time);
        } catch (Exception e) {

            e.printStackTrace();
        }
        return strDate;
    }

    public static String formatDateInmm(long time, boolean ishavtime) {

        SimpleDateFormat sdf = new SimpleDateFormat(ishavtime ? "yyyy年MM月dd日 HH:mm" : "yyyy年MM月dd日");
        String strDate = "";
        try {
            time *= 1000;
            if (time <= 0) {
                time = System.currentTimeMillis();
            }
            Date date = new Date();
            date.setTime(time);
            strDate = sdf.format(time);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strDate;
    }

}
