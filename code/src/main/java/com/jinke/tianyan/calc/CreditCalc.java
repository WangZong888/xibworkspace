package com.jinke.tianyan.calc;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import com.jinke.tianyan.util.Global;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jinke.tianyan.common.CommonUtil;

/**
 * Date Author Version Description
 * -----------------------------------------------------------------------------
 * ---- 2019年5月28日 上午11:03:53 wangyue 1.0 To create
 */
public class CreditCalc {
    private static Logger logger = LoggerFactory.getLogger(CreditCalc.class);

    public static JSONObject calc(JSONObject historyJson, JSONObject applyJson, JdbcTemplate secondaryJdbcTemplate, JdbcTemplate primaryJdbcTemplate ) throws Exception {

        JSONObject result = new JSONObject();
        if (historyJson != null && historyJson.get("businessList") != null) {
            //申请月
            String applyMonth = CommonUtil.getFormatMonth(applyJson.getJSONObject("params").getString("applyDate"));
            //近六个月销售金额变异系数
            result.put("r6_cv", r6_cv(historyJson, applyMonth));
            //近六个月的销售金额趋势
            result.put("r6_trend", r6_trend(historyJson, applyMonth));
            String str = r6_sum_new(historyJson, applyMonth);
            if(!"-99".equals(str)){
                String[] split_value = str.split("\\|");
                Double r6_sum = Double.valueOf(split_value[0]);
                Double avg_cout = Double.valueOf(split_value[1]);
                if(avg_cout != 0.0){
                    BigDecimal bg = new BigDecimal(r6_sum/avg_cout);
                    double r6_mean = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
                    result.put("r6_sum", r6_sum);
                    result.put("r6_mean", r6_mean);
                }

            }

            Integer missing_month_count = 6-missing_month_count(historyJson, applyMonth);
            logger.info("近六个月销售金额缺失个数:"+missing_month_count);
            result.put("missing_month_count", missing_month_count);
            result.put("flow_continuity", (6 - missing_month_count) >= 3 ? true : false);
            result.put("max_outAmount_ratio", max_outAmount_ratio(historyJson, applyMonth));
            //	result.put("outAmount_year_ratio", outAmount_year_ratio(historyJson,applyMonth));

            result.put("max_month_diff", max_month_diff(historyJson, applyMonth));
            result.put("r3_apply_ratio", r3_apply_ratio(historyJson, applyMonth, applyJson));
            result.put("latest_apply_date", latest_apply_date(applyJson, secondaryJdbcTemplate));
            result.put("latest_approval_date", latest_approval_date(applyJson, secondaryJdbcTemplate,primaryJdbcTemplate ));
            Double outAmount_order_mean =  outAmount_order_mean(historyJson, applyJson);
            result.put("outAmount_order_mean", outAmount_order_mean);

            Double r12_mean =  r12_mean(historyJson, applyJson);
            result.put("r12_mean", r12_mean);
            result.put("purchase_interval_mean", purchase_interval_mean(historyJson));
            Double outAmount_order_std = outAmount_order_std(historyJson, applyJson);
            result.put("outAmount_order_std", outAmount_order_std);
            result.put("age", age(applyJson));
            result.put("if_local_person", if_local_person(applyJson));
            distribution(result, outAmount_order_mean, outAmount_order_std);
        }
        return result;
    }

    public static Double r6_cv(JSONObject jsonObject, String applyMonth) {

        try {
            //接口返回单据业务日期和销售金额
            JSONArray list = jsonObject.getJSONArray("businessList");
            List<Double> dList = new ArrayList<>();
            //申请日期不包含当月的
            List<String> months = CommonUtil.getSixMonth(false, applyMonth);
            for (int i = 0; i < list.size(); i++) {

                if (months.contains(list.getJSONObject(i).getString("businessDate"))) {

                    dList.add(list.getJSONObject(i).getDouble("outAmount"));
                }
            }
            Double[] outAmounts = new Double[]{};
            // 样本标准差/均值
            // 样本标准差
            double standardDeviation = CommonUtil.getStandardDeviation(dList.toArray(outAmounts));
            double average = CommonUtil.findAverageWithoutUsingStream(dList.toArray(outAmounts));

            Double result = 0D;
            if (average != 0) {
                result = standardDeviation / average;
            }
            BigDecimal bg = new BigDecimal(result);
            double r6_cv = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            logger.info("近六个月销售金额变异系数:"+r6_cv);
            return r6_cv;
        } catch (Exception e) {
            logger.error("计算+《申请-申请信息-》——《r6_cv（近六个月销售金额变异系数)》出错", e);
            return 0D;
        }
    }

