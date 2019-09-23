package cn.jk.rdds

import java.sql.{Connection, ResultSet, Statement}

import cn.jk.util.{ConnectPoolUtil, DateFormat}

/**
  * Created by YiZe on 2019/6/10.
  *
  *
  */
object getCreditApplyTime {
  /**
    * 获取上一次的申请时间
    *
    * @param str
    * @return
    */
  def getApplyTime(str: String,risk_id:String): String = {
    var apply_date_arr = ""

    val connection = ConnectPoolUtil.getConnection()
    val sql = "select apply_date as apply_date  from credit_apply_info_dtl where  apply_date is not null  and  risk_id !=  '"+ risk_id +"' and  credit_no_key = '" + str + "' order by apply_date desc  limit 1"
    val psmt = connection.prepareStatement(sql)
    val resultSet = psmt.executeQuery()
    while (resultSet.next()) {
      apply_date_arr = resultSet.getString("apply_date")
    }
    apply_date_arr
  }

  /**
    * 节点前的流水
    *
    * @param supplier_code
    * @param first_apply_time
    */
  def getFirstAvg(supplier_code: String, first_apply_time: String, credit_no: String): Double = {
    var res = 0.0
    val connection = ConnectPoolUtil.getConnection()
    val sql = "select round(avg(amount),2) as first_avg_amount  from use_credit_apply where  supplier_code = '" + supplier_code + "' and order_date < '" + first_apply_time + "' and credit_no = '" + credit_no + "'"
    val psmt = connection.prepareStatement(sql)
    val resultSet = psmt.executeQuery()
    while (resultSet.next()) {
      res = resultSet.getDouble("first_avg_amount")
    }
    res
  }

  def getFirstAvg_new(supplier_code:String,first_apply_time: String, credit_no_key: String): Double = {
    //根据credit_no_key查询这个主体的所有riskid，然后去单据表里找所有的riskid
    var conn: Connection = null
    var stmt: Statement = null
    var res = 0.0
    try{
      conn = ConnectPoolUtil.getConnection()
      stmt  = conn.createStatement()
      //查询节点前的所有riskid
      val riskIdSql = "select risk_id from credit_apply_info_dtl where credit_no_key = '" + credit_no_key + "'  and supplier_code = '" + supplier_code + "' "
      val riskid_set: ResultSet = stmt.executeQuery(riskIdSql)
      val sbb = new StringBuffer()
      while(riskid_set.next()){
        val ele = riskid_set.getString("risk_id")
        sbb.append("\'"+ele+"\',")
      }
      if(sbb!=null && sbb.length()!=0){
        val riskid_list = sbb.toString
        val elelist = riskid_list.substring(0,riskid_list.length-1)
        val beforeAvgSql = "select round(avg(amount),2) as first_avg_amount from use_credit_apply where credit_risk_id in ("+elelist+")  and order_date < '" + DateFormat.formatPayDate(first_apply_time) + "' "
        val beforeAvg = stmt.executeQuery(beforeAvgSql)
        while (beforeAvg.next()) {
          res = beforeAvg.getDouble("first_avg_amount")
        }
      }
    }catch {
      case e:Exception=>e.printStackTrace()
    }finally {
      ConnectPoolUtil.closeCon(stmt,conn)
    }
    res
  }
  def getFirstAvg_new1(supplier_code:String,credit_risk_id: String): Double = {
    //根据credit_no_key查询这个主体的所有riskid，然后去单据表里找所有的riskid
    var conn: Connection = null
    var stmt: Statement = null
    var res = 0.0
    try{
      conn = ConnectPoolUtil.getConnection()
      stmt  = conn.createStatement()
      //查询节点前的所有riskid
      val riskIdSql = "select round(r12_mean,2) as first_avg_amount  from credit_apply_info where credit_risk_id != '" + credit_risk_id + "'  and supplier_code = '" + supplier_code + "' " + " order by create_time desc limit 1"
      val beforeAvg = stmt.executeQuery(riskIdSql)
        while (beforeAvg.next()) {
          res = beforeAvg.getDouble("first_avg_amount")
        }
    }catch {
      case e:Exception=>e.printStackTrace()
    }finally {
      ConnectPoolUtil.closeCon(stmt,conn)
    }
    res
  }


  def getEndAvg(supplier_code: String, first_apply_time: String, apply_time: String, credit_no: String): Double = {
    var res = 0.0
    val connection = ConnectPoolUtil.getConnection()
    val sql = "select round(avg(amount),2) as end_avg_amount  from business_data_from_sftp where  supplier_code = '" + supplier_code + "' and order_date < '" + apply_time + "' and order_date >=  '" + DateFormat.formatPayDate(first_apply_time) + "' and credit_no = '" + credit_no + "'"
    val psmt = connection.prepareStatement(sql)
    val resultSet = psmt.executeQuery()
    while (resultSet.next()) {
      res = resultSet.getDouble("end_avg_amount")
    }
    res
  }

  def getEndAvg_new(supplier_code: String, first_apply_time: String, apply_time: String, credit_no_key: String): Double = {
    //根据credit_no_key查询这个主体的所有riskid，然后去单据表里找所有的riskid
    var conn: Connection = null
    var stmt: Statement = null
    var res = 0.0
    try{
      conn = ConnectPoolUtil.getConnection()
      stmt  = conn.createStatement()
      //查询节点前的所有riskid
      val riskIdSql = "select risk_id from credit_apply_info_dtl where credit_no_key = '" + credit_no_key + "'  and supplier_code = '" + supplier_code + "' "
      val riskid_set: ResultSet = stmt.executeQuery(riskIdSql)
      val sbb = new StringBuffer()
      while(riskid_set.next()){
        val ele = riskid_set.getString("risk_id")
        sbb.append("\'"+ele+"\',")
      }
      if(sbb!=null && sbb.length()!=0){
        val riskid_list = sbb.toString
        val elelist = riskid_list.substring(0,riskid_list.length-1)
        val beforeAvgSql = "select round(avg(amount),2) as first_avg_amount from use_credit_apply where credit_risk_id in ("+elelist+")  and order_date < '" + apply_time + "' and order_date >=  '" + DateFormat.formatPayDate(first_apply_time) + "' "
        val beforeAvg = stmt.executeQuery(beforeAvgSql)
        while (beforeAvg.next()) {
          res = beforeAvg.getDouble("first_avg_amount")
        }
      }
    }catch {
      case e:Exception=>e.printStackTrace()
    }finally {
      ConnectPoolUtil.closeCon(stmt,conn)
    }
    res
  }
  def getEndAvg_new1(supplier_code:String,credit_risk_id: String): Double = {
    //根据credit_no_key查询这个主体的所有riskid，然后去单据表里找所有的riskid
    var conn: Connection = null
    var stmt: Statement = null
    var res = 0.0
    try{
      conn = ConnectPoolUtil.getConnection()
      stmt  = conn.createStatement()
      //查询节点前的所有riskid
      val riskIdSql = "select round(r12_mean,2) as first_avg_amount  from credit_apply_info where credit_risk_id = '" + credit_risk_id + "'  and supplier_code = '" + supplier_code + "' " + " order by create_time desc limit 1"
      val beforeAvg = stmt.executeQuery(riskIdSql)
      while (beforeAvg.next()) {
        res = beforeAvg.getDouble("first_avg_amount")
      }
    }catch {
      case e:Exception=>e.printStackTrace()
    }finally {
      ConnectPoolUtil.closeCon(stmt,conn)
    }
    res
  }



}
