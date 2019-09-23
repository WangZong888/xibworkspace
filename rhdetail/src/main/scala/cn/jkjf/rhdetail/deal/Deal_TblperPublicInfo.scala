package cn.jkjf.rhdetail.deal

import java.sql.{Connection, Statement}

import cn.jkjf.rhdetail.utils.{ConnectPoolUtil, Global}

import scala.xml.Elem

object Deal_TblperPublicInfo{

  def  savetomysql(itr: Iterator[Elem]):Unit = {
    var conn: Connection = null
    var stmt: Statement = null
    try{
      conn = ConnectPoolUtil.getConnection()
      conn.setAutoCommit(false)
      stmt = conn.createStatement()
      itr.foreach(f=>{
        val temp = f \\ "TblperPublicInfo"
        temp.foreach(m=>{
          val id = (m \ "Id").text
          val report_id = (m \ "ReportId").text
          val area = (m \ "Area").text
          val register_date = (m \ "Registerdate").text
          val month_duration = Global.getIntValue((m \ "Monthduration").text).getOrElse(0)
          val work_date = (m \ "Workdate").text
          val state = (m \ "State").text
          val own_basic_money = Global.getIntValue((m \ "Ownbasicmoney").text).getOrElse(0)
          val money = Global.getIntValue((m \ "Money").text).getOrElse(0)
          val organ_name = (m \ "Organname").text
          val pause_reason = (m \ "Pausereason").text
          val get_time = (m \ "Gettime").text
          val cus_name = (m \ "CusName").text
          val cert_type = (m \ "CertType").text
          val cert_no = (m \ "CertNo").text
          val input_id = (m \ "InputId").text
          val input_drid = (m \ "InputDrId").text
          val input_date = (m \ "InputDate").text

          if(report_id!=null && !"".equals(report_id) && id!=null && !"".equals(id)){
            stmt.addBatch("replace into rh_detail_tblperpublicinfo(" +
              "id,report_id," +
              "area,register_date,month_duration,work_date,state," +
              "own_basic_money,money,organ_name,pause_reason,get_time," +
              "cus_name,cert_type,cert_no,input_id,input_drid,input_date) " +
              "values(" +
              "'" + id+"','"+report_id+"'," +
              "'"+area+"','"+register_date+"','"+month_duration+ "','"+work_date+"','"+state+"'," +
              "'"+own_basic_money+"','"+money+"','"+organ_name+"','"+pause_reason+"','"+get_time+"'," +
              "'"+cus_name+"','"+cert_type+"','"+cert_no+"','"+input_id+"','"+input_drid+"','"+input_date+"') ")
          }
        })
        println("完成存储：rh_detail_tblperpublicinfo")
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
