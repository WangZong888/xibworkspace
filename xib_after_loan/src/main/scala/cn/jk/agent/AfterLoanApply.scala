package cn.jk.agent

import cn.jk.pojo.Global
import cn.jk.util.{DateUtil, KafkaProducerUtil}
import com.alibaba.fastjson.{JSON, JSONObject}
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD

/**
  * Created by YiZe on 2019/6/5.
  */
object AfterLoanApply {


  def afterLoan(ssc: SparkContext, values: String): Unit = {
    val path = Global.HDFSURL
    val contractRDDs: RDD[String] = ssc.textFile(s"${path}contract/")
    //去掉头部
    //val contractRDD = contractRDDs.mapPartitionsWithIndex { (idx, iter) => if (idx == 0) iter.drop(1) else iter }
    val repay_dtlRDDs: RDD[String] = ssc.textFile(s"${path}repay_dtl/")
    //去掉头部
    //val repay_dtlRDD = repay_dtlRDDs.mapPartitionsWithIndex { (idx, iter) => if (idx == 0) iter.drop(1) else iter }
    val repay_resultRDDs: RDD[String] = ssc.textFile(s"${path}repay_result/")
    //去掉头部
    //val repay_resultRDD = repay_resultRDDs.mapPartitionsWithIndex { (idx, iter) => if (idx == 0) iter.drop(1) else iter }
    val accounting_resultRDDs = ssc.textFile(s"${path}out_acct_result/")
    //去掉头部
    //val accounting_resultRDD = accounting_resultRDDs.mapPartitionsWithIndex { (idx, iter) => if (idx == 0) iter.drop(1) else iter }

    /*    *
          * 贷后指标计算,存入mysql,kafka发消息
          *
          * */
    try {
      AfterLoanIndex.afterLoan(contractRDDs, repay_resultRDDs, values)
      println("贷后指标计算完成")
    } catch {
      case e: Exception =>
        val obj = new JSONObject()
        //val params = new JSONObject()
        var ip,eventId,risk_id = ""
        val str = JSON.parseObject(values)
        val json= JSON.parseObject(str.getString("params"))
        if (json.containsKey("ip")) ip = json.getString("ip")
        if (str.containsKey("eventId")) eventId = str.getString("eventId")
        if (json.containsKey("riskId")) risk_id = json.getString("riskId")
        obj.put("eventId", eventId)
//        obj.put("riskId", risk_id)
//        params.put("ip", ip)
        obj.put("message", "贷后指标计算异常")
        obj.put("status", "500")
//        obj.put("params",params)
        obj.put("params",json)
        println("贷后指标计算失败")
        KafkaProducerUtil.sendSync("xiaguohang_repeatloan_receive", obj.toJSONString)
       println("返回给引擎的json消息" + obj.toJSONString)
        e.printStackTrace()
    }

  }
}
