package cn.jk.util

import scala.collection.mutable.ArrayBuffer

/**
  * Created by YiZe on 2019/6/6.
  *
  * @todo 计算逾期天数 逾期次数
  */
object Util {
  def anysis(start:ArrayBuffer[Int]): ArrayBuffer[Int] ={
    val y = ArrayBuffer[String]()
    val zz = ArrayBuffer[Int]()
    for (elem <- 0 to start.size - 1) {
      if (start(elem) > 30) {
        y += elem + "|" + start(elem)
      }else{
        if (start(elem) >0 && start(elem) <= 30){
          zz += start(elem)
        }
      }
    }
    println(y)
    val str = new StringBuilder()
    for (i <- 0 to y.size - 1) {
      if (i == 0) {
        str.append(i)
      }
      if (i != 0) {
        if (y(i).split("\\|")(0).toInt - y(i - 1).split("\\|")(0).toInt == 1) {
          if (i == 1) {
            str.append("0," + y(i).split("\\|")(0).toInt)
          } else {
            str.append("," + y(i).split("\\|")(0).toInt)
          }
        }
        if (y(i).split("\\|")(0).toInt - y(i - 1).split("\\|")(0).toInt > 1) {
          str.append("|" + y(i).split("\\|")(0).toInt)
        }
      }
    }
    //println(str)
    val temp = str.toString().split("\\|")
    val z = ArrayBuffer[Int]()
    for (i <- temp) {
      if (!i.isEmpty && i != null) {
        val strings: Array[String] = i.split(",")
        if (strings.size >= 1) {
          var maxvalue = 0
          for (str <- strings) {
            if (start(str.toInt) > maxvalue) {
              maxvalue = start(str.toInt)
            }
          }
          zz += maxvalue
        }
      }

    }
    zz
  }
}
