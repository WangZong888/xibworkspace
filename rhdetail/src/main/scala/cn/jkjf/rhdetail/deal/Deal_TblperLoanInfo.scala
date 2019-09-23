package cn.jkjf.rhdetail.deal

import java.sql.{Connection, Statement}

import cn.jkjf.rhdetail.utils.{ConnectPoolUtil, Global}

import scala.xml.Elem

object Deal_TblperLoanInfo{

  def  savetomysql(itr: Iterator[Elem]):Unit = {
    var conn: Connection = null
    var stmt: Statement = null
    try{
      conn = ConnectPoolUtil.getConnection()
      conn.setAutoCommit(false)
      stmt = conn.createStatement()
      itr.foreach(f=>{
        val temp = f \\ "TblperLoanInfo"
        println("TblperLoanInfo--------"+temp)
        temp.foreach(m=>{
          val id = (m \ "Id").text
          val report_id = (m \ "ReportId").text
          val finance_org = (m \ "Financeorg").text
          val state = (m \ "State").text
          val state_end_date = (m \ "Stateenddate").text
          val state_end_month = (m \ "Stateendmonth").text
          val class5_state = (m \ "Class5state").text
          val balance = Global.getIntValue((m \ "Balance").text).getOrElse(0)
          val remain_payment_cyc = Global.getIntValue((m \ "Remainpaymentcyc").text).getOrElse(0)
          val scheduled_payment_amount = Global.getIntValue((m \ "Scheduledpaymentamount").text).getOrElse(0)
          val scheduled_payment_date = (m \ "Scheduledpaymentdate").text
          val actual_payment_amount = Global.getIntValue((m \ "Actualpaymentamount").text).getOrElse(0)
          val recent_paydate = (m \ "Recentpaydate").text
          val curr_overdue_cyc = Global.getIntValue((m \ "Curroverduecyc").text).getOrElse(0)
          val curr_overdue_amount = Global.getIntValue((m \ "Curroverdueamount").text).getOrElse(0)
          val overdue_31to60_amount = Global.getIntValue((m \ "Overdue31to60amount").text).getOrElse(0)
          val overdue_61to90_amount = Global.getIntValue((m \ "Overdue61to90amount").text).getOrElse(0)
          val overdue_91to180_amount = Global.getIntValue((m \ "Overdue91to180amount").text).getOrElse(0)
          val overdue_over180_amount = Global.getIntValue((m \ "Overdueover180amount").text).getOrElse(0)
          val last24_month_begin_month = (m \ "Last24monthbeginmonth").text
          val account = (m \ "Account").text
          val loan_type = (m \ "Type").text
          val currency = (m \ "Currency").text
          val open_date = (m \ "Opendate").text
          val end_date = (m \ "Enddate").text
          val credit_limit_amount = Global.getIntValue((m \ "Creditlimitamount").text).getOrElse(0)
          val guarantee_type = (m \ "Guaranteetype").text
          val payment_rating = (m \ "Paymentrating").text
          val payment_cyc = (m \ "Paymentcyc").text
          val last24_month_end_month = (m \ "Last24monthendmonth").text
          val latest24_state = (m \ "Latest24state").text
          val last_fiveyear_beginmonth = (m \ "Lastfiveyearbeginmonth").text
          val last_fiveyear_endmonth = (m \ "Lastfiveyearendmonth").text
          val loan_id = (m \ "LoanId").text
          val cue = (m \ "Cue").text
          val cus_name = (m \ "CusName").text
          val cert_type = (m \ "CertType").text
          val cert_no = (m \ "CertNo").text
          val input_id = (m \ "InputId").text
          val input_drid = (m \ "InputDrId").text
          val input_date = (m \ "InputDate").text

          if(report_id!=null && !"".equals(report_id) && id!=null && !"".equals(id)){
            stmt.addBatch("replace into rh_detail_tblperloaninfo(" +
              "id,report_id," +
              "finance_org,state,state_end_date,state_end_month,class5_state,balance," +
              "remain_payment_cyc,scheduled_payment_amount,scheduled_payment_date,actual_payment_amount,recent_paydate,curr_overdue_cyc,curr_overdue_amount," +
              "overdue_31to60_amount,overdue_61to90_amount,overdue_91to180_amount,overdue_over180_amount,last24_month_begin_month,account,loan_type," +
              "currency,open_date,end_date,credit_limit_amount,guarantee_type,payment_rating,payment_cyc," +
              "last24_month_end_month,latest24_state,last_fiveyear_beginmonth,last_fiveyear_endmonth,loan_id,cue," +
              "cus_name,cert_type,cert_no,input_id,input_drid,input_date) " +
              "values(" +
              "'" + id+"','"+report_id+"'," +
              "'"+finance_org+"','"+state+"','"+state_end_date+ "','"+state_end_month+"','"+class5_state+"','"+balance+"'," +
              "'"+remain_payment_cyc+"','"+scheduled_payment_amount+"','"+scheduled_payment_date+"','"+actual_payment_amount+"','"+recent_paydate+"','"+curr_overdue_cyc+"','"+curr_overdue_amount+"'," +
              "'"+overdue_31to60_amount+"','"+overdue_61to90_amount+"','"+overdue_91to180_amount+"','"+overdue_over180_amount+"','"+last24_month_begin_month+"','"+account+"','"+loan_type+"'," +
              "'"+currency+"','"+open_date+"','"+end_date+"','"+credit_limit_amount+"','"+guarantee_type+"','"+payment_rating+"','"+payment_cyc+"'," +
              "'"+last24_month_end_month+"','"+latest24_state+"','"+last_fiveyear_beginmonth+"','"+last_fiveyear_endmonth+"','"+loan_id+"','"+cue+"'," +
              "'"+cus_name+"','"+cert_type+"','"+cert_no+"','"+input_id+"','"+input_drid+"','"+input_date+"') ")
          }
        })
        println("完成存储：rh_detail_tblperloaninfo")
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
