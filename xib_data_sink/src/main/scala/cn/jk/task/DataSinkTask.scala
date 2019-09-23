package cn.jk.task

import cn.jk.agent.DataSinkTranser
import org.apache.log4j.{Level, Logger}
import org.apache.spark.sql.SparkSession

object DataSinkTask {
  def getSparkSession = {
    SparkSession
      .builder()
      .appName(s"${this.getClass.getSimpleName}")
      .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
       .master("local[1]")
      .getOrCreate()
  }

  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.ERROR)
    val session: SparkSession = getSparkSession
    val sc = session.sparkContext
    DataSinkTranser.afterLoan(sc)
  }
}
