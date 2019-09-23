package cn.jkjf.rhdetail.deal

import java.sql.{Connection, Statement}

import cn.jkjf.rhdetail.utils.ConnectPoolUtil

import scala.xml.Elem

object Deal_TblperCompetence{

  def  savetomysql(itr: Iterator[Elem]):Unit = {
    var conn: Connection = null
    var stmt: Statement = null
    try{
      conn = ConnectPoolUtil.getConnection()
      conn.setAutoCommit(false)
      stmt = conn.createStatement()
      itr.foreach(f=>{
        val temp = f \\ "TblperCompetence"
        temp.foreach(m=>{
          val id = (m \ "Id").text
          val report_id = (m \ "ReportId").text
          val competency_name = (m \ "Competencyname").text
          val grade = (m \ "Grade").text
          val award_date = (m \ "Awarddate").text
          val end_date = (m \ "Enddate").text
          val revoke_date = (m \ "Revokedate").text
          val organ_name = (m \ "Organname").text
          val area = (m \ "Area").text
          val cus_name = (m \ "CusName").text
          val cert_type = (m \ "CertType").text
          val cert_no = (m \ "CertNo").text
          val input_id = (m \ "InputId").text
          val input_drid = (m \ "InputDrId").text
          val input_date = (m \ "InputDate").text

          if(report_id!=null && !"".equals(report_id) && id!=null && !"".equals(id)){
            stmt.addBatch("replace into rh_detail_tblpercompetence(" +
              "id,report_id," +
              "competency_name,grade,award_date,end_date,revoke_date,organ_name,area," +
              "cus_name,cert_type,cert_no,input_id,input_drid,input_date) " +
              "values('" + id+"','"+report_id+"'," +
              "'"+competency_name+"','"+grade+"','"+award_date+ "','"+end_date+ "','"+revoke_date+ "','"+organ_name+ "','"+area+ "'," +
              "'"+cus_name+"','"+cert_type+"','"+cert_no+"','"+input_id+"','"+input_drid+"','"+input_date+"') ")
          }
        })
        println("完成存储：rh_detail_tblpercompetence")
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
