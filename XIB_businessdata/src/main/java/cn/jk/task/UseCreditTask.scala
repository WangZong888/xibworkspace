package cn.jk.task

import cn.jk.util.{DateFormat, KafkaProducerUtil, Save_To_Mysql}
import com.alibaba.fastjson.{JSON, JSONArray, JSONObject}
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.spark.rdd.RDD

import scala.collection.mutable

/**
  * Created by YiZe on 2019/5/30.
  *
  * @todo 用信申请
  */
object UseCreditTask {
  def doUseCredit(topic: String, value: String): Unit = {
    println(value + "\001" + topic)
    println("用信申请：开始往xib_credit_apply_info中发送数据")
    KafkaProducerUtil.sendSync("xib_credit_apply_info", value + "\001" + topic)
    var platform_id, platform_name, credit_risk_id, order_apply_no_xib, order_apply_no, apply_amt,
    fiduciary_account, opening_bank_name, bank_account, supplier_name, supplier_code, applyterm, callback_url,
    bill_no, order_type, order_date, order_end_date, amount, loanpurpose, riskId = ""
    println("用信申请：结束往xib_credit_apply_info中发送数据")
    //用信申请
    if (null != topic && topic.equals("xiaguohang")) {
      val jsons: JSONObject = JSON.parseObject(value)
      if (jsons.containsKey("params")) {
        val params: String = jsons.getString("params")
        val json: JSONObject = JSON.parseObject(params)
        if (json.containsKey("platformId")) platform_id = json.getString("platformId")
        if (json.containsKey("riskId")) riskId = json.getString("riskId")
        if (json.containsKey("platformName")) platform_name = json.getString("platformName")
        if (json.containsKey("creditRiskId")) credit_risk_id = json.getString("creditRiskId")
        if (json.containsKey("orderApplyNoXib")) order_apply_no_xib = json.getString("orderApplyNoXib")
        if (json.containsKey("orderApplyNo")) order_apply_no = json.getString("orderApplyNo")
        if (json.containsKey("applyAmt")) apply_amt = json.getString("applyAmt")
        if (json.containsKey("fiduciaryAccount")) fiduciary_account = json.getString("fiduciaryAccount")
        if (json.containsKey("openingBankName")) opening_bank_name = json.getString("openingBankName")
        if (json.containsKey("bankAccount")) bank_account = json.getString("bankAccount")
        if (json.containsKey("supplierName")) supplier_name = json.getString("supplierName")
        if (json.containsKey("supplierCode")) supplier_code = json.getString("supplierCode")
        if (json.containsKey("applyterm")) applyterm = json.getString("applyterm")
        if (json.containsKey("loanpurpose")) loanpurpose = json.getString("loanpurpose")
        if (json.containsKey("callbackUrl")) callback_url = json.getString("callbackUrl")

        if (json.containsKey("billList")) {
          val noArr: JSONArray = JSON.parse(json.getString("billList")).asInstanceOf[JSONArray]
          import scala.collection.JavaConversions._
          noArr.map(line => {
            var map: mutable.Map[String, Object] = mutable.Map[String, Object]()

            val jSONObject = JSON.parseObject(line.toString)
            if (jSONObject.containsKey("billNo")) bill_no = jSONObject.getString("billNo")
            if (jSONObject.containsKey("orderType")) order_type = jSONObject.getString("orderType")
            if (jSONObject.containsKey("orderDate")) order_date = jSONObject.getString("orderDate")
            if (jSONObject.containsKey("orderEndDate")) order_end_date = jSONObject.getString("orderEndDate")
            if (jSONObject.containsKey("amount")) amount = jSONObject.getString("amount")

            map += ("platform_id" -> platform_id)
            map += ("risk_id" -> riskId)
            map += ("platform_name" -> platform_name)
            map += ("credit_risk_id" -> credit_risk_id)
            map += ("order_apply_no_xib" -> order_apply_no_xib)
            map += ("order_apply_no" -> order_apply_no)
            map += ("apply_amt" -> apply_amt.toDouble.formatted("%.2f"))
            map += ("fiduciary_account" -> fiduciary_account)
            map += ("opening_bank_name" -> opening_bank_name)
            map += ("bank_account" -> bank_account)
            map += ("supplier_name" -> supplier_name)
            map += ("supplier_code" -> supplier_code)
            map += ("applyterm" -> applyterm)
            map += ("loanpurpose" -> loanpurpose)
            map += ("callback_url" -> callback_url)
            map += ("bill_no" -> bill_no)
            map += ("order_type" -> order_type)
            map += ("order_date" -> DateFormat.format(order_date))
            map += ("order_end_date" -> DateFormat.format(order_end_date))
            map += ("amount" -> amount)

            try{
              Save_To_Mysql.save_data(map, "use_credit_apply")
            }catch {
              case e:Exception=>println("存储use_credit_apply失败");e.printStackTrace()
            }
          })
        }
      }
    }


  }

}
