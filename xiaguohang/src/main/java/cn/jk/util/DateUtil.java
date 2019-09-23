package cn.jk.util;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by YiZe on 2019/5/19.
 */
public class DateUtil {
    /**
     * @// TODO: 2019/5/19 获取前一天日期
     * @return
     */
    public static String getYesterdayByCalendar(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE,-1);
        Date time = calendar.getTime();
        String yesterday = new SimpleDateFormat("yyyy-MM-dd").format(time);
        return yesterday;
    }
    /**
     * @// TODO: 2019/5/19 获取当天日期
     * @return
     */
    public static String getCurrentByCalendar(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE,0);
        Date time = calendar.getTime();
        String yesterday = new SimpleDateFormat("yyyy-MM-dd").format(time);
        return yesterday;
    }
}
