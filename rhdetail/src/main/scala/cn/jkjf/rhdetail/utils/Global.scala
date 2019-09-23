package cn.jkjf.rhdetail.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.regex.{Matcher, Pattern}

/**
  * 工具类
  * add by grace 2019-05-05
  */
object Global {

  def isNumerical(str:String):Int ={
    val pattern: Pattern = Pattern.compile ("^[-]?[0-9]+(.[0-9]+)?$")
    val isNum: Matcher = pattern.matcher (str)
    if (! (isNum.matches) ) {
      return 0
    }
    return 1
  }

  def getCreate_Date():String={
    var format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    return format.format(new Date())
  }

  def getCreate_Date2():String={
    var format = new SimpleDateFormat("yyyy-MM-dd")
    return format.format(new Date())
  }

  def getCreate_Date3():String={
    var format = new SimpleDateFormat("HH-mm-ss")
    return format.format(new Date())
  }

  def getCreate_Date_ns():String={
    var format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSXXX")
    return format.format(new Date())
  }

  def getIntValue(str:String):Option[Int]={
    if(str!=null && !"".equals(str)){
      Some(str.toInt)
    }else{
      None
    }
  }

  def getDoubleValue(str:String):Option[Double]={
    if(str!=null && !"".equals(str)){
      Some(str.toDouble)
    }else{
      None
    }
  }

  def formatPayDate(str: String):String={
    val reg1 = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$";
    val reg2 = "^\\d{4}-\\d{2}-\\d{2}$";
    val reg3 = "^\\d{4}/\\d{2}/\\d{2}$";
    val reg4 = "^\\d{4}\\d{2}\\d{2}$";
    val matcher1: Matcher = Pattern.compile (reg1).matcher(str)
    val matcher2: Matcher = Pattern.compile (reg2).matcher(str)
    val matcher3: Matcher = Pattern.compile (reg3).matcher(str)
    val matcher4: Matcher = Pattern.compile (reg4).matcher(str)
    if(matcher1.matches()){
      var formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
      formatter.setLenient(false)
      val newDate = formatter.parse(str)
      formatter = new SimpleDateFormat("yyyy-MM-dd")
      return formatter.format(newDate)
    }else if(matcher2.matches()){
      var formatter = new SimpleDateFormat("yyyy-MM-dd")
      formatter.setLenient(false)
      val newDate = formatter.parse(str)
      formatter = new SimpleDateFormat("yyyy-MM-dd")
      return formatter.format(newDate)
    }else if(matcher3.matches()){
      var formatter = new SimpleDateFormat("yyyy/MM/dd")
      formatter.setLenient(false)
      val newDate = formatter.parse(str)
      formatter = new SimpleDateFormat("yyyy-MM-dd")
      return formatter.format(newDate)
    }else if(matcher4.matches()){
      var formatter = new SimpleDateFormat("yyyyMMdd")
      formatter.setLenient(false)
      val newDate = formatter.parse(str)
      formatter = new SimpleDateFormat("yyyy-MM-dd")
      return formatter.format(newDate)
    }else return ""
  }


  def formatTradeDate(str: String):String={
    if (str!=null && !"".equals(str)){
      var formatter = new SimpleDateFormat("yyyy-MM-dd")
      formatter.setLenient(false)
      val newDate = formatter.parse(str)
      formatter = new SimpleDateFormat("yyyy-MM-dd")
      return formatter.format(newDate)
    }else{
      return ""
    }
  }



}
