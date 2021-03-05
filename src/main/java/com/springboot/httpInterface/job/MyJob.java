package com.springboot.httpInterface.job;

import com.springboot.common.JsonObjectToAttach;
import com.springboot.common.KafkaSaveData;
import com.springboot.common.ReadPropertiesUtils;
import org.apache.commons.collections.map.HashedMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.kafka.clients.consumer.*;


/**
 * Created by yzn00 on 2019/7/4.
 */
public class MyJob implements BaseJob {
//    @Autowired
//    PersonService personService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
//        personService.selectAllPerson();
        saveCocumerData();
//        System.out.println("hello,my first springboot job!" + context.getJobDetail().getKey());
//        _log.info("hello,my first springboot job!" + context.getJobDetail().getKey());
    }

    private static Logger _log = LoggerFactory.getLogger(MyJob.class);

    final static int NUM_PROCESS = 6;
    static Map<String, String> config = new HashMap<String, String>();

    //取得有效主题
    static Map<String, String> topicM = new HashMap<>();

    //取得table对应的code
    static Map<String,String> tblCd = new HashedMap();

    final String topicName = "topics";
    final String configName = "project.properties";

    //消费者
    static Map<String,Consumer> consumerMap = new HashedMap();

    //保存消费信息
    static Map<String,KafkaSaveData> kafakData = new HashedMap();

    public MyJob() {
        try {
            if (config.size() == 0)
                config.putAll(ReadPropertiesUtils.readConfig(configName));
            if(tblCd.size()==0)
                tblCd = JsonObjectToAttach.getTableTextCode(topicName,null,false);
            if (topicM.size() == 0)
                //取得有效主题
                topicM = JsonObjectToAttach.getValidProperties(topicName, null, null,false);
//            if(consumerMap.size() == 0){
//                for (Map.Entry<String,String> m :topicM.entrySet()){
////                    Consumer c =KafkaSaveData.createConsumer();
////                    consumerMap.put(m.getKey(),c);
//
//                    String[] tabAndMark = null;
//                    if (m.getValue().indexOf(",") >= 0) {
//                        tabAndMark = m.getValue().split(",");
//                    }
//
//                    if(kafakData.get(m.getKey())==null)
//                        kafakData.put(m.getKey(), new KafkaSaveData(m.getKey(), tabAndMark == null ? m.getValue() : tabAndMark[0],
//                            tabAndMark == null ? "false" : tabAndMark[1], tabAndMark == null ? "false" : tabAndMark[2],tblCd,null));
//                }
//            }
        } catch (IOException e) {
            System.out.println(e.toString());
        }

    }

    /**
     * 消费数据保存到数据库
     */
    private void saveCocumerData() {

//        System.out.println(config);
//        new KafkaSaveData("bingfu","web_data_profil").start();

        ExecutorService executorService = Executors.newFixedThreadPool(NUM_PROCESS);
        for (Map.Entry<String, String> m : topicM.entrySet()) {
            KafkaSaveData kafkaSaveData = kafakData.get(m.getKey());
            if(kafkaSaveData == null) {
                String[] tabAndMark = null;
                if (m.getValue().indexOf(",") >= 0) {
                    tabAndMark = m.getValue().split(",");
                }

                 kafkaSaveData = new KafkaSaveData(m.getKey(), tabAndMark == null ? m.getValue() : tabAndMark[0],
//                        tabAndMark == null ? "false" : tabAndMark[1], tabAndMark == null ? "false" : tabAndMark[2],null, consumerMap.get(m.getKey()));
                         tabAndMark == null ? "false" : tabAndMark[1], tabAndMark == null ? "false" : tabAndMark[2],tblCd, null);
            }
            executorService.execute(kafkaSaveData);
        }
        executorService.shutdown();
    }


}
