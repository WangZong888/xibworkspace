package cn.jkjf.rhdetail.deal

import java.sql.{Connection, Statement}

import cn.jkjf.rhdetail.utils.{ConnectPoolUtil, Global}

import scala.xml.Elem

object Deal_TblperInfoSummary {

  def  savetomysql(itr: Iterator[Elem]):Unit = {
    var conn: Connection = null
    var stmt: Statement = null
    try{
      conn = ConnectPoolUtil.getConnection()
      conn.setAutoCommit(false)
      stmt = conn.createStatement()
      itr.foreach(f=>{
        val temp = f \\ "TblperInfoSumarry"
        temp.foreach(m=>{
          val id = (m \ "Id").text
          val report_id = (m \ "ReportId").text
          val s1 =Global.getIntValue((m \ "HouseLoanCount").text).getOrElse(0)
          val s2 = Global.getIntValue((m \ "BusiHouseLoanCount").text).getOrElse(0)
          val s3 =  Global.getIntValue((m \ "OtherLoanCount").text).getOrElse(0)
          val s4 = (m \ "FirstLoanOpenMonth").text
          val s5 = Global.getIntValue((m \ "LoanCardCount").text).getOrElse(0)
          val s6 = (m \ "FirstLoanCardOpenMonth").text
          val s7 = Global.getIntValue((m \ "StandardLoanCardCount").text).getOrElse(0)
          val s8 = (m \ "FStandardLoanCardOpenmonth").text
          val s9 =Global.getIntValue((m \ "AnnounceCount").text).getOrElse(0)
          val s10 = Global.getIntValue((m \ "DissentCount").text).getOrElse(0)
          val s11 = (m \ "Score").text
          val s12 = (m \ "ScoreMonth").text
          val s13 =  Global.getIntValue((m \ "FellBackCount").text).getOrElse(0)
          val s14 =  Global.getIntValue((m \ "FellBackBalance").text).getOrElse(0)
          val s15 =  Global.getIntValue((m \ "AssetDissCount").text).getOrElse(0)
          val s16 =  Global.getIntValue((m \ "AssetDissBalcnce").text).getOrElse(0)
          val s17 =  Global.getIntValue((m \ "AssureRepayCount").text).getOrElse(0)
          val s18 =  Global.getIntValue((m \ "AssureRepayBalance").text).getOrElse(0)
          val s19 =  Global.getIntValue((m \ "LoanOverdueCount").text).getOrElse(0)
          val s20 =  Global.getIntValue((m \ "LoanOverdueMonths").text).getOrElse(0)
          val s21 =  Global.getIntValue((m \ "LoanOverdueHigestAmountPM").text).getOrElse(0)
          val s22 =  Global.getIntValue((m \ "LoanOverdueMaxDuration").text).getOrElse(0)
          val s23 =  Global.getIntValue((m \ "LoanCardOverdueCount").text).getOrElse(0)
          val s24 =  Global.getIntValue((m \ "LoanCardOverdueMonths").text).getOrElse(0)
          val s25 =  Global.getIntValue((m \ "LoanCardOverdueHAmountPM").text).getOrElse(0)
          val s26 =  Global.getIntValue((m \ "LoanCardOverdueMaxDuration").text).getOrElse(0)
          val s27 =  Global.getIntValue((m \ "SLoanCardOdueCount").text).getOrElse(0)
          val s28 =  Global.getIntValue((m \ "SLoanCarOdueMonths").text).getOrElse(0)
          val s29 =  Global.getIntValue((m \ "SLoanCardOdueHAmountPM").text).getOrElse(0)
          val s30 =  Global.getIntValue((m \ "SLoanCardOdueMaxDuration").text).getOrElse(0)
          val s31 =  Global.getIntValue((m \ "UnpaidloanFinanceCorpCount").text).getOrElse(0)
          val s32 =  Global.getIntValue((m \ "UnpaidloanFinanceOrgCount").text).getOrElse(0)
          val s33 =  Global.getIntValue((m \ "UnpaidloanAccountCount").text).getOrElse(0)
          val s34 =  Global.getIntValue((m \ "UnpaidloanCreditLimit").text).getOrElse(0)
          val s35 =  Global.getIntValue((m \ "UnpaidloanBalance").text).getOrElse(0)
          val s36 =  Global.getIntValue((m \ "UnpaidloanLastsixMonthRepay").text).getOrElse(0)
          val s37 =  Global.getIntValue((m \ "UndestyryloancardFancCount").text).getOrElse(0)
          val s38 =  Global.getIntValue((m \ "UndestyryloancardOrgCount").text).getOrElse(0)
          val s39 =  Global.getIntValue((m \ "UndestyryloancardAccountcoun").text).getOrElse(0)
          val s40 =  Global.getIntValue((m \ "UndestyryloancardCreditLimit").text).getOrElse(0)
          val s41 =  Global.getIntValue((m \ "UndestyryloancardMaxCredit").text).getOrElse(0)
          val s42 =  Global.getIntValue((m \ "UndestyryloancardMinCredit").text).getOrElse(0)
          val s43 =  Global.getIntValue((m \ "UndestyryloancardUsedCredit").text).getOrElse(0)
          val s44 =  Global.getIntValue((m \ "UndestyryloancardUsedAvg6").text).getOrElse(0)
          val s45 =  Global.getIntValue((m \ "UndestyrysloancardFancCount").text).getOrElse(0)
          val s46 =  Global.getIntValue((m \ "UndestyrysloancardOrgCount").text).getOrElse(0)
          val s47 =  Global.getIntValue((m \ "UndestyrysloancardAccountcoun").text).getOrElse(0)
          val s48 =  Global.getIntValue((m \ "UndestyrysloancardCreditlimit").text).getOrElse(0)
          val max_credit =  Global.getIntValue((m \ "UndestyrysloancardMaxCredit").text).getOrElse(0)
          val min_credit =  Global.getIntValue((m \ "UndestyrysloancardMinCredit").text).getOrElse(0)
          val usedc_redit =  Global.getIntValue((m \ "UndestyrysloancardUsedCredit").text).getOrElse(0)
          val use_davg6 =  Global.getIntValue((m \ "UndestyrysloancardUsedAvg6").text).getOrElse(0)
          val guar_count =  Global.getIntValue((m \ "GuarCount").text).getOrElse(0)
          val guar_amount =  Global.getIntValue((m \ "GuarAmount").text).getOrElse(0)
          val guar_balance =  Global.getIntValue((m \ "GuarBalance").text).getOrElse(0)
          val cus_name =  (m \ "CusName").text
          val cert_type = (m \ "CertType").text
          val cert_no = (m \ "CertNo").text
          val input_id = (m \ "InputId").text
          val input_drid = (m \ "InputDrId").text
          val input_date = (m \ "InputDate").text

          if(report_id!=null && !"".equals(report_id) && id!=null && !"".equals(id)){
            val sb = new StringBuilder
            sb.append("replace into rh_detail_tblperinfosummary(" )
            sb.append("id,report_id," )
            sb.append("house_loan_count,busi_house_loan_count,other_loan_count,first_loan_openmonth,loan_card_count," )
            sb.append("first_loancard_openmonth,standard_loancard_count,fstandard_loancard_openmonth,announce_count,dissent_count," )
            sb.append("score,score_month,fellback_count,fellback_balance,assetdiss_count,assetdiss_balcnce," )
            sb.append("assure_repay_count,assure_repay_balance,loan_overdue_count,loan_overdue_months," )
            sb.append("loan_overdue_higest_amountpm,loan_overdue_max_duration,loan_card_overdue_count,loan_card_overdue_months,loan_card_overdue_hamountpm," )
            sb.append("loan_card_overdue_maxduration,sloan_cardodue_count,sloan_carodue_months,sloan_cardodue_hamountpm,sloan_cardodue_max_duration," )
            sb.append("unpaid_loan_financecorp_count,unpaid_loan_financeorg_count,unpaid_loan_account_count,unpaid_loan_credit_limit,unpaid_loan_balance," )
            sb.append("unpaid_loan_lastsixmonth_repay,undestyry_loancardfanc_count,undestyry_loancardorg_count,undestyry_loancard_accountcoun," )
            sb.append("undestyry_loancardcredit_limit,undestyry_loancard_maxcredit," )
            sb.append("undestyry_loancard_mincredit,undestyry_loancard_usedcredit,undestyry_loancard_usedavg6," )
            sb.append("undestyry_sloancard_fanccount,undestyry_sloancard_orgcount,undestyry_sloancard_accountcoun," )
            sb.append("undestyry_sloancard_creditlimit,undestyry_sloancard_maxcredit,undestyry_sloancard_mincredit," )
            sb.append("undestyry_sloancard_usedcredit,undestyry_sloancard_usedavg6,guar_count,guar_amount,guar_balance," )
            sb.append("cus_name,cert_type,cert_no,input_id,input_drid,input_date) " )
            sb.append("values(" )
            sb.append("'" + id+"','"+report_id+"'," )
            sb.append("'"+s1+"','"+s2+"','"+s3+ "','"+s4+"','"+s5+"'," )
            sb.append("'"+s6+"','"+s7+"','"+s8+"','"+s9+"','"+s10+"'," )
            sb.append("'"+s11+"','"+s12+"','"+s13+"','"+s14+"','"+s15+"','"+s16+"'," )
            sb.append("'"+s17+"','"+s18+"','"+s19+"','"+s20+"'," )
            sb.append("'"+s21+"','"+s22+"','"+s23+"','"+s24+"','"+s25+"'," )
            sb.append("'"+s26+"','"+s27+"','"+s28+"','"+s29+"','"+s30+"'," )
            sb.append("'"+s31+"','"+s32+"','"+s33+"','"+s34+"','"+s35+"'," )
            sb.append("'"+s36+"','"+s37+"','"+s38+"','"+s39+"'," )
            sb.append("'"+s40+"','"+s41+"'," )
            sb.append("'"+s42+"','"+s43+"','"+s44+"'," )
            sb.append("'"+s45+"','"+s46+"','"+s47+"'," )
            sb.append("'"+s48+"','"+max_credit+"','"+min_credit+"'," )
            sb.append("'"+usedc_redit+"','"+use_davg6+"','"+guar_count+"','"+guar_amount+"','"+guar_balance+"'," )
            sb.append("'"+cus_name+"','"+cert_type+"','"+cert_no+"','"+input_id+"','"+input_drid+"','"+input_date+"') ")

            val sql = sb.toString()
            stmt.addBatch(sql)
          }
        })
        println("完成存储：rh_detail_tblperinfosummary")
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
