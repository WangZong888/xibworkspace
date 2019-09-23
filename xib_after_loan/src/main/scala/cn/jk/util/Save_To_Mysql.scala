package cn.jk.util

import java.sql.{Connection, Statement}

import scala.collection.mutable

object Save_To_Mysql {

  def save_data(field_mapper:mutable.Map[String, Object],tablename:String):Unit = {
    var conn: Connection = null
    var stmt: Statement = null
    try{
      conn = ConnectPoolUtil.getConnection()
      conn.setAutoCommit(false)
      stmt = conn.createStatement()
      val field_list = new StringBuffer
      val field_value_list = new StringBuffer
      field_mapper.keys.foreach(m=>{
        field_list.append(m+",")
        field_value_list.append("\'"+field_mapper(m)+"\',")
      })

      val field_list_result = field_list.substring(0,field_list.length-1)
      val field_value_list_result = field_value_list.substring(0,field_value_list.length-1)
      //先备份，在删除插入
      val insert_sql = s"replace into ${tablename}("+field_list_result+") values("+field_value_list_result+")"
      stmt.execute(insert_sql)
      conn.commit()
    }catch {
      case e:Exception=>e.printStackTrace();conn.rollback()
    }finally {
      ConnectPoolUtil.closeCon(stmt,conn)
    }

  }
}
