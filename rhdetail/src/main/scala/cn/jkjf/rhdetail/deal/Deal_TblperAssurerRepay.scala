package cn.jkjf.rhdetail.deal

import java.sql.{Connection, Statement}

import cn.jkjf.rhdetail.utils.{ConnectPoolUtil, Global}

import scala.xml.Elem

object Deal_TblperAssurerRepay{

  def  savetomysql(itr: Iterator[Elem]):Unit = {
    var conn: Connection = null
    var stmt: Statement = null
    try{
      conn = ConnectPoolUtil.getConnection()
      conn.setAutoCommit(false)
      stmt = conn.createStatement()
      itr.foreach(f=>{
        val temp = f \\ "TblperAssurerRepay"
        temp.foreach(m=>{
          val id = (m \ "Id").text
          val report_id = (m \ "ReportId").text
          val organ_name = (m \ "Organname").text
          val latest_assurerrepay_date = (m \ "Latestassurerrepaydate").text
          val money = Global.getIntValue((m \ "Money").text).getOrElse(0)
          val latest_repay_date = (m \ "Latestrepaydate").text
          val balance = Global.getIntValue((m \ "Balance").text).getOrElse(0)
          val cus_name = (m \ "CusName").text
          val cert_type = (m \ "CertType").text
          val cert_no = (m \ "CertNo").text
          val input_id = (m \ "InputId").text
          val input_drid = (m \ "InputDrId").text
          val input_date = (m \ "InputDate").text

          if(report_id!=null && !"".equals(report_id) && id!=null && !"".equals(id)){
            stmt.addBatch("replace into rh_detail_tblperassurerrepay(" +
              "id,report_id," +
              "organ_name,latest_assurerrepay_date,money,latest_repay_date,balance," +
              "cus_name,cert_type,cert_no,input_id,input_drid,input_date) " +
              "values(" +
              "'"+id+"','"+report_id+"'," +
              "'"+organ_name+"','"+latest_assurerrepay_date+"','"+money+"','"+latest_repay_date+"','"+balance+"'," +
              "'"+cus_name+"','"+cert_type+"','"+cert_no+"','"+input_id+"','"+input_drid+"','"+input_date+"') ")
          }

        })
        println("完成存储：rh_detail_tblperassurerrepay")
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
