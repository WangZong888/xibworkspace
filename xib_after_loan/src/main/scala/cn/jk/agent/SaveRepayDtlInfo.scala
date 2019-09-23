package cn.jk.agent

import java.sql.{Connection, Statement}

import cn.jk.pojo.Global
import cn.jk.util.{ConnectPoolUtil, Save_To_BackData}

import scala.collection.mutable

object SaveRepayDtlInfo {

  def save_repaydtl(contractArr: Array[String] ):Unit = {
    var conn: Connection = null
    var stmt: Statement = null
    try{
      conn = ConnectPoolUtil.getConnection()
      conn.setAutoCommit(false)
      stmt = conn.createStatement()
      //先将数据备份到轨迹表里
      val back_info = "insert into repay_detail_info_back (rpterm,cert_id,cint,aint,credit_id,order_risk_id,total_amount,actual_paydate,credit_risk_id,credit_apply_no,trans_req_no,capital,sell_credit_id,oint) select rpterm,cert_id,cint,aint,credit_id,order_risk_id,total_amount,actual_paydate,credit_risk_id,credit_apply_no,trans_req_no,capital,sell_credit_id,oint  from repay_detail_info"
      val delete_info = "delete from repay_detail_info"
      stmt.addBatch(back_info)
      stmt.addBatch(delete_info)
      contractArr.foreach(x=>{
       // { Save_To_Mysql_New.save_data(x,"xib_contract_info",conn,stmt)}
       var map: mutable.Map[String, Object] = mutable.Map[String, Object]()
        val lines = x.split(Global.regex_rule)
        val creditApplyNo = lines(0)
        val creditRiskId = lines(1)
        val orderRiskId = lines(2)
        val certId = lines(3)
        val creditId = lines(4)
        val sellCreditId = lines(5)
        //val contractId = lines(6)
        val rpterm = lines(6)
        val actualPaydate = lines(7)
        val transReqNo = lines(8)
        val totalAmount = lines(9)
        val capital = lines(10)
        val aint = lines(11)
        val oint = lines(12)
        val cint = lines(13)
        map += ("credit_apply_no" -> creditApplyNo)
        map += ("credit_risk_id" -> creditRiskId)
        map += ("order_risk_id" -> orderRiskId)
        map += ("cert_id" -> certId)
        map += ("credit_id" -> creditId)
        map += ("sell_credit_id" -> sellCreditId)
        //map += ("contract_id" -> contractId)
        map += ("rpterm" -> rpterm)
        map += ("actual_paydate" -> actualPaydate)
        map += ("trans_req_no" -> transReqNo)
        map += ("total_amount" -> totalAmount)
        map += ("capital" -> capital)
        map += ("aint" -> aint)
        map += ("oint" -> oint)
        map += ("cint" -> cint)

        Save_To_BackData.save_data(map,"repay_detail_info",conn,stmt)

      })
      stmt.executeBatch
      conn.commit()
    }catch {
      case e:Exception=>e.printStackTrace();conn.rollback()
    }finally {
      ConnectPoolUtil.closeCon(stmt,conn)
    }
  }
}
