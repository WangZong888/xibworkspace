package cn.jkjf.rhdetail.deal

import java.sql.{Connection, Statement}

import cn.jkjf.rhdetail.utils.ConnectPoolUtil

import scala.xml.Elem

object Deal_TblperCrePbRFInfo{

  def  savetomysql(itr: Iterator[Elem]):Unit = {
    var conn: Connection = null
    var stmt: Statement = null
    try{
      conn = ConnectPoolUtil.getConnection()
      conn.setAutoCommit(false)
      stmt = conn.createStatement()
      itr.foreach(f=>{
        val temp = f \\ "TblperCrePbRFInfo"
        temp.foreach(m=>{
          val pri_num = (m \ "PriNum").text
          val pri_bar_code = (m \ "PriBarcode").text
          val pri_account = (m \ "PriAccount").text
          val pri_emp = (m \ "PriEmp").text
          val pri_oa_date = (m \ "PriOaDate").text
          val pri_firsthot_date = (m \ "PriFirstHotDate").text
          val pri_lasthot_date = (m \ "PriLastHotDate").text
          val pri_recenthot_date = (m \ "PriRecentHotDate").text
          val pri_paydeposit_com = (m \ "PriPayDepositCom").text
          val pri_paydeposit_per = (m \ "PriPayDepositPer").text
          val pri_monthpay_deposit = (m \ "PriMonthPayDeposit").text
          val pri_infodate = (m \ "PriInfoDate").text
          val pri_24monthretst = (m \ "Pri24MonthRetst").text
          val pri_new_id = (m \ "PriNewId").text
          val pri_new_date = (m \ "PriNewDate").text
          val pri_update_id = (m \ "PriUpdateId").text
          val pri_update_date = (m \ "PriUpdateDate").text
          val cus_name = (m \ "CusName").text
          val cert_type = (m \ "CertType").text
          val cert_no = (m \ "CertNo").text
          val input_id = (m \ "InputId").text
          val input_drid = (m \ "InputDrId").text
          val input_date = (m \ "InputDate").text

          if(pri_num!=null && !"".equals(pri_num)){
            stmt.addBatch("replace into rh_detail_tblpercrepbrfinfo(" +
              "pri_num," +
              "pri_bar_code,pri_account,pri_emp,pri_oa_date,pri_firsthot_date,pri_lasthot_date,pri_recenthot_date,pri_paydeposit_com," +
              "pri_paydeposit_per,pri_monthpay_deposit,pri_infodate,pri_24monthretst,pri_new_id,pri_new_date,pri_update_id,pri_update_date," +
              "cus_name,cert_type,cert_no,input_id,input_drid,input_date) " +
              "values(" +
              "'" + pri_num+"'," +
              "'"+pri_bar_code+"','"+pri_account+"','"+pri_emp+"','"+pri_oa_date+"','"+pri_firsthot_date+"','"+pri_lasthot_date+"','"+pri_recenthot_date+"','"+pri_paydeposit_com+"'," +
              "'"+pri_paydeposit_per+"','"+pri_monthpay_deposit+"','"+pri_infodate+ "','"+pri_24monthretst+"','"+pri_new_id+"','"+pri_new_date+"','"+pri_update_id+"','"+pri_update_date+"'," +
              "'"+cus_name+"','"+cert_type+"','"+cert_no+"','"+input_id+"','"+input_drid+"','"+input_date+"') ")
          }
        })
        println("存储完成：rh_detail_tblpercrepbrfinfo")
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
