package cn.jkjf.rhdetail.utils

import com.typesafe.config.{Config, ConfigFactory}
import org.apache.curator.framework.CuratorFrameworkFactory
import org.apache.curator.retry.ExponentialBackoffRetry
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.common.TopicPartition
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.dstream.InputDStream
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.kafka010._

import scala.collection.JavaConversions._
import scala.collection.mutable

/**
  * Created by Macbook on 2018/10/24.
  */
object KafkaZookeeperCheckPoint {
  val config: Config = ConfigFactory.load()
  // ZK client
  val client = {
    val client = CuratorFrameworkFactory
      .builder
      .connectString(config.getString("ZKQUORUM"))
      .retryPolicy(new ExponentialBackoffRetry(1000, 3))
      .namespace("rhdetail")
      .build()
    client.start()
    client
  }
  // offset 路径起始位置
  val Globe_kafkaOffsetPath = "/kafka/rhdetail/offsets"

  // 路径确认函数  确认ZK中路径存在，不存在则创建该路径
  def ensureZKPathExists(path: String) = {

    if (client.checkExists().forPath(path) == null) {
      client.create().creatingParentsIfNeeded().forPath(path)
    }

  }

  // 保存 新的 offset
  def storeOffsets(offsetRange: Array[OffsetRange], groupName: String) = {

    for (o <- offsetRange) {
      val zkPath = s"${Globe_kafkaOffsetPath}/${groupName}/${o.topic}/${o.partition}"
      ensureZKPathExists(zkPath)
      // 向对应分区第一次写入或者更新Offset 信息
      println("---Offset写入ZK------\nTopic：" + o.topic + ", Partition:" + o.partition + ", Offset:" + o.untilOffset+ ", date:" + Global.getCreate_Date)
      client.setData().forPath(zkPath, o.untilOffset.toString.getBytes())
    }
  }

  def getFromOffset(topic: Array[String], groupName: String): (Map[TopicPartition, Long], Int) = {

    // Kafka 0.8和0.10的版本差别，0.10 为 TopicPartition   0.8 TopicAndPartition
    var fromOffset: Map[TopicPartition, Long] = Map()

    val topic1 = topic(0).toString

    // 读取ZK中保存的Offset，作为Dstrem的起始位置。如果没有则创建该路径，并从 0 开始Dstream
    val zkTopicPath = s"${Globe_kafkaOffsetPath}/${groupName}/${topic1}"

    // 检查路径是否存在
    ensureZKPathExists(zkTopicPath)

    // 获取topic的子节点，即 分区
    val childrens = client.getChildren().forPath(zkTopicPath)

    // 遍历分区
    val offSets: mutable.Buffer[(TopicPartition, Long)] = for {
      p <- childrens
    }
      yield {

        // 遍历读取子节点中的数据：即 offset
        val offsetData = client.getData().forPath(s"$zkTopicPath/$p")
        // 将offset转为Long
        val offSet = java.lang.Long.valueOf(new String(offsetData)).toLong
        // 返回  (TopicPartition, Long)
        (new TopicPartition(topic1, Integer.parseInt(p)), offSet)
      }
    println(offSets.toMap)

    if (offSets.isEmpty) {
      (offSets.toMap, 0)
    } else {
      (offSets.toMap, 1)
    }


  }

  def createMyZookeeperDirectKafkaStream(ssc:StreamingContext, kafkaParams:Map[String, Object], topics:Array[String],
                                         groupName:String ):InputDStream[ConsumerRecord[String, String]] = {

    // get offset  flag = 1  表示基于已有的offset计算  flag = 表示从头开始(最早或者最新，根据Kafka配置)
    val (fromOffsets, flag) = getFromOffset(topics, groupName)
    var kafka_messages:InputDStream[ConsumerRecord[String, String]] = null
    /*if (flag == 1){
      // 加上消息头
      //val messageHandler = (mmd:MessageAndMetadata[String, String]) => (mmd.topic, mmd.message())
      println(fromOffsets)
      kafkaStream = KafkaUtils.createDirectStream(ssc, LocationStrategies.PreferConsistent,
        ConsumerStrategies.Subscribe(topic, kafkaParams, fromOffsets))
      println(fromOffsets)

    } else {
      kafkaStream = KafkaUtils.createDirectStream(ssc, LocationStrategies.PreferConsistent,
        ConsumerStrategies.Subscribe(topic, kafkaParams))
    }*/
    if (flag == 1) {
      println(fromOffsets)
      kafka_messages = KafkaUtils.createDirectStream[String, String](
        ssc,
        PreferConsistent, //consumer调度分区位置策略
        Subscribe[String, String](topics, kafkaParams, fromOffsets) //consumer消费策略
      )
    } else {
      kafka_messages = KafkaUtils.createDirectStream[String, String](
        ssc,
        PreferConsistent, //consumer调度分区位置策略
        Subscribe[String, String](topics, kafkaParams) //consumer消费策略
      )
    }
    kafka_messages
  }


}
