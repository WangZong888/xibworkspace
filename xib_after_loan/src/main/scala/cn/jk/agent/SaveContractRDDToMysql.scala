package cn.jk.agent

import cn.jk.pojo.Global
import org.apache.spark.rdd.RDD

import scala.collection.mutable

/**
  * Created by YiZe on 2019/6/5.
  */
object SaveContractRDDToMysql {
  def contreactRDD(contract: RDD[String]): RDD[mutable.Map[String, Object]] = {
    var map: mutable.Map[String, Object] = mutable.Map[String, Object]()
    val res: RDD[mutable.Map[String, Object]] = contract.map(line => {
      val lines: Array[String] = line.split(Global.regex_rule)
      val creditApplyNo = lines(0)
      val creditRiskId = lines(1)
      val orderRiskId = lines(2)
      val certId = lines(3)
      val creditId = lines(4)
      val sellCreditId = lines(5)
      val contractId = lines(6)
      val loanTime = lines(7)
      val loanAmt = lines(8)
      val rate = lines(9)
      val oRate = lines(10)
      val monthlyRepayDay = lines(11)
      val valueDate = lines(12)
      val dueDate = lines(13)
      val nextRepaydate = lines(14)
      val lastExpirydate = lines(15)
      val status = lines(16)
      val overdueStatus = lines(17)
      val overdueDays = lines(18)
      val finishDate = lines(19)
      val repaymentMethod = lines(20)
      val balance = lines(21)
      val normalCapital = lines(22)
      val overdueCapital = lines(23)
      val repaidCapital = lines(24)
      val totalAint = lines(25)
      val overdueAint = lines(26)
      val repaidAint = lines(27)
      val totalOint = lines(28)
      val overdueOint = lines(29)
      val repaidOint = lines(30)
      val overdueCint = lines(31)
      val payCint = lines(32)
      val repaidCint = lines(33)
      map += ("credit_apply_no" -> creditApplyNo)
      map += ("credit_risk_id" -> creditRiskId)
      map += ("order_risk_id" -> orderRiskId)
      map += ("cert_id" -> certId)
      map += ("credit_id" -> creditId)
      map += ("sell_credit_id" -> sellCreditId)
      map += ("contract_id" -> contractId)
      map += ("loan_time" -> loanTime)
      map += ("loan_amt" -> loanAmt)
      map += ("rate" -> rate)
      map += ("orate" -> oRate)
      map += ("monthly_repay_day" -> monthlyRepayDay)
      map += ("value_date" -> valueDate)
      map += ("due_date" -> dueDate)
      map += ("next_repaydate" -> nextRepaydate)
      map += ("last_expirydate" -> lastExpirydate)
      map += ("status" -> status)
      map += ("overdue_status" -> overdueStatus)
      map += ("overdue_days" -> overdueDays)
      map += ("finish_date" -> finishDate)
      map += ("repayment_method" -> repaymentMethod)
      map += ("balance" -> balance)
      map += ("normal_capital" -> normalCapital)
      map += ("overdue_capital" -> overdueCapital)
      map += ("repaid_capital" -> repaidCapital)
      map += ("total_aint" -> totalAint)
      map += ("overdue_aint" -> overdueAint)
      map += ("repaid_aint" -> repaidAint)
      map += ("totalOint" -> totalOint)
      map += ("overdueOint" -> overdueOint)
      map += ("repaidOint" -> repaidOint)
      map += ("overdue_cint" -> overdueCint)
      map += ("pay_cint" -> payCint)
      map += ("repaid_cint" -> repaidCint)
      map
    })
    //    Save_To_Mysql.save_data(map,"contract_info")
    res
  }
}
