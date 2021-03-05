package com.springboot.common;

import com.alibaba.fastjson.JSONObject;
import com.springboot.scala.SaveCosumerData;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;
import scala.collection.JavaConverters;
import scala.collection.Seq;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;


public class KafkaSaveData extends Thread {
    private String topic;//主题
    private static Map<String, String> config = new HashMap<String, String>();
    private String table ="web_data_profil";//表名
    private String isDelInsert = "false";
    private String isTrancate = "false";
    private Map textCode = null;
    private  Consumer<String, String> consumer = null;
    final static String GATE_EVENT_TBL = "cqyl_pre.GATE_GATE_EVT";
    final  static  String  LOCKPRE = "pre";
    final  static  String  LOCKMAIN = "main";

    protected static final Logger logger = Logger.getLogger(KafkaSaveData.class.getName());

    public KafkaSaveData(String topic,String table,String isDelInsert,String isTruncate,Map textCode,Consumer consumer ) {
        super();
        this.topic = topic;
        this.table = table;
        this.isDelInsert = isDelInsert;
        this.isTrancate = isTruncate;
        this.consumer = consumer;
        this.textCode = textCode;
    }

    //创建消费者
    public  Consumer<String, String> createConsumer() {

        try {
            if (config.keySet().size() <= 0)
                config.putAll(ReadPropertiesUtils.readConfig("project.properties"));
        } catch (IOException e) {
            System.out.println(e.toString());
        }

        Properties properties = new Properties();
        //声明zookeeper集群链接地址
        properties.put("zookeeper.connect", config.get("zkQuorum"));
        //必须要使用别的组名称， 如果生产者和消费者都在同一组，则不能访问同一组内的topic数据
        //当前消费者的组名称
        properties.put("group.id", config.get("group"));
        properties.put("zookeeper.session.timeout.ms", "30000");
        properties.put("max.poll.interval.ms","10000");
        properties.put("enable.auto.commit",config.get("enable.auto.commit"));
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, config.get("metadata.broker.list"));
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        return new KafkaConsumer<String, String>(properties);

    }

    @Override
    public void run() {
        Logger.getLogger(this.getClass().getName()).setLevel(Level.OFF);
        //创建消费者
        if(consumer == null)
            consumer = createConsumer();
        //主题数map
        consumer.subscribe(Arrays.asList(topic));
//        try {
//            Thread.sleep(123000);
//            System.out.println("消费者对象1：" + consumer+"["+new Date()+"]");
//        }catch (Exception e){
//
//        }


//        while (true) {
        ConsumerRecords<String, String> records = null;
        try {
            records = consumer.poll(Integer.parseInt(config.get("pollNum")));
        }finally {
            consumer.commitSync();
            consumer.close();
            consumer = null;
        }
            //System.out.println(records);
            List<String[]> reds = new ArrayList<>();
            List<String[]> sqlListDyc = new ArrayList<>();

            boolean isRecordExists = false;//没有消费数据

            for (ConsumerRecord<String, String> record : records) {
                isRecordExists = true;
                logger.setLevel(Level.INFO);
                logger.info("接收到: " + record.offset() + record.key() + record.value());
//                System.out.println("接收到: " + record.offset() + record.key() + record.value());
                String[] nameValue = {String.valueOf(record.offset()), record.value()};
                try {
                    String[] array = JsonObjectToAttach.getJsonList(record.value(), null);
                    if (array != null) {
                        //表名固定了，根据实际情况修改
                        for (int m = 0; m < this.table.split(";").length; m++) {
                            if (textCode != null && textCode.size() > 0) {
                                JSONObject jsonObject = null;
                                try {
                                    String code = textCode.get(table.split(";")[m]).toString();
                                    jsonObject = JSONObject.parseObject(record.value());
                                    if (jsonObject.get("tx_code") != null && !StringUtils.isEmpty(code) && !jsonObject.get("tx_code").equals(code))
                                        continue;
                                } catch (Exception exx) {

                                }

                            }
                            //删除当前表数据，保留历史表数据
                            String[] sql = JsonObjectToAttach.getBatchStatement(array, table.split(";")[m], "", "",
                                    !(isDelInsert.indexOf(";") > 0 ? isDelInsert.split(";")[m] : isDelInsert).equalsIgnoreCase("false"), new HashMap(),
                                    !(isTrancate.indexOf(";") > 0 ? isTrancate.split(";")[m] : isTrancate).equalsIgnoreCase("false"));
                            if (!reds.contains(sql) && null != sql)
                                reds.add(sql);

                            String[] sqlDyc = JsonObjectToAttach.getMetaSqls(table.split(";")[m], "", array);
                            if (null != sqlDyc && !sqlListDyc.contains(sqlDyc))
                                sqlListDyc.add(sqlDyc);
                        }

                    }

                } catch (Exception e) {
                    System.out.println(e.toString());
                }
            }

        for (int m = 0; m < this.table.split(";").length; m++) {
            if(!isRecordExists && table.split(";")[m].equalsIgnoreCase(GATE_EVENT_TBL)){//闸机执行没数据操作
                String[] sqlDyc = JsonObjectToAttach.getMetaSqls(GATE_EVENT_TBL, "", null);
                if (null != sqlDyc && !sqlListDyc.contains(sqlDyc))
                    sqlListDyc.add(sqlDyc);
            }
            //推送更新数据
           UpdTblProducter u =  new UpdTblProducter(table.split(";")[m]);
            u = null;
        }



            try {
                Seq<String[]> tmpSeq = JavaConverters.asScalaIteratorConverter(reds.iterator()).asScala().toSeq();
                if (tmpSeq.size() > 0) {
                    synchronized (LOCKPRE) {
                        SaveCosumerData.main(tmpSeq.toList());
                    }
                }

                tmpSeq = JavaConverters.asScalaIteratorConverter(sqlListDyc.iterator()).asScala().toSeq();
                if (tmpSeq.size() > 0) {
                    synchronized (LOCKMAIN) {
                        SaveCosumerData.main(tmpSeq.toList());
                    }
                }

//                Thread.sleep(500);
            } catch (Exception e) {
                System.out.println(e.toString());
            }
//        }
    }

    //线程数量
    public static final int NUM_PROCESS = 6;
    public static void main(String[] args) {
        try {
            config.putAll(ReadPropertiesUtils.readConfig("project.properties"));
        } catch (IOException e) {
            System.out.println(e.toString());
        }
//        System.out.println(config);
//        new KafkaSaveData("bingfu","web_data_profil").start();

        //取得有效主题
        Map<String,String> topicM = JsonObjectToAttach.getValidProperties("topics",null,null,false);
        ExecutorService executorService =  Executors.newFixedThreadPool(NUM_PROCESS);
        for(Map.Entry<String, String> m : topicM.entrySet()){
            String []tabAndMark = null;
            if(m.getValue().indexOf(",")>=0){
                tabAndMark =m.getValue().split(",");
            }

            KafkaSaveData kafkaSaveData = new KafkaSaveData(m.getKey(),tabAndMark==null?m.getValue():tabAndMark[0],
                    tabAndMark==null?"false":tabAndMark[1],tabAndMark==null?"false":tabAndMark[2],null,null);
            executorService.execute(kafkaSaveData);
        }
        executorService.shutdown();
    }

}
