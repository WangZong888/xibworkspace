package cn.bigdata.rhdetailindex.task

import java.text.DecimalFormat

import cn.bigdata.rhdetailindex.deal.Cal_Derive_Collect
import cn.bigdata.rhdetailindex.utils.{KafkaProducerUtil, KafkaZookeeperCheckPoint, SendContext}
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
object RHDetail_Index_Task {

  def getSparkSession = {
    SparkSession
      .builder()
      .appName(s"${this.getClass.getSimpleName}")
      .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
      //.master("local[1]")
      .getOrCreate()
  }

  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.ERROR)
    val config: Config = ConfigFactory.load()
    val session: SparkSession = getSparkSession
    val sc = session.sparkContext
    val ssc = new StreamingContext(sc, Seconds(1))
    val topics = Array(config.getString("RECEIVE_TOPIC"))
    val df = new DecimalFormat(".##")

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
      if (!rdd.isEmpty()) {
        val lines = rdd.map(x => {
          x.value()
        })
        try {
          println("开始计算指标数据")
          val maprdd= lines.map(m => {
            Try {
              XML.loadString(m)
            }
          })
          if(maprdd.filter(_.isSuccess).isEmpty()){
            throw new RuntimeException("XML format error,no data")
          }else{
            val perrdd: RDD[Elem] = maprdd.filter(_.isSuccess).map(_.get).persist(StorageLevel.MEMORY_ONLY_SER)
            perrdd.collect()
            //指标计算处理逻辑
            Cal_Derive_Collect.cal_index(perrdd)
            perrdd.unpersist()
          }
        } catch {
          case e:RuntimeException=>e.printStackTrace()
            val send_info = SendContext.send_data("", "", "", "0", e.getMessage)
            println("发送给引擎的数据:"+send_info)
            KafkaProducerUtil.sendSync(config.getString("SEND_TOPIC"), send_info)
          case e: Exception => e.printStackTrace()
            val send_info = SendContext.send_data("", "", "", "0", "transformation error")
            println("发送给引擎的数据:"+send_info)
            KafkaProducerUtil.sendSync(config.getString("SEND_TOPIC"), send_info)
        } finally {
          KafkaZookeeperCheckPoint.storeOffsets(rdd.asInstanceOf[HasOffsetRanges].offsetRanges, config.getString("GROUPID"))
        }
      }
    })
    ssc.start()
    ssc.awaitTermination()
  }

}
