package cn.jk.analysis

import cn.jk.pojo.Label
import org.apache.spark.rdd.RDD
import org.slf4j.{Logger, LoggerFactory}

/**
  * Created by YiZe on 2019/5/21.
  *
  * @todo 贷后指标计算
  */
object AfterLoanAnalysisTask {
  val log = LoggerFactory.getLogger(AfterLoanAnalysisTask.getClass)

  /**
    * @todo 用信列表RDD解析
    * @return
    */
  def usecreditListRDD(): RDD[(String, String, String, String, String, String, String, String, String, String, String, String)] = {
    //用信列表
    val usecreditList: RDD[String] = TransformRDD.transforRDD(Session.getSparkSession(), Label.USECREDITLIST)
    val dropHeader: RDD[String] = usecreditList.mapPartitionsWithIndex { (idx, iter) => if (idx == 0) iter.drop(1) else iter }
    val resultSet: RDD[(String, String, String, String, String, String, String, String, String, String, String, String)] = dropHeader.map(lines => {
      val line = lines.split(",")

      val idOfCore = line(0)
      val creditNo = line(1)
      val billNo = line(2)
      val lendingResult = line(3)
      val loanNo = line(4)
      val interestRate = line(5)
      val creditPeriod = line(6)
      val dateOfInterest = line(7)
      val dueDate = line(8)
      val repaymentMethod = line(9)
      val loanAmount = line(10)
      val lendingDate = line(11)
      (idOfCore, creditNo, billNo, lendingResult, loanNo, interestRate, creditPeriod, dateOfInterest, dueDate, repaymentMethod, loanAmount, lendingDate)
    })
    resultSet
  }

  /**
    * @todo 还款列表RDD解析
    * @return
    */
  def reimbursementListRDD(): RDD[(String, String, String, String, String, String, String, String, String)] = {
    //还款列表
    val reimbursementList: RDD[String] = TransformRDD.transforRDD(Session.getSparkSession(), Label.REIMBURSEMENTLIST)
    val dropHeader: RDD[String] = reimbursementList.mapPartitionsWithIndex { (idx, iter) => if (idx == 0) iter.drop(1) else iter }
    val resultSet: RDD[(String, String, String, String, String, String, String, String, String)] = dropHeader.map(lines => {
      val line = lines.split(",")
      val idOfCore = line(0)
      val creditNo = line(1)
      val loanNo = line(2)
      val repaymentAmount = line(3)
      val repaymentTime = line(4)
      val outstandingAmount = line(5)
      val repaymentDay = line(6)
      val lateCharge = line(7)
      val repaymentStatus = line(8)
      (idOfCore, creditNo, loanNo, repaymentAmount, repaymentTime, outstandingAmount, repaymentDay, lateCharge, repaymentStatus)
    })
    resultSet
  }

  def main(args: Array[String]): Unit = {
    usecreditListRDD()
    reimbursementListRDD()

  }
}
