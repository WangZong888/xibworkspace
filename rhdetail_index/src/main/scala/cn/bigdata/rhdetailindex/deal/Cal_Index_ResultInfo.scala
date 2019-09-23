package cn.bigdata.rhdetailindex.deal

import cn.bigdata.rhdetailindex.utils.Global

import scala.xml.Elem

object Cal_Index_ResultInfo {

  def cal_indx(elem: Elem, risk_id: String, query_time: String): Int = {
    var inquiry_num_3m = 0
    val compare_date = Global.getChangeDate(Global.formatPayDate(Read_From_Mysql.read_appladate(risk_id)), -3)
    val recorddetail_info = elem \\ "TblperRecordDetail"
    recorddetail_info
      .filter(f => {
        ("贷款审批".equals((f \\ "Queryreason").text) || "信用卡审批".equals((f \\ "Queryreason").text)) && !"".equals(query_time) && !query_time.equals((f \\ "Querydate").text)
      })
      .foreach(m => {
        val per_querydate = (m \\ "Querydate").text
        val id = (m \\ "Id").text
        if (Global.formatPayDate(per_querydate).compareTo(compare_date) >= 0) {
          println("per_querydate----->" + Global.formatPayDate(per_querydate) + ",compare_date------>" + compare_date)
          inquiry_num_3m += 1

        }
      })
    inquiry_num_3m
  }
}
