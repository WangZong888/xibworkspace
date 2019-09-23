
package cn.jq.bigdata.utils;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

/**
 * kafka 消息生产者
 * @author grace 2018-06-25
 *
 */
@SuppressWarnings("all")
@Component
public class KafkaProducerUtil {


    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerUtil.class);

    private static KafkaProducer<String, String> producer;
    static {
        getProducer();
    }
    public static void getProducer() {
        Properties props = new Properties();
        //props.put("bootstrap.servers", "192.168.107.34:9092,192.168.107.29:9092,192.168.107.35:9092");
        props.put("bootstrap.servers", Global.BOOTSTRAP_SERVERS);
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        producer = new KafkaProducer<>(props);

    }

    /*
     * 消息发送
     */
    public static void sendSync_key(final String topic,final String key, final String value) {
        try {
            RecordMetadata recordMetadata = producer.send(new ProducerRecord<String, String>(topic,key, value)).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public static void sendSync(final String topic,final String value) {
        try {
            RecordMetadata recordMetadata = producer.send(new ProducerRecord<String, String>(topic, value)).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }


    public static void close(){
        if(producer!=null){
            producer.close();
        }
    }

}
