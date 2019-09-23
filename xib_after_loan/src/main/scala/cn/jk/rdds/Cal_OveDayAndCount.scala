package cn.jk.rdds

import java.util

import cn.jk.util.DateFormat
import org.apache.spark.rdd.RDD

import scala.collection.mutable.ArrayBuffer

object Cal_OveDayAndCount {

  def cal_oveday(contractRes:RDD[(String, String, String, String, String, String, String, String, String, String)],repayRes: RDD[(String, String, String, String, String, String, String, String, String, Long)] ):String={

    val contract_arr: Array[String] = contractRes.map(m=>{m._3}).collect()
    var ove_num_result = 0
    var ove_day_result = 0
    val repay_arr: Array[(String, String, String)] = repayRes.sortBy(s => (s._3, s._7)).map(m => {
      var cur_day = ""
      if("-".equals(m._8)){
        cur_day = DateFormat.getCurrentDay()
      }else{
        cur_day = m._8
      }
      (m._3, m._7, cur_day)
    }).collect()
      for(ele <-contract_arr){
        var result_arr  = ArrayBuffer[String]()
        var ove_day  = ArrayBuffer[String]()
        var flag_arr  = ArrayBuffer[String]()
        var list:util.ArrayList[ArrayBuffer[String]] = new util.ArrayList[ArrayBuffer[String]]
        val arr: Array[(String, String, String)] = repay_arr.filter(f=>{f._1.equals(ele)})
        for(i <- arr.indices ){
          val temp_arr: ArrayBuffer[String] = ArrayBuffer[String]()
          temp_arr+=arr(i)._1
          temp_arr+=arr(i)._2
          temp_arr+=arr(i)._3
          //dis_days
          if(i == arr.length-1 ){
            temp_arr += "0"
          }else{
            temp_arr += DateFormat.datediff(arr(i)._2,arr(i+1)._2).toString
          }
          //逾期天数
          if(DateFormat.datediff(arr(i)._2,arr(i)._3)<0){
            temp_arr += "0"
            ove_day += "0"
          }else{
            temp_arr += DateFormat.datediff(arr(i)._2,arr(i)._3).toString
            ove_day += DateFormat.datediff(arr(i)._2,arr(i)._3).toString
          }
          //逾期天数是否大于dis_days
          if(temp_arr(4).toInt-temp_arr(3).toInt>0){
            temp_arr += "1"
          }else{
            temp_arr += "0"
          }
          list.add(temp_arr)
        }

        //根据list，计算逾期次数flag
        for(i<- 0 until list.size()){
          if(i==0){
            list.get(i) += "1"
            flag_arr += "1"
          }else{
            if("1".equals(list.get(i-1)(5))) {
              list.get(i) += list.get(i-1)(6)
              flag_arr += list.get(i-1)(6)
            } else {
              list.get(i) += (list.get(i-1)(6).toInt +1).toString
              flag_arr += (list.get(i-1)(6).toInt +1).toString
            }
          }
          //println(list.get(i))
        }
        //近两年逾期次数 f.toInt == 0
        val ove_count = flag_arr.max.toInt - ove_day.count(f=>{f.toInt == 0})
        ove_num_result +=ove_count
        /*
          基于逾期次数flag，计算近两年最长逾期天数
         */
        for(i<- 0 until list.size()){
          if(list.get(i)(4).toInt !=0){
            result_arr += list.get(i)(6)+"|"+list.get(i)(1)+"|"+list.get(i)(2)
          }
        }
        if(result_arr.nonEmpty){
          val stringToStrings: Map[String, ArrayBuffer[String]] = result_arr.groupBy(k=>k.split("\\|")(0))
          for((k,v) <-stringToStrings){
            /*
            2,ArrayBuffer(2|20190215|20190217)
            5,ArrayBuffer(5|20190615|20190825, 5|20190715|20190825, 5|20190815|20190830)
            3,ArrayBuffer(3|20190315|20190420, 3|20190415|20190420)
             */
            val temp  = ArrayBuffer[String]()
            for(i<-v.indices){
              temp += v(i).split("\\|")(1)
              temp += v(i).split("\\|")(2)
            }
            ove_day_result += DateFormat.datediff(temp.min,temp.max).toInt
          }
        }
      }
    ove_num_result+"|"+ove_day_result
  }

}
