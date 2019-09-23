package cn.jq.bigdata.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class SendContextUtil {

    /*
    --returnStatus 1-成功   0-失败
    {"riskId":"880001001","returnStatus":"1","returnMsg":"SUCCESS","taskType":"rhcredit"}
    {"riskId":"880001001","returnStatus":"0","returnMsg":"ErrorMsg","taskType":"rhcredit"}
     */
    public static String send_date(String riskid,String returnStatus,String returnMsg){
        StringBuffer sb = new StringBuffer();
        sb.append("{");
        sb.append("\"riskId\":\"").append(riskid).append("\",");
        sb.append("\"returnStatus\":\"").append(returnStatus).append("\",");
        sb.append("\"returnMsg\":\"").append(returnMsg).append("\",");
        sb.append("\"taskType\":\"").append("rhcredit");
        sb.append("\"}");
        JSONObject json = JSON.parseObject(sb.toString());
        return json.toString();
    }
}