    public static Double r6_trend(JSONObject jsonObject, String applyMonth) {

        try {
            JSONArray list = jsonObject.getJSONArray("businessList");
            List<String> sixMonths = CommonUtil.getSixMonth(false, applyMonth);
            List<String> threeMonths = CommonUtil.getthreeMonth(false, applyMonth);

            // 近6个月和
            Double sixMonthSum = 0D;
            for (int i = 0; i < list.size(); i++) {

                if (sixMonths.contains(list.getJSONObject(i).getString("businessDate"))) {
                    sixMonthSum += list.getJSONObject(i).getDouble("outAmount");
                }
            }
            // 近3个月和
            Double threeMonthSum = 0D;
            for (int i = 0; i < list.size(); i++) {

                if (threeMonths.contains(list.getJSONObject(i).get("businessDate"))) {
                    threeMonthSum += list.getJSONObject(i).getDouble("outAmount");
                }
            }
            // 近三个月和/（近六个月和-近三个月和）
            Double result = 0D;
            if (sixMonthSum - threeMonthSum != 0) {
                result = threeMonthSum / (sixMonthSum - threeMonthSum);
            }

            BigDecimal bg = new BigDecimal(result);
            double r6_trend = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            logger.info("近六个月的销售金额趋势:"+r6_trend);
            return r6_trend;
        } catch (Exception e) {
            logger.error("计算《申请-申请信息-衍生》——《r6_trend（近六个月的销售金额趋势)》出错", e);
            return 0D;
        }
    }

    public static Double r6_sum(JSONObject jsonObject, String applyMonth) {

        try {
            JSONArray list = jsonObject.getJSONArray("businessList");

            List<String> sixMonths = CommonUtil.getSixMonth(false, applyMonth);
            // 近6个月和
            Double sixMonthSum = 0D;
            int count = 0;
            for (int i = 0; i < list.size(); i++) {

                if (sixMonths.contains(list.getJSONObject(i).getString("businessDate"))) {
                    sixMonthSum += list.getJSONObject(i).getDouble("outAmount");
                    count++;
                }
            }
            System.out.println("sixMonthSum-------------"+sixMonthSum);
            System.out.println("count----------------"+count);
            BigDecimal bg = new BigDecimal(sixMonthSum);
            double r6_sum = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            logger.info("近六个月销售金额和:"+r6_sum);
            return r6_sum;
        } catch (Exception e) {
            logger.error("计算《申请-申请信息-衍生》——《r6_sum（近六个月销售金额和)》出错", e);
            return 0D;
        }
    }

    public static String r6_sum_new(JSONObject jsonObject, String applyMonth) {

        try {
            JSONArray list = jsonObject.getJSONArray("businessList");

            List<String> sixMonths = CommonUtil.getSixMonth(false, applyMonth);
            // 近6个月和
            Double sixMonthSum = 0D;
            int count = 0;
            for (int i = 0; i < list.size(); i++) {

                if (sixMonths.contains(list.getJSONObject(i).getString("businessDate"))) {
                    sixMonthSum += list.getJSONObject(i).getDouble("outAmount");
                    count++;
                }
            }
            BigDecimal bg = new BigDecimal(sixMonthSum);
            double r6_sum = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            logger.info("近六个月销售金额和:"+r6_sum);
            return r6_sum+"|"+count;
        } catch (Exception e) {
            logger.error("计算《申请-申请信息-衍生》——《r6_sum（近六个月销售金额和)》出错", e);
            return "-99";
        }
    }

