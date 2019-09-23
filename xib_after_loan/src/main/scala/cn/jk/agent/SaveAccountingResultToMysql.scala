package cn.jk.agent

import cn.jk.pojo.Global
import org.apache.spark.rdd.RDD

import scala.collection.mutable

/**
  * Created by YiZe on 2019/6/6.
  *
  * @todo 出账结果反馈
  */
object SaveAccountingResultToMysql {
  def accResultRDD(accRes: RDD[String]): RDD[mutable.Map[String, Object]] = {
    var map: mutable.Map[String, Object] = mutable.Map[String, Object]()
    val res: RDD[mutable.Map[String, Object]] = accRes.map(line => {
      val lines: Array[String] = line.split(Global.regex_rule)
      val creditApplyNo = lines(0)
      val creditRiskId = lines(1)
      val orderRiskId = lines(2)
      val certId = lines(3)
      val creditId = lines(4)
      val sellCreditId = lines(5)
      val lendingResult = lines(6)
      val resultReason = lines(7)
      val loanNo = lines(8)
      val interestRate = lines(9)
      val creditPeriod = lines(11)
      val dateOfInterest = lines(12)
      val dueDate = lines(13)
      val repaymentMethod = lines(14)
      val loanAmout = lines(15)
      val lendingDate = lines(16)

      map += ("credit_apply_no" -> creditApplyNo)
      map += ("credit_risk_id" -> creditRiskId)
      map += ("order_risk_id" -> orderRiskId)
      map += ("cert_id" -> certId)
      map += ("credit_id" -> creditId)
      map += ("sell_credit_id" -> sellCreditId)
      map += ("lending_result" -> lendingResult)
      map += ("result_reason" -> resultReason)
      map += ("loan_no" -> loanNo)
      map += ("interest_rate" -> interestRate)
      map += ("credit_period" -> creditPeriod)
      map += ("date_of_interest" -> dateOfInterest)
      map += ("due_date" -> dueDate)
      map += ("repayment_method" -> repaymentMethod)
      map += ("loan_amout" -> loanAmout)
      map += ("lending_date" -> lendingDate)
      map
    })
    res
  }
}
