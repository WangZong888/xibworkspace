package com.jinke.tianyan.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

/**
 * Date Author Version Description
 * -----------------------------------------------------------------------------
 * ---- 2019年5月28日 上午11:01:25 wangyue 1.0 To create
 */
public class CommonUtil {

    public static double getStandardDeviation(Double[] array) {

	double sum = 0;
	for (int i = 0; i < array.length; i++) {
	    sum += array[i]; // 求出数组的总和
	}
	double average = sum / array.length; // 求出数组的平均数
	double total = 0;
	for (int i = 0; i < array.length; i++) {
	    total += (array[i] - average) * (array[i] - average); // 求出方差，如果要计算方差的话这一步就可以了
	}
	double standardDeviation = 0d; // 求出标准差
	if (array.length - 1 != 0) {

	    standardDeviation = Math.sqrt(total / (array.length - 1));
	}
	return standardDeviation;
    }

    // 求平均值
    public static double findAverageWithoutUsingStream(Double[] array) {
	Double sum = 0D;
	for (int i = 0; i < array.length; i++) {
	    sum += array[i];
	}
	double ave = (double) sum / array.length;
	return ave;
    }

    /***
     * 
     * @param iscurrent
     *            是否包含当前月
     * @return
     */
    public static List<String> getSixMonth(boolean iscurrent, String applyMonth) {
	String dateString;
	Calendar cal = Calendar.getInstance();
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
	dateString = sdf.format(cal.getTime());
	List<String> rqList = new ArrayList<>();
	int sum = iscurrent ? 6 : 7;

	for (int i = 0; i < sum; i++) {
	    dateString = sdf.format(cal.getTime());
	    if (!iscurrent && !dateString.equals(applyMonth)) {
		rqList.add(dateString.substring(0, 6));
	    } else {
		rqList.add(dateString.substring(0, 6));
	    }
	    cal.add(Calendar.MONTH, -1);

	}
	return rqList;
    }

    public static List<String> getthreeMonth(boolean iscurrent, String applyMonth) {
	String dateString;
	Calendar cal = Calendar.getInstance();
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
	dateString = sdf.format(cal.getTime());
	List<String> rqList = new ArrayList<>();
	int sum = iscurrent ? 3 : 4;
	for (int i = 0; i < sum; i++) {
	    dateString = sdf.format(cal.getTime());

	    if (!iscurrent && !dateString.equals(applyMonth)) {
		rqList.add(dateString.substring(0, 6));
	    } else {
		rqList.add(dateString.substring(0, 6));
	    }

	    cal.add(Calendar.MONTH, -1);
	}
	// 不包含当前月
	rqList.remove(0);
	return rqList;
    }

    public static String getNowMonth() {
	String dateString;
	Calendar cal = Calendar.getInstance();
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
	dateString = sdf.format(cal.getTime());
	return dateString;
    }

	public static String getNowDay() {
		String dateString;
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		dateString = sdf.format(cal.getTime());
		return dateString;
	}

    public static String getFormatMonth(String applyDate) {

	if (applyDate != null && applyDate.length() == 8) {

	    return applyDate.substring(0, 6);

	}
	return applyDate;
    }

    public static Double getMax(List<Double> list) {

	return Collections.max(list);
    }

    public static List<String> getFromMonth(String applyMonth, Integer from) {
	String dateString;
	Calendar cal = Calendar.getInstance();
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
	dateString = applyMonth;
	List<String> rqList = new ArrayList<>();
	for (int i = 0; i < from + 1; i++) {
	    dateString = sdf.format(cal.getTime());

	    rqList.add(dateString.substring(0, 6));

	    cal.add(Calendar.MONTH, -1);
	}
	// 不包含当前月
	rqList.remove(0);
	return rqList;
    }

    public static Integer getDiffMonth(String d1, String d2) throws ParseException {

	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
	Calendar bef = Calendar.getInstance();
	Calendar aft = Calendar.getInstance();
	bef.setTime(sdf.parse(d1));
	aft.setTime(sdf.parse(d2));
	int result = aft.get(Calendar.MONTH) - bef.get(Calendar.MONTH);
	int month = (aft.get(Calendar.YEAR) - bef.get(Calendar.YEAR)) * 12;
	return Math.abs(month + result);

    }

    public static long getDiffDay(String d1, String d2) throws ParseException {

	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	// 跨年的情况会出现问题哦
	// 如果时间为：2016-03-18 11:59:59 和 2016-03-19 00:00:01的话差值为 1
	Date fDate = sdf.parse(d1);
	Date oDate = sdf.parse(d2);
	Calendar aCalendar = Calendar.getInstance();
	aCalendar.setTime(fDate);
	int day1 = aCalendar.get(Calendar.DAY_OF_YEAR);
	aCalendar.setTime(oDate);
	int day2 = aCalendar.get(Calendar.DAY_OF_YEAR);
	int days = day2 - day1;
	return days;
    }



    public static int getDaysBetween(String start_time, String end_Time)  throws Exception {
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        final Calendar cal = Calendar.getInstance();
        cal.setTime(sdf.parse(start_time));
        final long time1 = cal.getTimeInMillis();
        cal.setTime(sdf.parse(end_Time));
        final long time2 = cal.getTimeInMillis();
        long between_days =  (time2-time1)/(1000*3600*24);
        return (int)between_days;
    }


    public static int IdNOToAge(String IdNO) {
	int leh = IdNO.length();
	String dates = "";
	if (leh == 18) {
	    dates = IdNO.substring(6, 10);
	    SimpleDateFormat df = new SimpleDateFormat("yyyy");
	    String year = df.format(new Date());
	    int u = Integer.parseInt(year) - Integer.parseInt(dates);
	    return u;
	} else {
	    dates = IdNO.substring(6, 8);
	    return Integer.parseInt(dates);
	}

    }

    public static String timeStamp2Date(String seconds, String format) {
	if (seconds == null || seconds.isEmpty() || seconds.equals("null")) {
	    return "";
	}
	if (format == null || format.isEmpty()) {
	    format = "yyyyMMdd";
	}
	SimpleDateFormat sdf = new SimpleDateFormat(format);
	return sdf.format(new Date(Long.valueOf(seconds)));
    }
    
    public static JSONArray jsonArraySort(JSONArray jsonArr,String sortField) {
		JSONArray sortedJsonArray = new JSONArray();
		List<JSONObject> jsonValues = new ArrayList<JSONObject>();
		for (int i = 0; i < jsonArr.size(); i++) {
			jsonValues.add(jsonArr.getJSONObject(i));
		}
		Collections.sort(jsonValues, new Comparator<JSONObject>() {
			// You can change "Name" with "ID" if you want to sort by ID
			private final String KEY_NAME = sortField;

			@Override
			public int compare(JSONObject a, JSONObject b) {
				String valA = new String();
				String valB = new String();
				try {
					// 这里是a、b需要处理的业务，需要根据你的规则进行修改。
					String aStr = a.getString(KEY_NAME);
					valA = aStr.replaceAll("-", "");
					String bStr = b.getString(KEY_NAME);
					valB = bStr.replaceAll("-", "");
				} catch (JSONException e) {
					// do something
				}
				return valA.compareTo(valB);
				// if you want to change the sort order, simply use the following:
				// return -valA.compareTo(valB);
			}
		});
		for (int i = 0; i < jsonArr.size(); i++) {
			sortedJsonArray.add(jsonValues.get(i));
		}
		return sortedJsonArray;
}

    public static boolean isNaN(double v) {
        return (v != v);
    }
}