    public static Double r6_mean(Double r6_sum) {

        if (r6_sum > 0) {
            return new BigDecimal(r6_sum / 6).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        }
        return 0d;

    }

    public static Integer missing_month_count(JSONObject jsonObject, String applyMonth) {

        try {
            JSONArray list = jsonObject.getJSONArray("businessList");

            List<String> sixMonths = CommonUtil.getSixMonth(false, applyMonth);
            Integer missing_month_count = 0;
            for (int i = 0; i < list.size(); i++) {

                if (sixMonths.contains(list.getJSONObject(i).getString("businessDate")) || list.getJSONObject(i).getDouble("outAmount") == 0) {
                    missing_month_count++;
                }
            }
            return missing_month_count;
        } catch (Exception e) {
            logger.error("计算《申请-申请信息-衍生》——《missing_month_count（近六个月销售金额缺失个数)》出错", e);
            return 0;
        }
    }

    public static Double max_outAmount_ratio(JSONObject jsonObject, String applyMonth) {

        try {
            JSONArray list = jsonObject.getJSONArray("businessList");
            List<Double> dList = new ArrayList<>();
            String nowMonth = CommonUtil.getNowMonth();
            for (int i = 0; i < list.size(); i++) {
                if (!nowMonth.equals(list.getJSONObject(i).get("businessDate"))) {
                    if (list.getJSONObject(i).getDouble("outAmount") != null) {

                        dList.add(list.getJSONObject(i).getDouble("outAmount"));
                    }

                }
            }
            Double max = CommonUtil.getMax(dList);
            Double[] outAmounts = new Double[]{};
            Double average = CommonUtil.findAverageWithoutUsingStream(dList.toArray(outAmounts));
            //最大销售金额/(销售金额均值)

            Double result = 0D;
            if (average != 0) {
                result = max / average;
            }
            BigDecimal bg = new BigDecimal(result);
            double max_outAmount_ratio = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            logger.info("最大销售金额占比:"+max_outAmount_ratio);
            return max_outAmount_ratio;
        } catch (Exception e) {
            logger.error("计算《申请-申请信息-衍生》——《max_outAmount_ratio（最大销售金额占比)》出错", e);
            return 0D;
        }
    }

    public static Double outAmount_year_ratio(JSONObject jsonObject, String applyMonth) {

        try {
            JSONArray list = jsonObject.getJSONArray("businessList");

            List<String> oneMonths = CommonUtil.getFromMonth(applyMonth, 1);
            List<String> thirteenMonths = CommonUtil.getFromMonth(applyMonth, 13);

            Double thirteenSum = 0D;
            Double oneSum = 0D;
            for (int i = 0; i < list.size(); i++) {
                if (thirteenMonths.contains(list.getJSONObject(i).getString("businessDate"))) {

                    thirteenSum += list.getJSONObject(i).getDouble("outAmount");

                }
            }
            for (int i = 0; i < list.size(); i++) {
                if (oneMonths.contains(list.getJSONObject(i).getString("businessDate"))) {

                    oneSum += list.getJSONObject(i).getDouble("outAmount");

                }
            }
            //距离申请时间十三个月的销售金额/距离申请时间一个月的销售金额

            Double result = 0D;
            if (oneSum != 0) {
                result = thirteenSum / oneSum;
            }
            BigDecimal bg = new BigDecimal(result);
            double outAmount_year_ratio = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            return outAmount_year_ratio;
        } catch (Exception e) {
            logger.error("计算《申请-申请信息-衍生》——《outAmount_year_ratio（销售金额同比)》出错", e);
            return 0D;
        }

    }

