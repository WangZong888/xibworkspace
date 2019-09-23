package cn.jkjf.rhdetail.deal

import java.sql.{Connection, Statement}

import cn.jkjf.rhdetail.utils.{ConnectPoolUtil, Global}

import scala.xml.Elem

object Deal_TblZrrGuarLoanMainInfo{

  def  savetomysql(itr: Iterator[Elem]):Unit = {
    var conn: Connection = null
    var stmt: Statement = null
    try{
      conn = ConnectPoolUtil.getConnection()
      conn.setAutoCommit(false)
      stmt = conn.createStatement()
      itr.foreach(f=>{
        val temp = f \\ "TblZrrGuarLoanMainInfo"
        temp.foreach(m=>{
          val serno = (m \ "Serno").text
          val guar_contract_no = (m \ "GuarContractNo").text
          val id = (m \ "Id").text
          val main_busi_no = (m \ "MainBusiNo").text
          val main_busi_amt = Global.getDoubleValue((m \ "MainBusiAmt").text).getOrElse(0.0)
          val main_busi_bal = Global.getDoubleValue((m \ "MainBusiBal").text).getOrElse(0.0)
          val busi_curry = (m \ "BusiCurry").text
          val status = (m \ "Status").text
          val cust_name = (m \ "CustName").text
          val five_class = (m \ "FiveClass").text
          val input_id = (m \ "InputId").text
          val input_drid = (m \ "InputDrId").text
          val input_date = (m \ "InputDate").text

          if(serno!=null && !"".equals(serno) && id!=null && !"".equals(id)&& guar_contract_no!=null && !"".equals(guar_contract_no)){
            stmt.addBatch("replace into rh_detail_tblzrrguarloanmaininfo(" +
              "serno,guar_contract_no,id," +
              "main_busi_no,main_busi_amt,main_busi_bal,busi_curry,status,cust_name,five_class," +
              "input_id,input_drid,input_date) " +
              "values('" + serno+"','"+guar_contract_no+"','"+id+"'," +
              "'"+main_busi_no+"','"+main_busi_amt+"','"+main_busi_bal+"','"+busi_curry+"','"+status+"','"+cust_name+"','"+five_class+"'," +
              "'"+input_id+"','"+input_drid+"','"+input_date+"') ")
          }
        })
        println("完成存储：rh_detail_tblzrrguarloanmaininfo")
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
