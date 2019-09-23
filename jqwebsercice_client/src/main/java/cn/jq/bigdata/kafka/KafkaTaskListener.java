package cn.jq.bigdata.kafka;

//import cn.jq.bigdata.deal.CreditReport_Request_new;
//import cn.jq.bigdata.deal.CreditReport_Request_new2;

import cn.jq.bigdata.deal.CreditReport_new;
import cn.jq.bigdata.deal.CreditReport_new_bak;
import cn.jq.bigdata.utils.Global;
import cn.jq.bigdata.utils.KafkaProducerUtil;
import cn.jq.bigdata.utils.SendContextUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.log4j.Logger;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;


@Component
public class KafkaTaskListener {
    Logger logger = Logger.getLogger(KafkaTaskListener.class);

    @KafkaListener(topics = {"${kafka.consumer.topic}"})
    public void listen(ConsumerRecord<?, ?> record, Acknowledgment ack) {

        String riskid = "";
        String t24org = "";
        String idno = "";
        String idname = "";
        Boolean is_Riskid = false;
        Boolean is_T24org = false;
        Boolean is_IdNo = false;
        Boolean is_IdName = false;

        String topic = "";
        try {
            String value = (String) record.value();
            final int i = value.indexOf("\001");
            if(i == -1){
                throw new RuntimeException("接收的消息格式非法，无法解析");
            }
            String[] data = value.split("\001");
            String dealdata = data[0];
            topic = data[1];
            System.out.println("topic----" + topic);
            if ("xiaguohang_first_loan".equals(topic)) {
                final boolean jsonValid = Global.isJSONValid(dealdata);
                if (jsonValid) {
                    JSONObject jsonObject = JSON.parseObject(dealdata);
                    if (jsonObject.containsKey("params")) {
                        String params = jsonObject.getString("params");
                        System.out.println("params:" + params);
                        JSONObject paramObject = JSON.parseObject(params);
                        if (paramObject.containsKey("riskId")) {
                            riskid = paramObject.getString("riskId");
                            if (riskid != null && !"".equals(riskid)) {
                                is_Riskid = true;
                            } else {
                                throw new RuntimeException("riskId为空，无法进行解析");
                            }
                        } else {
                            throw new RuntimeException("无riskId无法进行解析");
                        }
                        if (paramObject.containsKey("t24OrgCode")) {
                            t24org = paramObject.getString("t24OrgCode");
                            if (t24org != null && !"".equals(t24org)) {
                                is_T24org = true;
                            } else {
                                throw new RuntimeException("t24OrgCode为空，无法进行解析");
                            }
                        } else {
                            throw new RuntimeException("无t24OrgCode无法进行解析");
                        }
                        if (paramObject.containsKey("idCard")) {
                            idno = paramObject.getString("idCard");
                            if (idno != null && !"".equals(idno)) {
                                is_IdNo = true;
                            } else {
                                throw new RuntimeException("idCard为空，无法进行解析");
                            }
                        } else {
                            throw new RuntimeException("无idCard无法进行解析");
                        }
                        if (paramObject.containsKey("name")) {
                            idname = paramObject.getString("name");
                            if (idname != null && !"".equals(idname)) {
                                is_IdName = true;
                            } else {
                                throw new RuntimeException("name为空，无法进行解析");
                            }
                        } else {
                            throw new RuntimeException("无name无法进行解析");
                        }
                        if (is_Riskid && is_T24org && is_IdNo && is_IdName) {
                            if (Global.ISREQUEST == 0) {
                                CreditReport_new.request_data(riskid, t24org, idno, idname);
                            } else {
                                CreditReport_new_bak.request_data(riskid, t24org, idno, idname);
                            }
                        }
                    }
                } else {
                    //json格式问题
                    throw new RuntimeException("不是有效的json格式");
                }
            }

        } catch (RuntimeException e) {
            e.printStackTrace();
            if ("xiaguohang_first_loan".equals(topic)) {
                String context = SendContextUtil.send_date(riskid, "0", e.getMessage());
                System.out.println("返回给引擎的数据：" + context);
                KafkaProducerUtil.sendSync("xib_credit_report", context);
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        } finally {
            ack.acknowledge();
        }
    }

}
