package cn.jk.analysis

import org.apache.spark.sql.SparkSession

/**
  * Created by YiZe on 2019/5/21.
  */
object Session {
    def getSparkSession(): SparkSession ={
      val session = SparkSession
        .builder()
        .master("local[2]")
        .appName(s"${this.getClass.getSimpleName}")
        .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
        .getOrCreate()
      session
    }
}
