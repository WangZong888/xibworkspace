package cn.jk.util

import java.sql.{Connection, Statement}

import cn.jk.pojo.Global
import org.apache.commons.dbcp.BasicDataSource


object ConnectPoolUtil {


  Class.forName("com.mysql.jdbc.Driver")
  private var bs:BasicDataSource = null
  /**
    * 创建数据源
    * @return
    */
  def getDataSource():BasicDataSource={
//    val config: Config = ConfigFactory.load()
    if(bs==null){
      bs = new BasicDataSource()
      bs.setDriverClassName("com.mysql.jdbc.Driver")
      bs.setUrl(Global.MYSQL_DRIVER_URL)
      bs.setUsername(Global.MYSQL_USERNAME)
      bs.setPassword(Global.MYSQL_PWD)
      bs.setMaxIdle(100)          //设置最大并发数
      bs.setMaxActive(20)
      bs.setInitialSize(10)          //数据库初始化时，创建的连接个数
      bs.setMinIdle(10)              //最小空闲连接数
      bs.setMaxWait(10000)
      bs.setTestWhileIdle(true)
      bs.setTestOnBorrow(false)
      bs.setMinEvictableIdleTimeMillis(1800000)
      bs.setTimeBetweenEvictionRunsMillis(30000)
    }
    bs
  }

  /**
    * 释放数据源
    */
  def shutDownDataSource(){
    if(bs!=null){
      bs.close()
    }
  }

  /**
    * 获取数据库连接
    * @return
    */
  def getConnection():Connection={
    var con:Connection = null
    try {
      if(bs!=null){
        con = bs.getConnection()
      }else{
        con = getDataSource().getConnection()
      }
    } catch{
      case e:Exception => println(e.getMessage)
    }
    con
  }


  /**
    * 关闭连接
    */
  def closeCon(stmt:Statement,con:Connection){
    if(stmt!=null){
      try {
        stmt.close()
      } catch{
        case e:Exception => println(e.getMessage)
      }
    }
    if(con!=null){
      try {
        con.close()
      } catch{
        case e:Exception => println(e.getMessage)
      }
    }
  }

}
