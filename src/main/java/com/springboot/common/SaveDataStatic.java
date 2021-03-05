package com.springboot.common;

import com.springboot.scala.SaveCosumerData;
import scala.collection.JavaConverters;
import scala.collection.Seq;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;


public class SaveDataStatic extends Thread {
    private String topic;//主题
    private static Map<String, String> config = new HashMap<String, String>();
    private String table = "web_data_profil";//表名
    private String isDelInsert = "false";
    private String isTrancate = "false";
    private List<String> listJsonString = new ArrayList<>();

final static Logger logger =
        Logger.getLogger(SaveDataStatic.class.getName());

    final  static  String  LOCKPRE = "preS";
    final  static  String  LOCKMAIN = "mainS";
//    static  long i = 0;
    public SaveDataStatic(String topic, String table, String isDelInsert, String isTruncate, List<String> jsonString) {
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
                String[] array = JsonObjectToAttach.getJsonList(jsonString, null);
                if (array != null) {
                    //表名固定了，根据实际情况修改
                    for (int m = 0; m < this.table.split(";").length; m++) {
                        //删除当前表数据，保留历史表数据
                        String[] sql = JsonObjectToAttach.getBatchStatement(array, table.split(";")[m], "", "",
                                !(isDelInsert.indexOf(";") > 0 ? isDelInsert.split(";")[m] : isDelInsert).equalsIgnoreCase("false"), new HashMap(),
                                !(isTrancate.indexOf(";") > 0 ? isTrancate.split(";")[m] : isTrancate).equalsIgnoreCase("false"));

                        if (!reds.contains(sql) && sql != null)
                            reds.add(sql);

                        String[] strDynamic = JsonObjectToAttach.getMetaSqls(table.split(";")[m], null, array);
                        if (!listDynamic.contains(strDynamic) && null != strDynamic)
                            listDynamic.add(strDynamic);
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
                    SaveCosumerData.main(tmpSeq.toList());
//                    logger.info( "end  :"+i++ + new Date().toString());
                }
            }
//            Thread.sleep(50);
            tmpSeq = JavaConverters.asScalaIteratorConverter(listDynamic.iterator()).asScala().toSeq();
            if (tmpSeq.size() > 0) {
                synchronized (LOCKMAIN) {
                    SaveCosumerData.main(tmpSeq.toList());
                }
            }
//            Thread.sleep(50);
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

        String jsonString = "{\n" +
                "  \"tx_code\": \"0102\",\n" +
                "  \"results\": [\n" +
                "    {\n" +
                "      \"activityName\": \"参加活动1\",\n" +
                "      \"cardNo\": \"渝B03789\",\n" +
                "      \"barCode\": \"1\",\n" +
                "      \"checkTime\": \"2018-06-01 10:11:00\",\n" +
                "      \"accessPoint\": \"北3\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        List<String> listJson = new ArrayList<>();
        listJson.add(jsonString);

        //取得静态数据表
        Map<String, String> topicM = JsonObjectToAttach.getValidProperties("StaticJob", null, null,true);
        ExecutorService executorService = Executors.newFixedThreadPool(NUM_PROCESS);
        for (Map.Entry<String, String> m : topicM.entrySet()) {
            String[] tabAndMark = null;
            if (m.getValue().indexOf(",") >= 0) {
                tabAndMark = m.getValue().split(",");
            }

            SaveDataStatic saveDataStatic = new SaveDataStatic(m.getKey(), tabAndMark == null ? m.getValue() : tabAndMark[0],
                    tabAndMark == null ? "false" : tabAndMark[1], tabAndMark == null ? "false" : tabAndMark[2],listJson);
            executorService.execute(saveDataStatic);
        }
        executorService.shutdown();
    }

}
