package cn.jk.agent

import cn.jk.pojo.Global
import org.apache.spark.rdd.RDD

import scala.collection.mutable

/**
  * Created by YiZe on 2019/6/5.
  */
object SaveRepayDtlToMysql {
  def repayDtlRDD(repayDtl: RDD[String]): RDD[mutable.Map[String, Object]] = {
    var map: mutable.Map[String, Object] = mutable.Map[String, Object]()
    val res: RDD[mutable.Map[String, Object]] = repayDtl.map(line => {
      val lines: Array[String] = line.split(Global.regex_rule)
      val creditApplyNo = lines(0)
      val creditRiskId = lines(1)
      val orderRiskId = lines(2)
      val certId = lines(3)
      val creditId = lines(4)
      val sellCreditId = lines(5)
      val rpterm = lines(6)
      val actualPaydate = lines(7)
      val transReqNo = lines(8)
      val totalAmount = lines(9)
      val capital = lines(10)
      val aint = lines(11)
      val oint = lines(12)
      val cint = lines(13)
      map += ("credit_apply_no" -> creditApplyNo)
      map += ("credit_risk_id" -> creditRiskId)
      map += ("order_risk_id" -> orderRiskId)
      map += ("cert_id" -> certId)
      map += ("credit_id" -> creditId)
      map += ("sell_credit_id" -> sellCreditId)
      map += ("rpterm" -> rpterm)
      map += ("actual_paydate" -> actualPaydate)
      map += ("trans_req_no" -> transReqNo)
      map += ("total_amount" -> totalAmount)
      map += ("capital" -> capital)
      map += ("aint" -> aint)
      map += ("oint" -> oint)
      map += ("cint" -> cint)
      map
    })
    res
  }
}
