package com.springboot.common;

import com.alibaba.fastjson.JSONObject;
import com.springboot.scala.SaveCosumerData;
import org.apache.commons.lang3.StringUtils;
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
    private String objectType = "objectType";
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
                String[] array = JsonObjectToAttach.getJsonList(jsonString, "data",true);
                if (array != null) {
                    //表名固定了，根据实际情况修改
                    for (int m = 0; m < this.table.split(";").length; m++) {

                        JSONObject jsonObject = null;
                        try {
                            String [] jsonArray = JsonObjectToAttach.getJsonList(jsonString, "data",false);
                            jsonObject = JSONObject.parseObject(jsonArray[0]);
                            if (jsonObject.get(objectType) != null &&  !jsonObject.get(objectType).equals(JsonObjectToAttach.staticTableRelation.get(this.table.split(";")[m])))
                                continue;
                        } catch (Exception exx) {

                        }
                        //处理json key重复字段
                        String [] newArray = JsonObjectToAttach.processMutikeys(array,this.table.split(";")[m],"");



                        //删除当前表数据，保留历史表数据
                        String[] sql = JsonObjectToAttach.getBatchStatement(newArray, table.split(";")[m], "", "",
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

        String tokenJsonString = "{\n" +
                "  \"code\": \"1\",\n" +
                "  \"message\": \"success\",\n" +
                "  \"data\": {\n" +
                "    \"interrupt\": false,\n" +
                "    \"timestamp\": 1608536849696,\n" +
                "    \"tokenId\": \"6197eceb-3648-4023-9692-6182ff1b38cf\",\n" +
                "    \"systemId\": \"20201221124515046-C4AC-CD36D1AD2\",\n" +
                "    \"systemCode\": \"testdemo\",\n" +
                "    \"systemName\": \"测试demo\",\n" +
                "    \"schemas\": [\n" +
                "      {\n" +
                "        \"objectType\": \"TARGET_ORGANIZATION\",\n" +
                "        \"objectId\": \"20201221124517089-815D-D3C4C7AA6\",\n" +
                "        \"objectCode\": \"testdemo_Org\",\n" +
                "        \"objectName\": \"测试demo机构\",\n" +
                "        \"objectAttributes\": [\n" +
                "          {\n" +
                "            \"type\": \"STRING\",\n" +
                "            \"provisionMethod\": \"AUTO\",\n" +
                "            \"reconcileMethod\": \"AUTO\",\n" +
                "            \"code\": \"_parent\",\n" +
                "            \"name\": \"父机构\",\n" +
                "            \"isRequired\": false,\n" +
                "            \"isUniqued\": false,\n" +
                "            \"isMultiValue\": false,\n" +
                "            \"isEncrypted\": false\n" +
                "          },\n" +
                "          {\n" +
                "            \"type\": \"STRING\",\n" +
                "            \"provisionMethod\": \"AUTO\",\n" +
                "            \"reconcileMethod\": \"AUTO\",\n" +
                "            \"code\": \"_organization\",\n" +
                "            \"name\": \"机构\",\n" +
                "            \"isRequired\": false,\n" +
                "            \"isUniqued\": false,\n" +
                "            \"isMultiValue\": false,\n" +
                "            \"isEncrypted\": false\n" +
                "          },\n" +
                "          {\n" +
                "            \"type\": \"STRING\",\n" +
                "            \"provisionMethod\": \"AUTO\",\n" +
                "            \"reconcileMethod\": \"AUTO\",\n" +
                "            \"code\": \"code\",\n" +
                "            \"name\": \"代码\",\n" +
                "            \"length\": 128,\n" +
                "            \"isRequired\": false,\n" +
                "            \"isUniqued\": false,\n" +
                "            \"isMultiValue\": false,\n" +
                "            \"isEncrypted\": false\n" +
                "          },\n" +
                "          {\n" +
                "            \"type\": \"STRING\",\n" +
                "            \"provisionMethod\": \"AUTO\",\n" +
                "            \"reconcileMethod\": \"AUTO\",\n" +
                "            \"code\": \"name\",\n" +
                "            \"name\": \"名称\",\n" +
                "            \"length\": 128,\n" +
                "            \"isRequired\": true,\n" +
                "            \"isUniqued\": false,\n" +
                "            \"isMultiValue\": false,\n" +
                "            \"isEncrypted\": false\n" +
                "          },\n" +
                "          {\n" +
                "            \"type\": \"STRING\",\n" +
                "            \"provisionMethod\": \"AUTO\",\n" +
                "            \"reconcileMethod\": \"AUTO\",\n" +
                "            \"code\": \"fullname\",\n" +
                "            \"name\": \"机构全名\",\n" +
                "            \"length\": 128,\n" +
                "            \"isRequired\": false,\n" +
                "            \"isUniqued\": false,\n" +
                "            \"isMultiValue\": false,\n" +
                "            \"isEncrypted\": false\n" +
                "          },\n" +
                "          {\n" +
                "            \"type\": \"STRING\",\n" +
                "            \"provisionMethod\": \"AUTO\",\n" +
                "            \"reconcileMethod\": \"AUTO\",\n" +
                "            \"code\": \"description\",\n" +
                "            \"name\": \"描述\",\n" +
                "            \"length\": 512,\n" +
                "            \"isRequired\": false,\n" +
                "            \"isUniqued\": false,\n" +
                "            \"isMultiValue\": false,\n" +
                "            \"isEncrypted\": false\n" +
                "          },\n" +
                "          {\n" +
                "            \"type\": \"INTEGER\",\n" +
                "            \"provisionMethod\": \"AUTO\",\n" +
                "            \"reconcileMethod\": \"AUTO\",\n" +
                "            \"code\": \"sequence\",\n" +
                "            \"name\": \"序号\",\n" +
                "            \"length\": 10,\n" +
                "            \"isRequired\": false,\n" +
                "            \"isUniqued\": false,\n" +
                "            \"isMultiValue\": false,\n" +
                "            \"isEncrypted\": false\n" +
                "          },\n" +
                "          {\n" +
                "            \"type\": \"BOOLEAN\",\n" +
                "            \"provisionMethod\": \"AUTO\",\n" +
                "            \"reconcileMethod\": \"AUTO\",\n" +
                "            \"code\": \"isDisabled\",\n" +
                "            \"name\": \"禁用\",\n" +
                "            \"length\": 1,\n" +
                "            \"isRequired\": false,\n" +
                "            \"isUniqued\": false,\n" +
                "            \"isMultiValue\": false,\n" +
                "            \"isEncrypted\": false\n" +
                "          },\n" +
                "          {\n" +
                "            \"type\": \"TIMESTAMP\",\n" +
                "            \"provisionMethod\": \"AUTO\",\n" +
                "            \"reconcileMethod\": \"AUTO\",\n" +
                "            \"code\": \"createAt\",\n" +
                "            \"name\": \"创建日期\",\n" +
                "            \"isRequired\": false,\n" +
                "            \"isUniqued\": false,\n" +
                "            \"isMultiValue\": false,\n" +
                "            \"isEncrypted\": false\n" +
                "          },\n" +
                "          {\n" +
                "            \"type\": \"TIMESTAMP\",\n" +
                "            \"provisionMethod\": \"AUTO\",\n" +
                "            \"reconcileMethod\": \"AUTO\",\n" +
                "            \"code\": \"updateAt\",\n" +
                "            \"name\": \"更新日期\",\n" +
                "            \"isRequired\": false,\n" +
                "            \"isUniqued\": false,\n" +
                "            \"isMultiValue\": false,\n" +
                "            \"isEncrypted\": false\n" +
                "          },\n" +
                "          {\n" +
                "            \"type\": \"STRING\",\n" +
                "            \"provisionMethod\": \"AUTO\",\n" +
                "            \"reconcileMethod\": \"AUTO\",\n" +
                "            \"code\": \"email\",\n" +
                "            \"name\": \"电子邮箱\",\n" +
                "            \"length\": 128,\n" +
                "            \"isRequired\": false,\n" +
                "            \"isUniqued\": false,\n" +
                "            \"isMultiValue\": false,\n" +
                "            \"isEncrypted\": false\n" +
                "          },\n" +
                "          {\n" +
                "            \"type\": \"STRING\",\n" +
                "            \"provisionMethod\": \"AUTO\",\n" +
                "            \"reconcileMethod\": \"AUTO\",\n" +
                "            \"code\": \"linkAddress\",\n" +
                "            \"name\": \"联系地址\",\n" +
                "            \"length\": 256,\n" +
                "            \"isRequired\": false,\n" +
                "            \"isUniqued\": false,\n" +
                "            \"isMultiValue\": false,\n" +
                "            \"isEncrypted\": false\n" +
                "          },\n" +
                "          {\n" +
                "            \"type\": \"STRING\",\n" +
                "            \"provisionMethod\": \"AUTO\",\n" +
                "            \"reconcileMethod\": \"AUTO\",\n" +
                "            \"code\": \"type\",\n" +
                "            \"name\": \"类型\",\n" +
                "            \"length\": 32,\n" +
                "            \"isRequired\": false,\n" +
                "            \"isUniqued\": false,\n" +
                "            \"isMultiValue\": false,\n" +
                "            \"isEncrypted\": false,\n" +
                "            \"lookupDefinitionCode\": \"system.organization.type\"\n" +
                "          }\n" +
                "        ]\n" +
                "      },\n" +
                "      {\n" +
                "        \"objectType\": \"TARGET_ACCOUNT\",\n" +
                "        \"objectId\": \"20201221124519685-9FB3-6D79C00A8\",\n" +
                "        \"objectCode\": \"testdemo_TargetAccount\",\n" +
                "        \"objectName\": \"测试demo目标账号\",\n" +
                "        \"objectAttributes\": [\n" +
                "          {\n" +
                "            \"type\": \"STRING\",\n" +
                "            \"provisionMethod\": \"AUTO\",\n" +
                "            \"reconcileMethod\": \"AUTO\",\n" +
                "            \"code\": \"_user\",\n" +
                "            \"name\": \"用户\",\n" +
                "            \"isRequired\": false,\n" +
                "            \"isUniqued\": false,\n" +
                "            \"isMultiValue\": false,\n" +
                "            \"isEncrypted\": false\n" +
                "          },\n" +
                "          {\n" +
                "            \"type\": \"STRING\",\n" +
                "            \"provisionMethod\": \"AUTO\",\n" +
                "            \"reconcileMethod\": \"AUTO\",\n" +
                "            \"code\": \"_organization\",\n" +
                "            \"name\": \"所属机构\",\n" +
                "            \"isRequired\": false,\n" +
                "            \"isUniqued\": false,\n" +
                "            \"isMultiValue\": false,\n" +
                "            \"isEncrypted\": false\n" +
                "          },\n" +
                "          {\n" +
                "            \"type\": \"STRING\",\n" +
                "            \"provisionMethod\": \"AUTO\",\n" +
                "            \"reconcileMethod\": \"AUTO\",\n" +
                "            \"code\": \"username\",\n" +
                "            \"name\": \"账号名\",\n" +
                "            \"length\": 64,\n" +
                "            \"isRequired\": true,\n" +
                "            \"isUniqued\": false,\n" +
                "            \"isMultiValue\": false,\n" +
                "            \"isEncrypted\": false\n" +
                "          },\n" +
                "          {\n" +
                "            \"type\": \"STRING\",\n" +
                "            \"provisionMethod\": \"AUTO\",\n" +
                "            \"reconcileMethod\": \"AUTO\",\n" +
                "            \"code\": \"password\",\n" +
                "            \"name\": \"密码\",\n" +
                "            \"length\": 64,\n" +
                "            \"isRequired\": false,\n" +
                "            \"isUniqued\": false,\n" +
                "            \"isMultiValue\": false,\n" +
                "            \"isEncrypted\": true\n" +
                "          },\n" +
                "          {\n" +
                "            \"type\": \"STRING\",\n" +
                "            \"provisionMethod\": \"AUTO\",\n" +
                "            \"reconcileMethod\": \"AUTO\",\n" +
                "            \"code\": \"fullname\",\n" +
                "            \"name\": \"姓名\",\n" +
                "            \"length\": 64,\n" +
                "            \"isRequired\": true,\n" +
                "            \"isUniqued\": false,\n" +
                "            \"isMultiValue\": false,\n" +
                "            \"isEncrypted\": false\n" +
                "          },\n" +
                "          {\n" +
                "            \"type\": \"BOOLEAN\",\n" +
                "            \"provisionMethod\": \"AUTO\",\n" +
                "            \"reconcileMethod\": \"AUTO\",\n" +
                "            \"code\": \"isDisabled\",\n" +
                "            \"name\": \"禁用\",\n" +
                "            \"length\": 1,\n" +
                "            \"isRequired\": false,\n" +
                "            \"isUniqued\": false,\n" +
                "            \"isMultiValue\": false,\n" +
                "            \"isEncrypted\": false\n" +
                "          },\n" +
                "          {\n" +
                "            \"type\": \"BOOLEAN\",\n" +
                "            \"provisionMethod\": \"AUTO\",\n" +
                "            \"reconcileMethod\": \"AUTO\",\n" +
                "            \"code\": \"isLocked\",\n" +
                "            \"name\": \"锁定\",\n" +
                "            \"length\": 1,\n" +
                "            \"isRequired\": false,\n" +
                "            \"isUniqued\": false,\n" +
                "            \"isMultiValue\": false,\n" +
                "            \"isEncrypted\": false\n" +
                "          },\n" +
                "          {\n" +
                "            \"type\": \"TIMESTAMP\",\n" +
                "            \"provisionMethod\": \"AUTO\",\n" +
                "            \"reconcileMethod\": \"AUTO\",\n" +
                "            \"code\": \"createAt\",\n" +
                "            \"name\": \"创建日期\",\n" +
                "            \"isRequired\": false,\n" +
                "            \"isUniqued\": false,\n" +
                "            \"isMultiValue\": false,\n" +
                "            \"isEncrypted\": false\n" +
                "          },\n" +
                "          {\n" +
                "            \"type\": \"TIMESTAMP\",\n" +
                "            \"provisionMethod\": \"AUTO\",\n" +
                "            \"reconcileMethod\": \"AUTO\",\n" +
                "            \"code\": \"updateAt\",\n" +
                "            \"name\": \"更新日期\",\n" +
                "            \"isRequired\": false,\n" +
                "            \"isUniqued\": false,\n" +
                "            \"isMultiValue\": false,\n" +
                "            \"isEncrypted\": false\n" +
                "          },\n" +
                "          {\n" +
                "            \"type\": \"BOOLEAN\",\n" +
                "            \"provisionMethod\": \"AUTO\",\n" +
                "            \"reconcileMethod\": \"AUTO\",\n" +
                "            \"code\": \"isSystem\",\n" +
                "            \"name\": \"系统账号\",\n" +
                "            \"length\": 1,\n" +
                "            \"isRequired\": false,\n" +
                "            \"isUniqued\": false,\n" +
                "            \"isMultiValue\": false,\n" +
                "            \"isEncrypted\": false\n" +
                "          },\n" +
                "          {\n" +
                "            \"type\": \"BOOLEAN\",\n" +
                "            \"provisionMethod\": \"AUTO\",\n" +
                "            \"reconcileMethod\": \"AUTO\",\n" +
                "            \"code\": \"isPublic\",\n" +
                "            \"name\": \"公共账号\",\n" +
                "            \"length\": 1,\n" +
                "            \"isRequired\": false,\n" +
                "            \"isUniqued\": false,\n" +
                "            \"isMultiValue\": false,\n" +
                "            \"isEncrypted\": false\n" +
                "          },\n" +
                "          {\n" +
                "            \"type\": \"BOOLEAN\",\n" +
                "            \"provisionMethod\": \"AUTO\",\n" +
                "            \"reconcileMethod\": \"AUTO\",\n" +
                "            \"code\": \"isMaster\",\n" +
                "            \"name\": \"主账号\",\n" +
                "            \"length\": 1,\n" +
                "            \"isRequired\": false,\n" +
                "            \"isUniqued\": false,\n" +
                "            \"isMultiValue\": false,\n" +
                "            \"isEncrypted\": false\n" +
                "          },\n" +
                "          {\n" +
                "            \"type\": \"STRING\",\n" +
                "            \"provisionMethod\": \"AUTO\",\n" +
                "            \"reconcileMethod\": \"AUTO\",\n" +
                "            \"code\": \"email\",\n" +
                "            \"name\": \"邮箱\",\n" +
                "            \"length\": 128,\n" +
                "            \"isRequired\": false,\n" +
                "            \"isUniqued\": false,\n" +
                "            \"isMultiValue\": false,\n" +
                "            \"isEncrypted\": false\n" +
                "          },\n" +
                "          {\n" +
                "            \"type\": \"STRING\",\n" +
                "            \"provisionMethod\": \"AUTO\",\n" +
                "            \"reconcileMethod\": \"AUTO\",\n" +
                "            \"code\": \"employeeNo\",\n" +
                "            \"name\": \"员工号\",\n" +
                "            \"length\": 32,\n" +
                "            \"isRequired\": false,\n" +
                "            \"isUniqued\": false,\n" +
                "            \"isMultiValue\": false,\n" +
                "            \"isEncrypted\": false\n" +
                "          },\n" +
                "          {\n" +
                "            \"type\": \"STRING\",\n" +
                "            \"provisionMethod\": \"AUTO\",\n" +
                "            \"reconcileMethod\": \"AUTO\",\n" +
                "            \"code\": \"mobile\",\n" +
                "            \"name\": \"手机号\",\n" +
                "            \"length\": 32,\n" +
                "            \"isRequired\": false,\n" +
                "            \"isUniqued\": false,\n" +
                "            \"isMultiValue\": false,\n" +
                "            \"isEncrypted\": false\n" +
                "          },\n" +
                "          {\n" +
                "            \"type\": \"STRING\",\n" +
                "            \"provisionMethod\": \"AUTO\",\n" +
                "            \"reconcileMethod\": \"AUTO\",\n" +
                "            \"code\": \"sex\",\n" +
                "            \"name\": \"性别\",\n" +
                "            \"length\": 32,\n" +
                "            \"isRequired\": false,\n" +
                "            \"isUniqued\": false,\n" +
                "            \"isMultiValue\": false,\n" +
                "            \"isEncrypted\": false\n" +
                "          }\n" +
                "        ]\n" +
                "      }\n" +
                "    ],\n" +
                "    \"enableSync\": true,\n" +
                "    \"enablePull\": true,\n" +
                "    \"enablePush\": true,\n" +
                "    \"objectCodes4Push\": [\n" +
                "      \"testdemo_Org\",\n" +
                "      \"testdemo_TargetAccount\"\n" +
                "    ],\n" +
                "    \"debug\": false\n" +
                "  }\n" +
                "}";

        String pullJsonString = "{\n" +
                "  \"code\": \"1\",\n" +
                "  \"message\": \"success\",\n" +
                "  \"data\": {\n" +
                "    \"interrupt\": false,\n" +
                "    \"timestamp\": 1608537202840,\n" +
                "    \"taskId\": \"20201221154223919-14E7-734636690\",\n" +
                "    \"objectType\": \"TARGET_ACCOUNT\",\n" +
                "    \"objectCode\": \"testdemo_TargetAccount\",\n" +
                "    \"effectOn\": \"CREATED\",\n" +
                "    \"data\": {\n" +
                "      \"_user\": \"zhangsan\",\n" +
                "      \"_organization\": null,\n" +
                "      \"username\": \"zhangsan\",\n" +
                "      \"password\": null,\n" +
                "      \"fullname\": \"张三测试\",\n" +
                "      \"isDisabled\": false,\n" +
                "      \"isLocked\": false,\n" +
                "      \"createAt\": \"2020-12-21 15:42:23.000\",\n" +
                "      \"updateAt\": \"2020-12-21 15:42:23.000\",\n" +
                "      \"isSystem\": false,\n" +
                "      \"isPublic\": false,\n" +
                "      \"isMaster\": true,\n" +
                "      \"email\": \"zhangsan@crecg.com\",\n" +
                "      \"employeeNo\": null,\n" +
                "      \"mobile\": \"13247703738\",\n" +
                "      \"sex\": \"1\"\n" +
                "    },\n" +
                "    \"id\": \"20201221154223828-3236-DD9FB740B\"\n" +
                "  }\n" +
                "}\n";


        String id = "";
        JSONObject data = null;
        try{
            data = JSONObject.parseObject(pullJsonString);
            JSONObject jsonObject =JSONObject.parseObject(data.get("data").toString());

            id = jsonObject.get("id").toString();
            JSONObject target = JSONObject.parseObject(jsonObject.get("data").toString());
            target.put("id",id);
            jsonObject.put("data",target);
            data.put("data",jsonObject);
        }catch (Exception ex){
            ex.printStackTrace();
        }

        List<String> listJson = new ArrayList<>();
        listJson.add(data.toJSONString());

        //取得静态数据表
        Map<String, String> topicM = JsonObjectToAttach.getValidProperties("UserPullJob", null, "",true);
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
