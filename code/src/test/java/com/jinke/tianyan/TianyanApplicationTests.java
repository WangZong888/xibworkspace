package com.jinke.tianyan;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jinke.tianyan.util.EncryptUtils;
import com.jinke.tianyan.util.StringUtil;
import org.junit.Test;
import org.junit.runner.RunWith;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jinke.tianyan.service.WLHistoryDataService.getRandom;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TianyanApplicationTests {

  /*  @Value("${xiahuohang.wenliurl}")
    private String wenliurl;

    @Autowired
    private RestTemplate restTemplate;


    @Test
    public void historyData() throws Exception {

        JSONObject jsonObject = new JSONObject();
        List<JSONObject> list = new ArrayList<>();
        Double sum = 0D;

        if ("1".equals("0")) {    //挡板
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
            //私钥
            String fileContent = EncryptUtils.readBase64FromResource("cert/user-rsa.pfx");
            //公钥
            String cerContent = EncryptUtils.readBase64FromResource("cert/public-rsa.cer");

            PrivateKey privateKey = EncryptUtils.readPrivateKey(fileContent, "123456", "PKCS12");
            Map<String, Object> oriRequestMap = new HashMap<String, Object>();
            oriRequestMap.put("channelId", "welink");
            oriRequestMap.put("creditNoKey", "c1144075985184889437");
            oriRequestMap.put("idOfCore", "PWELINK");
            oriRequestMap.put("serviceID", "qyx.queryHistorySales");
            String plainText = StringUtil.getOrderByLexicographic(oriRequestMap);
            String signedText = EncryptUtils.sign(plainText, "SHA256WithRSA", privateKey);
            //message += "&signData=" + signedText;
            oriRequestMap.put("signData", signedText);

            //请求
            JSONObject result = restTemplate.postForObject(wenliurl, oriRequestMap, JSONObject.class);
            System.out.println(result.toJSONString());
            String resultSign = result.getString("signData");
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
                if (flag) {
                    System.out.println("验签成功");
                    if (result.getJSONObject("result") != null) {
                        System.out.println(result.getJSONObject("result"));
                        jsonObject.putAll(result.getJSONObject("result"));
                    }
                } else {

                    throw new Exception("验签失败");
                }
            }
        }
      *//*  jsonObject.put("channelId", channelId);
        jsonObject.put("idOfCore", idOfCore);
        jsonObject.put("creditNoKey", creditNoKey);*//*


        //String sql = "insert into calc_log (request_data,calc_result,type) VALUES(?,?,?)";
        //secondaryJdbcTemplate.update(sql, message, jsonObject.toJSONString(), "4");
        System.out.println(jsonObject.toJSONString());
        //return jsonObject;
    }*/


}
