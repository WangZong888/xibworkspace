package com.jinke.tianyan.calc;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jinke.tianyan.common.CommonUtil;
import com.jinke.tianyan.service.WLHistoryDataService;

/**
 * Date Author Version Description
 * -----------------------------------------------------------------------------
 * ---- 2019年5月30日 上午9:18:27 wangyue 1.0 To create
 */
public class UseCreditCalc {

    private static Logger logger = LoggerFactory.getLogger(UseCreditCalc.class);

    public static JSONObject calc(JSONObject applyJson, JdbcTemplate primaryJdbcTemplate, JdbcTemplate secondaryJdbcTemplate, WLHistoryDataService dataService) throws ParseException {

        System.out.println("用信申请传入json-------------------------" + applyJson.toString());
        JSONObject result = new JSONObject();
        result.put("saler_judge", saler_judge(applyJson, secondaryJdbcTemplate));

        int lendingDate_day_diff = latest_lendingDate_day_diff_new(applyJson, secondaryJdbcTemplate, result);
        result.put("lendingDate_day_diff", lendingDate_day_diff);

        credit_bal_and_loan_judge_and_overdue_count(applyJson, secondaryJdbcTemplate, primaryJdbcTemplate, result);

        JSONArray list = Amount_judge(applyJson, primaryJdbcTemplate, secondaryJdbcTemplate, result, dataService);
        int ADate_day_diff = latest_ADate_day_diff_new(applyJson, primaryJdbcTemplate, secondaryJdbcTemplate, result, dataService);
        result.put("list", list);
        result.put("ADate_day_diff", ADate_day_diff);

        result.put("platform_id", applyJson.getString("platform_id"));
        result.put("id_of_core", applyJson.getString("id_of_core"));
        result.put("order_apply_no", applyJson.getJSONObject("params").getString("orderApplyNo"));
        result.put("cert_id", applyJson.getString("cert_id"));
        result.put("credit_id", applyJson.getString("credit_id"));
        result.put("order_risk_id", applyJson.getJSONObject("params").getString("riskId"));
        return result;
    }

    private static Boolean saler_judge(JSONObject applyJson, JdbcTemplate secondaryJdbcTemplate) {
        boolean flag = false;
        try {
            String supplierCode = applyJson.getJSONObject("params").getString("supplierCode");
            String creditRiskId = applyJson.getJSONObject("params").getString("creditRiskId");

            if (!"".equals(supplierCode) && !"".equals(creditRiskId)) {
                String sql = "select sell_credit_id from credit_apply_info_dtl where risk_id=? limit 1";
                String creditCodeOfCustomer = "";
                try {
                    creditCodeOfCustomer = secondaryJdbcTemplate.queryForObject(sql, String.class, creditRiskId);
                } catch (Exception e) {
                }
                if (!"".equals(creditCodeOfCustomer) && supplierCode.equals(creditCodeOfCustomer)) {
                    flag = true;
                }
            }
            logger.info("《saler_judge（供应商是否一致)》" + flag);
            return flag;
        } catch (Exception e) {
            logger.error("计算《单据-出账信息-衍生》——《saler_judge（供应商是否一致)》出错", e);
            return false;
        }
    }

/*    private static JSONArray latest_lendingDate_day_diff(JSONObject applyJson, JdbcTemplate secondaryJdbcTemplate,
                                                         JSONObject result) throws ParseException {

        try {

            String creditRiskId = applyJson.getJSONObject("params").getString("creditRiskId");
            System.out.println("creditRiskId:" + creditRiskId);
            String creditNo = getCreditNo(secondaryJdbcTemplate, creditRiskId);
            System.out.println("creditNo:" + creditNo);
            JSONArray dataList = applyJson.getJSONObject("params").getJSONArray("billList");
            JSONArray billList = new JSONArray();
            String loanDate = getloanDate(secondaryJdbcTemplate, creditNo);
            String nowDay = CommonUtil.getNowDay();
            if (loanDate != null && !"".equals(loanDate)) {
                loanDate = new SimpleDateFormat("yyyyMMdd").format(new SimpleDateFormat("yyyy-MM-dd").parse(loanDate));
                for (int i = 0; i < dataList.size(); i++) {
                    JSONObject bill = (JSONObject) dataList.get(i);
                    String orderDate = bill.getString("orderDate");
                    if (orderDate != null) {
                        if (orderDate.length() == 8) {
                            billList.add(CommonUtil.getDiffDay(loanDate, orderDate));
                        } else {
                            billList.add(CommonUtil.getDiffDay(loanDate, CommonUtil.timeStamp2Date(orderDate, null)));
                        }
                    }
                }
            } else {
                for (int i = 0; i < dataList.size(); i++) {
                    billList.add(0);
                }
            }
            result.put("credit_no_key", creditNo);
            result.put("credit_risk_id", creditRiskId);
            logger.info("《latest_lendingDate_day_diff（本次申请与上次放款天数差)》" + billList.toJSONString());
            return billList;
        } catch (Exception e) {
            logger.error("计算《单据-出账信息-衍生》——《latest_lendingDate_day_diff（本次申请与上次放款天数差)》出错", e);
            return null;
        }
    }*/

