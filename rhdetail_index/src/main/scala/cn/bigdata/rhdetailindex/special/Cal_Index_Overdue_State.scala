package cn.bigdata.rhdetailindex.special

import scala.xml.{Elem, NodeSeq}
import util.control.Breaks._

/*
  新增当前是否有逾期记录：0-无  1-有  需要两张表的信息
  add by grace 2019-06-04
 */
object Cal_Index_Overdue_State {

  def cal_overdue_state(elem: Elem): String = {
    var overdue_state = ""
    //人行征信详版-贷款交易信息明细

    //人行征信详版-贷记卡信息
    val cardinfo = elem \\ "TblperLoanCardInfo"
    /*
    第一部分 TblperLoanCardInfo：贷记卡
    需求变更：add by grace 2019-07-27
     */
    var flag1 = false
    val temp1: NodeSeq = cardinfo.filter(f => {
      "贷记卡".equals((f \ "CardType").text)
    })
    breakable {
      temp1.foreach(f => {
        val status24 = (f \\ "Last24state").text
        if (!"".equals(status24)) {
          val status_sp: Array[String] = status24.split(",")
          if (status_sp(status_sp.length - 1).matches("\\d*")) {
            if (status_sp(status_sp.length - 1).toInt >= 1) {
              flag1 = true
              break
            }
          }
        }
      })
    }
    /*
       第二部分 TblperLoanCardInfo：准贷记卡
       需求变更：add by grace 2019-07-27
        */
    var flag2 = false
    val temp2: NodeSeq = cardinfo.filter(f => {
      "准贷记卡".equals((f \ "CardType").text)
    })
    breakable {
      temp2.foreach(f => {
        val status24 = (f \\ "Last24state").text
        if (!"".equals(status24)) {
          val status_sp: Array[String] = status24.split(",")
          if (status_sp(status_sp.length - 1).matches("\\d*")) {
            if (status_sp(status_sp.length - 1).toInt >= 3) {
              flag2 = true
              break
            }
          }

        }
      })
    }
    /*
       第三部分 TblperLoanInfo
       需求变更：add by grace 2019-07-27
        */
    var flag3 = false
    val loaninfo = elem \\ "TblperLoanInfo"
    breakable {
      loaninfo.foreach(f => {
        val status24 = (f \\ "Latest24state").text
        if (!"".equals(status24)) {
          val status_sp: Array[String] = status24.split(",")
          if (status_sp(status_sp.length - 1).matches("\\d*")) {
            if (status_sp(status_sp.length - 1).toInt >= 1) {
              flag3 = true
              break
            }
          }

        }
      })
    }

    if (flag1 || flag2 || flag3) {
      overdue_state = "1"
    } else {
      overdue_state = "0"
    }
    overdue_state
  }
}
