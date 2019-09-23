package cn.jk.agent

import cn.jk.pojo.Global
import cn.jk.util.DateUtil
import com.alibaba.fastjson.{JSON, JSONObject}
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD

object DataSinkTranser {

  def afterLoan(sc: SparkContext): Unit = {
    val path = Global.HDFSURL
//    val contractRDDs: RDD[String] = sc.textFile(s"${path}contract/" + DateUtil.getYesterdayByCalendar + "/")
      val contractRDDs: RDD[String] = sc.textFile(s"${path}contract/")
    contractRDDs.foreach(println)
    //去掉头部
//    val contractRDD = contractRDDs.mapPartitionsWithIndex { (idx, iter) => if (idx == 0) iter.drop(1) else iter }
    val repay_dtlRDDs: RDD[String] = sc.textFile(s"${path}repay_dtl/")
    //去掉头部
//    val repay_dtlRDD = repay_dtlRDDs.mapPartitionsWithIndex { (idx, iter) => if (idx == 0) iter.drop(1) else iter }
    val repay_resultRDDs: RDD[String] = sc.textFile(s"${path}repay_result/")
    //去掉头部
//    val repay_resultRDD = repay_resultRDDs.mapPartitionsWithIndex { (idx, iter) => if (idx == 0) iter.drop(1) else iter }
    val accounting_resultRDDs = sc.textFile(s"${path}out_acct_result/")
    //去掉头部
//    val accounting_resultRDD = accounting_resultRDDs.mapPartitionsWithIndex { (idx, iter) => if (idx == 0) iter.drop(1) else iter }

    /**
      * @todo 写入mysql
      */

    try {
      //待写的方法：备份主表到备份表信息，然后删除主表信息，插入新的数据
      val strings: Array[String] = contractRDDs.collect()
      SaveContractInfo.save_contract(strings)
      println("xib_contract_info存储完成")
    } catch {
      case e: Exception => println("xib_contract_info存储失败"); e.printStackTrace()
    }

    try {
      val strings: Array[String] = repay_dtlRDDs.collect()
      SaveRepayDtlInfo.save_repaydtl(strings)
      println("repay_detail_info存储完成")
    } catch {
      case e: Exception => println("repay_detail_info存储失败"); e.printStackTrace()
    }

    try {
      val strings: Array[String] = repay_resultRDDs.collect()
      SaveRepayResultInfo.save_repayresult(strings)
      println("repay_result_plan_info存储完成")
    } catch {
      case e: Exception => println("repay_result_plan_info存储失败"); e.printStackTrace()
    }
    try {
      val strings: Array[String] = accounting_resultRDDs.collect()
      SaveAccountResultInfo.save_accresult(strings)
      println("accounting_result存储完成")
    } catch {
      case e: Exception => println("accounting_result存储失败"); e.printStackTrace()
    }
    sc.stop()
  }
}
