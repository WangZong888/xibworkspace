package cn.bigdata.rhdetailindex.deal

import cn.bigdata.rhdetailindex.utils.Global

import scala.xml.Elem

/*
4.计算信息概要相关指标
  需求变更，在信息概要里增加“最长逾期期数”指标   add by grace 2019-06-04
  方法返回：发卡机构数|最长逾期期数
 */
object Cal_Index_Summary {

  def cal_index(elem: Elem):String = {
    var financeorgcount = 0
    val summary_info = elem \\ "TblperInfoSumarry"
    var max_overdue_num = 0
    summary_info.foreach(m => {
      financeorgcount = Global.getIntValue((m \ "UnpaidloanFinanceOrgCount").text).getOrElse(0)
      max_overdue_num = Integer.min((m \ "LoanOverdueMaxDuration").text.toInt,(m \ "LoanCardOverdueMaxDuration").text.toInt)
    })
    financeorgcount+"|"+max_overdue_num
  }

}
