package com.springboot.httpInterface.job;

import com.alibaba.fastjson.JSONObject;
import com.mchange.v2.collection.MapEntry;
import com.springboot.common.KafkaProducer;
import com.springboot.common.KafkaSaveData;
import com.springboot.httpInterface.SpringContextUtil;
import com.springboot.httpInterface.services.RyDataLargeService;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by yzn00 on 2019/8/7.
 */
public class ProductJob implements BaseJob {
    static RyDataLargeService ryDataLargeService;
    final int NUM_PROCESS = 6;
    String topic = "ta_data_upt_msg";//固定写入


    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

        Map mm = new HashedMap();
        if(ryDataLargeService == null)
            ryDataLargeService = (RyDataLargeService) SpringContextUtil.getBean(RyDataLargeService.class);
        List<Map> listUpd = ryDataLargeService.getUpdateTableInfo(mm);
        if(listUpd.size()<=0)
            return;
//        ExecutorService executorService = Executors.newFixedThreadPool(NUM_PROCESS);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for(Map m:listUpd){
            JSONObject jsonObject = new JSONObject();
            for(Object entry:m.entrySet()){
                Map.Entry<String,String> map = (Map.Entry<String,String>)entry;
                if(!map.getKey().equalsIgnoreCase("stat"))
                    if(!map.getKey().equalsIgnoreCase("update_time"))
                        jsonObject.put(map.getKey(),map.getValue());
                    else
                        jsonObject.put(map.getKey(),simpleDateFormat.format(map.getValue()));
            }
            KafkaProducer kafkaProducer = new KafkaProducer(topic,jsonObject.toJSONString());
            kafkaProducer.run();
            ryDataLargeService.deleteUpdateTableInfo(m);
//            executorService.execute(kafkaProducer);
        }
//        executorService.shutdown();

    }

    public static void main(String [] args){
        String topic = "ta_data_upt_msg";//固定写入
        KafkaSaveData kafkaSaveData = new KafkaSaveData(topic,null,null,null,null,null);
        kafkaSaveData.start();
    }
}
