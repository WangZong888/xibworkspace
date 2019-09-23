package com.jinke.tianyan.util;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Global {

    // public static final String BOOTSTRAP_SERVERS = "hadoop01:9092,hadoop02:9092,hadoop03:9092";
   public static final String BOOTSTRAP_SERVERS = "bigdata02:9092,bigdata03:9092,bigdata04:9092";

    public static final String MYSQL_DRIVER_URL = "jdbc:mysql://10.10.188.12:12003/bigdata";
    public static final String MYSQL_USERNAME = "mcl";
    public static final String MYSQL_PWD = "mcl";

/*    public static final String MYSQL_DRIVER_URL = "jdbc:mysql://localhost:3306/dbty";
    public static final String MYSQL_USERNAME = "root";
    public static final String MYSQL_PWD = "root";*/

    public static final int ISREQUEST = 0;

    public static String getRequest_Time() {
        final SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        return format.format(new Date());
    }

    public static String getToday() {
        final SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        return format.format(new Date());
    }

    public static String getQueryDate() {
        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(new Date());
    }

    public final static boolean isJSONValid(String test) {
        try {
            JSONObject.parseObject(test);
        } catch (JSONException ex) {
            try {
                JSONObject.parseArray(test);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }
}
