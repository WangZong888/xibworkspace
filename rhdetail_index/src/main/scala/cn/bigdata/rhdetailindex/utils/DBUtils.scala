package cn.bigdata.rhdetailindex.utils

import java.sql.{Connection, DriverManager}

import com.typesafe.config.{Config, ConfigFactory}

object DBUtils {
  Class.forName("com.mysql.jdbc.Driver")
  def getConnection: Connection = {
    val config: Config = ConfigFactory.load()
    DriverManager.getConnection(config.getString("MYSQL_DRIVER_URL"), config.getString("MYSQL_USERNAME"), config.getString("MYSQL_PWD"))
  }
  def close(conn: Connection): Unit = {
    try{
      if(!conn.isClosed() || conn != null){
        conn.close()
      }
    }
    catch {
      case ex: Exception => {
        ex.printStackTrace()
      }
    }finally {
      conn.close()
    }
  }
}
