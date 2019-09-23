package cn.bigdata.rhdetailindex.utils

import com.alibaba.fastjson.{JSON, JSONObject}

object SendContext {

  def send_data(apply_no_xib:String,
                risk_id:String,
                request_biz_id:String,
                returnStatus:String,
                returnMsg:String):String = {
    val sb: StringBuffer = new StringBuffer
    sb.append("{")
    sb.append("\"riskId\":\"").append(risk_id).append("\",")
    sb.append("\"returnStatus\":\"").append(returnStatus).append("\",")
    sb.append("\"returnMsg\":\"").append(returnMsg).append("\",")
    sb.append("\"taskType\":\"").append("rhcredit")
    sb.append("\"}")
    val json: JSONObject = JSON.parseObject(sb.toString)
    json.toString
  }
}
