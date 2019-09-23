package cn.jkjf.rhdetail.deal

import java.sql.{Connection, Statement}

import cn.jkjf.rhdetail.utils.{ConnectPoolUtil, Global}

import scala.xml.Elem

object Deal_TblperLoanCardInfo{

  def  savetomysql(itr: Iterator[Elem]):Unit = {
    var conn: Connection = null
    var stmt: Statement = null
    try{
      conn = ConnectPoolUtil.getConnection()
      conn.setAutoCommit(false)
      stmt = conn.createStatement()
      itr.foreach(f=>{
        val temp = f \\ "TblperLoanCardInfo"
        println("TblperLoanCardInfo--------"+temp)
        temp.foreach(m=>{
          val id = (m \ "Id").text
          val report_id = (m \ "ReportId").text
          val card_type = (m \ "CardType").text
          val cue = (m \ "Cue").text
          val finance_org = (m \ "Financeorg").text
          val state = (m \ "State").text
          val sharecredit_limit_amount = Global.getIntValue((m \ "Sharecreditlimitamount").text).getOrElse(0)
          val usedcredit_limit_amount = Global.getIntValue((m \ "Usedcreditlimitamount").text).getOrElse(0)
          val latest_6month_usedavg_amount = Global.getIntValue((m \ "Latest6monthusedavgamount").text).getOrElse(0)
          val used_highest_amount = Global.getIntValue((m \ "Usedhighestamount").text).getOrElse(0)
          val schedule_dpayment_date = (m \ "Scheduledpaymentdate").text
          val schedule_dpayment_amount = Global.getIntValue((m \ "Scheduledpaymentamount").text).getOrElse(0)
          val actual_payment_amount = Global.getIntValue((m \ "Actualpaymentamount").text).getOrElse(0)
          val recent_paydate = (m \ "Recentpaydate").text
          val curr_overdue_cyc = Global.getIntValue((m \ "Curroverduecyc").text).getOrElse(0)
          val curr_overdue_amount = Global.getIntValue((m \ "Curroverdueamount").text).getOrElse(0)
          val loancard_id = (m \ "LoanCardId").text
          val account = (m \ "Account").text
          val currency = (m \ "Currency").text
          val open_date = (m \ "Opendate").text
          val credit_limit_amount = Global.getIntValue((m \ "Creditlimitamount").text).getOrElse(0)
          val guarantee_type = (m \ "Guaranteetype").text
          val state_end_date = (m \ "Stateenddate").text
          val state_end_month = (m \ "Stateendmonth").text
          val guanantee_money = Global.getIntValue((m \ "Guananteemoney").text).getOrElse(0)
          val overdue_31to60_amount = Global.getIntValue((m \ "Overdue31to60amount").text).getOrElse(0)
          val overdue_61to90_amount = Global.getIntValue((m \ "Overdue61to90amount").text).getOrElse(0)
          val overdue_91to180_amount = Global.getIntValue((m \ "Overdue91to180amount").text).getOrElse(0)
          val overdue_over180_amount = Global.getIntValue((m \ "Overdueover180amount").text).getOrElse(0)
          val begin_month24_month = (m \ "Beginmonth24month").text
          val end_month_24month = (m \ "Endmonth24month").text
          val last24_state = (m \ "Last24state").text
          val begin_month_fi = (m \ "BeginmonthFi").text
          val end_month_five_year = (m \ "EndmonthFiveyear").text
          val cus_name = (m \ "CusName").text
          val cert_type = (m \ "CertType").text
          val cert_no = (m \ "CertNo").text
          val input_id = (m \ "InputId").text
          val input_drid = (m \ "InputDrId").text
          val input_date = (m \ "InputDate").text

          if(report_id!=null && !"".equals(report_id) && id!=null && !"".equals(id)){
            stmt.addBatch("replace into rh_detail_tblperloancardinfo(" +
              "id,report_id," +
              "card_type,cue,finance_org,state,sharecredit_limit_amount,usedcredit_limit_amount,latest_6month_usedavg_amount," +
              "used_highest_amount,schedule_dpayment_date,schedule_dpayment_amount,actual_payment_amount,recent_paydate,curr_overdue_cyc,curr_overdue_amount," +
              "loancard_id,account,currency,open_date,credit_limit_amount,guarantee_type,state_end_date," +
              "state_end_month,guanantee_money,overdue_31to60_amount,overdue_61to90_amount,overdue_91to180_amount,overdue_over180_amount,begin_month24_month," +
              "end_month_24month,last24_state,begin_month_fi,end_month_five_year," +
              "cus_name,cert_type,cert_no,input_id,input_drid,input_date) " +
              "values('" + id+"','"+report_id+"'," +
              "'"+card_type+"','"+cue+"','"+finance_org+ "','"+state+"','"+sharecredit_limit_amount+"','"+usedcredit_limit_amount+"','"+latest_6month_usedavg_amount+"'," +
              "'"+used_highest_amount+"','"+schedule_dpayment_date+"','"+schedule_dpayment_amount+"','"+actual_payment_amount+"','"+recent_paydate+"','"+curr_overdue_cyc+"','"+curr_overdue_amount+"'," +
              "'"+loancard_id+"','"+account+"','"+currency+"','"+open_date+"','"+credit_limit_amount+"','"+guarantee_type+"','"+state_end_date+"'," +
              "'"+state_end_month+"','"+guanantee_money+"','"+overdue_31to60_amount+"','"+overdue_61to90_amount+"','"+overdue_91to180_amount+"','"+overdue_over180_amount+"','"+begin_month24_month+"'," +
              "'"+end_month_24month+"','"+last24_state+"','"+begin_month_fi+"','"+end_month_five_year+"'," +
              "'"+cus_name+"','"+cert_type+"','"+cert_no+"','"+input_id+"','"+input_drid+"','"+input_date+"') ")
          }
        })
        println("完成存储：rh_detail_tblperloancardinfo")
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
