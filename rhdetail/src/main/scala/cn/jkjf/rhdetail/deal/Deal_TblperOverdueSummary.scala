package cn.jkjf.rhdetail.deal

import java.sql.{Connection, Statement}

import cn.jkjf.rhdetail.utils.{ConnectPoolUtil, Global}

import scala.xml.Elem

object Deal_TblperOverdueSummary{

  def  savetomysql(itr: Iterator[Elem]):Unit = {
    var conn: Connection = null
    var stmt: Statement = null
    try{
      conn = ConnectPoolUtil.getConnection()
      conn.setAutoCommit(false)
      stmt = conn.createStatement()
      itr.foreach(f=>{
        val temp = f \\ "TblperOverdueSummary"
        temp.foreach(m=>{
          val id = (m \ "Id").text
          val report_id = (m \ "ReportId").text
          val bad_debt_count = Global.getIntValue((m \ "BadDebtCount").text).getOrElse(0)
          val bad_debt_balance = Global.getIntValue((m \ "BadDebtBalance").text).getOrElse(0)
          val asset_count = Global.getIntValue((m \ "AssetCount").text).getOrElse(0)
          val asset_balance = Global.getIntValue((m \ "AssetBalance").text).getOrElse(0)
          val assurer_repay_count = Global.getIntValue((m \ "AssurerrepayCount").text).getOrElse(0)
          val assurer_repay_balance = Global.getIntValue((m \ "AssurerrepayBalance").text).getOrElse(0)
          val cus_name = (m \ "CusName").text
          val cert_type = (m \ "CertType").text
          val cert_no = (m \ "CertNo").text
          val input_id = (m \ "InputId").text
          val input_drid = (m \ "InputDrId").text
          val input_date = (m \ "InputDate").text

          if(report_id!=null && !"".equals(report_id) && id!=null && !"".equals(id)){
            stmt.addBatch("replace into rh_detail_tblperoverduesummary(" +
              "id,report_id," +
              "bad_debt_count,bad_debt_balance,asset_count,asset_balance,assurer_repay_count,assurer_repay_balance," +
              "cus_name,cert_type,cert_no,input_id,input_drid,input_date) " +
              "values(" +
              "'" + id+"','"+report_id+"'," +
              "'"+bad_debt_count+"','"+bad_debt_balance+"','"+asset_count+ "','"+asset_balance+"','"+assurer_repay_count+"','"+assurer_repay_balance+"'," +
              "'"+cus_name+"','"+cert_type+"','"+cert_no+"','"+input_id+"','"+input_drid+"','"+input_date+"') ")
          }
        })
        println("完成存储：rh_detail_tblperoverduesummary")
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
