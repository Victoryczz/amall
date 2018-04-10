package seu.vczz.amall.util;


import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Date;

/**
 * CREATE by vczz on 2018/4/9
 * 时间工具类
 */
public class DateTimeUtil {

    //joda-time
    //str-->date
    //date-->


    public static final String STAND_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 时间字符串转格式化时间
     * 真方便啊
     * @param dateTimeStr
     * @param formatStr
     * @return
     */
    public static Date strToDate(String dateTimeStr, String formatStr){
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(formatStr);
        DateTime dateTime = dateTimeFormatter.parseDateTime(dateTimeStr);
        return dateTime.toDate();
    }

    /**
     * 时间转字符串
     * @param date
     * @param formatStr
     * @return
     */
    public static String dateToStr(Date date, String formatStr){
        if (date == null){
            return StringUtils.EMPTY;
        }
        DateTime dateTime = new DateTime(date);
        return dateTime.toString(formatStr);
    }
    /**
     * 时间字符串转格式化时间
     * 真方便啊
     * @param dateTimeStr
     * @return
     */
    public static Date strToDate(String dateTimeStr){
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(STAND_FORMAT);
        DateTime dateTime = dateTimeFormatter.parseDateTime(dateTimeStr);
        return dateTime.toDate();
    }

    /**
     * 时间转字符串
     * @param date
     * @return
     */
    public static String dateToStr(Date date){
        if (date == null){
            return StringUtils.EMPTY;
        }
        DateTime dateTime = new DateTime(date);
        return dateTime.toString(STAND_FORMAT);
    }


    public static void main(String[] args) {
        System.out.println(DateTimeUtil.dateToStr(new Date(), "yyyy-MM-dd"));
        System.out.println(DateTimeUtil.strToDate("2011-11-11","yyyy-MM-dd"));
    }


}
