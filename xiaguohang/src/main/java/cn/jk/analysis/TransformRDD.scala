package cn.jk.analysis

import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession

/**
  * Created by YiZe on 2019/5/21.
  */
object TransformRDD {
  def transforRDD(session: SparkSession, path: String): RDD[String] = {
    session.sparkContext.textFile(path)
  }
}
