package cn.jkjf.rhdetail.deal

import java.sql.{Connection, Statement}

import cn.jkjf.rhdetail.utils.ConnectPoolUtil

import scala.xml.Elem

object Deal_TblZrrGuaranteedList{

  def  savetomysql(itr: Iterator[Elem]):Unit = {
    var conn: Connection = null
    var stmt: Statement = null
    try{
      conn = ConnectPoolUtil.getConnection()
      conn.setAutoCommit(false)
      stmt = conn.createStatement()
      itr.foreach(f=>{
        val temp = f \\ "TblZrrGuaranteedList"
        temp.foreach(m=>{
          val serno = (m \ "Serno").text
          val guar_contract_no = (m \ "GuarContractNo").text
          val id = (m \ "Id").text
          val person_name = (m \ "PersonName").text
          val person_code = (m \ "PersonCode").text
          val input_id = (m \ "InputId").text
          val input_drid = (m \ "InputDrId").text
          val input_date = (m \ "InputDate").text

          if(serno!=null && !"".equals(serno) && id!=null && !"".equals(id)&& guar_contract_no!=null && !"".equals(guar_contract_no)){
            stmt.addBatch("replace into rh_detail_tblzrrguaranteedlist(" +
              "serno,guar_contract_no,id," +
              "person_name,person_code," +
              "input_id,input_drid,input_date) " +
              "values('" + serno+"','"+guar_contract_no+"','"+id+"'," +
              "'"+person_name+"','"+person_code+"'," +
              "'"+input_id+"','"+input_drid+"','"+input_date+"') ")
          }
        })
        println("完成存储：rh_detail_tblzrrguaranteedlist")
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
