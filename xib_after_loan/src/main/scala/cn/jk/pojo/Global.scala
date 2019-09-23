package cn.jk.pojo

import java.text.SimpleDateFormat
import java.util.Date

import com.alibaba.fastjson.JSON

/**
  * Created by YiZe on 2019/5/24.
  */
object Global {

  /*  val BROKERS = "bigdata02:9092,bigdata03:9092,bigdata04:9092"
  val GROUPID = "xib-afterloan"
  val GROUPNAME = "xib-afterloan"
  val ZKQUORUM = "bigdata01:2181,bigdata02:2181,bigdata03:2181,bigdata04:2181,bigdata05:2181"
  val HDFSURL = "hdfs://bigdata01:8020/user/xiaguohang/"
  val MYSQL_DRIVER_URL = "jdbc:mysql://10.10.188.12:12003/bigdata_sit"
  val MYSQL_USERNAME = "mcl_sit"
  val MYSQL_PWD = "mcl_sit"
  val INVAILD_NUM = -99
  val regex_rule = "\\x01"
  val regex_rule = ","*/

  val BROKERS = "hadoop01:9092,hadoop02:9092,hadoop03:9092"
  val GROUPID = "xib-afterloan"
  val GROUPNAME = "xib-afterloan"
  val ZKQUORUM = "hadoop01:2181,hadoop02:2181,hadoop03:2181"
  val HDFSURL = "hdfs://hadoop01:9000/user/xiaguohang/"
  val MYSQL_DRIVER_URL = "jdbc:mysql://localhost:3306/dbty"
  val MYSQL_USERNAME = "root"
  val MYSQL_PWD = "root"
  val INVAILD_NUM = -99
  //val regex_rule = "\\x01"
  val regex_rule = ","


  def getCreate_Date():String={
    var format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    return format.format(new Date())
  }

  def isJson(str:String):Boolean = {
    var isFlag = true
    try{
      JSON.parseObject(str)
    }catch {
      case e:Exception=>isFlag = false;e.printStackTrace()
    }
    isFlag
  }
  def getStringValue(str: String): Option[String] = {
    if (str != null && !"".equals(str)) {
      Some(str)
    } else {
      None
    }
  }


}
