package cn.jkjf.rhdetail.deal

import java.sql.{Connection, Statement}

import cn.jkjf.rhdetail.utils.{ConnectPoolUtil, Global}

import scala.xml.Elem

object Deal_TblperSpecialTrade{

  def  savetomysql(itr: Iterator[Elem]):Unit = {
    var conn: Connection = null
    var stmt: Statement = null
    try{
      conn = ConnectPoolUtil.getConnection()
      conn.setAutoCommit(false)
      stmt = conn.createStatement()
      itr.foreach(f=>{
        val temp = f \\ "TblperSpecialTrade"
        temp.foreach(m=>{
          val id = (m \ "Id").text
          val report_id = (m \ "ReportId").text
          val loan_id = (m \ "LoanId").text
          val changing_type = (m \ "Changingtype").text
          val get_time = (m \ "Gettime").text
          val ghanging_months = Global.getIntValue((m \ "Ghangingmonths").text).getOrElse(0)
          val ghanging_amount = Global.getIntValue((m \ "Ghangingamount").text).getOrElse(0)
          val content = (m \ "Content").text
          val org_remark = (m \ "Orgremark").text
          val org_remark_date = (m \ "Orgremarkdate").text
          val self_statement = (m \ "Selfstatement").text
          val self_statement_date = (m \ "Selfstatementdate").text
          val object_label = (m \ "Objectlabel").text
          val object_label_date = (m \ "Objectlabeldate").text
          val cus_name = (m \ "CusName").text
          val cert_type = (m \ "CertType").text
          val cert_no = (m \ "CertNo").text
          val input_id = (m \ "InputId").text
          val input_drid = (m \ "InputDrId").text
          val input_date = (m \ "InputDate").text

          if(report_id!=null && !"".equals(report_id) && id!=null && !"".equals(id)){
            stmt.addBatch("replace into rh_detail_tblperspecialtrade(" +
              "id,report_id," +
              "loan_id,changing_type,get_time,ghanging_months,ghanging_amount,content,org_remark," +
              "org_remark_date,self_statement,self_statement_date,object_label,object_label_date," +
              "cus_name,cert_type,cert_no,input_id,input_drid,input_date) " +
              "values(" +
              "'" + id+"','"+report_id+"'," +
              "'"+loan_id+"','"+changing_type+"','"+get_time+ "','"+ghanging_months+"','"+ghanging_amount+"','"+content+"','"+org_remark+"'," +
              "'"+org_remark_date+"','"+self_statement+"','"+self_statement_date+"','"+object_label+"','"+object_label_date+"'," +
              "'"+cus_name+"','"+cert_type+"','"+cert_no+"','"+input_id+"','"+input_drid+"','"+input_date+"') ")
          }
        })
        println("完成存储：rh_detail_tblperspecialtrade")
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
