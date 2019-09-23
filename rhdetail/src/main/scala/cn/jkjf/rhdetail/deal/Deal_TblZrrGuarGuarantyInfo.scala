package cn.jkjf.rhdetail.deal

import java.sql.{Connection, Statement}

import cn.jkjf.rhdetail.utils.{ConnectPoolUtil, Global}

import scala.xml.Elem

object Deal_TblZrrGuarGuarantyInfo{

  def  savetomysql(itr: Iterator[Elem]):Unit = {
    var conn: Connection = null
    var stmt: Statement = null
    try{
      conn = ConnectPoolUtil.getConnection()
      conn.setAutoCommit(false)
      stmt = conn.createStatement()
      itr.foreach(f=>{
        val temp = f \\ "TblZrrGuarGuarantyInfo"
        temp.foreach(m=>{
          val serno = (m \ "Serno").text
          val guar_contract_no = (m \ "GuarContractNo").text
          val id = (m \ "Id").text
          val guaranty_no = (m \ "GuarantyNo").text
          val guaranty_amt = Global.getDoubleValue((m \ "GuarantyAmt").text).getOrElse(0.0)
          val guaranty_currency = (m \ "GuarantyCurrency").text
          val guaranty_eva_amt = Global.getDoubleValue((m \ "GuarantyEvaAmt").text).getOrElse(0.0)
          val guaranty_eva_currency = (m \ "GuarantyEvaCurrency").text
          val eva_date = (m \ "EvaDate").text
          val eva_org_name = (m \ "EvaOrgName").text
          val eva_org_no = (m \ "EvaOrgNo").text
          val guaranty_type = (m \ "GuarantyType").text
          val reg_org_name = (m \ "RegOrgName").text
          val reg_date = (m \ "RegDate").text
          val guaranty_desc = (m \ "GuarantyDesc").text
          val input_id = (m \ "InputId").text
          val input_drid = (m \ "InputDrId").text
          val input_date = (m \ "InputDate").text

          if(serno!=null && !"".equals(serno) && id!=null && !"".equals(id)&& guar_contract_no!=null && !"".equals(guar_contract_no)){
            stmt.addBatch("replace into rh_detail_tblzrrguarguarantyinfo(" +
              "serno,guar_contract_no,id," +
              "guaranty_no,guaranty_amt,guaranty_currency,guaranty_eva_amt,guaranty_eva_currency,eva_date," +
              "eva_org_name,eva_org_no,guaranty_type,reg_org_name,reg_date,guaranty_desc," +
              "input_id,input_drid,input_date) " +
              "values(" +
              "'" + serno+"','"+guar_contract_no+"','"+id+"'," +
              "'"+guaranty_no+"','"+guaranty_amt+"','"+guaranty_currency+"','"+guaranty_eva_amt+"','"+guaranty_eva_currency+"','"+eva_date+"'," +
              "'"+eva_org_name+"','"+eva_org_no+"','"+guaranty_type+"','"+reg_org_name+"','"+reg_date+"','"+guaranty_desc+"'," +
              "'"+input_id+"','"+input_drid+"','"+input_date+"') ")
          }
        })
        println("完成存储：rh_detail_tblzrrguarguarantyinfo")
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
