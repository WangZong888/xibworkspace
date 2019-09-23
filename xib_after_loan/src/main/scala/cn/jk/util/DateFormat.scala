package cn.jk.util

import java.text.SimpleDateFormat
import java.util.{Calendar, Date}
import java.util.regex.{Matcher, Pattern}

/**
  * Created by YiZe on 2019/5/27.
  */
object DateFormat {
  def formatPayDate(str: String): String = {
    val reg1 = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$"
    val reg2 = "^\\d{4}-\\d{2}-\\d{2}$"
    val reg3 = "^\\d{4}/\\d{2}/\\d{2}$"
    val reg4 = "^\\d{4}\\d{2}\\d{2}$"
    val matcher1: Matcher = Pattern.compile(reg1).matcher(str)
    val matcher2: Matcher = Pattern.compile(reg2).matcher(str)
    val matcher3: Matcher = Pattern.compile(reg3).matcher(str)
    val matcher4: Matcher = Pattern.compile(reg4).matcher(str)
    if (matcher1.matches()) {
      var formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
      formatter.setLenient(false)
      val newDate = formatter.parse(str)
      formatter = new SimpleDateFormat("yyyy-MM-dd")
      return formatter.format(newDate)
    } else if (matcher2.matches()) {
      var formatter = new SimpleDateFormat("yyyy-MM-dd")
      formatter.setLenient(false)
      val newDate = formatter.parse(str)
      formatter = new SimpleDateFormat("yyyy-MM-dd")
      return formatter.format(newDate)
    } else if (matcher3.matches()) {
      var formatter = new SimpleDateFormat("yyyy/MM/dd")
      formatter.setLenient(false)
      val newDate = formatter.parse(str)
      formatter = new SimpleDateFormat("yyyy-MM-dd")
      return formatter.format(newDate)
    } else if (matcher4.matches()) {
      var formatter = new SimpleDateFormat("yyyyMMdd")
      formatter.setLenient(false)
      val newDate = formatter.parse(str)
      formatter = new SimpleDateFormat("yyyy-MM-dd")
      return formatter.format(newDate)
    } else return ""
  }

  def format(times: String): String = {
    val formats = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    val str: String = formats.format(new Date(times.toLong))
    str
  }

/*  def datediff(startTime: String, endTime: String): Long = {
    var day = 0L
    if (!"".equals(startTime) && !"".equals(endTime) && !"-".equals(startTime) && !"-".equals(endTime)) {

      val startDate = new SimpleDateFormat("yyyyMMdd").parse(startTime)
      val endDate = new SimpleDateFormat("yyyyMMdd").parse(endTime)
      val between = endDate.getTime - startDate.getTime
      //val second = between / 1000
      //val hour = between / 1000 / 3600
      day = between / 1000 / 3600 / 24
      //val year = between / 1000 / 3600 / 24 / 365
    }
    day
  }*/

  def datediff(startTime: String, endTime: String): Long = {
    var day = 0L
    if (!"".equals(startTime) && !"".equals(endTime) && !"-".equals(startTime) && !"-".equals(endTime)) {

      val startDate = new SimpleDateFormat("yyyyMMdd").parse(startTime)
      val endDate: Date = new SimpleDateFormat("yyyyMMdd").parse(endTime)
      val between = endDate.getTime - startDate.getTime
      //val second = between / 1000
      //val hour = between / 1000 / 3600
      day = between / 1000 / 3600 / 24
      //val year = between / 1000 / 3600 / 24 / 365
    } else {
      if (("".equals(endTime) || "-".equals(endTime)) && !"".equals(startTime) && !"-".equals(startTime)) {
        val startDate = new SimpleDateFormat("yyyyMMdd").parse(startTime)
        val date = new Date()
        val format = new SimpleDateFormat("yyyyMMdd")
        val now: String = format.format(date)
        val endDate: Date = format.parse(now)
        if(startTime.compareToIgnoreCase(now)<0){
          val between = endDate.getTime - startDate.getTime
          day = between / 1000 / 3600 / 24
        }
      }
    }
    day
  }



  def getChangeDate(inputDate:String,num:Int):String = {
    val cal:Calendar=Calendar.getInstance()
//    val sdf: SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd")
    val sdf: SimpleDateFormat = new SimpleDateFormat("yyyyMMdd")
    var date: Date  = null
    try{
      if(!"-".equals(inputDate) && !"".equals(inputDate) && null != inputDate) {
//        date = sdf.parse(inputDate)
        val dates = sdf.parse(inputDate)
        val format = new SimpleDateFormat("yyyy-MM-dd")
        val str = format.format(dates)
        date = format.parse(str)
      }
    }catch{
      case e:Exception=>e.printStackTrace();println("合同loanTime为空")
    }
    cal.setTime(date)
    cal.add(Calendar.MONTH,num)
    val strDate = sdf.format(cal.getTime())
    strDate
  }

  def getCurrentDay():String={
    val format = new SimpleDateFormat("yyyyMMdd")
    format.format(new Date())
  }
}

