package cn.jkjf.rhdetail.task

import java.text.DecimalFormat

import cn.jkjf.rhdetail.deal.MatchingXml
import cn.jkjf.rhdetail.utils.{Global, KafkaZookeeperCheckPoint}
import com.typesafe.config.{Config, ConfigFactory}
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.log4j.{Level, Logger}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.apache.spark.storage.StorageLevel
import org.apache.spark.streaming.dstream.InputDStream
import org.apache.spark.streaming.kafka010.HasOffsetRanges
import org.apache.spark.streaming.{Seconds, StreamingContext}

import scala.util.Try
import scala.xml.{Elem, XML}

/**
  * add by grace 2019-06-02.
  */
object RHDetailTask {

  def getSparkSession = {
    SparkSession
      .builder()
      .appName(s"${this.getClass.getSimpleName}")
      .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
     //.master("local[1]")
      .getOrCreate()
  }

  def main(args: Array[String]): Unit = {
    System.setProperty("HADOOP_USER_NAME","hdfs")
    Logger.getLogger("org").setLevel(Level.ERROR)
    val config: Config = ConfigFactory.load()
    val session: SparkSession = getSparkSession
    val sc = session.sparkContext
    val ssc = new StreamingContext(sc, Seconds(1))
    val topics = Array(config.getString("RECEIVE_TOPIC"))
    val df = new DecimalFormat(".##")
    //val accum = sc.longAccumulator("rhdetail_Accumulator")
    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> config.getString("BROKERS"),
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> config.getString("GROUPID"),
      "auto.offset.reset" -> "latest",
      "fetch.message.max.bytes" -> (2621440: java.lang.Integer),
      "max.partition.fetch.bytes" -> (2048000: java.lang.Integer),
      "receive.buffer.bytes" -> (10485760: java.lang.Integer),
      "max.poll.records" -> (20000: java.lang.Integer),
      "heartbeat.interval.ms" -> (30000: java.lang.Integer),
      "session.timeout.ms" -> (40000: java.lang.Integer),
      "request.timeout.ms" -> (50000: java.lang.Integer),
      "enable.auto.commit" -> (false: java.lang.Boolean)
    )
    val kafka_messages: InputDStream[ConsumerRecord[String, String]] = KafkaZookeeperCheckPoint.createMyZookeeperDirectKafkaStream(ssc, kafkaParams, topics, config.getString("GROUPID"))
    kafka_messages.foreachRDD(rdd => {
      val todaydate = Global.getCreate_Date2()
      val timer = Global.getCreate_Date3()
      if (!rdd.isEmpty()) {
        val lines = rdd.map(x => {
          x.value()
        })
        try {
          println("开始处理数据")
          val maprdd= lines.map(m => {
            Try {
               XML.loadString(m)
            }
          })
          if(maprdd.filter(_.isSuccess).isEmpty()){
            throw new RuntimeException("XML format error,no data");
          }else{
            val perrdd: RDD[Elem] = maprdd.filter(_.isSuccess).map(_.get).persist(StorageLevel.MEMORY_ONLY_SER)
            perrdd.collect()
            maprdd.filter(_.isSuccess).map(_.get).repartition(1).saveAsTextFile(config.getString("SAVEPATH")+todaydate+"/"+timer)
            MatchingXml.xmlmatch(perrdd)
            perrdd.unpersist()
          }
        } catch {
          case e: Exception => e.printStackTrace()
        } finally {
          KafkaZookeeperCheckPoint.storeOffsets(rdd.asInstanceOf[HasOffsetRanges].offsetRanges, config.getString("GROUPID"))
        }
      }
    })
    ssc.start()
    ssc.awaitTermination()
  }

}
