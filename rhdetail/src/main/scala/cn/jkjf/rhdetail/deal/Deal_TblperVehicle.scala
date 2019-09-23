package cn.jkjf.rhdetail.deal

import java.sql.{Connection, Statement}

import cn.jkjf.rhdetail.utils.ConnectPoolUtil

import scala.xml.Elem

object Deal_TblperVehicle{

  def  savetomysql(itr: Iterator[Elem]):Unit = {
    var conn: Connection = null
    var stmt: Statement = null
    try{
      conn = ConnectPoolUtil.getConnection()
      conn.setAutoCommit(false)
      stmt = conn.createStatement()
      itr.foreach(f=>{
        val temp = f \\ "TblperVehicle"
        temp.foreach(m=>{
          val id = (m \ "Id").text
          val report_id = (m \ "ReportId").text
          val engine_code = (m \ "Enginecode").text
          val license_code = (m \ "Licensecode").text
          val brand = (m \ "Brand").text
          val car_type = (m \ "Cartype").text
          val use_character = (m \ "Usecharacter").text
          val state = (m \ "State").text
          val pledge_flag = (m \ "Pledgeflag").text
          val get_time = (m \ "Gettime").text
          val cus_name = (m \ "CusName").text
          val cert_type = (m \ "CertType").text
          val cert_no = (m \ "CertNo").text
          val input_id = (m \ "InputId").text
          val input_drid = (m \ "InputDrId").text
          val input_date = (m \ "InputDate").text

          if(report_id!=null && !"".equals(report_id) && id!=null && !"".equals(id)){
            stmt.addBatch("replace into rh_detail_tblpervehicle(" +
              "id,report_id," +
              "engine_code,license_code,brand,car_type," +
              "use_character,state,pledge_flag,get_time," +
              "cus_name,cert_type,cert_no,input_id,input_drid,input_date) " +
              "values(" +
              "'" + id+"','"+report_id+"'," +
              "'"+engine_code+"','"+license_code+"','"+brand+ "','"+car_type+"'," +
              "'"+use_character+"','"+state+"','"+pledge_flag+"','"+get_time+"'," +
              "'"+cus_name+"','"+cert_type+"','"+cert_no+"','"+input_id+"','"+input_drid+"','"+input_date+"') ")
          }
        })
        println("完成存储：rh_detail_tblpervehicle")
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
