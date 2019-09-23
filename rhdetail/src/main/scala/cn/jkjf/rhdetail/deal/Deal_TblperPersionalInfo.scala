package cn.jkjf.rhdetail.deal

import java.sql.{Connection, Statement}

import cn.jkjf.rhdetail.utils.ConnectPoolUtil

import scala.xml.Elem

object Deal_TblperPersionalInfo {

  def  savetomysql(itr: Iterator[Elem]):Unit = {
    var conn: Connection = null
    var stmt: Statement = null
    try{
      conn = ConnectPoolUtil.getConnection()
      conn.setAutoCommit(false)
      stmt = conn.createStatement()
      itr.foreach(f=>{
        val temp = f \\ "TblperPersionalInfo"
        temp.foreach(m=>{
          val id = (m \ "Id").text
          val report_id = (m \ "ReportId").text
          val gender = (m \ "Gender").text
          val birthday = (m \ "Birthday").text
          val marital_state = (m \ "MaritalStste").text
          val mobile = (m \ "Mobile").text
          val officetele_phone_no = (m \ "OfficeTelephoneNo").text
          val hometele_phone_no = (m \ "HomeTelephoneNo").text
          val edu_level = (m \ "EduLevel").text
          val edu_degree = (m \ "EduDegree").text
          val post_address = (m \ "PostAddress").text
          val registered_address = (m \ "RegisteredAddress").text
          val spouse_name = (m \ "SpouseName").text
          val spouse_cert_type = (m \ "SpouseCertType").text
          val spouse_cert_no = (m \ "SpouseCertNo").text
          val spouse_employer = (m \ "SpouseEmployer").text
          val spouse_telephome_no = (m \ "SpouseTelephomeNo").text
          val cus_name = (m \ "CusName").text
          val cert_type = (m \ "CertType").text
          val cert_no = (m \ "CertNo").text
          val input_id = (m \ "InputId").text
          val input_drid = (m \ "InputDrId").text
          val input_date = (m \ "InputDate").text

          if(report_id!=null && !"".equals(report_id) && id!=null && !"".equals(id)){
            stmt.addBatch( "replace into rh_detail_tblperpersionalinfo(" +
              "id,report_id," +
              "gender,birthday,marital_state,mobile,officetele_phone_no,hometele_phone_no,edu_level,edu_degree," +
              "post_address,registered_address,spouse_name,spouse_cert_type,spouse_cert_no,spouse_employer,spouse_telephome_no," +
              "cus_name,cert_type,cert_no,input_id,input_drid,input_date) " +
              "values(" +
              "'"+id+"','"+report_id+"'," +
              "'"+gender+"','"+birthday+"','"+marital_state+ "','"+mobile+"','"+officetele_phone_no+"','"+hometele_phone_no+"','"+edu_level+"'," +
              "'"+edu_degree+"','"+post_address+"','"+registered_address+"','"+spouse_name+"','"+spouse_cert_type+"','"+spouse_cert_no+"','"+spouse_employer+"','"+spouse_telephome_no+"'," +
              "'"+cus_name+"','"+cert_type+"','"+cert_no+"','"+input_id+"','"+input_drid+"','"+input_date+"') ")
          }
        })
        println("完成存储：rh_detail_tblperpersionalinfo")
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