    private static int latest_lendingDate_day_diff_new(JSONObject applyJson, JdbcTemplate secondaryJdbcTemplate,
                                                       JSONObject result) throws ParseException {
        int lendingDate_day_diff = -99;
        try {
            String creditRiskId = applyJson.getJSONObject("params").getString("creditRiskId");
            String creditNo = getCreditNo(secondaryJdbcTemplate, creditRiskId);
            JSONArray dataList = applyJson.getJSONObject("params").getJSONArray("billList");
            JSONArray billList = new JSONArray();
            String nowDay = CommonUtil.getNowDay();
            if (nowDay != null && !"".equals(nowDay)) {
                String loanDate = getloanDate(secondaryJdbcTemplate, creditNo);
                if (loanDate != null && !"".equals(loanDate) && !"-".equals(loanDate)) {
                    lendingDate_day_diff = (int) CommonUtil.getDaysBetween(loanDate, nowDay);
                }
            }
            result.put("credit_no_key", creditNo);
            result.put("credit_risk_id", creditRiskId);
            logger.info("《latest_lendingDate_day_diff（本次申请与上次放款天数差)》" + lendingDate_day_diff);
            return lendingDate_day_diff;
        } catch (Exception e) {
            logger.error("计算《单据-出账信息-衍生》——《latest_lendingDate_day_diff（本次申请与上次放款天数差)》出错", e);
            return lendingDate_day_diff;
        }
    }


