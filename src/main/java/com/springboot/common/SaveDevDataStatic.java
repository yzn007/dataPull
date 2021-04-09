package com.springboot.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.springboot.scala.SaveModelData;
import scala.collection.JavaConverters;
import scala.collection.Seq;

import java.io.IOException;
import java.security.Key;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;


public class SaveDevDataStatic extends Thread {
    private String topic;//主题
    private static Map<String, String> config = new HashMap<String, String>();
    private String table = "web_data_profil";//表名
    private String isDelInsert = "false";
    private String isTrancate = "false";
    private String objectType = "objectType";
    private List<String> listJsonString = new ArrayList<>();

final static Logger logger =
        Logger.getLogger(SaveDevDataStatic.class.getName());

    final  static  String  LOCKPRE = "preS";
    final  static  String  LOCKMAIN = "mainS";
//    static  long i = 0;
    public SaveDevDataStatic(String topic, String table, String isDelInsert, String isTruncate, List<String> jsonString) {
        super();
        this.topic = topic;
        this.table = table;
        this.isDelInsert = isDelInsert;
        this.isTrancate = isTruncate;
        this.listJsonString = jsonString;
    }


    @Override
    public void run() {


        //System.out.println(records);
        List<String[]> reds = new ArrayList<>();
        List<String[]> listDynamic = new ArrayList<>();
        try {
//            int k = 0;
            for(String jsonString:listJsonString) {
                String[] array = JsonObjectToAttach.getJsonList(jsonString, "data",true);
                if (array != null) {
                    //表名固定了，根据实际情况修改
                    for (int m = 0; m < this.table.split(";").length; m++) {

//                        JSONObject jsonObject = null;
//                        try {
//                            String [] jsonArray = JsonObjectToAttach.getJsonList(jsonString, "data",false);
//                            jsonObject = JSONObject.parseObject(jsonArray[0]);
//                            if (jsonObject.get(objectType) != null &&  !jsonObject.get(objectType).equals(JsonObjectToAttach.staticTableRelation.get(this.table.split(";")[m])))
//                                continue;
//                        } catch (Exception exx) {
//
//                        }
                        //处理json key重复字段
                        String [] newArray = JsonObjectToAttach.processMutikeys(array,this.table.split(";")[m],"");



                        //删除当前表数据，保留历史表数据
                        String[] sql = JsonObjectToAttach.getBatchStatement(newArray, table.split(";")[m], "", "",
                                !(isDelInsert.indexOf(";") > 0 ? isDelInsert.split(";")[m] : isDelInsert).equalsIgnoreCase("false"), new HashMap(),
                                !(isTrancate.indexOf(";") > 0 ? isTrancate.split(";")[m] : isTrancate).equalsIgnoreCase("false"));

                        if (!reds.contains(sql) && sql != null)
                            reds.add(sql);

                    }
//                    k++;//只删除一次
                }
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }

        try {
            Seq<String[]> tmpSeq = JavaConverters.asScalaIteratorConverter(reds.iterator()).asScala().toSeq();
            if (tmpSeq.size() > 0) {
                synchronized (LOCKPRE) {
//                    logger.info( "start:"+tmpSeq.size() + new Date().toString());
                    SaveModelData.main(tmpSeq.toList());
//                    logger.info( "end  :"+i++ + new Date().toString());
                }
            }
        } catch (Exception e) {
            System.out.println(e.toString());
//            System.out.println(reds.get(0).toString());
//            System.out.println(listDynamic.get(0));
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


        String pullJsonString = "{\n" +
                "  \"data\": {\n" +
                "    \"topos\": [\n" +
                "      {\n" +
                "        \"clients\": [\n" +
                "          {\n" +
                "            \"status\": 0,\n" +
                "            \"conn_status\": 1,\n" +
                "            \"device_name\": \"woc224_WOC\",\n" +
                "            \"device_id\": 224\n" +
                "          },\n" +
                "          {\n" +
                "            \"status\": 1,\n" +
                "            \"conn_status\": 1,\n" +
                "            \"device_name\": \"woc225_WOC\",\n" +
                "            \"device_id\": 225\n" +
                "          },\n" +
                "          {\n" +
                "            \"status\": 2,\n" +
                "            \"conn_status\": 2,\n" +
                "            \"device_name\": \"woc226_WOC\",\n" +
                "            \"device_id\": 226\n" +
                "          }\n" +
                "        ],\n" +
                "        \"server\": {\n" +
                "          \"status\": 2,\n" +
                "          \"device_id\": 221,\n" +
                "          \"device_name\": \"woc1_WOC\"\n" +
                "        }\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}";


        String id = "";
        JSONObject data = null;
        try{
            data = JSONObject.parseObject(pullJsonString);
            JSONObject jsonObject =JSONObject.parseObject(data.get("data").toString());

            JSONArray jsonArray  = jsonObject.getJSONArray("topos");

            //server信息
            JSONObject []server = new JSONObject[1];
            JSONArray[] clients = new JSONArray[1];
            jsonArray.forEach(c->{
                JSONObject jso =  (JSONObject)c;
                if(jso.containsKey("server"))
                    server[0] =(JSONObject) jso.get("server");
                if(jso.containsKey("clients"))
                    clients[0] = (JSONArray)jso.get("clients");
             });

            JSONArray jsonArrayTarget = new JSONArray();
            for(Object o:clients[0]){
                JSONObject obj = JSONObject.parseObject(o.toString());
//                obj.put("serverId",server[0].get("device_id"));
                jsonArrayTarget.add(obj);
            }
            //重新打包json
            jsonObject = new JSONObject();
            for(Map.Entry<String,Object> map:server[0].entrySet()){
                jsonObject.put(map.getKey(),map.getValue());
            }
            jsonObject.put("clients",jsonArrayTarget);
            data.put("data",jsonObject);
        }catch (Exception ex){
            ex.printStackTrace();
        }

        List<String> listJson = new ArrayList<>();
        listJson.add(data.toJSONString());

        //取得静态数据表
        Map<String, String> topicM = JsonObjectToAttach.getValidProperties("BBCJob", null, "",true);
        ExecutorService executorService = Executors.newFixedThreadPool(NUM_PROCESS);
        for (Map.Entry<String, String> m : topicM.entrySet()) {
            String[] tabAndMark = null;
            if (m.getValue().indexOf(",") >= 0) {
                tabAndMark = m.getValue().split(",");
            }

            SaveDevDataStatic saveDataStatic = new SaveDevDataStatic(m.getKey(), tabAndMark == null ? m.getValue() : tabAndMark[0],
                    tabAndMark == null ? "false" : tabAndMark[1], tabAndMark == null ? "false" : tabAndMark[2],listJson);
            executorService.execute(saveDataStatic);
        }
        executorService.shutdown();
    }

}
