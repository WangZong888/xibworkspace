package cn.bigdata.rhdetailindex.deal

import cn.bigdata.rhdetailindex.utils.Global

import scala.xml.{Elem, NodeSeq}

/*7.计算  历史逾期次数指标
          需求变更，指标改为距离申请日期近两年 add by grace 2019-06-04
          需求变更，增加贷记卡和准贷记卡的区别 add by grace 2019-07-24
*/
object Cal_Index_Overdue_5y {

  def cal_index(elem: Elem,risk_id:String):Int = {
    var rh_overdue_times_his_5y = 0
    val loan_info = elem \\ "TblperLoanCardInfo"
    /*
    1.第一部分：rh_detail_TblperLoanCardInfo 中贷记卡
     */
    val temp1: NodeSeq = loan_info.filter(f=>{"贷记卡".equals((f \ "CardType").text)})
    var max1 = 0
    temp1.foreach(f=>{
      val status24 =  (f \\ "Last24state").text
      if(!"".equals(status24)){
        val status_sp: Array[String] = status24.split(",")
        status_sp.foreach(f=>{
          if(f.matches("\\d*") ){
            if(f.toInt>=1){
              if(f.toInt>max1){
                max1 = f.toInt
              }
            }
          }
        })
      }
    })
    /*
    2.第二部分：rh_detail_TblperLoanCardInfo 中准贷记卡
     */
    var max2 = 0
    val temp2: NodeSeq = loan_info.filter(f=>{"准贷记卡".equals((f \ "CardType").text)})
    temp2.foreach(f=>{
      val status24 =  (f \\ "Last24state").text
      if(!"".equals(status24)){
        val status_sp: Array[String] = status24.split(",")
        status_sp.foreach(f=>{
          if(f.matches("\\d*") ){
            if(f.toInt>=3){
              if(f.toInt>max2){
                max2 = f.toInt
              }
            }
          }
        })
      }
    })

    if(max2>0){
      max2 = max2-2
    }
    /*
    3.第三部分：rh_detail_TblperOverdueRecord
     */
    val compare_date = Global.getChangeDate( Global.formatPayDate(Read_From_Mysql.read_appladate(risk_id)),-60)
    val compare_date_month = compare_date.substring(0,7)
    var max3 = 0
    val overdue_info = elem \\ "TblperOverdueRecord"
    overdue_info.foreach(m=>{
      val month  = (m \\ "Month").text
      val Lastmonths  = ((m \\ "Lastmonths").text).toInt

      if(Global.formatPayDate(month).compareTo(compare_date_month)>=0){
        if(Lastmonths>max3){
          max3 = Lastmonths
        }
      }
    })
    /*
    4. 第四部分：TblperLoanInfo
     */
    val loan = elem \\ "TblperLoanInfo"
    var max4 = 0
    loan.foreach(f=>{
      val status24 =  (f \\ "Latest24state").text
      if(!"".equals(status24)){
        val status_sp: Array[String] = status24.split(",")
        status_sp.foreach(f=>{
          if(f.matches("\\d*") ){
            if(f.toInt>=3){
              if(f.toInt>max4){
                max4 = f.toInt
              }
            }
          }
        })
      }
    })
    val arr = Array(max1,max2,max3,max4)

    rh_overdue_times_his_5y = arr.max
    rh_overdue_times_his_5y
  }
}
