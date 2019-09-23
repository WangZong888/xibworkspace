package cn.bigdata.rhdetailindex.deal

import cn.bigdata.rhdetailindex.deal.Cal_Derive_Collect.{INVAILD_NUM, df}
import cn.bigdata.rhdetailindex.utils.Global

import scala.xml.Elem
/*
  5.计算贷款交易信息明细相关指标：未结清贷款平均合同金额、未结清贷款比例
   方法返回：未结清贷款平均合同金额|未结清贷款比例
*/
object Cal_Index_LoanInfo {

  def  cal_index(elem: Elem):String = {
    var unsettled_bal_sum = 0.0
    var unsettled_bal_count = 0
    var unsettled_bal_mean = 0.0
    var account_sum = 0.0
    var unsetteled_ratio = 0.0
    var loan_repayamount = 0.0
    val loan_info = elem \\ "TblperLoanInfo"
    loan_info.foreach(m => {
      val balance = Global.getIntValue((m \ "Balance").text).getOrElse(0)
      val state = (m \ "State").text
      val loan_type = (m \ "Type").text
      val account = Global.getDoubleValue((m \ "Account").text).getOrElse(0.0)
      loan_repayamount+=balance*0.1
      if (!"结清".equals(state) && !"房贷".equals(loan_type)) {
        unsettled_bal_count += 1
        unsettled_bal_sum += balance
      }
      if (unsettled_bal_count == 0) {
        unsettled_bal_mean = INVAILD_NUM
      } else {
        unsettled_bal_mean = df.format(unsettled_bal_sum / unsettled_bal_count).toDouble
      }

      if (!"结清".equals(state)) {
        account_sum += account
      }

      if (account_sum == 0.0) {
        unsetteled_ratio = INVAILD_NUM
      } else {
        unsetteled_ratio = df.format(unsettled_bal_sum / account_sum).toDouble
      }
    })
    unsettled_bal_mean+"|"+unsetteled_ratio+"|"+df.format(loan_repayamount)
  }
}
