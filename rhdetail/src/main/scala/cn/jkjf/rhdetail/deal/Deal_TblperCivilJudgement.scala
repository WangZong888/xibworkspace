package cn.jkjf.rhdetail.deal

import java.sql.{Connection, Statement}

import cn.jkjf.rhdetail.utils.{ConnectPoolUtil, Global}

import scala.xml.Elem

object Deal_TblperCivilJudgement{

  def  savetomysql(itr: Iterator[Elem]):Unit = {
    var conn: Connection = null
    var stmt: Statement = null
    try{
      conn = ConnectPoolUtil.getConnection()
      conn.setAutoCommit(false)
      stmt = conn.createStatement()
      itr.foreach(f=>{
        val temp = f \\ "TblperCivilJudgement"
        temp.foreach(m=>{
          val id = (m \ "Id").text
          val report_id = (m \ "ReportId").text
          val court = (m \ "Court").text
          val case_reason = (m \ "Casereason").text
          val register_date = (m \ "Registerdate").text
          val closed_type = (m \ "Closedtype").text
          val case_result = (m \ "Caseresult").text
          val case_valid_date = (m \ "Casevalidatedate").text
          val suit_object = (m \ "Suitobject").text
          val suit_object_money = Global.getIntValue((m \ "Suitobjectmoney").text).getOrElse(0)
          val cus_name = (m \ "CusName").text
          val cert_type = (m \ "CertType").text
          val cert_no = (m \ "CertNo").text
          val input_id = (m \ "InputId").text
          val input_drid = (m \ "InputDrId").text
          val input_date = (m \ "InputDate").text

          if(report_id!=null && !"".equals(report_id) && id!=null && !"".equals(id)){
            stmt.addBatch("replace into rh_detail_tblperciviljudgement(" +
              "id,report_id," +
              "court,case_reason,register_date,closed_type,case_result,case_valid_date,suit_object,suit_object_money," +
              "cus_name,cert_type,cert_no,input_id,input_drid,input_date) " +
              "values(" +
              "'"+id+"','"+report_id+"'," +
              "'"+court+"','"+case_reason+"','"+register_date+"','"+closed_type+"','"+case_result+"','"+case_valid_date+"','"+suit_object+"','"+suit_object_money+"'," +
              "'"+cus_name+"','"+cert_type+"','"+cert_no+"','"+input_id+"','"+input_drid+"','"+input_date+"') ")
          }
        })
        println("完成存储：rh_detail_tblperciviljudgement")
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
