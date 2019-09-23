package cn.jkjf.rhdetail.deal

import java.sql.{Connection, Statement}

import cn.jkjf.rhdetail.utils.{ConnectPoolUtil, Global}

import scala.xml.Elem

object Deal_TblperTelPayment{

  def  savetomysql(itr: Iterator[Elem]):Unit = {
    var conn: Connection = null
    var stmt: Statement = null
    try{
      conn = ConnectPoolUtil.getConnection()
      conn.setAutoCommit(false)
      stmt = conn.createStatement()
      itr.foreach(f=>{
        val temp = f \\ "TblperTelPayment"
        temp.foreach(m=>{
          val id = (m \ "Id").text
          val report_id = (m \ "ReportId").text
          val organ_name = (m \ "Organname").text
          val tel_pay_type = (m \ "Type").text
          val register_date = (m \ "Registerdate").text
          val state = (m \ "State").text
          val arrear_money = Global.getIntValue((m \ "Arrearmoney").text).getOrElse(0)
          val arrear_months = (m \ "Arrearmonths").text
          val status24 = (m \ "Status24").text
          val get_time = (m \ "Gettime").text
          val cus_name = (m \ "CusName").text
          val cert_type = (m \ "CertType").text
          val cert_no = (m \ "CertNo").text
          val input_id = (m \ "InputId").text
          val input_drid = (m \ "InputDrId").text
          val input_date = (m \ "InputDate").text

          if(report_id!=null && !"".equals(report_id) && id!=null && !"".equals(id)){
            stmt.addBatch("replace into rh_detail_tblpertelpayment(" +
              "id,report_id," +
              "organ_name,tel_pay_type,register_date,state," +
              "arrear_money,arrear_months,status24,get_time," +
              "cus_name,cert_type,cert_no,input_id,input_drid,input_date) " +
              "values(" +
              "'" + id+"','"+report_id+"'," +
              "'"+organ_name+"','"+tel_pay_type+"','"+register_date+ "','"+state+ "'," +
              "'"+arrear_money+ "','"+arrear_months+ "','"+status24+ "','"+get_time+ "'," +
              "'"+cus_name+"','"+cert_type+"','"+cert_no+"','"+input_id+"','"+input_drid+"','"+input_date+"') ")
          }
        })
        println("完成存储：rh_detail_tblpertelpayment")
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
