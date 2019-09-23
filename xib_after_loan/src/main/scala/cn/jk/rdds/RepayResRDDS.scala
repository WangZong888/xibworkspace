package cn.jk.rdds

import cn.jk.pojo.Global
import cn.jk.util.DateFormat
import org.apache.spark.rdd.RDD

/**
  * Created by YiZe on 2019/6/6.
  */
object RepayResRDDS {
  def repayResRDD(repayRes: RDD[String]): RDD[(String, String, String, String, String, String, String, String, String, Long)] = {
    val repayResRDDS: RDD[(String, String, String, String, String, String, String, String, String, Long)] = repayRes.map(line => {
      val lines: Array[String] = line.split(Global.regex_rule,-1)
      val creditApplyNo = lines(0)
      val creditRiskId = lines(1)
      val orderRiskId = lines(2)
      val certId = lines(3)
      val creditId = lines(4)
      val sellCreditId = Global.getStringValue(lines(5)).getOrElse("-")
    /*  val contractId = lines(6)
      val paymentAccno = lines(7)
      val rpterm = lines(8)*/
      val payDate = lines(8)
     /* val totalAmount = lines(10)
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
      val actualPaydate = lines(22)*/
      val finishDate = Global.getStringValue(lines(22)).getOrElse("-")
      var ove_day = 0
      if("".equals(payDate)){
        ove_day = Global.INVAILD_NUM
      }else{
        ove_day = DateFormat.datediff(payDate,finishDate).toInt
      }
      val status = lines(23)
      (creditApplyNo, creditRiskId, orderRiskId, certId, creditId, sellCreditId, payDate, finishDate, status, ove_day)
    })
    repayResRDDS

  }
}