    public static Integer max_month_diff(JSONObject jsonObject, String applyMonth) throws ParseException {

        try {
            JSONArray array = jsonObject.getJSONArray("businessList");
            array = CommonUtil.jsonArraySort(array, "businessDate");
            String haveMoneyMonth = "";
            for (int i = 0; i < array.size(); i++) {
                if (array.getJSONObject(i).getDouble("outAmount") != null && array.getJSONObject(i).getDouble("outAmount") >= 0) {

                    haveMoneyMonth = array.getJSONObject(i).getString("businessDate");
                    break;
                }

            }
            if (haveMoneyMonth != null && applyMonth != null) {
                final Integer diffMonth = CommonUtil.getDiffMonth(haveMoneyMonth, applyMonth);
                logger.info("最早流水至今的月份数:"+diffMonth);
                return diffMonth;
            }
            return 0;
        } catch (Exception e) {
            logger.error("计算《申请-申请信息-衍生》——《max_month_diff（最早流水至今的月份数)》出错", e);
            return 0;
        }
    }

    public static Double r3_apply_ratio(JSONObject history, String applyMonth, JSONObject apply) throws ParseException {

        try {
            JSONArray list = history.getJSONArray("businessList");

            List<String> threeMonths = CommonUtil.getthreeMonth(false, applyMonth);
            Double threeSum = 0D;
            for (int i = 0; i < list.size(); i++) {
                if (threeMonths.contains(list.getJSONObject(i).getString("businessDate"))) {

                    threeSum += list.getJSONObject(i).getDouble("outAmount");

                }
            }
            Double threeAvg = threeSum / 3;
            Double applyAmount = apply.getJSONObject("params").getDouble("applyAmount");


            Double result = 0D;
            if (applyAmount != null && applyAmount != 0) {
                result = threeAvg / applyAmount;
            }
            BigDecimal bg = new BigDecimal(result);
            double r3_apply_ratio = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            logger.info("近三个月销售金额申请金额比:"+r3_apply_ratio);
            return r3_apply_ratio;
        } catch (Exception e) {
            logger.error("计算《申请-申请信息-衍生》——《r3_apply_ratio（近三个月销售金额申请金额比)》出错", e);
            return 0D;
        }
    }

    public static Double outAmount_order_mean(JSONObject history, JSONObject apply) throws ParseException {
        try {
            JSONArray list = history.getJSONArray("businessList");
            Double purchaseFrequency = history.getDouble("purchaseFrequency");
            Double sum = 0D;
            Double result = 0D;
            if (purchaseFrequency != null && purchaseFrequency > 0) {
                for (int i = 0; i < list.size(); i++) {
                    sum += (list.getJSONObject(i).getDouble("outAmount") / purchaseFrequency);
                }
                result = sum / list.size();
            }
            BigDecimal bg = new BigDecimal(result);
            double outAmount_order_mean = bg.setScale(6, BigDecimal.ROUND_HALF_UP).doubleValue();
            logger.info("件均销售金额均值:"+outAmount_order_mean);
            return outAmount_order_mean;
        } catch (Exception e) {
            logger.error("计算《申请-申请信息-衍生》——《outAmount_order_mean（件均销售金额均值)》出错", e);
            return 0D;
        }
    }

    public static Double r12_mean(JSONObject history, JSONObject apply) throws ParseException {
        try {
            JSONArray list = history.getJSONArray("businessList");
            Double sum = 0D;
            Double result = 0D;
            if(list.size()>0){
                for (int i = 0; i < list.size(); i++) {
                    sum += (list.getJSONObject(i).getDouble("outAmount") );
                }
                result = sum / list.size();
            }


            BigDecimal bg = new BigDecimal(result);
            double wl_order_mean = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
            logger.info("近12个月流水均值:"+wl_order_mean);
            return wl_order_mean;
        } catch (Exception e) {
            logger.error("计算《申请-申请信息-衍生》——《近12个月流水均值出错", e);
            return 0D;
        }
    }



    public static Double purchase_interval_mean(JSONObject historyJson) throws ParseException {

        try {
            Double purchaseFrequency = historyJson.getDouble("purchaseFrequency");

            Double result = 0D;
            if (purchaseFrequency != null && purchaseFrequency != 0) {
                result = 30 / purchaseFrequency;
            }
            return Math.ceil(result);

        } catch (Exception e) {
            logger.error("计算《申请-申请信息-衍生》——《purchase_interval_mean（平均采购间隔)》出错", e);
            return 0D;
        }
    }

