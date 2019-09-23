package cn.jk.util

import java.text.SimpleDateFormat
import java.util.regex.{Matcher, Pattern}

/**
  * Created by YiZe on 2019/5/21.
  */
object DateFormat {
  def formatPayDate(str: String):String={
    val reg1 = "^\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}$"
    val reg2 = "^\\d{4}-\\d{2}-\\d{2}$"
    val reg3 = "^\\d{4}/\\d{2}/\\d{2}$"
    val reg4 = "^\\d{4}\\d{2}\\d{2}$"
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
}
