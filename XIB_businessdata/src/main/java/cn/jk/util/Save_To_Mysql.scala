package cn.jk.util

import java.sql.{Connection, Statement}

import scala.collection.mutable

object Save_To_Mysql {

  def save_data(field_mapper:mutable.Map[String, Object],tablename:String):Unit = {
    println("开始进行数据存储。。。。")
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
      val sql = s"replace into ${tablename}("+field_list_result+") values("+field_value_list_result+")"
      println("执行的sql-------------"+sql)
      stmt.execute(sql)
      conn.commit()
      println("数据存储完成。。。。")
    }catch {
      case e:Exception=>e.printStackTrace();conn.rollback()
    }finally {
      ConnectPoolUtil.closeCon(stmt,conn)
    }

  }
}
