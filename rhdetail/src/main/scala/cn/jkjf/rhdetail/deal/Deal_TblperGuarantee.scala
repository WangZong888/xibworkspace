package cn.jkjf.rhdetail.deal

import java.sql.{Connection, Statement}

import cn.jkjf.rhdetail.utils.{ConnectPoolUtil, Global}

import scala.xml.Elem

object Deal_TblperGuarantee{

  def  savetomysql(itr: Iterator[Elem]):Unit = {
    var conn: Connection = null
    var stmt: Statement = null
    try{
      conn = ConnectPoolUtil.getConnection()
      conn.setAutoCommit(false)
      stmt = conn.createStatement()
      itr.foreach(f=>{
        val temp = f \\ "TblperGuarantee"
        temp.foreach(m=>{
          val id = (m \ "Id").text
          val report_id = (m \ "ReportId").text
          val organ_name = (m \ "Organname").text
          val contract_money = (m \ "Contractmoney").text
          val begin_date = (m \ "Begindate").text
          val end_date = (m \ "Enddate").text
          val guanantee_money = Global.getIntValue((m \ "Guananteemoney").text).getOrElse(0)
          val guarantee_balance = Global.getIntValue((m \ "Guaranteebalance").text).getOrElse(0)
          val class5_state = (m \ "Class5state").text
          val billing_date = (m \ "Billingdate").text
          val cus_name = (m \ "CusName").text
          val cert_type = (m \ "CertType").text
          val cert_no = (m \ "CertNo").text
          val input_id = (m \ "InputId").text
          val input_drid = (m \ "InputDrId").text
          val input_date = (m \ "InputDate").text

          if(report_id!=null && !"".equals(report_id) && id!=null && !"".equals(id)){
            stmt.addBatch("replace into rh_detail_tblperguarantee(" +
              "id,report_id," +
              "organ_name,contract_money,begin_date,end_date," +
              "guanantee_money,guarantee_balance,class5_state,billing_date," +
              "cus_name,cert_type,cert_no,input_id,input_drid,input_date) " +
              "values(" +
              "'" + id+"','"+report_id+"'," +
              "'"+organ_name+"','"+contract_money+"','"+begin_date+ "','"+end_date+"'," +
              "'"+guanantee_money+"','"+guarantee_balance+"','"+class5_state+"','"+billing_date+"'," +
              "'"+cus_name+"','"+cert_type+"','"+cert_no+"','"+input_id+"','"+input_drid+"','"+input_date+"') ")
          }
        })
        println("完成存储：rh_detail_tblperguarantee")
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
