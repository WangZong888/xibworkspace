package cn.jk.rdds

import cn.jk.pojo.Global
import org.apache.spark.rdd.RDD

/**
  * Created by YiZe on 2019/6/6.
  *  x._4.equals(cert_id) && DateFormat.getChangeDate(apply_date, -24) <= x._7 && !"".equals(x._9) && !"-".equals(x._9) && !"-".equals(x._7) && !"-".equals(x._8)
  */
object contractRDDS {
  def contreactRDD(contract: RDD[String]): RDD[(String, String, String, String, String, String, String, String, String, String)] = {
    val contractRDDS: RDD[(String, String, String, String, String, String, String, String, String, String)] = contract.map(line => {
      val lines: Array[String] = line.split(Global.regex_rule,-1)
      val creditApplyNo = lines(0)
      val creditRiskId = lines(1)
      val orderRiskId = lines(2)
      val certId = lines(3)
      val creditId = lines(4)
      val sellCreditId = lines(5)
      //val contractId = lines(6)
      val loanTime = lines(6)
      /*val loanAmt = lines(7)
      val rate = lines(8)
      val oRate = lines(9)
      val monthlyRepayDay = lines(10)*/
      val valueDate = lines(11)
      /*val dueDate = lines(12)
      val nextRepaydate = lines(13)
      val lastExpirydate = lines(14)

      val overdueStatus = lines(16)
      val overdueDays = lines(17)*/
      val status = lines(15)
      val finishDate = lines(18)
      /*val repaymentMethod = lines(19)
      val balance = lines(20)
      val normalCapital = lines(21)
      val overdueCapital = lines(22)
      val repaidCapital = lines(23)
      val totalAint = lines(24)
      val overdueAint = lines(25)
      val repaidAint = lines(26)
      val totalOint = lines(27)
      val overdueOint = lines(28)
      val repaidOint = lines(29)
      val overdueCint = lines(30)
      val payCint = lines(31)
      val repaidCint = lines(32)*/

      (creditApplyNo, creditRiskId, orderRiskId, certId, creditId, sellCreditId, loanTime, valueDate, finishDate,status)
    })
    contractRDDS
  }
}
