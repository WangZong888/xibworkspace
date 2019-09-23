package cn.bigdata.rhdetailindex.deal

import cn.bigdata.rhdetailindex.utils.Global

import scala.xml.{Elem, NodeSeq}

/*7.计算  历史逾期次数指标
          需求变更，指标改为距离申请日期近两年 add by grace 2019-06-04
          需求变更，增加贷记卡和准贷记卡的区别 add by grace 2019-07-24
*/
object Cal_Index_Overdue {

  def cal_index(elem: Elem,risk_id:String):Int = {
    var rh_overdue_times_his = 0
    val loan_info = elem \\ "TblperLoanCardInfo"
    /*
    1.第一部分：rh_detail_TblperLoanCardInfo 中贷记卡
     */
    val temp1: NodeSeq = loan_info.filter(f=>{"贷记卡".equals((f \ "CardType").text)})
    var loan_card_dj_count = 0
    temp1.foreach(f=>{
      val status24 =  (f \\ "Last24state").text
      if(!"".equals(status24)){
        val status_sp: Array[String] = status24.split(",")
        status_sp.foreach(f=>{
          if(f.matches("\\d*") ){
            if(f.toInt>=1){
              loan_card_dj_count+=1
            }
          }
        })
      }
    })
    /*
    2.第二部分：rh_detail_TblperLoanCardInfo 中准贷记卡
     */
    var loan_card_zdj_count = 0
    val temp2: NodeSeq = loan_info.filter(f=>{"准贷记卡".equals((f \ "CardType").text)})
    temp2.foreach(f=>{
      val status24 =  (f \\ "Last24state").text
      if(!"".equals(status24)){
        val status_sp: Array[String] = status24.split(",")
        status_sp.foreach(f=>{
          if(f.matches("\\d*") ){
            if(f.toInt>=3){
              loan_card_zdj_count+=1
            }
          }
        })
      }
    })

    /*
    3.第三部分：rh_detail_TblperOverdueRecord
     */
    val compare_date = Global.getChangeDate( Global.formatPayDate(Read_From_Mysql.read_appladate(risk_id)),-24)
    val compare_date_month = compare_date.substring(0,7)
    var overdue_count = 0
    val overdue_info = elem \\ "TblperOverdueRecord"
    overdue_info.foreach(m=>{
      val month  = (m \\ "Month").text
      if(Global.formatPayDate(month).compareTo(compare_date_month)>=0){
        val Lastmonths  = (m \\ "Lastmonths").text
        overdue_count+=Lastmonths.toInt
      }
    })
    /*
    4. 第四部分：TblperLoanInfo
     */
    val loan = elem \\ "TblperLoanInfo"
    var loan_count = 0
    loan.foreach(f=>{
      val status24 =  (f \\ "Latest24state").text
      if(!"".equals(status24)){
        val status_sp: Array[String] = status24.split(",")
        status_sp.foreach(f=>{
          if(f.matches("\\d*") ){
            if(f.toInt>=1){
              loan_count+=1
            }
          }
        })
      }
    })

    rh_overdue_times_his = loan_card_dj_count+loan_card_zdj_count+overdue_count+loan_count
    rh_overdue_times_his
  }
}
