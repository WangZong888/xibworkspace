package cn.jk.agent

import java.sql.{Connection, Statement}

import cn.jk.pojo.Global
import cn.jk.util.{ConnectPoolUtil, Save_To_BackData}

import scala.collection.mutable

object SaveAccountResultInfo {

  def save_accresult(contractArr: Array[String]): Unit = {
    var conn: Connection = null
    var stmt: Statement = null
    try {
      conn = ConnectPoolUtil.getConnection()
      conn.setAutoCommit(false)
      stmt = conn.createStatement()
      //先将数据备份到轨迹表里
      val back_info = "insert into accounting_result_back (interest_rate,repayment_method,cert_id,due_date,credit_id,lending_date,loan_no,order_risk_id,date_of_interest,credit_risk_id,credit_apply_no,sell_credit_id,credit_period,lending_result,result_reason,loan_amount,business_id,business_type,ind_name,ind_cert_type,ent_name) select interest_rate,repayment_method,cert_id,due_date,credit_id,lending_date,loan_no,order_risk_id,date_of_interest,credit_risk_id,credit_apply_no,sell_credit_id,credit_period,lending_result,result_reason,loan_amount,business_id,business_type,ind_name,ind_cert_type,ent_name  from accounting_result"
      val delete_info = "delete from accounting_result"
      stmt.addBatch(back_info)
      stmt.addBatch(delete_info)
      contractArr.foreach(x => {
        var map: mutable.Map[String, Object] = mutable.Map[String, Object]()
        val lines = x.split(Global.regex_rule)
        val creditApplyNo = lines(0)
        val creditRiskId = lines(1)
        val orderRiskId = lines(2)
        val certId = lines(3)
        val creditId = lines(4)
        val sellCreditId = lines(5)

        val businessId = lines(6)
        val businessType = lines(7)
        val indName = lines(8)
        val indCertType = lines(9)
        val entName = lines(10)

        //val contractId = lines(6)
        val lendingResult = lines(11)
        val resultReason = lines(12)
        val loanNo = lines(13)
        val interestRate = lines(14)
        val creditPeriod = lines(15)
        val dateOfInterest = lines(16)
        val dueDate = lines(17)
        val repaymentMethod = lines(18)
        var loanAmout = "0.0"
        if (null != lines(19) || !"".equals(lines(19))) {
          loanAmout = lines(19)
        }
        val lendingDate = lines(20)

        map += ("credit_apply_no" -> creditApplyNo)
        map += ("credit_risk_id" -> creditRiskId)
        map += ("order_risk_id" -> orderRiskId)
        map += ("cert_id" -> certId)
        map += ("credit_id" -> creditId)
        map += ("sell_credit_id" -> sellCreditId)

        map += ("business_id" -> businessId)
        map += ("business_type" -> businessType)
        map += ("ind_name" -> indName)
        map += ("ind_cert_type" -> indCertType)
        map += ("ent_name" -> entName)


        //map += ("contract_id" -> contractId)
        map += ("lending_result" -> lendingResult)
        map += ("result_reason" -> resultReason)
        map += ("loan_no" -> loanNo)
        map += ("interest_rate" -> interestRate)
        map += ("credit_period" -> creditPeriod)
        map += ("date_of_interest" -> dateOfInterest)
        map += ("due_date" -> dueDate)
        map += ("repayment_method" -> repaymentMethod)
        map += ("loan_amount" -> loanAmout)
        map += ("lending_date" -> lendingDate)
        Save_To_BackData.save_data(map, "accounting_result", conn, stmt)
      })
      stmt.executeBatch
      conn.commit()
    } catch {
      case e: Exception => e.printStackTrace(); conn.rollback()
    } finally {
      ConnectPoolUtil.closeCon(stmt, conn)
    }
  }
}
