package cn.jkjf.rhdetail.deal

import java.sql.{Connection, Statement}

import cn.jkjf.rhdetail.utils.{ConnectPoolUtil, Global}

import scala.xml.Elem

object Deal_TblperResultInfo {

  def  savetomysql(itr: Iterator[Elem]):Unit = {
    var conn: Connection = null
    var stmt: Statement = null
    try{
      conn = ConnectPoolUtil.getConnection()
      conn.setAutoCommit(false)
      stmt = conn.createStatement()
      itr.foreach(f=>{
        val temp = f \\ "TblperResultInfo"
        temp.foreach(m=>{
          val id = Global.getIntValue((m \ "Id").text).getOrElse(0)
          val bar_code = (m \ "BarCode").text
          val report_id = (m \ "ReportSn").text
          val query_time = (m \ "QueryTime").text
          val report_create_time = (m \ "ReportCreateTime").text
          val name = (m \ "Name").text
          val cert_type = (m \ "CertType").text
          val cert_no = (m \ "CertNo").text
          val query_reason = (m \ "QueryReason").text
          val qyery_format = (m \ "QyeryFormat").text
          val query_org = (m \ "QueryOrg").text
          val user_code = (m \ "UserCode").text
          val query_result_cue = (m \ "QueryResultCue").text
          val input_id = (m \ "InputId").text
          val input_brid = (m \ "InputDrId").text
          val input_date = (m \ "InputDate").text

          if(report_id!=null && !"".equals(report_id) && id!=null && !"".equals(id)){
            stmt.addBatch("replace into rh_detail_tblperresultinfo(" +
              "id,bar_code," +
              "report_id,query_time,report_create_time,name,cert_type,cert_no,query_reason," +
              "qyery_format,query_org,user_code,query_result_cue," +
              "input_id,input_brid,input_date) " +
              "values(" +
              "'" + id+"','" + bar_code+"'," +
              "'" + report_id+"','" + query_time+"','" + report_create_time+"','" + name+"','" + cert_type+"','" + cert_no+"','" + query_reason+"'," +
              "'" + qyery_format+"','" + query_org+"','" + user_code+"','" + query_result_cue+"'," +
              "'" + input_id+"','" + input_brid+"','" + input_date+"') ")
          }
        })
        println("完成存储：rh_detail_tblperresultinfo")
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