    public static String latest_apply_date(JSONObject applyJson, JdbcTemplate secondaryJdbcTemplate) {

        try {

            String creditNoKey = applyJson.getJSONObject("params").getString("creditNoKey");
            String sql = "select max(apply_date) from credit_apply_info_dtl where credit_no_key=?";
            String data = secondaryJdbcTemplate.queryForObject(sql, String.class, creditNoKey);
            return data;
        } catch (Exception e) {
            logger.error("计算《申请-申请信息-衍生》——《latest_apply_date（最近一次申请日期)》出错", e);
            return null;
        }
    }

    public static String latest_approval_date(JSONObject applyJson, JdbcTemplate secondaryJdbcTemplate, JdbcTemplate primaryJdbcTemplate) {

        try {
            String creditNoKey = applyJson.getJSONObject("params").getString("creditNoKey");
            String sql = "select max(created_at) from credit_app_result where credit_code=? and status='1'";
            // String data = fourthJdbcTemplate.queryForObject(sql, String.class,creditNoKey);
            String data = primaryJdbcTemplate.queryForObject(sql, String.class, creditNoKey);
            return data;
        } catch (Exception e) {
            logger.error("计算《申请-申请信息-衍生》——《latest_approval_date（最近一次审批通过日期)》出错", e);
            return null;
        }
    }

    public static Double outAmount_order_std(JSONObject history, JSONObject apply) throws ParseException {

        try {
            JSONArray list = history.getJSONArray("businessList");

            Double purchaseFrequency = history.getDouble("purchaseFrequency");
            List<Double> outAmount = new ArrayList<>();
            Double result = 0D;
            if (purchaseFrequency != null && purchaseFrequency > 0) {

                for (int i = 0; i < list.size(); i++) {

                    outAmount.add((list.getJSONObject(i).getDouble("outAmount") / purchaseFrequency));

                }
                Double[] outAmounts = new Double[]{};
                result = CommonUtil.getStandardDeviation(outAmount.toArray(outAmounts));
            }


            BigDecimal bg = new BigDecimal(result);
            double outAmount_order_mean = bg.setScale(6, BigDecimal.ROUND_HALF_UP).doubleValue();
            return outAmount_order_mean;
        } catch (Exception e) {
            logger.error("计算《申请-申请信息-衍生》——《outAmount_order_std（件均销售金额标准差)》出错", e);
            return 0D;
        }
    }

    public static Integer age(JSONObject applyJson) {

        try {

            String idcard = applyJson.getJSONObject("params").getString("certId");
            Integer age = CommonUtil.IdNOToAge(idcard);
            return age;
        } catch (Exception e) {

            logger.error("计算《申请-申请信息-衍生》——《age（年龄)》出错", e);
            return 0;
        }
    }

    public static Boolean if_local_person(JSONObject applyJson) {

        try {

            String idcard = applyJson.getJSONObject("params").getString("certId");
            String addressCompCode = applyJson.getJSONObject("params").getString("addressCompCode");
            String cityCode = idcard.substring(0, 6);
            if (cityCode.equals(addressCompCode)) {

                return true;
            }
            return false;
        } catch (Exception e) {

            logger.error("计算《申请-申请信息-衍生》——《if_local_person（是否本地人口)》出错", e);
            return false;
        }
    }

    public static JSONObject distribution(JSONObject result, Double outAmount_order_mean, Double outAmount_order_std) {

        result.put("distribution_upper_68", new BigDecimal((outAmount_order_mean + (outAmount_order_std))).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
        result.put("distribution_upper_95", new BigDecimal((outAmount_order_mean + (outAmount_order_std * 2))).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
        result.put("distribution_upper_99", new BigDecimal((outAmount_order_mean + (outAmount_order_std * 3))).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
        result.put("distribution_lower_68", new BigDecimal((outAmount_order_mean - (outAmount_order_std))).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
        result.put("distribution_lower_95", new BigDecimal((outAmount_order_mean - (outAmount_order_std * 2))).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
        result.put("distribution_lower_99", new BigDecimal((outAmount_order_mean - (outAmount_order_std * 3))).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());

        return result;
    }
}
