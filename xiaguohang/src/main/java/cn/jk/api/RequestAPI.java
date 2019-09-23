package cn.jk.api;

import cn.jk.util.EncryptUtils;
import cn.jk.util.StringUtil;
import cn.jk.util.ToMysql;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.PrivateKey;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by YiZe on 2019/5/19.
 *
 * @// TODO: 2019/5/20 使用申请kafka推送信息，存储到mysql 表后，RequestApi读取mysql 获取拼接URL请求贷后风控预警，获取响应JSON
 */
public class RequestAPI {
    //public void request(String channelId, String idOfCore, String creditNokey, String startTime, String endTine) {

    public void request() throws Exception{
        //私钥
        String fileContent = EncryptUtils.readBase64FromResource("D:\\workspacenew\\xiaguohang\\src\\main\\resources\\user-rsa.pfx");
        //公钥
        String cerContent = EncryptUtils.readBase64FromResource("D:\\workspacenew\\xiaguohang\\src\\main\\resources\\public-rsa.cer");
        PrivateKey privateKey = EncryptUtils.readPrivateKey(fileContent, "123456", "PKCS12");


        String body = "{\n" +
                "    \"idOfCore\": \"PWELINK\",\n" +
                "    \"serviceID\": \"qyx.queryLoanWarning\",\n" +
                "    \"channelId\": \"welink\",\n" +
                "    \"startTime\": \"2019-01-01\",\n" +
                "    \"endTime\": \"2019-07-01\",\n" +
                "    \"creditNoKey\": \"c1144075985184889437\"\n" +
                "}";
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

        InputStream in = null;
        try {
            URL url;
            url = new URL("http://www-devhf.qiyexi.com/xibapi/route");
            HttpURLConnection httpConn = (HttpURLConnection)url.openConnection();
            httpConn.setDoOutput(true);
            httpConn.setDoInput(true);
            httpConn.setRequestMethod("POST");
            httpConn.setConnectTimeout(60000);
            httpConn.setRequestProperty("Content-Type","text/xml;charset=utf-8");
            httpConn.getOutputStream().write(json.getBytes("utf-8"));
            httpConn.getOutputStream().flush();
            httpConn.getOutputStream().close();

            httpConn.connect();
            in = httpConn.getInputStream();
            if(in ==null){
                System.out.println("接收到的消息为空");
                throw new RuntimeException("返回内容为空");
            }
            StringBuffer sbf = new StringBuffer();
            byte[] buffer = new byte[2048];
            int length = 0;
            while ((length = in.read(buffer,0,buffer.length)) != -1){
                sbf.append(new String(buffer,0,length,"utf-8"));
            }
            System.out.println(sbf.toString());
            //插入mysql
            ToMysql.dhAlarm(sbf.toString());
        } catch (Exception e) {
            e.printStackTrace();

        }finally {
            if(in!=null){
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        /*// 请求接口地址
        String url = "https://www-devhf.qiyexi.com/xibapi/route";
        HttpClient httpclient = null;
        PostMethod post = null;
        try {
            //创建连接
            httpclient = new HttpClient();
            post = new PostMethod(url);
            // 设置编码方式
            post.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
            // 添加参数
       *//*     post.addParameter("channelId", channelId);
            post.addParameter("idOfCore", idOfCore);
            post.addParameter("creditNokey", creditNokey);
            post.addParameter("startTime", startTime);
            post.addParameter("endTine", endTine);*//*
            post.addParameter("channelId", "welink");
            post.addParameter("idOfCore", "PWELINK");
            post.addParameter("creditNoKey", "c1144075985184889437");
            post.addParameter("startTime", "2019-01-01");
            post.addParameter("endTime", "2019-07-01");
            post.addParameter("serviceID", "qyx.queryLoanWarning");
            //私钥
            String fileContent = EncryptUtils.readBase64FromResource("C:\\Users\\Administrator\\Desktop\\xiaguohang\\src\\main\\resources\\user-rsa.pfx");
            //公钥
            String cerContent = EncryptUtils.readBase64FromResource("C:\\Users\\Administrator\\Desktop\\xiaguohang\\src\\main\\resources\\public-rsa.cer");

            PrivateKey privateKey = EncryptUtils.readPrivateKey(fileContent, "123456", "PKCS12");
            Map<String, Object> oriRequestMap = new HashMap<String, Object>();
            oriRequestMap.put("channelId", "welink");
            oriRequestMap.put("idOfCore", "PWELINK");
            oriRequestMap.put("creditNoKey", "c1144075985184889437");
            oriRequestMap.put("startTime", "2019-01-01");
            oriRequestMap.put("endTime", "2019-07-01");
            oriRequestMap.put("serviceID", "qyx.queryLoanWarning");

            String plainText = StringUtil.getOrderByLexicographic(oriRequestMap);
            String signedText = EncryptUtils.sign(plainText, "SHA256WithRSA", privateKey);
            System.out.println("签名信息------------" + signedText);
            post.addParameter("signData", signedText);
            // 执行请求
            httpclient.executeMethod(post);
            // 接口返回信息
            String info = new String(post.getResponseBody(), "UTF-8");
            System.out.println("接口返回信息-------------"+info);
            //TODO 插入mysql
            //ToMysql.dhAlarm(info);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭连接，释放资源
            post.releaseConnection();
            ((SimpleHttpConnectionManager) httpclient.getHttpConnectionManager()).shutdown();
        }*/
    }

    public static void main(String[] args) throws Exception{
        new RequestAPI().request();
    }
}
