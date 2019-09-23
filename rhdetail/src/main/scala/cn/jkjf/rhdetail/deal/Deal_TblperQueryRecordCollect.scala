package cn.jkjf.rhdetail.deal

import java.sql.{Connection, Statement}

import cn.jkjf.rhdetail.utils.{ConnectPoolUtil, Global}

import scala.xml.Elem

object Deal_TblperQueryRecordCollect{

  def  savetomysql(itr: Iterator[Elem]):Unit = {
    var conn: Connection = null
    var stmt: Statement = null
    try{
      conn = ConnectPoolUtil.getConnection()
      conn.setAutoCommit(false)
      stmt = conn.createStatement()
      itr.foreach(f=>{
        val temp = f \\ "TblperQueryRecordCollect"
        temp.foreach(m=>{
          val id = (m \ "Id").text
          val report_id = (m \ "ReportId").text
          val lastmonth_org_loanapprove = Global.getIntValue((m \ "LastmonthorgLoanapprove").text).getOrElse(0)
          val lastmonth_org_cardapprove = Global.getIntValue((m \ "LastmonthorgCardapprove").text).getOrElse(0)
          val lastmonth_query_loanapprove = Global.getIntValue((m \ "LastmonthqueryLoanapprove").text).getOrElse(0)
          val lastmonth_query_cardapprove = Global.getIntValue((m \ "LastmonthqueryCardapprove").text).getOrElse(0)
          val lastmonth_query_selfquery = Global.getIntValue((m \ "LastmonthquerySelfquery").text).getOrElse(0)
          val last2year_query_loanmanage = Global.getIntValue((m \ "Last2yearqueryLoanmanage").text).getOrElse(0)
          val last2year_query_guarantee = Global.getIntValue((m \ "Last2yearqueryGuarantee").text).getOrElse(0)
          val last2year_query_realcommer = Global.getIntValue((m \ "Last2yearqueryRealcommer").text).getOrElse(0)
          val cus_name = (m \ "CusName").text
          val cert_type = (m \ "CertType").text
          val cert_no = (m \ "CertNo").text
          val input_id = (m \ "InputId").text
          val input_drid = (m \ "InputDrId").text
          val input_date = (m \ "InputDate").text

          if(report_id!=null && !"".equals(report_id) && id!=null && !"".equals(id)){
            stmt.addBatch("replace into rh_detail_tblperqueryrecordcollect(" +
              "id,report_id," +
              "lastmonth_org_loanapprove,lastmonth_org_cardapprove," +
              "lastmonth_query_loanapprove,lastmonth_query_cardapprove,lastmonth_query_selfquery," +
              "last2year_query_loanmanage,last2year_query_guarantee,last2year_query_realcommer," +
              "cus_name,cert_type,cert_no,input_id,input_drid,input_date) " +
              "values(" +
              "'" + id+"','"+report_id+"'," +
              "'"+lastmonth_org_loanapprove+"','"+lastmonth_org_cardapprove+"'," +
              "'"+lastmonth_query_loanapprove+ "','"+lastmonth_query_cardapprove+"','"+lastmonth_query_selfquery+"'," +
              "'"+last2year_query_loanmanage+"','"+last2year_query_guarantee+"','"+last2year_query_realcommer+"'," +
              "'"+cus_name+"','"+cert_type+"','"+cert_no+"','"+input_id+"','"+input_drid+"','"+input_date+"') ")
          }
        })
        println("完成存储：rh_detail_tblperqueryrecordcollect")
      })
      stmt.executeBatch()
      conn.commit()
    }catch {
      case e:Exception=>e.printStackTrace();conn.rollback()
    }finally {
      ConnectPoolUtil.closeCon(stmt,conn)
    }
  }
}
