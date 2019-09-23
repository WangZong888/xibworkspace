package cn.jkjf.rhdetail.deal

import java.sql.{Connection, Statement}

import cn.jkjf.rhdetail.utils.{ConnectPoolUtil, Global}

import scala.xml.Elem

object Deal_TblZrrGuarLoanSumInfo{

  def  savetomysql(itr: Iterator[Elem]):Unit = {
    var conn: Connection = null
    var stmt: Statement = null
    try{
      conn = ConnectPoolUtil.getConnection()
      conn.setAutoCommit(false)
      stmt = conn.createStatement()
      itr.foreach(f=>{
        val temp = f \\ "TblZrrGuarLoanSumInfo"
        temp.foreach(m=>{
          val serno = (m \ "Serno").text
          val guar_contract_no = (m \ "GuarContractNo").text
          val id = (m \ "Id").text
          val business_amt = Global.getDoubleValue((m \ "BusinessAmt").text).getOrElse(0.0)
          val business_bal = Global.getDoubleValue((m \ "BusinessBal").text).getOrElse(0.0)
          val business_type = (m \ "BusinessType").text
          val input_id = (m \ "InputId").text
          val input_drid = (m \ "InputDrId").text
          val input_date = (m \ "InputDate").text

          if(serno!=null && !"".equals(serno) && id!=null && !"".equals(id)&& guar_contract_no!=null && !"".equals(guar_contract_no)){
            stmt.addBatch("replace into rh_detail_tblzrrguarloansuminfo(" +
              "serno,guar_contract_no,id," +
              "business_amt,business_bal,business_type," +
              "input_id,input_drid,input_date) " +
              "values(" +
              "'" + serno+"','"+guar_contract_no+"','"+id+"'," +
              "'"+business_amt+"','"+business_bal+"','"+business_type+"'," +
              "'"+input_id+"','"+input_drid+"','"+input_date+"') ")
          }
        })
        println("完成存储：rh_detail_tblzrrguarloansuminfo")
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
