package cn.jq.bigdata.deal;

import cn.jq.bigdata.client.JQRequesetHeader;
import cn.jq.bigdata.client.JQRequestBody;
import cn.jq.bigdata.client.JQRequestModel;
import cn.jq.bigdata.client.JQRquestBodyReq;
import cn.jq.bigdata.utils.*;
import org.apache.commons.lang.StringEscapeUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreditReport_new {

/*    public static void main(String[] args) {
        request_data("888888888888888888866666","CN0018014","350201198903077572","测试个人3000219836");
       // dealToXml();
    }*/
    public static void request_data(String riskid,String T24org,String idno,String idname) {
        InputStream in = null;
        try {
            URL url;
            //http://10.10.63.208:9080/cifs/ciisWebService
            url = new URL("http://10.10.55.110:18990/ESB-WS/InputProxy/Common02?wsdl");

            String reqMsg = dealToXml(T24org,idno,idname);
            HttpURLConnection httpConn = (HttpURLConnection)url.openConnection();
            httpConn.setDoOutput(true);
            httpConn.setDoInput(true);
            httpConn.setRequestMethod("POST");
            httpConn.setConnectTimeout(60000);
            httpConn.setRequestProperty("Content-Type","text/xml;charset=utf-8");
            httpConn.getOutputStream().write(reqMsg.getBytes("utf-8"));
            httpConn.getOutputStream().flush();
            httpConn.getOutputStream().close();

            httpConn.connect();
            in = httpConn.getInputStream();
            if(in ==null){
                System.out.println("接收到的消息为空");
                throw new RuntimeException("人行征信返回内容为空");
            }
            StringBuffer sbf = new StringBuffer();
            byte[] buffer = new byte[2048];
            int length = 0;
            //while ((length = in.read(buffer,0,buffer.length)) != -1){
            while ((length = in.read(buffer)) >= 0){
                sbf.append(new String(buffer,0,length,"utf-8"));
            }
            String respMsg = StringEscapeUtils.unescapeXml(sbf.toString());
            System.out.println("收到服务器返回的报文内容："+respMsg);
            //从服务器返回的字符串中截取人行征信的内容
            final String result = getResultContext(respMsg);
            if(result!=null){
                String isSuccess = getDealStatus(result);
                String queryStatus = getQueryStatus(result);
                if("000000000".equals(isSuccess) &&  "".equals(queryStatus)){
                    //交易成功，往kafka发送消息
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String sendKafka = result.replaceAll("</Service>","<CreditRiskId>"+riskid+"</CreditRiskId></Service>").replaceAll("<!\\[CDATA\\[","").replaceAll("]]>","");
                            System.out.println("发送给kafka的消息："+sendKafka);
                            KafkaProducerUtil.sendSync(Global.TOPIC, sendKafka);
                        }
                    });
                    thread.start();
                    System.out.println("OK");

                }else{
                    /* 银行端查询人行征信信息错误，直接把结果返回给引擎，不发送给kafka
                    * 没有获取到数据，指标默认缺失，存储-99
                    * */
                    PreparedStatement ps = null;
                    Connection conn = null;
                    try{
                        conn = DBUtils.getCon();
                        conn.setAutoCommit(false);
                        String default_value = "-99";
                        String sql = "REPLACE INTO person_credit_detail (risk_id,query_time,name,cert_id,total_ucla,total_scla,used_share_ratio," +
                                "max_used_share_ratio,amount_mean,finance_org_count,unsettled_bal_mean,unsetteled_ratio,inquiry_num_3m,rh_overdue_times_his," +
                                "max_overdue_num,overdue_state,illegal_state,loan_status_error,card_status_error,max_creditamount,loan_repayamount)" +
                                " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
                        ps = conn.prepareStatement(sql);
                        ps.setString(1, riskid);
                        ps.setString(2, Global.getQueryDate());
                        ps.setString(3, idname);
                        ps.setString(4, idno);
                        ps.setString(5, default_value);
                        ps.setString(6, default_value);
                        ps.setString(7, default_value);
                        ps.setString(8, default_value);
                        ps.setString(9, default_value);
                        ps.setString(10, default_value);
                        ps.setString(11, default_value);
                        ps.setString(12, default_value);
                        ps.setString(13, default_value);
                        ps.setString(14, default_value);
                        ps.setString(15, default_value);
                        ps.setInt(16, 0);
                        ps.setInt(17, 0);
                        ps.setInt(18, 0);
                        ps.setInt(19, 0);
                        ps.setString(20, default_value);
                        ps.setString(21, default_value);
                        ps.executeUpdate();
                        conn.commit();
                    }catch (Exception e){
                        conn.rollback();
                        e.printStackTrace();
                    }finally {
                        if(ps!=null) ps.close();
                        if(conn!=null) DBUtils.close(conn);
                    }
                    String context =  SendContextUtil.send_date(riskid,"1","SUCCESS");
                    System.out.println("返回给引擎的数据："+context);
                    KafkaProducerUtil.sendSync("xib_credit_report", context);
                }
            }else {
                System.out.println("no match data");
            }
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
    }


    public static String dealToXml(String T24org,String idno,String idname){
        //封装jqRequestModel对象
        JQRequestModel jqmode = new JQRequestModel();
        JQRequestBody jqbody = new JQRequestBody();
        JQRequesetHeader jqheader = new JQRequesetHeader();
        JQRquestBodyReq bodyReq = new JQRquestBodyReq();

        final String uuid = UUID.randomUUID().toString().toLowerCase().replaceAll("-","");
        System.out.println("uuid-----------------"+uuid);
        jqheader.setServiceCode("CDMT000540");//固定
        jqheader.setChannelId("S02");//固定
        jqheader.setExternalReference(uuid);
        jqheader.setOriginalChannelId("S02");
        jqheader.setOriginalReference(uuid);
        jqheader.setRequestTime(Global.getRequest_Time());
        jqheader.setVersion("1.0");
        jqheader.setRequestType("0");
        jqheader.setEncrypt("0");
        jqheader.setTradeDate(Global.getToday());
        jqheader.setRequestBranchCode("000000000");
        jqheader.setRequestOperatorId("");
        jqheader.setRequestOperatorType("1");
        jqheader.setTermType("00000");
        jqheader.setTermNo("0000000000");
        jqmode.setHeader(jqheader);

        bodyReq.setBranchId(T24org);//取T24机构编码
        bodyReq.setUserId("001");//如果是线上贷款查询的话，userid为空
        bodyReq.setGlobalType("0");
        bodyReq.setGlobalId(idno);//解析身份证号
        bodyReq.setCustomerId(idname);//解析姓名
        bodyReq.setQueryReason("02");//贷后管理 - “01”；贷款审批 - “02”
        bodyReq.setVerType("30");//银行版 - “30”
        bodyReq.setIdauthFlag("0");
        bodyReq.setImageId("");
        bodyReq.setEffectiveDay("30"); //生产环境改成1
        bodyReq.setOnline_loan("");
        bodyReq.setNote("");
        jqbody.setBodyReq(bodyReq);
        jqmode.setBody(jqbody);
        String xml = JaXmlBeanUtil.parseBeanToXml(JQRequestModel.class,jqmode);
        String send_header = "<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:def=\"http://DefaultNamespace\"><soapenv:Header/><soapenv:Body><def:esbRequest soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\"><in0 xsi:type=\"soapenc:string\" xmlns:soapenc=\"http://schemas.xmlsoap.org/soap/encoding/\"><![CDATA[ ";
        String send_tail = "]]></in0></def:esbRequest></soapenv:Body></soapenv:Envelope>";
        String sendContext = send_header+xml+send_tail;
        System.out.println("请求人行征信的报文："+sendContext);
        return  sendContext;
    }


    public static String getResultContext(String respMsg ){
        String result = "";
        Pattern p1 = Pattern.compile("<esbRequestReturn>(.*)</esbRequestReturn>");
        Matcher m1 = p1.matcher(respMsg);
        while(m1.find()){
            result = m1.group(1);
            System.out.println("服务器返回的人行征信的主要内容："+result);
        }
        return result;
    }

    public static String getDealStatus(String result ){
        String isSuccess = "";

        Pattern p2 = Pattern.compile("<ReturnCode>(.*)</ReturnCode>");
        Matcher m2 = p2.matcher(result);
        while(m2.find()){
            isSuccess = m2.group(1);
            System.out.println("人行征信查询的状态是："+isSuccess);
        }
        return isSuccess;
    }

    public static String getQueryStatus(String result ){
        String isSuccess = "";

        Pattern p2 = Pattern.compile("<QueryResultCue>(.*)</QueryResultCue>");
        Matcher m2 = p2.matcher(result);
        while(m2.find()){
            isSuccess = m2.group(1);
            System.out.println("人行征信查询结果的状态是："+isSuccess);
        }
        return isSuccess;
    }
}
