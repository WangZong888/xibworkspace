package com.jinke.tianyan.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import com.jinke.tianyan.util.DBUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jinke.tianyan.util.EncryptUtils;
import com.jinke.tianyan.util.StringUtil;

/**
 * Date                     Author         Version     Description
 * ---------------------------------------------------------------------------------
 * 2019年5月28日 上午10:06:38          wangyue        1.0         To create
 */
@Service
public class WLHistoryDataService {

    @Value("${xiahuohang.wenliurl}")
    private String wenliurl;

    @Autowired
    @Qualifier("primaryJdbcTemplate")
    private JdbcTemplate primaryJdbcTemplate;

    @Autowired
    @Qualifier("secondaryJdbcTemplate")
    private JdbcTemplate secondaryJdbcTemplate;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${xiahuohang.isrequest}")
    private String isRequest;

    @SuppressWarnings({"rawtypes", "unused"})
    @Autowired
    private KafkaTemplate kafkaTemplate;


    public JSONObject historyData(String channelId, String idOfCore, String creditNoKey) throws Exception {

        JSONObject jsonObject = new JSONObject();
        List<JSONObject> list = new ArrayList<>();
        Double sum = 0D;
        String message = "channelId=" + channelId + "&creditNoKey=" + creditNoKey + "&idOfCore=" + idOfCore + "&serviceID=qyx.queryHistorySales";

        if ("1".equals(isRequest)) {    //挡板
            for (int i = 0; i < 12; i++) {
                JSONObject json = new JSONObject();
                if (i < 9) {
                    if (i > 2) {
                        sum += 5000 * (i + 1);
                        json.put("bussinessDate", "20180" + (i + 1));
                        json.put("outAmount", getRandom());
                        list.add(json);
                    }
                } else {
                    sum += 5000 * (i + 1);
                    json.put("bussinessDate", "2018" + (i + 1));
                    json.put("outAmount", getRandom());
                    list.add(json);

                }
            }
            for (int i = 0; i < 6; i++) {
                JSONObject json = new JSONObject();
                sum += 5000 * (i + 1);
                json.put("bussinessDate", "20190" + (i + 1));
                json.put("outAmount", getRandom());
                list.add(json);
            }

            jsonObject.put("bussinessList", list);
            jsonObject.put("outAmount", sum);
            jsonObject.put("cooperationPeriod", 16);
            jsonObject.put("purchaseFrequency", 2);


        } else {
            //私钥/root/bigdata/cert/user-rsa.pfx
//            String fileContent = EncryptUtils.readBase64FromResource("/root/bigdata/cert/user-rsa.pfx");
            String fileContent = EncryptUtils.readBase64FromFile("cert/user-rsa.pfx");
            //公钥/root/bigdata/cert/public-rsa.cer
//            String cerContent = EncryptUtils.readBase64FromResource("/root/bigdata/cert/public-rsa.cer");
            String cerContent = EncryptUtils.readBase64FromFile("cert/public-rsa.cer");
            PrivateKey privateKey = EncryptUtils.readPrivateKey(fileContent, "123456", "PKCS12");

            Map<String, Object> oriRequestMap = new HashMap<String, Object>();
            oriRequestMap.put("channelId", channelId);
            oriRequestMap.put("creditNoKey", creditNoKey);
            oriRequestMap.put("idOfCore", idOfCore);
            oriRequestMap.put("serviceID", "qyx.queryHistorySales");

            String plainText = StringUtil.getOrderByLexicographic(oriRequestMap);
            String signedText = EncryptUtils.sign(plainText, "SHA256WithRSA", privateKey);
            message += "&signData=" + signedText;
            oriRequestMap.put("signData", signedText);
            //请求
            JSONObject result = restTemplate.postForObject(wenliurl, oriRequestMap, JSONObject.class);

            String resultSign = result.getString("signData");
            System.out.println("resultSign-------------------" + resultSign);
            if (resultSign != null) {

                //验签 ，拿示例报文为例，应用方如何验密
                //响应报文转map
                Map<String, Object> bodyMap = new ObjectMapper().readValue(result.toString(), new ObjectMapper().getTypeFactory().constructMapType(HashMap.class, String.class, Object.class));
                //取得签名
                String signedText1 = (String) bodyMap.remove("signData");
                //构造签名原文
                String plainText1 = StringUtil.getOrderByLexicographic(bodyMap);
                //公钥
                X509Certificate certificate = StringUtil.loadCertificate(cerContent);
                //验签
                boolean flag = StringUtil.verify(plainText1, signedText1, "SHA256WithRSA", certificate.getPublicKey());
                System.out.println("flag----------"+flag);
//                boolean flag = true;
                if (flag) {
                    System.out.println("验签成功");
                    if ("2000".equals(result.getString("code"))) {
                        if (result.getJSONObject("result") != null) {
                            jsonObject.putAll(result.getJSONObject("result"));
                        }
                    } else {
                        String resutl_value = "";
                        String msg = "";
                        if (result.containsKey("result")) {
                            resutl_value = result.getString("result");
                        }
                        if (result.containsKey("msg")) {
                            msg = result.getString("result");
                        }
                        if ("fail".equals(resutl_value) && msg.indexOf("查无数据") != -1) {
                            //文沥接口查询无数据，数据库指标存入-99
                            result.put("returnStatus", "1");
                            result.put("returnMsg", "查无数据");
                        } else {
                            result.put("returnStatus", "0");
                            result.put("returnMsg", "ERROR");
                        }

                        //kafkaTemplate.send("xiaguohang_order_apply", result.toJSONString());
                        //System.out.println("返回给引擎的消息：" + result.toJSONString());
                    }
                } else {
                    throw new Exception("验签失败");
                }
            }
        }
        jsonObject.put("channelId", channelId);
        jsonObject.put("idOfCore", idOfCore);
        jsonObject.put("creditNoKey", creditNoKey);


        String sql = "insert into calc_log (request_data,calc_result,type) VALUES(?,?,?)";
        secondaryJdbcTemplate.update(sql, message, jsonObject.toJSONString(), "4");
        return jsonObject;
    }

    public static Double getRandom() {

        int intFlag = (int) (Math.random() * 1000000);

        String flag = String.valueOf(intFlag);
        if (flag.length() != 6 && !flag.substring(0, 1).equals("9")) {
            intFlag = intFlag + 100000;
        }
        return (double) intFlag;
    }

}