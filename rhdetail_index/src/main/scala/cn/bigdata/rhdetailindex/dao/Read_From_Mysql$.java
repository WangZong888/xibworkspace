/*
object Read_From_Mysql {

//  def read_appladate(risk_id:String):String = {
//    var conn: Connection = null
//    var stmt: Statement = null
//    var apply_date = Global.getCreate_Date()
//    try{
//      conn = ConnectPoolUtil.getConnection()
//      conn.setAutoCommit(false)
//      stmt = conn.createStatement()
//      val sql = s"select apply_date from credit_apply_info_dtl where risk_id = '${risk_id}' order by create_time desc limit 1"
//      val rs: ResultSet = stmt.executeQuery(sql)
//      while (rs.next()){
//        apply_date = rs.getString("apply_date")
//      }
//      conn.commit()
//    }catch {
//      case e:Exception=>e.printStackTrace();conn.rollback()
//    }finally {
//      ConnectPoolUtil.closeCon(stmt,conn)
//    }
//    apply_date
//  }
}
*/
