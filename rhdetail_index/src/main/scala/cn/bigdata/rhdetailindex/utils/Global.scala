package cn.bigdata.rhdetailindex.utils

import java.text.SimpleDateFormat
import java.util.{Calendar, Date}
import java.util.regex.{Matcher, Pattern}

/**
  * 工具类
  * add by grace 2019-05-05
  */
object Global {

  def isNumerical(str: String): Int = {
    val pattern: Pattern = Pattern.compile("^[-]?[0-9]+(.[0-9]+)?$")
    val isNum: Matcher = pattern.matcher(str)
    if (!(isNum.matches)) {
      return 0
    }
    return 1
  }

  def getCreate_Date(): String = {
    var format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    return format.format(new Date())
  }

  def getCreate_Date_ns(): String = {
    var format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSXXX")
    return format.format(new Date())
  }

  def getIntValue(str: String): Option[Int] = {
    if (str != null && !"".equals(str)) {
      Some(str.toInt)
    } else {
      None
    }
  }

  def getDoubleValue(str: String): Option[Double] = {
    if (str != null && !"".equals(str)) {
      Some(str.toDouble)
    } else {
      None
    }
  }

  def formatPayDate(str: String): String = {
    val reg1 = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$"
    val reg2 = "^\\d{4}-\\d{2}-\\d{2}$"
    val reg3 = "^\\d{4}/\\d{2}/\\d{2}$"
    val reg4 = "^\\d{4}\\d{2}\\d{2}$"
    val reg5 = "^\\d{4}.\\d{2}.\\d{2} \\d{2}:\\d{2}:\\d{2}$"
    val reg6 = "^\\d{4}.\\d{2}.\\d{2}$"
    val reg7 = "^\\d{4}.\\d{2}$"
    val matcher1: Matcher = Pattern.compile(reg1).matcher(str)
    val matcher2: Matcher = Pattern.compile(reg2).matcher(str)
    val matcher3: Matcher = Pattern.compile(reg3).matcher(str)
    val matcher4: Matcher = Pattern.compile(reg4).matcher(str)
    val matcher5: Matcher = Pattern.compile(reg5).matcher(str)
    val matcher6: Matcher = Pattern.compile(reg6).matcher(str)
    val matcher7: Matcher = Pattern.compile(reg7).matcher(str)
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
    } else if (matcher5.matches()) {
      var formatter = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss")
      formatter.setLenient(false)
      val newDate = formatter.parse(str)
      formatter = new SimpleDateFormat("yyyy-MM-dd")
      return formatter.format(newDate)
    } else if (matcher6.matches()) {
      var formatter = new SimpleDateFormat("yyyy.MM.dd")
      formatter.setLenient(false)
      val newDate = formatter.parse(str)
      formatter = new SimpleDateFormat("yyyy-MM-dd")
      return formatter.format(newDate)
    } else if (matcher7.matches()) {
      var formatter = new SimpleDateFormat("yyyy.MM")
      formatter.setLenient(false)
      val newDate = formatter.parse(str)
      formatter = new SimpleDateFormat("yyyy-MM")
      return formatter.format(newDate)
    }else return ""
  }


  def formatTradeDate(str: String): String = {
    if (str != null && !"".equals(str)) {
      var formatter = new SimpleDateFormat("yyyy-MM-dd")
      formatter.setLenient(false)
      val newDate = formatter.parse(str)
      formatter = new SimpleDateFormat("yyyy-MM-dd")
      return formatter.format(newDate)
    } else {
      return ""
    }
  }


  def getStartDay_twelve(): String = {
    var dateFormat:
      SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd")
    var cal: Calendar = Calendar.getInstance()
    cal.add(Calendar.MONTH, -12)
    cal.set(Calendar.DATE, 1)
    var yesterday = dateFormat.format(cal.getTime())
    yesterday
  }


  def getmonthsBetween(start_time: String, end_Time: String) = {
    val sdf = new SimpleDateFormat("yyyy-MM-dd")
    val c1 = Calendar.getInstance()
    val c2 = Calendar.getInstance()
    c1.setTime(sdf.parse(start_time))
    c2.setTime(sdf.parse(end_Time))
    val year1 = c1.get(Calendar.YEAR)
    val year2 = c2.get(Calendar.YEAR)
    val month1 = c1.get(Calendar.MONTH)
    val month2 = c2.get(Calendar.MONTH)
    val day1 = c1.get(Calendar.DAY_OF_MONTH)
    val day2 = c2.get(Calendar.DAY_OF_MONTH)

    var yearInterval = year1 - year2
    if (month1 < month2 || month1 == month2 && day1 < day2) yearInterval -= 1
    var monthInterval = (month1 + 12) - month2
    if (day1 < day2) monthInterval -= 1;
    monthInterval %= 12
    val monthsDiff = Math.abs(yearInterval * 12 + monthInterval)
    monthsDiff
  }

  def getStartDay_three():String={
    val  dateFormat:
      SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd")
    val cal:Calendar=Calendar.getInstance()
    cal.add(Calendar.MONTH,-3)
    cal.set(Calendar.DATE, 1)
    val yesterday=dateFormat.format(cal.getTime())
    yesterday
  }


  def getStartDay_six():String={
    val  dateFormat:
      SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd")
    val cal:Calendar=Calendar.getInstance()
    cal.add(Calendar.MONTH,-6)
    cal.set(Calendar.DATE, 1)
    val yesterday=dateFormat.format(cal.getTime())
    yesterday
  }

  def getChangeDate(inputDate:String,num:Int):String = {
    val cal:Calendar=Calendar.getInstance()
    val sdf: SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd")
    var date: Date  = null
    try{
      date = sdf.parse(inputDate);
    }catch{
      case e:Exception=>e.printStackTrace()
    }
    cal.setTime(date)
    cal.add(Calendar.MONTH,num);
    val strDate = sdf.format(cal.getTime())
    strDate
  }

}
