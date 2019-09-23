package cn.jk.util

import java.text.SimpleDateFormat
import java.util.Date
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
    var str = ""
    if(times!=null && !"".equals(times)){
      val formats = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
      str = formats.format(new Date(times.toLong))
    }
    str
  }

  def datediff(startTime: String, endTime: String): Long = {
    var day = 0
    if(startTime!=null && !"".equals(startTime) && endTime!=null && !"".equals(endTime)){
      val startDate = new SimpleDateFormat("yyyy-MM-dd").parse(startTime)
      val endDate = new SimpleDateFormat("yyyy-MM-dd").parse(endTime)
      val between = endDate.getTime - startDate.getTime
      //val second = between / 1000
      //val hour = between / 1000 / 3600
      val day: Long = between / 1000 / 3600 / 24
      //val year = between / 1000 / 3600 / 24 / 365
    }
    day
  }
}