    public static String getCreditNo(JdbcTemplate secondaryJdbcTemplate, String creditRiskId) {
        String creditno = "";
        try {
            creditno = secondaryJdbcTemplate.queryForObject("select credit_code from credit_apply_info_dtl where risk_id=? limit 1", String.class, creditRiskId);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
        return creditno;
    }

    public static String getloanDate(JdbcTemplate secondaryJdbcTemplate, String creditNo) {
        String loanDate = "";
        try {
            loanDate = secondaryJdbcTemplate.queryForObject("select max(lending_date) from accounting_result where credit_id=? and lending_result='1'", String.class, creditNo);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
        return loanDate;
    }

    @SuppressWarnings("unchecked")
    private static JSONArray Amount_judge(JSONObject applyJson, JdbcTemplate primaryJdbcTemplate,
                                          JdbcTemplate secondaryJdbcTemplate, JSONObject result, WLHistoryDataService dataService) {

        String creditRiskId = applyJson.getJSONObject("params").getString("creditRiskId");
        JSONArray judges = new JSONArray();
        Map<String, Object> param = new HashMap<>();
        try {

            try {
                param = secondaryJdbcTemplate.queryForMap("select platform_id,id_of_core,credit_no_key,cert_id,credit_id from credit_apply_info_dtl where risk_id=? limit 1", creditRiskId);
            } catch (EmptyResultDataAccessException e) {
            }
            if (param != null && !"".equals(param)) {
                String channelId = String.valueOf(param.get("platform_id"));
                String idOfCore = String.valueOf(param.get("id_of_core"));
                String creditNoKey = String.valueOf(param.get("credit_no_key") == null ? "" : param.get("credit_no_key"));
                String certId = String.valueOf(param.get("cert_id"));
                String creditId = String.valueOf(param.get("credit_id"));
                applyJson.put("platform_id", channelId);
                applyJson.put("id_of_core", idOfCore);
                applyJson.put("credit_no_key", creditNoKey);
                applyJson.put("cert_id", certId);
                applyJson.put("credit_id", creditId);
                logger.info("用信阶段开始请求文沥获取采购频率");
                JSONObject json = dataService.historyData(channelId, idOfCore, creditNoKey);
                System.out.println("文沥返回报文：" + json);
                JSONArray flow_list = json.getJSONArray("businessList");
                logger.info("用信阶段请求文沥获取采购频率结束");
                Double purchaseFrequency = json.getDouble("purchaseFrequency") == null ? 0.0 : json.getDouble("purchaseFrequency");
                logger.info("用信阶段 purchaseFrequency--------->" + purchaseFrequency);
                List<JSONObject> list = (List<JSONObject>) applyJson.getJSONObject("params").get("billList");
                Double sum = 0D;
                Double outAmount_order_mean = 0D;
                Double outAmount_order_std = 0D;
                double amount_purchase = 0.0;
                //计算销售金额均值  和  销售金额/采购的样本标准差
                double flow_sum = 0.0;
                double flow_avg = 0.0;
                double flow_avg_purchase = 0.0;

                List<Double> outAmount = new ArrayList<>();
                for (int i = 0; i < flow_list.size(); i++) {
                    double current_money =  flow_list.getJSONObject(i).getDouble("outAmount")/purchaseFrequency;
                    outAmount.add(current_money);
                    flow_sum += flow_list.getJSONObject(i).getDouble("outAmount");
                }
                Double[] outAmounts = new Double[]{};
                outAmount_order_std = CommonUtil.getStandardDeviation(outAmount.toArray(outAmounts));

                if (flow_list.size() > 0) {
                    flow_avg = new BigDecimal(flow_sum / flow_list.size()).setScale(6, BigDecimal.ROUND_HALF_UP).doubleValue();
                }
                if (purchaseFrequency != null && purchaseFrequency > 0) {
                    //出账申请间隔天数参考值
                    result.put("day_diff_refer", new BigDecimal((30 / purchaseFrequency) - 5).setScale(6, BigDecimal.ROUND_HALF_UP).doubleValue());
                    flow_avg_purchase = new BigDecimal(flow_avg / purchaseFrequency).setScale(6, BigDecimal.ROUND_HALF_UP).doubleValue();
                } else {
                    result.put("day_diff_refer", -99);
                }

                for (int i = 0; i < list.size(); i++) {
                    JSONObject jsonObject = new JSONObject();
                    Double tmp = Math.abs(((list.get(i).getDouble("amount") == null ? 0.0 : list.get(i).getDouble("amount") - flow_avg_purchase) / outAmount_order_std));
                    jsonObject.put("billno", list.get(i).getString("billNo"));
                    jsonObject.put("amount", list.get(i).getDouble("amount"));
                    jsonObject.put("orderDate", list.get(i).getString("orderDate"));
                    jsonObject.put("Amount_judge_68", tmp < 1);
                    jsonObject.put("Amount_judge_95", tmp > 1 && tmp < 2);
                    jsonObject.put("Amount_judge_99", tmp > 2 && tmp < 3);
                    judges.add(jsonObject);
                }
            }
            logger.info("《Amount_judge（单据金额判断)》" + judges.toJSONString());
            return judges;
        } catch (Exception e) {
            logger.error("计算《单据-出账信息-衍生》——《Amount_judge（单据金额判断)》出错", e);
            return judges;
        }

    }

    private static JSONArray latest_ADate_day_diff(JSONObject applyJson, JdbcTemplate primaryJdbcTemplate,
                                                   JdbcTemplate secondaryJdbcTemplate, JSONObject result, WLHistoryDataService dataService) {

        JSONArray dataList = applyJson.getJSONObject("params").getJSONArray("billList");
        String creditRiskId = applyJson.getJSONObject("params").getString("creditRiskId");
        JSONArray billList = new JSONArray();
        try {
            String sql = "select max(order_date) from use_credit_apply where credit_risk_id=?";
            String date = secondaryJdbcTemplate.queryForObject(sql, String.class, creditRiskId);
            if (date != null && !"".equals(date)) {

                date = new SimpleDateFormat("yyyyMMdd").format(new SimpleDateFormat("yyyy-MM-dd").parse(date));
                for (int i = 0; i < dataList.size(); i++) {

                    JSONObject bill = (JSONObject) dataList.get(i);
                    String orderDate = bill.getString("orderDate");
                    if (orderDate != null) {

                        if (orderDate.length() == 8) {

                            billList.add(CommonUtil.getDiffDay(date, orderDate));
                        } else {
                            billList.add(CommonUtil.getDiffDay(date, CommonUtil.timeStamp2Date(orderDate, null)));
                        }
                    }
                }
            }
            logger.info("《latest_ADate_day_diff（本次提现申请距离最近一次提现申请的天数差)》" + billList.toJSONString());
            return billList;
        } catch (Exception e) {
            logger.error("计算《单据-出账信息-衍生》——《latest_ADate_day_diff（本次提现申请距离最近一次提现申请的天数差)》出错", e);
            return billList;
        }

    }

    private static int latest_ADate_day_diff_new(JSONObject applyJson, JdbcTemplate primaryJdbcTemplate,
                                                 JdbcTemplate secondaryJdbcTemplate, JSONObject result, WLHistoryDataService dataService) {

        JSONArray dataList = applyJson.getJSONObject("params").getJSONArray("billList");
        String creditRiskId = applyJson.getJSONObject("params").getString("creditRiskId");
        JSONArray billList = new JSONArray();
        int latest_ADate_day_diff_new = -99;
        try {
            String sql = "select max(create_time) from use_credit_apply where credit_risk_id=?";
            String date = secondaryJdbcTemplate.queryForObject(sql, String.class, creditRiskId);
            if (date != null && !"".equals(date)) {
                date = new SimpleDateFormat("yyyyMMdd").format(new SimpleDateFormat("yyyy-MM-dd").parse(date));
                for (int i = 0; i < dataList.size(); i++) {
                    JSONObject bill = (JSONObject) dataList.get(i);
                    String orderDate = bill.getString("orderDate");
                    if (orderDate != null) {
                        if (orderDate.length() == 8) {
                            latest_ADate_day_diff_new = (int) CommonUtil.getDiffDay(date, orderDate);
                        } else {
                            latest_ADate_day_diff_new = (int) CommonUtil.getDiffDay(date, CommonUtil.timeStamp2Date(orderDate, null));
                        }
                    }
                }
            }
            logger.info("《latest_ADate_day_diff（本次提现申请距离最近一次提现申请的天数差)》" + latest_ADate_day_diff_new);
            return latest_ADate_day_diff_new;
        } catch (Exception e) {
            logger.error("计算《单据-出账信息-衍生》——《latest_ADate_day_diff（本次提现申请距离最近一次提现申请的天数差)》出错", e);
            return latest_ADate_day_diff_new;
        }

    }


    private static void credit_bal_and_loan_judge_and_overdue_count(JSONObject applyJson,
                                                                    JdbcTemplate secondaryJdbcTemplate, JdbcTemplate primaryJdbcTemplate, JSONObject result) {

        try {

            String creditRiskId = applyJson.getJSONObject("params").getString("creditRiskId");

            String sql = "select credit_apply_no,credit_no_key,credit_id from credit_apply_info_dtl where risk_id=? limit 1";
            Map<String, Object> params = null;
            try {
                params = secondaryJdbcTemplate.queryForMap(sql, creditRiskId);
            } catch (EmptyResultDataAccessException e) {
                params = null;
            }
            String creditNoKey = "";
            String creditId = "";
            String creditApplyNo = "";
            if (params != null && !"".equals(params)) {
                creditNoKey = String.valueOf(params.get("credit_no_key"));
                creditId = String.valueOf(params.get("credit_id"));
                creditApplyNo = String.valueOf(params.get("credit_apply_no"));
            }
            System.out.println("creditNoKey:" + creditNoKey);
            System.out.println("creditId:" + creditId);
            System.out.println("creditApplyNo:" + creditApplyNo);
            result.put("credit_apply_no", creditApplyNo);
            // 总额度
            sql = "select quota from credit_app_result where risk_id=? limit 1";
            int lineOfCredit = 0;
            try {
                lineOfCredit = primaryJdbcTemplate.queryForObject(sql, Integer.class, creditRiskId);
            } catch (EmptyResultDataAccessException e) {
                lineOfCredit = 0;
            }

            // 未结清额度
            sql = "select sum(loan_amt) from xib_contract_info where credit_apply_no=? and status != '06' and loan_amt !='-99'";
            Double usedMoney = null;
            try {
                usedMoney = secondaryJdbcTemplate.queryForObject(sql, Double.class, creditApplyNo);
            } catch (EmptyResultDataAccessException e) {
            }
            if (usedMoney == null || usedMoney < 0 ) {
                usedMoney = 0d;
            }
            Double balanced = lineOfCredit - usedMoney;
            result.put("credit_bal", balanced);
            Double applyAmt = applyJson.getJSONObject("params").getDouble("applyAmt");
            if (balanced >= applyAmt) {
                result.put("loan_judge", true);
            } else {
                result.put("loan_judge", false);
            }
            sql = "select count(1) from xib_contract_info  where credit_id=? and status = '05' ";
            Integer overdue_count = null;
            try {
                overdue_count = secondaryJdbcTemplate.queryForObject(sql, Integer.class, creditId);
            } catch (EmptyResultDataAccessException e) {
            }
            result.put("overdue_count", overdue_count);
            logger.info("《credit_bal_and_loan_judge_and_overdue_count（授信余额、本次出账金额判断、在贷逾期笔数)》" + balanced + "|" + (balanced >= applyAmt) + "|" + overdue_count);
        } catch (Exception e) {
            logger.error("计算《单据-出账信息-衍生》——《credit_bal_and_loan_judge_and_overdue_count（授信余额、本次出账金额判断、在贷逾期笔数)》出错", e);
        }
    }
}
