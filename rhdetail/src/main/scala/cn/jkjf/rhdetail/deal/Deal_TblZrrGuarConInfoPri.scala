package cn.jkjf.rhdetail.deal

import java.sql.{Connection, Statement}

import cn.jkjf.rhdetail.utils.{ConnectPoolUtil, Global}

import scala.xml.Elem

object Deal_TblZrrGuarConInfoPri {

  def  savetomysql(itr: Iterator[Elem]):Unit = {
    var conn: Connection = null
    var stmt: Statement = null
    try{
      conn = ConnectPoolUtil.getConnection()
      conn.setAutoCommit(false)
      stmt = conn.createStatement()
      itr.foreach(f=>{
        val temp = f \\ "TblZrrGuarConInfoPri"
        temp.foreach(m=>{
          val id = (m \ "Id").text
          val serno = (m \ "Serno").text
          val guar_contract_no = (m \ "GuarContractNo").text
          val guar_contract_amt = Global.getDoubleValue((m \ "GuarContractAmt").text).getOrElse(0.0)
          val guar_contract_currency = (m \ "GuarContractCurrency").text
          val guar_amt = Global.getDoubleValue((m \ "GuarAmt").text).getOrElse(0.0)
          val guar_currency = (m \ "GuarCurrency").text
          val org_name = (m \ "OrgName").text
          val guar_contract_status = (m \ "GuarContractStatus").text
          val signdate = (m \ "SignDate").text
          val guar_contract_type = (m \ "GuarContractType").text
          val guarantee_way = (m \ "GuaranteeWay").text
          val guar_cus_id = (m \ "GuarCusId").text
          val guar_cus_name = (m \ "GuarCusName").text
          val guar_cus_cert_type = (m \ "GuarCusCertType").text
          val guar_cus_cert_no = (m \ "GuarCusCertNo").text
          val input_id = (m \ "InputId").text
          val input_drid = (m \ "InputDrId").text
          val input_date = (m \ "InputDate").text

          if(serno!=null && !"".equals(serno) && guar_contract_no!=null && !"".equals(guar_contract_no) && id!=null && !"".equals(id)){
            stmt.addBatch("replace into rh_detail_tblzrrguarconinfopri(" +
              "id,serno,guar_contract_no," +
              "guar_contract_amt,guar_contract_currency,guar_amt," +
              "guar_currency,org_name,guar_contract_status,signdate," +
              "guar_contract_type,guarantee_way,guar_cus_id,guar_cus_name,guar_cus_cert_type,guar_cus_cert_no," +
              "input_id,input_drid,input_date) " +
              "values(" +
              "'" + id+"','"+serno+"','"+guar_contract_no+"'," +
              "'"+guar_contract_amt+"','"+guar_contract_currency+ "','"+guar_amt+"'," +
              "'"+guar_currency+"','"+org_name+"','"+guar_contract_status+"','"+signdate+"','" +
              ""+guar_contract_type+"','"+guarantee_way+"','"+guar_cus_id+"','"+guar_cus_name+"','"+guar_cus_cert_type+"','"+guar_cus_cert_no+"'," +
              "'"+input_id+"','"+input_drid+"','"+input_date+"') ")
          }
        })
        println("完成存储：rh_detail_tblzrrguarconinfopri")
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
