package cn.jk.util

import java.sql.{Connection, Statement}

import scala.collection.mutable

object Save_To_BackData {

  def save_data(field_mapper:mutable.Map[String, Object],tablename:String,conn: Connection,stmt: Statement):Unit = {
    try{
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
      stmt.addBatch(insert_sql)
    }catch {
      case e:Exception=>e.printStackTrace()
    }

  }
}
