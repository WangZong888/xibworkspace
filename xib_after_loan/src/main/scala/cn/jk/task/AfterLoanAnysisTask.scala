package cn.jk.task

import cn.jk.agent.AfterLoanApply
import cn.jk.pojo.Global
import cn.jk.util.{KafkaProducerUtil, KafkaZookeeperCheckPoint}
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.log4j.{Level, Logger}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.dstream.InputDStream
import org.apache.spark.streaming.kafka010.HasOffsetRanges
import org.apache.spark.streaming.{Seconds, StreamingContext}

/** cn.jk.task.AfterLoanAnysisTask
  * Created by YiZe on 2019/6/6.
  */
object AfterLoanAnysisTask {
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
      .config("spark.executor.heartbeatInterval","60")
      .master("local[1]")
      .getOrCreate()
  }

  def main(args: Array[String]): Unit = {
    Logger.getLogger("org").setLevel(Level.ERROR)
    val session: SparkSession = getSparkSession
    val sc = session.sparkContext
    val ssc: StreamingContext = new StreamingContext(sc, Seconds(1))
    val topics = Array("xib_credit_apply_info")
    //val topics = Array("xiaguohang_repeatloan_inner")
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
            val strings: Array[String] = value.split("\001")
            (strings(1), strings(0))
          })
          resTopic.foreach(println)
          var set = Set[String]()
          resTopic.collect().foreach(f=>{
            set  += f._1+"|"+f._2
          })
          for(x<-set){
            val split_str = x.split("\\|")
            if (null != split_str(0) && split_str(0).equals("xiaguohang_repeatloan_send")) {
              if(Global.isJson(split_str(1))){
                AfterLoanApply.afterLoan(rdd.sparkContext,split_str(1))
              }else{
                KafkaProducerUtil.sendSync("xiaguohang_repeatloan_receive", "消息异常，非JSON格式" + split_str(1))
              }
            }
          }
        }
        catch {
          case e: Exception => e.printStackTrace()
        }
        finally {
          KafkaZookeeperCheckPoint.storeOffsets(rdd.asInstanceOf[HasOffsetRanges].offsetRanges, Global.GROUPID)
        }
      }
    })
    ssc.start()
    ssc.awaitTermination()
  }

}
