package cn.jk.agent

import cn.jk.pojo.Global
import cn.jk.util.DateFormat
import org.apache.spark.rdd.RDD

import scala.collection.mutable

/**
  * Created by YiZe on 2019/6/6.
  *
  * @todo 还款计划反馈
  */
object SaveRepayResToMysql {
  def repayResRDD(repayRes: RDD[String]): RDD[mutable.Map[String, Object]] = {
    var map: mutable.Map[String, Object] = mutable.Map[String, Object]()
    val res: RDD[mutable.Map[String, Object]] = repayRes.map(line => {
      val lines: Array[String] = line.split(Global.regex_rule)
      val creditApplyNo = lines(0)
      val creditRiskId = lines(1)
      val orderRiskId = lines(2)
      val certId = lines(3)
      val creditId = lines(4)
      val sellCreditId = lines(5)
      val contractId = lines(6)
      val paymentAccno = lines(7)
      val rpterm = lines(8)
      val payDate = lines(9)
      val totalAmount = lines(10)
      val valueDate = lines(11)
      val expiryDate = lines(12)
      val rate = lines(13)
      val payCapital = lines(14)
      val actualCapital = lines(15)
      val payAint = lines(16)
      val actualAint = lines(17)
      val payOint = lines(18)
      val actualOint = lines(19)
      val payCint = lines(20)
      val actualCint = lines(21)
      val actualPaydate = lines(22)
      val finishDate = lines(23)
      val status = lines(24)
      val ove_day = DateFormat.datediff(lines(9),lines(23)).toString

      map += ("credit_apply_no" -> creditApplyNo)
      map += ("credit_risk_id" -> creditRiskId)
      map += ("order_risk_id" -> orderRiskId)
      map += ("cert_id" -> certId)
      map += ("credit_id" -> creditId)
      map += ("sell_credit_id" -> sellCreditId)
      map += ("contract_id" -> contractId)
      map += ("payment_accno" -> paymentAccno)
      map += ("rpterm" -> rpterm)
      map += ("pay_date" -> payDate)
      map += ("total_amount" -> totalAmount)
      map += ("value_date" -> valueDate)
      map += ("expiry_date" -> expiryDate)
      map += ("rate" -> rate)
      map += ("pay_capital" -> payCapital)
      map += ("actual_capital" -> actualCapital)
      map += ("pay_aint" -> payAint)
      map += ("actual_aint" -> actualAint)
      map += ("pay_oint" -> payOint)
      map += ("actual_oint" -> actualOint)
      map += ("pay_cint" -> payCint)
      map += ("actual_cint" -> actualCint)
      map += ("actual_paydate" -> actualPaydate)
      map += ("finish_date" -> finishDate)
      map += ("status" -> status)
      map += ("ove_day" -> ove_day)
      map
    })
    res
  }
}
