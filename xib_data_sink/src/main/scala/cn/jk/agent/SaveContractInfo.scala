package cn.jk.agent

import java.sql.{Connection, Statement}

import cn.jk.pojo.Global
import cn.jk.util.{ConnectPoolUtil, Save_To_BackData}

import scala.collection.mutable

object SaveContractInfo {

  def save_contract(contractArr: Array[String] ):Unit = {
    var conn: Connection = null
    var stmt: Statement = null
    try{
      conn = ConnectPoolUtil.getConnection()
      conn.setAutoCommit(false)
      stmt = conn.createStatement()
      //先将数据备份到轨迹表里
      val back_info = "insert into xib_contract_info_back (cert_id,repayment_method,due_date,overdue_aint,overdue_cint,repaidOint,orate,loan_amt,credit_id,overdue_capital,monthly_repay_day,order_risk_id,repaid_capital,overdue_status,next_repaydate,credit_risk_id,credit_apply_no,balance,sell_credit_id,finish_date,total_aint,overdue_days,repaid_aint,normal_capital,last_expirydate,repaid_cint,totalOint,status,overdueOint,value_date,rate,pay_cint,loan_time,id) select cert_id,repayment_method,due_date,overdue_aint,overdue_cint,repaidOint,orate,loan_amt,credit_id,overdue_capital,monthly_repay_day,order_risk_id,repaid_capital,overdue_status,next_repaydate,credit_risk_id,credit_apply_no,balance,sell_credit_id,finish_date,total_aint,overdue_days,repaid_aint,normal_capital,last_expirydate,repaid_cint,totalOint,status,overdueOint,value_date,rate,pay_cint,loan_time,id  from xib_contract_info"
      val delete_info = "delete from xib_contract_info"
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
        val sellCreditId = lines(5)
       // val contractId = lines(6)
        val loanTime = lines(6)
        val loanAmt = Global.getStringValue(lines(7)).getOrElse("0.0")
        val rate = lines(8)
        val oRate = lines(9)
        val monthlyRepayDay = lines(10)
        val valueDate = lines(11)
        val dueDate = lines(12)
        val nextRepaydate = lines(13)
        val lastExpirydate = lines(14)
        val status = lines(15)
        val overdueStatus = lines(16)
        val overdueDays = lines(17)
        val finishDate = lines(18)
        val repaymentMethod = lines(19)
        val balance = lines(20)
        val normalCapital = lines(21)
        val overdueCapital =  Global.getStringValue(lines(22)).getOrElse("0.0")
        val repaidCapital = Global.getStringValue(lines(23)).getOrElse("0.0")
        val totalAint = Global.getStringValue(lines(24)).getOrElse("0.0")
        val overdueAint = Global.getStringValue(lines(25)).getOrElse("0.0")
        val repaidAint = Global.getStringValue(lines(26)).getOrElse("0.0")
        val totalOint = Global.getStringValue(lines(27)).getOrElse("0.0")
        val overdueOint = Global.getStringValue(lines(28)).getOrElse("0.0")
        val repaidOint = Global.getStringValue(lines(29)).getOrElse("0.0")
        val overdueCint = Global.getStringValue(lines(30)).getOrElse("0.0")
        val payCint = Global.getStringValue(lines(31)).getOrElse("0.0")
        val repaidCint = Global.getStringValue(lines(32)).getOrElse("0.0")

        map += ("credit_apply_no" -> creditApplyNo)
        map += ("credit_risk_id" -> creditRiskId)
        map += ("order_risk_id" -> orderRiskId)
        map += ("cert_id" -> certId)
        map += ("credit_id" -> creditId)
        map += ("sell_credit_id" -> sellCreditId)
        //map += ("contract_id" -> contractId)
        map += ("loan_time" -> loanTime)
        map += ("loan_amt" -> loanAmt)
        map += ("rate" -> rate)
        map += ("orate" -> oRate)
        map += ("monthly_repay_day" -> monthlyRepayDay)
        map += ("value_date" -> valueDate)
        map += ("due_date" -> dueDate)
        map += ("next_repaydate" -> nextRepaydate)
        map += ("last_expirydate" -> lastExpirydate)
        map += ("status" -> status)
        map += ("overdue_status" -> overdueStatus)
        map += ("overdue_days" -> overdueDays)
        map += ("finish_date" -> finishDate)
        map += ("repayment_method" -> repaymentMethod)
        map += ("balance" -> balance)
        map += ("normal_capital" -> normalCapital)
        map += ("overdue_capital" -> overdueCapital)
        map += ("repaid_capital" -> repaidCapital)
        map += ("total_aint" -> totalAint)
        map += ("overdue_aint" -> overdueAint)
        map += ("repaid_aint" -> repaidAint)
        map += ("totalOint" -> totalOint)
        map += ("overdueOint" -> overdueOint)
        map += ("repaidOint" -> repaidOint)
        map += ("overdue_cint" -> overdueCint)
        map += ("pay_cint" -> payCint)
        map += ("repaid_cint" -> repaidCint)

        Save_To_BackData.save_data(map,"xib_contract_info",conn,stmt)

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
