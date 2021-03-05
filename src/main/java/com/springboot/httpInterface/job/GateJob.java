package com.springboot.httpInterface.job;

import com.alibaba.fastjson.JSONObject;
import com.springboot.common.JsonObjectToAttach;
import com.springboot.common.KafkaSaveData;
import com.springboot.common.ReadPropertiesUtils;
import com.springboot.common.SaveDataStatic;
import com.springboot.httpInterface.controller.HttpServiceTest;
import org.apache.commons.collections.map.HashedMap;
import org.apache.kafka.clients.consumer.Consumer;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Created by yzn00 on 2019/7/4.
 */
public class GateJob implements BaseJob {
//    @Autowired
//    PersonService personService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
//        personService.selectAllPerson();
        saveCocumerData();
//        System.out.println("hello,my first springboot job!" + context.getJobDetail().getKey());
//        _log.info("hello,my first springboot job!" + context.getJobDetail().getKey());
    }

    final String topicName = GateJob.class.getSimpleName();
    final String configName = "project.properties";

    private static Logger _log = LoggerFactory.getLogger(GateJob.class);

    final static int NUM_PROCESS = 6;
    static Map<String, String> config = new HashMap<String, String>();
    HttpServiceTest httpServiceTest = null;
    //取得有效主题
    static Map<String, String> topicM = new HashMap<>();

    //取得table对应的code
    static Map<String,String> tblCd = new HashedMap();

    //消费者
    static Map<String,Consumer> consumerMap = new HashedMap();

    //保存消费信息
    static Map<String,SaveDataStatic> saveDataStaticMap = new HashedMap();

    public GateJob() {
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
            SaveDataStatic saveDataStatic = saveDataStaticMap.get(m.getKey());
            if(saveDataStatic == null) {
                String[] tabAndMark = null;
                if (m.getValue().indexOf(",") >= 0) {
                    tabAndMark = m.getValue().split(",");
                }
                String url = "https://apiexposition.ipalmap.com/bigScreen/queryAllExhibition";

                if(httpServiceTest==null)
                    httpServiceTest = new HttpServiceTest();
                try {

                    List<String> listJson = new ArrayList<>();
                    String getJson = httpServiceTest.getJsonData(url, "utf-8", "bdid", "04NL14", false);
                    String[] array = JsonObjectToAttach.getJsonList(getJson, "data");
                    if(array.length>0){
                        array = JsonObjectToAttach.getJsonList(array[0], "exhibitionList");
                    }
                    for(String f : array) {
                        JSONObject object = JSONObject.parseObject(f);
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("id",object.get("id"));
                        jsonObject.put("organizationName",object.get("organizationName"));
                        jsonObject.put("flid",object.get("flid"));
                        jsonObject.put("floorName",object.get("floorName"));
                        jsonObject.put("exhibitionHall",object.get("exhibitionHall"));
                        jsonObject.put("featureId",object.get("featureId"));
                        jsonObject.put("bdid",object.get("bdid"));
                        jsonObject.put("exhibitionPosition",object.get("exhibitionPosition"));
                        listJson.add(jsonObject.toJSONString());
                    }
                    saveDataStatic = new SaveDataStatic(m.getKey(), tabAndMark == null ? m.getValue() : tabAndMark[0],
                            tabAndMark == null ? "false" : tabAndMark[1], tabAndMark == null ? "false" : tabAndMark[2], listJson);
                }catch (Exception e){

                }
            }
            executorService.execute(saveDataStatic);
        }
        executorService.shutdown();
    }


}
