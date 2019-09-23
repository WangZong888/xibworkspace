package cn.bigdata.rhdetailindex.deal

import cn.bigdata.rhdetailindex.utils.Global

import scala.xml.Elem

/*
  新增 存有不良违法行为：0-无  1-有
  add by grace 2019-06-04
 */
object Cal_Index_OverdueSummary {

  def cal_illegal(elem: Elem):String = {
    var illegal_state = ""
    var num = 0
    val overdue_info = elem \\ "TblperOverdueSummary"
    overdue_info.foreach(m=>{
      val baddebt_count = Global.getIntValue((m \\ "BadDebtCount").text).getOrElse(0)
      val asset_count = Global.getIntValue((m \\ "AssetCount").text).getOrElse(0)
      val assurerrepay_count = Global.getIntValue((m \\ "AssurerrepayCount").text).getOrElse(0)
      num = baddebt_count + asset_count + assurerrepay_count
    })
    if(num>0){
      illegal_state = "1"
    }else{
      illegal_state = "0"
    }
    illegal_state
  }
}
