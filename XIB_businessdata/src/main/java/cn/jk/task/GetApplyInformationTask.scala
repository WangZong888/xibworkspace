package cn.jk.task

import cn.jk.pojo.Global
import cn.jk.util.KafkaZookeeperCheckPoint
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.log4j.{Level, Logger}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.dstream.InputDStream
import org.apache.spark.streaming.kafka010.HasOffsetRanges
import org.apache.spark.streaming.{Seconds, StreamingContext}


/**
  * Created by YiZe on 2019/5/24.
  */
object GetApplyInformationTask {
  def getSparkSession = {
    SparkSession
      .builder()
      .appName(s"${this.getClass.getSimpleName}")
      .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
      .config("spark.sql.warehouse.dir", "file:///")
      .config("spark.streaming.backpressure.enabled", "true") //开启spark反压
      .config("spark.streaming.backpressure.initialRate", "200") //spark启动后第一次处理的数据量
      .config("spark.streaming.concurrentJobs", "10") //提高job并发数
      .config("spark.streaming.kafka.consumer.poll.ms", "60000")
      //.master("local[1]")
      .getOrCreate()
  }

  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.ERROR)
    val session: SparkSession = getSparkSession
    val sc = session.sparkContext
    val ssc: StreamingContext = new StreamingContext(sc, Seconds(1))
    val topics = Array("xiaguohang_first_loan", "xiaguohang", "xiaguohang_repeatloan_send")
    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> Global.BROKERS,
      "key.deserializer" -> classOf[StringDeserializer],
      "value.deserializer" -> classOf[StringDeserializer],
      "group.id" -> Global.GROUPID,
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
    val kafka_messages: InputDStream[ConsumerRecord[String, String]] = KafkaZookeeperCheckPoint.createMyZookeeperDirectKafkaStream(ssc, kafkaParams, topics, Global.GROUPNAME)
    kafka_messages.foreachRDD(rdd => {
      if (!rdd.isEmpty()) {
        try {
          val resTopic: RDD[(String, String)] = rdd.map(x => {
            val value = x.value()
            val topic = x.topic()
            (topic, value)
          })
          resTopic.foreach(x=>{println("接收到的topic和消息分别是："+x._1+"----->"+x._2)})

          resTopic.foreach(x => {
            if (null != x._1 && (x._1.equals("xiaguohang_first_loan") || x._1.equals("xiaguohang_repeatloan_send"))) {
              println("接收到topic:"+ x._1 +",开始计算")
              CreditApplyTask.doCreditApply(x._1, x._2)
            } else {
              println("接收到topic:"+ x._1 +",开始计算")
              UseCreditTask.doUseCredit(x._1, x._2)
            }
          })

        }
        catch {
          case e: Exception => e.printStackTrace()
        }
        finally {
          KafkaZookeeperCheckPoint.storeOffsets(rdd.asInstanceOf[HasOffsetRanges].offsetRanges, Global.GROUPID)
        }
      }
    }

    )
    ssc.start()
    ssc.awaitTermination()
  }

}
