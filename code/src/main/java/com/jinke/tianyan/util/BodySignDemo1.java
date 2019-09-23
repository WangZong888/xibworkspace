/*
package com.jinke.tianyan.util;

import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

*/
/**
 * 访问开放平台服务报文加签，验签
 *
 * 加签用私钥
 *
 * 验签用公钥
 *
 *//*

public class BodySignDemo1 {


    public static void main(String[] args) throws Exception {

        //私钥
        String fileContent = EncryptUtils.readBase64FromResource("cert/user-rsa.pfx");
        //公钥
        String cerContent = EncryptUtils.readBase64FromResource("cert/public-rsa.cer");
        PrivateKey privateKey = EncryptUtils.readPrivateKey(fileContent, "123456", "PKCS12");

        //要加密的串
        */
/**
         app_id=2014072300007148
         method=alipay.mobile.public.menu.add
         charset=GBK
         sign_type=RSA2
         timestamp=2014-07-24 03:07:50
         biz_content={"button":[{"actionParam":"ZFB_HFCZ","actionType":"out","name":"话费充值"},{"name":"查询","subButton":[{"actionParam":"ZFB_YECX","actionType":"out","name":"余额查询"},{"actionParam":"ZFB_LLCX","actionType":"out","name":"流量查询"},{"actionParam":"ZFB_HFCX","actionType":"out","name":"话费查询"}]},{"actionParam":"http://m.alipay.com","actionType":"link","name":"最新优惠"}]}
         version=1.0
         *//*

        String body = "{\"app_id\":\"2014072300007148\",\"method\":\"alipay.mobile.public.menu.add\",\"charset\":\"GBK\",\"sign_type\":\"RSA2\",\"timestamp\":\"2014-07-24 03:07:50\",\"biz_content\":{\"button\":[{\"actionParam\":\"ZFB_HFCZ\",\"actionType\":\"out\",\"name\":\"话费充值\"},{\"name\":\"查询\",\"subButton\":[{\"actionParam\":\"ZFB_YECX\",\"actionType\":\"out\",\"name\":\"余额查询\"},{\"actionParam\":\"ZFB_LLCX\",\"actionType\":\"out\",\"name\":\"流量查询\"},{\"actionParam\":\"ZFB_HFCX\",\"actionType\":\"out\",\"name\":\"话费查询\"}]},{\"actionParam\":\"http://m.alipay.com\",\"actionType\":\"link\",\"name\":\"最新优惠\"}]},\"version\":\"1.0.0\"}}";
        //将要加密串转为 Map
        Map<String, Object> oriRequestMap = new ObjectMapper().readValue(body, new ObjectMapper().getTypeFactory().constructMapType(HashMap.class, String.class, Object.class));
        //按字典序排序并按 key=value 拼接成待加密串, 进行Base64编码,转字符为大写, 构造签名原文
        String plainText = StringUtil.getOrderByLexicographic(oriRequestMap);
        //使用私钥加密密文
        String signedText = EncryptUtils.sign(plainText, "SHA256WithRSA", privateKey);
        //signData字段放入Map
        oriRequestMap.put("signData",signedText);
        String json = new ObjectMapper().writeValueAsString(oriRequestMap);
        System.out.println("签名json串:"+json);


        //验签 ，拿示例报文为例，应用方如何验密
        //响应报文转map
        Map<String, Object> bodyMap = new ObjectMapper().readValue(json, new ObjectMapper().getTypeFactory().constructMapType(HashMap.class, String.class, Object.class));
        //取得签名
        String signedText1 = (String) bodyMap.remove("signData");
        //构造签名原文
        String plainText1 = StringUtil.getOrderByLexicographic(bodyMap);
        //公钥
        X509Certificate certificate = StringUtil.loadCertificate(cerContent);
        //验签
        boolean flag = StringUtil.verify(plainText1,signedText1,"SHA256WithRSA",certificate.getPublicKey());
        if(flag){
            System.out.println("验签成功");
        }else{
            System.out.println("验签失败");
        }


    }

    public String setRetExpectIncomeToNum(String retExpectIncome) {
        if (retExpectIncome.contains("~")) {
            return retExpectIncome.substring(retExpectIncome.lastIndexOf("~") + 1);
        }
        return retExpectIncome;
    }

//    public List<String> compareSort(List<String> version) {
//        version.sort((o1, o2) -> {
//            String[] ver1 = o1.split("\\.", -1);
//            String[] ver2 = o2.split("\\.", -1);
//            int length = Math.max(ver1.length, ver2.length);
//            for (int i = 0; i < length; i++) {
//                int v1 = i < ver1.length ? Integer.parseInt(ver1[i]) : 0;
//                int v2 = i < ver2.length ? Integer.parseInt(ver1[i]) : 0;
//                if (v1 < v2) {
//                    return -1;
//                }
//                if (v1 > v2) {
//                    return 1;
//                }
//            }
//            return 0;
//        });
//        return version;
//    }

}
*/
