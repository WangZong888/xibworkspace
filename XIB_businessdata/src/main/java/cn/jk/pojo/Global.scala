package cn.jk.pojo

import java.text.SimpleDateFormat
import java.util.Date

/**
  * Created by YiZe on 2019/5/24.
  */
object Global {
 val BROKERS = "bigdata02:9092,bigdata03:9092,bigdata04:9092"
  // val BROKERS = "hadoop01:9092,hadoop02:9092,hadoop03:9092"
  val GROUPID = "xib"
  val GROUPNAME = "xib"
  val ZKQUORUM = "bigdata01:2181,bigdata02:2181,bigdata03:2181"
 //val ZKQUORUM_HBASE = "bigdata04,bigdata05,bigdata06"
  val HDFSURL = "hdfs://bigdata01:8020"
  val MYSQL_DRIVER_URL = "jdbc:mysql://10.10.188.12:12003/bigdata_sit"
  val MYSQL_USERNAME = "mcl_sit"
  val MYSQL_PWD = "mcl_sit"

//  val MYSQL_DRIVER_URL = "jdbc:mysql://localhost:3306/dbty"
//  val MYSQL_USERNAME = "root"
//  val MYSQL_PWD = "root"
  //val INVAILD_NUM = -99


  def getCreate_Date():String={
    var format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    return format.format(new Date())
  }
}
