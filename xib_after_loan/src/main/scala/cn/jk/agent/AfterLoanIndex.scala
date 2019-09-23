package cn.jk.agent

import cn.jk.pojo.Global
import cn.jk.rdds.{Cal_OveDayAndCount, RepayResRDDS, contractRDDS, getCreditApplyTime}
import cn.jk.util.{DateFormat, KafkaProducerUtil, Save_To_Mysql, Util}
import com.alibaba.fastjson.{JSON, JSONObject}
import org.apache.spark.rdd.RDD

import scala.collection.mutable
import scala.util.control.Breaks.{break, breakable}

/**
  * Created by YiZe on 2019/6/6.
  *
  * @todo 指标项计算
  */
object AfterLoanIndex {
  def afterLoan(contractRDD: RDD[String], repay_resultRDD: RDD[String], values: String): Unit = {
    var map: mutable.Map[String, Object] = mutable.Map[String, Object]()
    val obj = new JSONObject()
    val params = new JSONObject()
    var credit_apply_no, platform_id, risk_id, cert_id, credit_id, sell_credit_id, apply_date, ip, eventId, supplier_name, supplier_code, credit_no_key = ""
    if (values != null && !"".equals(values) && values.length > 0) {
      val str = JSON.parseObject(values)
      val json: JSONObject = JSON.parseObject(str.getString("params"))
      if (json.containsKey("creditApplyNo")) credit_apply_no = json.getString("creditApplyNo")
      if (json.containsKey("riskId")) risk_id = json.getString("riskId")
      if (json.containsKey("certId")) cert_id = json.getString("certId")
      if (json.containsKey("creditId")) credit_id = json.getString("creditId")
      if (json.containsKey("sellCreditId")) sell_credit_id = json.getString("sellCreditId")
      if (json.containsKey("applyDate")) apply_date = json.getString("applyDate")
      if (json.containsKey("ip")) ip = json.getString("ip")
      if (str.containsKey("eventId")) eventId = str.getString("eventId")
      if (json.containsKey("supplierName")) supplier_name = json.getString("supplierName")
      if (json.containsKey("supplierCode")) supplier_code = json.getString("supplierCode")
      if (json.containsKey("creditNoKey")) credit_no_key = json.getString("creditNoKey")
      if (json.containsKey("platformId")) platform_id = json.getString("platformId")
      obj.put("params", json)
    }
    obj.put("eventId", eventId)

    println("cert_id--" + cert_id)
    println("credit_apply_no--" + credit_apply_no)
    println("risk_id--" + risk_id)
    println("creditNoKey--" + credit_no_key)
    //合同过滤。过滤出这个身份证对应在2年之内的合同
    //给衍生指标默认值  diffDay  refinance  maxOveDay  oveNum oveDays loan_Avg
    // && !"".equals(x._9) && !"-".equals(x._9) && !"-".equals(x._7) && !"-".equals(x._8)
    var maxOveDay,oveNum, oveDays, loan_Avg = Global.INVAILD_NUM.toString
    var diffDay,refinance = 0
    val contractRes = contractRDDS.contreactRDD(contractRDD).filter(x => {
      x._4.equals(cert_id) && DateFormat.getChangeDate(apply_date, -24) <= x._7
    })
    if (!contractRes.isEmpty()) {
      breakable{
        contractRes.collect().foreach(f=>{
          val minValueDate = f._8
          val maxFinshDate = f._9
          if("".equals(minValueDate) || "".equals(maxFinshDate) ){
            diffDay = Global.INVAILD_NUM
            break
          }else{
            val tempd = DateFormat.datediff(minValueDate, maxFinshDate).toInt
            diffDay += tempd
          }
        })
      }
      //两年内历史在贷天数
      println("两年内历史在贷天数:" + diffDay)


      //续贷次数
      refinance = (contractRes.filter(f=>{"06".equals(f._10)}).count() - 1).toInt
      if(refinance<0){
        refinance = 0
      }
      println("近两年续贷次数:" + refinance)


      //近两年最长逾期天数
      val set: Set[String] = contractRes.map(x => x._1).collect().toSet
      val repayRes: RDD[(String, String, String, String, String, String, String, String, String, Long)] = RepayResRDDS.repayResRDD(repay_resultRDD).filter(x => set.contains(x._1))
      if(!repayRes.isEmpty()){
        maxOveDay = repayRes.map(x => x._10).max().toString
      }
      println("近两年最长逾期天数:" + maxOveDay)


      /*
         计算近两年逾期次数  近两年累计逾期天数
       */
      val ove_data = Cal_OveDayAndCount.cal_oveday(contractRes,repayRes)
       oveNum = ove_data.split("\\|")(0)
       oveDays = ove_data.split("\\|")(1)
      println("近两年逾期次数:" + oveNum)
      println("近两年累计逾期天数:" + oveDays)
      //在贷流水比值计算
      //1、获取最早的申请时间
      val firstAvg: Double = getCreditApplyTime.getFirstAvg_new1(supplier_code, risk_id)
      println("firstAvg:" + firstAvg)
      //获取节点后的卖方业务数据
      val endAvg: Double = getCreditApplyTime.getEndAvg_new1(supplier_code, risk_id)
      println("endAvg:" + endAvg)
      //在贷流水比值为
      if (firstAvg != 0.0 && -99 != endAvg  && -99 !=firstAvg) {
        loan_Avg = (endAvg / firstAvg).formatted("%.2f")
      }
    }
    println("在贷流水比值为:"+ loan_Avg)
    map += ("credit_apply_no" -> credit_apply_no)
    map += ("credit_risk_id" -> risk_id)
    map += ("cert_id" -> cert_id)
    map += ("credit_Id" -> credit_id)
    map += ("loan_days_his" -> diffDay.toString)
    map += ("reborrow_times" -> refinance.toString)
    map += ("max_overduedays_his" -> maxOveDay)
    map += ("overdue_times_his" -> oveNum)
    map += ("accu_overdue_days_his" -> oveDays)
    map += ("cf_loan_apply_ratio" -> loan_Avg)
    Save_To_Mysql.save_data(map, "contract_repayment_detail")

    obj.put("message", "SUCCESS")
    obj.put("status", "200")
    //发送消息到kafka
    //println("返回给引擎的消息："+obj.toJSONString)
    KafkaProducerUtil.sendSync("xiaguohang_repeatloan_receive", obj.toJSONString)
    println("返回给引擎的json消息" + obj.toJSONString)

  }

}
