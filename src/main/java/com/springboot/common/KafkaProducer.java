package com.springboot.common;


import kafka.serializer.StringEncoder;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;


/**
 * Created by DFJX on 2019/6/23.
 */
public class KafkaProducer extends Thread {
    private static Map<String, String> config = new HashMap<String, String>();

    private String topic;
    private String value;
    public KafkaProducer(String topic, String value){
        super();
        this.topic = topic;
        this.value = value;
    }

    protected static final Logger logger = Logger.getLogger(KafkaProducer.class.getName());

    //创建生产者
    private Producer<Integer, String> createProducer(){
        Properties properties = new Properties();
        try {
            if(config.keySet().size()<=0)
                config.putAll(ReadPropertiesUtils.readConfig("project.properties"));
        } catch (IOException e) {
            System.out.println(e.toString());
        }

        //zookeeper单节点
        properties.put("zookeeper.connect",config.get("zkQuorum"));
        properties.put("serializer.class", StringEncoder.class.getName());
        // 声明kafka集群的 broker

        //kafka单节点
        properties.put("metadata.broker.list", config.get("metadata.broker.list"));
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put(org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,config.get("metadata.broker.list"));

        return new org.apache.kafka.clients.producer.KafkaProducer(properties);
    }

    public void run() {
        BufferedReader br = null;
        // 创建生产者
        Producer<Integer, String> producer = null;
        try {
            // 创建生产者
            producer = createProducer();
            if (!StringUtils.isEmpty(value)) {
//                System.out.println("生产数据为：" + value);
                logger.info(topic+"=====生产数据为：" + value);
                producer.send(new ProducerRecord<Integer, String>(topic, value + "\n"));
            }

        }catch (Exception e){
            writeFile(topic,value);
            System.out.print(e.toString());
        }finally {
            producer.close();
        }
    }

    public static void writeFile(String topic,String value){
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(topic, true)));
            out.write(value+"\r\n");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        try {
			config.putAll(ReadPropertiesUtils.readConfig("project.properties"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        System.out.println(config);
        // 使用kafka集群中创建好的主题 test
        new KafkaProducer("bingfu","D:/123.sql").start();
    }
}
