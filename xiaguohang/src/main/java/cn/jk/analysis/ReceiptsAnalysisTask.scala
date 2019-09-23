package cn.jk.analysis

import cn.jk.pojo.Label
import org.apache.spark.rdd.RDD

/**
  * Created by YiZe on 2019/5/21.
  *
  * @todo 单据指标计算
  */
object ReceiptsAnalysisTask {
  /**
    * @todo 单据列表RDD解析
    * @return
    */
  def businessdataRDD(): RDD[(String, String, String, String, String, String, String, String)] = {
    //单据列表
    val reimbursementList: RDD[String] = TransformRDD.transforRDD(Session.getSparkSession(), Label.BUSINESSDATA)
    val dropHeader: RDD[String] = reimbursementList.mapPartitionsWithIndex { (idx, iter) => if (idx == 0) iter.drop(1) else iter }
    val resultSet: RDD[(String, String, String, String, String, String, String, String)] = dropHeader.map(lines => {
      val line = lines.split(",")
      val creditNo = line(0)
      val creditName = line(1)
      val supplierCode = line(2)
      val supplierName = line(3)
      val orderType = line(4)
      val billNo = line(5)
      val amount = line(6)
      val orderDate = line(7)
      (creditNo, creditName, supplierCode, supplierName, orderType, billNo, amount, orderDate)
    })
    resultSet
  }

  def main(args: Array[String]): Unit = {
   /* //查流水表ID
    val selector = ""
    val param: String = GetreceiptsAnalysisParam.getParam(selector)
    val str: Double = param.split("\001")(0).toDouble
    businessdataRDD.map { lines =>

       val fenzi = (lines._6.toDouble) - str
       val fenmu =  Math.abs(str)
      //val Amount_judge_68 = if (((lines._6 - param.split("\001")()) / Math.abs(param.split("\001")())) < 1) 0 else 1
      val Amount_judge_68 = if ((fenzi/fenmu) < 1.0) 0 else 1
      val Amount_judge_95 = if ((fenzi/fenmu) > 1.0 && (fenzi/fenmu) < 2.0) 0 else 1
      val Amount_judge_99 = if ((fenzi/fenmu) > 2.0 && (fenzi/fenmu) < 3.0) 0 else 1
    }*/
  }
}
