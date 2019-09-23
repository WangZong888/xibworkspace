package cn.jk.agent

import java.sql.{Connection, Statement}

import cn.jk.pojo.Global
import cn.jk.util.{ConnectPoolUtil, DateFormat, Save_To_BackData}

import scala.collection.mutable

object SaveRepayResultInfo {

  def save_repayresult(contractArr: Array[String] ):Unit = {
    var conn: Connection = null
    var stmt: Statement = null
    try{
      conn = ConnectPoolUtil.getConnection()
      conn.setAutoCommit(false)
      stmt = conn.createStatement()
      //先将数据备份到轨迹表里
      val back_info = "insert into repay_result_plan_info_back (rpterm,cert_id,ove_day,credit_id,total_amount,order_risk_id,actual_paydate,credit_risk_id,credit_apply_no,payment_accno,sell_credit_id,actual_capital,finish_date,pay_capital,actual_aint,actual_oint,actual_cint,expiry_date,pay_oint,status,value_date,rate,pay_date,pay_aint,pay_cint,id) select rpterm,cert_id,ove_day,credit_id,total_amount,order_risk_id,actual_paydate,credit_risk_id,credit_apply_no,payment_accno,sell_credit_id,actual_capital,finish_date,pay_capital,actual_aint,actual_oint,actual_cint,expiry_date,pay_oint,status,value_date,rate,pay_date,pay_aint,pay_cint,id  from repay_result_plan_info"
      val delete_info = "delete from repay_result_plan_info"
      stmt.addBatch(back_info)
      stmt.addBatch(delete_info)
      contractArr.foreach(x=>{
       var map: mutable.Map[String, Object] = mutable.Map[String, Object]()
        val lines = x.split(Global.regex_rule,-1)
        val creditApplyNo = lines(0)
        val creditRiskId = lines(1)
        val orderRiskId = lines(2)
        val certId = lines(3)
        val creditId = lines(4)
        val sellCreditId = Global.getStringValue(lines(5)).getOrElse("-")
        //val contractId = lines(6)
        val paymentAccno = Global.getStringValue(lines(6)).getOrElse("-")
        val rpterm = lines(7)
        val payDate = lines(8)
        val totalAmount = lines(9)
        val valueDate = Global.getStringValue(lines(10)).getOrElse("-")
        val expiryDate = Global.getStringValue(lines(11)).getOrElse("-")
        val rate = Global.getStringValue(lines(12)).getOrElse("0.0")
        val payCapital = Global.getStringValue(lines(13)).getOrElse("0.0")
        val actualCapital = Global.getStringValue(lines(14)).getOrElse("0.0")
        val payAint = Global.getStringValue(lines(15)).getOrElse("0.0")
        val actualAint = Global.getStringValue(lines(16)).getOrElse("0.0")
        val payOint = Global.getStringValue(lines(17)).getOrElse("0.0")
        val actualOint = Global.getStringValue(lines(18)).getOrElse("0.0")
        val payCint = Global.getStringValue(lines(19)).getOrElse("0.0")
        val actualCint =  Global.getStringValue(lines(20)).getOrElse("0.0")
        val actualPaydate =  Global.getStringValue(lines(21)).getOrElse("-")
        val finishDate =  Global.getStringValue(lines(22)).getOrElse("-")
        val status = lines(23)
        val ove_day = DateFormat.datediff(payDate,finishDate).toString

        map += ("credit_apply_no" -> creditApplyNo)
        map += ("credit_risk_id" -> creditRiskId)
        map += ("order_risk_id" -> orderRiskId)
        map += ("cert_id" -> certId)
        map += ("credit_id" -> creditId)
        map += ("sell_credit_id" -> sellCreditId)
        //map += ("contract_id" -> contractId)
        map += ("payment_accno" -> paymentAccno)
        map += ("rpterm" -> rpterm)
        map += ("pay_date" -> payDate)
        map += ("total_amount" -> totalAmount)
        map += ("value_date" -> valueDate)
        map += ("expiry_date" -> expiryDate)
        map += ("rate" -> rate)
        map += ("pay_capital" -> payCapital)
        map += ("actual_capital" -> actualCapital)
        map += ("pay_aint" -> payAint)
        map += ("actual_aint" -> actualAint)
        map += ("pay_oint" -> payOint)
        map += ("actual_oint" -> actualOint)
        map += ("pay_cint" -> payCint)
        map += ("actual_cint" -> actualCint)
        map += ("actual_paydate" -> actualPaydate)
        map += ("finish_date" -> finishDate)
        map += ("status" -> status)
        map += ("ove_day" -> ove_day)
        Save_To_BackData.save_data(map,"repay_result_plan_info",conn,stmt)
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
