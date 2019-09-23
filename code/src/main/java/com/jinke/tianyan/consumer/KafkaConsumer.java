package com.jinke.tianyan.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jinke.tianyan.calc.CreditCalc;
import com.jinke.tianyan.calc.UseCreditCalc;
import com.jinke.tianyan.service.WLHistoryDataService;

/**
 * 消费者 使用@KafkaListener注解,可以指定:主题,分区,消费组
 */
@Component
public class KafkaConsumer {

    private static Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);

    @Autowired
    private WLHistoryDataService dataService;

    @Autowired
    @Qualifier("primaryJdbcTemplate")
    private JdbcTemplate primaryJdbcTemplate;

    @Autowired
    @Qualifier("secondaryJdbcTemplate")
    private JdbcTemplate secondaryJdbcTemplate;

/*
    @Autowired
    @Qualifier("thridaryJdbcTemplate")
    private JdbcTemplate thridaryJdbcTemplate;

    @Autowired
    @Qualifier("fourthJdbcTemplate")
    private JdbcTemplate fourthJdbcTemplate;
*/

    @SuppressWarnings("rawtypes")
    @Autowired
    private KafkaTemplate kafkaTemplate;

    @SuppressWarnings("unchecked")
    @KafkaListener(topics = {"xib_credit_apply_info"})
    public void receive(String message) throws Exception {

        logger.info("xib_credit_apply_info--消费消息", message);
        String params[] = message.split("\001");
        JSONObject jsonObject = JSONObject.parseObject(params[0]);
        JSONObject result = new JSONObject();

        String type = params[1];

        String opt = "";
        if ("xiaguohang_first_loan".equals(type) || "xiaguohang_repeatloan_send".equals(type)) {

            logger.info(type + "--消费消息:" + message);
            //授信申请数据解析，从kafka获取
            JSONObject credit = new JSONObject();
            String channelId = jsonObject.getJSONObject("params").getString("platformId");
            String idOfCore = jsonObject.getJSONObject("params").getString("idOfCore");
            String creditNoKey = jsonObject.getJSONObject("params").getString("creditNoKey");
            String riskId = jsonObject.getJSONObject("params").getString("riskId");
            logger.info("channelId:" + channelId);
            logger.info("idOfCore:" + idOfCore);
            logger.info("creditNoKey:" + creditNoKey);

            //请求文沥接口，获取返回数据
            logger.info("开始请求文沥接口");
            JSONObject json = dataService.historyData(channelId, idOfCore, creditNoKey);

            logger.info("请求文沥接口结束");
            logger.info("json------------" + json);

//            credit = CreditCalc.calc(json, jsonObject, secondaryJdbcTemplate, thridaryJdbcTemplate, fourthJdbcTemplate);
            if (null != json && json.containsKey("businessList")) {
                credit = CreditCalc.calc(json, jsonObject, secondaryJdbcTemplate, primaryJdbcTemplate);
                credit.put("name", jsonObject.getJSONObject("params").getString("name"));
                credit.put("apply_no", jsonObject.getJSONObject("params").getString("creditApplyNo"));
                credit.put("phone", jsonObject.getJSONObject("params").getString("phone"));
                credit.put("cert_type", jsonObject.getJSONObject("params").getString("creditType"));
                credit.put("cert_id", jsonObject.getJSONObject("params").getString("certId"));
                credit.put("credit_Id", jsonObject.getJSONObject("params").getString("creditId"));
                credit.put("supplier_code", jsonObject.getJSONObject("params").getString("supplierCode"));
                credit.put("supplier_name", jsonObject.getJSONObject("params").getString("supplierName"));

                String sql = "INSERT INTO credit_apply_info ("
                        + "credit_apply_no, cert_id, credit_id, r6_cv, "
                        + "r6_trend , r6_sum, missing_month_count, max_outAmount_ratio, "
                        + "outAmount_year_ratio, max_month_diff , r3_apply_ratio,latest_apply_date,"
                        + "latest_approval_date,outAmount_order_mean,purchase_interval_mean,outAmount_order_std,"
                        + "distribution_upper_68,distribution_upper_95,distribution_upper_99,distribution_lower_68,"
                        + "distribution_lower_95,distribution_lower_99,credit_risk_id,age,"
                        + "if_local_person,flow_continuity,r6_mean,r12_mean,supplier_code,supplier_name) VALUES "
                        + "(?, ?, ?, ?, "
                        + " ?, ?, ?, ?, "
                        + " ?, ?, ?, ?, "
                        + " ? ,?, ?, ?, "
                        + " ?, ?, ?, ?, "
                        + " ?, ?, ?, ?, "
                        + " ?, ?, ?, ?, ?, ?)";
                int flag = secondaryJdbcTemplate.update(sql, credit.getString("apply_no"), credit.getString("cert_id"),
                        credit.getString("credit_Id"), credit.getDouble("r6_cv"),
                        credit.getDouble("r6_trend"), credit.getDouble("r6_sum"),
                        credit.getInteger("missing_month_count"), credit.getDouble("max_outAmount_ratio"),
                        credit.getDouble("outAmount_year_ratio"), credit.getInteger("max_month_diff"),
                        credit.getDouble("r3_apply_ratio"), credit.getInteger("latest_apply_date"),
                        credit.getString("latest_approval_date"), credit.getDouble("outAmount_order_mean"),
                        credit.getDouble("purchase_interval_mean"), credit.getDouble("outAmount_order_std"),
                        credit.getDouble("distribution_upper_68"), credit.getDouble("distribution_upper_95"),
                        credit.getDouble("distribution_upper_99"), credit.getDouble("distribution_lower_68"),
                        credit.getDouble("distribution_lower_95"), credit.getDouble("distribution_lower_99"),
                        riskId, credit.getInteger("age"), credit.getBoolean("if_local_person"),
                        credit.getBoolean("flow_continuity"),
                        credit.getDouble("r6_mean"),
                        credit.getDouble("r12_mean"),
                        credit.getString("supplier_code"),
                        credit.getString("supplier_name"));
                if (flag > 0) {
                    result.put("returnStatus", "1");
                    result.put("returnMsg", "SUCCESS");
                } else {
                    result.put("returnStatus", "0");
                    result.put("returnMsg", "ERROR");
                }
                if ("xiaguohang_first_loan".equals(type)) {
                    opt = "1";
                    result.put("creditApplyNoXib", jsonObject.getJSONObject("params").getString("creditApplyNoXib"));
                    result.put("riskId", riskId);
                    result.put("platformId", channelId);
                    result.put("tblperRequestBizId", jsonObject.getString("eventId"));
                    result.put("creditNoKey", creditNoKey);

                } else {
                    opt = "2";
                    result.put("eventId", jsonObject.getString("eventId"));
                }
                result.put("params", credit);
                result.put("taskType", "wlflow");
                logger.info(type + "--计算结果:" + result);
                if ("xiaguohang_first_loan".equals(type)) {

                    kafkaTemplate.send("xib_credit_report", result.toJSONString());

                } else {
                    System.out.println("发送给贷后内部的消息是："+message);
                    kafkaTemplate.send("xiaguohang_repeatloan_inner", message);
                }
            } else {
                System.out.println("接口返回无数据，插入-99");
                JSONObject resJson = new JSONObject();
                String creditApplyNo = jsonObject.getJSONObject("params").getString("creditApplyNo");
                String certId = jsonObject.getJSONObject("params").getString("certId");
                String creditId = jsonObject.getJSONObject("params").getString("creditId");
                String supplier_code = jsonObject.getJSONObject("params").getString("supplierCode");
                String supplier_name = jsonObject.getJSONObject("params").getString("supplierName");
                JSONObject params1 = jsonObject.getJSONObject("params");
                String sql = "INSERT INTO credit_apply_info ("
                        + "credit_apply_no, cert_id, credit_id, r6_cv, "
                        + "r6_trend , r6_sum, missing_month_count, max_outAmount_ratio, "
                        + "outAmount_year_ratio, max_month_diff , r3_apply_ratio,latest_apply_date,"
                        + "outAmount_order_mean,purchase_interval_mean,outAmount_order_std,"
                        + "distribution_upper_68,distribution_upper_95,distribution_upper_99,distribution_lower_68,"
                        + "distribution_lower_95,distribution_lower_99,credit_risk_id,age,"
                        + "r6_mean,r12_mean,supplier_code,supplier_name) VALUES "
                        + "(?, ?, ?, -99, "
                        + " -99, -99, -99, -99, "
                        + " -99, -99, -99, -99, "
                        + " -99, -99, -99, "
                        + " -99, -99, -99, -99, "
                        + " -99, -99, ?, -99, "
                        + "  -99, -99, ?, ?)";
                System.out.println("开始插入-99数据");
                secondaryJdbcTemplate.update(sql, creditApplyNo, certId, creditId, riskId,supplier_code,supplier_name);
                System.out.println("插入-99数据完成，开始给引擎返回消息");
                resJson.put("taskType", "");
                resJson.put("returnStatus", "1");
                resJson.put("returnMsg", "SUCCESS");
                resJson.put("params",params1);
                if ("xiaguohang_first_loan".equals(type)) {
                    resJson.put("creditApplyNoXib", jsonObject.getJSONObject("params").getString("creditApplyNoXib"));
                    resJson.put("riskId", riskId);
                    resJson.put("platformId", channelId);
                    resJson.put("tblperRequestBizId", jsonObject.getString("eventId"));
                    resJson.put("creditNoKey", creditNoKey);
                } else {
                    resJson.put("eventId", jsonObject.getString("eventId"));
                }
                if ("xiaguohang_first_loan".equals(type)) {
                    kafkaTemplate.send("xib_credit_report", resJson.toJSONString());
                    System.out.println("发送到xib_credit_report的消息为---------" + resJson.toJSONString());
                } else {
                    kafkaTemplate.send("xiaguohang_repeatloan_inner", resJson.toJSONString());
                }
            }

        } else {

            logger.info(type + "--消费消息:" + message);
            logger.info("开始进行用信计算");
            try {
                JSONObject credit = UseCreditCalc.calc(jsonObject, primaryJdbcTemplate, secondaryJdbcTemplate, dataService);
                logger.info("用信返回计算结果" + credit.toJSONString());
                JSONArray jsonArray = credit.getJSONArray("list");
                int  lendingDate_day_diff = credit.getInteger("lendingDate_day_diff");
                int  ADate_day_diff = credit.getInteger("ADate_day_diff");
                int flag = 0;
                if (jsonArray != null && !jsonArray.isEmpty()) {
                    for (int i = 0; i < jsonArray.size(); i++) {
                        String sql = "INSERT INTO accounting_derivation ("
                                + "credit_apply_no, credit_risk_id, saler_judge, "
                                + "latest_lendingDate_day_diff,Amount_judge_68,Amount_judge_95,"
                                + "Amount_judge_99,cert_id,credit_id,"
                                + "credit_bal,loan_judge,overdue_count,latest_adate_day_diff,day_diff_refer,order_risk_id,bill_no,amount,order_date) VALUES (?, ?, ?, ? ,? ,? ,? ,? ,?,?,?,?,?,?,?,?,?,?)";

                        flag += secondaryJdbcTemplate.update(sql,
                                credit.getString("credit_apply_no"), credit.getString("credit_risk_id"), credit.getBoolean("saler_judge"),
                                lendingDate_day_diff, jsonArray.getJSONObject(i).get("Amount_judge_68"), jsonArray.getJSONObject(i).get("Amount_judge_95"),
                                jsonArray.getJSONObject(i).get("Amount_judge_99"), credit.getString("cert_id"), credit.getString("credit_id"),
                                credit.getDouble("credit_bal"), credit.getBoolean("loan_judge"), credit.getInteger("overdue_count"),
                                ADate_day_diff, credit.getDouble("day_diff_refer"), credit.getString("order_risk_id"),
                                jsonArray.getJSONObject(i).getString("billno"),
                                jsonArray.getJSONObject(i).getDouble("amount"),
                                jsonArray.getJSONObject(i).getString("orderDate"));

                    }
                }
                logger.info("插入数据库返回flag----------" + flag);
                String channelId = jsonObject.getJSONObject("params").getString("platformId");
                result.put("riskId", credit.getString("credit_risk_id"));
                result.put("platformId", channelId);
                result.put("credit_no_key", credit.getString("credit_no_key"));
                result.put("params", jsonObject.getJSONObject("params"));
                if (flag > 0) {
                    result.put("returnStatus", "1");
                    result.put("returnMsg", "SUCCESS");
                } else {
                    result.put("returnStatus", "0");
                    result.put("returnMsg", "ERROR");
                }
                opt = "3";
                result.put("eventId", jsonObject.getString("eventId"));
                result.put("taskType", "orderapply");
                logger.info(type + "--计算结果:" + result);
                kafkaTemplate.send("xiaguohang_order_apply", result.toJSONString());
                logger.info("Kafka消息发送成功，Msg为：" + result.toJSONString());
            } catch (Exception e) {
                kafkaTemplate.send("xiaguohang_order_apply", result.toJSONString());
                e.printStackTrace();
                throw new RuntimeException("用信阶段异常" + e.getMessage());
            }
        }
        String sql = "insert into calc_log (request_data,calc_result,type) VALUES(?,?,?)";
        secondaryJdbcTemplate.update(sql, message, result.toJSONString(), opt);
    }
}