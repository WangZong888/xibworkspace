package cn.jk.analysis

import cn.jk.pojo.Label
import cn.jk.util.{DateUtil, Save_To_Mysql}
import org.apache.commons.lang3.StringUtils
import org.apache.spark.rdd.RDD

import scala.collection.mutable

/**
  * Created by YiZe on 2019/5/29.
  *
  * @todo 解析从sftp 下载的数据，到mysql
  */
object RddSaveToMysql {

  /**
    * @todo 用信列表RDD解析
    * @return
    */
  def usecreditListRDD() {
    //用信列表
    val usecreditList: RDD[String] = TransformRDD.transforRDD(Session.getSparkSession(), Label.USECREDITLIST)
    val dropHeader: RDD[String] = usecreditList.mapPartitionsWithIndex { (idx, iter) => if (idx == 0) iter.drop(1) else iter }
    val ress: RDD[mutable.Map[String, Object]] = dropHeader.map(lines => {
      var map: mutable.Map[String, Object] = mutable.Map[String, Object]()
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
      //(idOfCore, creditNo, billNo, lendingResult, loanNo, interestRate, creditPeriod, dateOfInterest, dueDate, repaymentMethod, loanAmount, lendingDate)
      map += ("id_of_core" -> idOfCore)
      map += ("credit_no" -> creditNo)
      map += ("bill_no" -> billNo)
      map += ("lending_result" -> lendingResult)
      map += ("loan_no" -> loanNo)
      map += ("interest_rate" -> interestRate)
      map += ("credit_period" -> creditPeriod)
      map += ("date_of_interest" -> dateOfInterest)
      map += ("due_date" -> dueDate)
      map += ("repayment_method" -> repaymentMethod)
      map += ("loan_amount" -> loanAmount)
      map += ("lending_date" -> lendingDate)
      map
    })
    ress.foreachPartition(itr => {
      itr.foreach(x => {
        Save_To_Mysql.save_data(x, "use_credit_from_sftp")
      })
    })
  }

  /**
    * @todo 还款列表RDD解析
    * @return
    */
  def reimbursementListRDD() {
    //还款列表
    val reimbursementList: RDD[String] = TransformRDD.transforRDD(Session.getSparkSession(), Label.REIMBURSEMENTLIST)
    val dropHeader: RDD[String] = reimbursementList.mapPartitionsWithIndex { (idx, iter) => if (idx == 0) iter.drop(1) else iter }
    val ress: RDD[mutable.Map[String, Object]] = dropHeader.map(lines => {
      var map: mutable.Map[String, Object] = mutable.Map[String, Object]()
      val line = lines.split(",")
      val idOfCore = line(0)
      val creditNo = line(1)
      val loanNo = line(2)
      val repaymentAmount = line(3)
      val repaymentTime = line(4) //同line(6) 字段名相同
      val outstandingAmount = line(5)
      val overdueDays = line(6) //同line(4) 字段名相同，此处修改
      val lateCharge = line(7)
      val repaymentStatus = line(8)
      //      (idOfCore, creditNo, loanNo, repaymentAmount, repaymentTime, outstandingAmount, overdueDays, lateCharge, repaymentStatus)
      map += ("id_of_core" -> idOfCore)
      map += ("credit_no" -> creditNo)
      map += ("loan_no" -> loanNo)
      map += ("repayment_amount" -> repaymentAmount)
      map += ("repayment_time" -> repaymentTime)
      map += ("outstanding_amount" -> outstandingAmount)
      map += ("overdue_days" -> overdueDays)
      map += ("late_charge" -> lateCharge)
      map += ("repayment_status" -> repaymentStatus)
      map
    })
    ress.foreachPartition(itr => {
      itr.foreach(x => {
        Save_To_Mysql.save_data(x, "reimbursement_from_sftp")
      })
    })
  }

  /**
    * 卖方业务数据
    */
  def businessDataRDD(): Unit = {
    val business: RDD[String] = TransformRDD.transforRDD(Session.getSparkSession(), Label.BUSINESSDATA + DateUtil.getCurrentByCalendar + "/")
    val dropHeader: RDD[String] = business.mapPartitionsWithIndex { (idx, iter) => if (idx == 0) iter.drop(1) else iter }

   if(!dropHeader.isEmpty()){
     val ress: RDD[mutable.Map[String, Object]] = dropHeader.map(line => {
       var map: mutable.Map[String, Object] = mutable.Map[String, Object]()
       val lines = line.split(",")
       val creditNo = lines(0)
       val creditName = lines(1)
       val supplierName = lines(2)
       val supplierCode = lines(3)
       val billNo = lines(4)
       val orderType = lines(5)
       val orderDate = lines(6)
       val amount = lines(7)
       map += ("credit_no" -> creditNo)
       map += ("credit_name" -> creditName)
       map += ("supplier_code" -> supplierCode)
       map += ("supplier_name" -> supplierName)
       map += ("order_type" -> orderType)
       map += ("bill_no" -> billNo)
       map += ("amount" -> amount)
       map += ("order_date" -> orderDate)
       map
     })
     if(!ress.isEmpty()){
       ress.foreachPartition(itr => {
         itr.foreach(x => {
           Save_To_Mysql.save_data(x, "business_data_from_sftp")
         })
       })
     }
   }

  }

  /**
    * 授信列表
    */
  def creditListRDD(): Unit = {
    val credit = TransformRDD.transforRDD(Session.getSparkSession(), Label.CREDITLIST)
    val dropHeader = credit.mapPartitionsWithIndex { (idx, iter) => if (idx == 0) iter.drop(1) else iter }
    val ress: RDD[mutable.Map[String, Object]] = dropHeader.map(line => {
      var map: mutable.Map[String, Object] = mutable.Map[String, Object]()
      val lines = line.split(",",-1)
      val idOfCore = lines(0)
      val creditNo = lines(1)
      val salesOrganization = lines(2)
      val creditName = lines(3)
      val creditNo1 = lines(4) //于line(1) 重复
      val lineOfCredit = lines(5)
      val rate = lines(6)
      val durationOfCredit = lines(7)
      val repaymentMethod = lines(8)
      val creditTime = lines(9)
      val creditPeriodBegin = lines(10)
      val creditPeriodEnd = lines(11)
      var remark = "null"
      if (!StringUtils.isEmpty(lines(12))) {
        remark = lines(12)
      }
      map += ("id_of_core" -> idOfCore)
      map += ("credit_no" -> creditNo)
      map += ("sales_organization" -> salesOrganization)
      map += ("credit_name" -> creditName)
      map += ("credit_no1" -> creditNo1)
      map += ("line_of_credit" -> lineOfCredit)
      map += ("rate" -> rate)
      map += ("duration_of_credit" -> durationOfCredit)
      map += ("repayment_method" -> repaymentMethod)
      map += ("credit_time" -> creditTime)
      map += ("credit_period_begin" -> creditPeriodBegin)
      map += ("credit_period_end" -> creditPeriodEnd)
      map += ("remark" -> remark)
      map
    })
    ress.foreachPartition(itr => {
      itr.foreach(x => {
        Save_To_Mysql.save_data(x, "credit_list_from_sftp")
      })
    })
  }

}
