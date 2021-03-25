package com.springboot.httpInterface.job;

import cn.crec.pojo.OutParam;
import cn.crec.pull.PullUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.springboot.common.JsonObjectToAttach;
import com.springboot.common.SaveDataStatic;
import com.springboot.common.SaveDevDataStatic;
import com.springboot.httpInterface.controller.HttpServiceTest;
import org.apache.commons.lang3.StringUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.crypto.Data;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Created by yzn00 on 2021/3/9.
 */
public class BBCJob implements BaseJob {

    private static Logger _log = LoggerFactory.getLogger(BBCJob.class);

    //取得静态表
    static Map<String, String> topicS = new HashMap<>();

    final String topicName = BBCJob.class.getSimpleName();

    HttpServiceTest httpServiceTest = null;

    public BBCJob() {
        if (topicS.size() == 0)
            //取得静态表
            topicS = JsonObjectToAttach.getValidProperties(topicName, null, null, true);

    }


    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

        //获取token

        //注销token

        //测试数据
        String dataStr = "{\n" +
                "  \"data\": {\n" +
                "    \"topos\": [\n" +
                "      {\n" +
                "        \"clients\": [\n" +
                "          {\n" +
                "            \"status\": 1,\n" +
                "            \"conn_status\": 1,\n" +
                "            \"device_name\": \"woc3_WOC\",\n" +
                "            \"device_id\": 224\n" +
                "          },\n" +
                "          {\n" +
                "            \"status\": 2,\n" +
                "            \"conn_status\": 2,\n" +
                "            \"device_name\": \"woc3_WOC\",\n" +
                "            \"device_id\": 225\n" +
                "          },\n" +
                "          {\n" +
                "            \"status\": 3,\n" +
                "            \"conn_status\": 3,\n" +
                "            \"device_name\": \"woc3_WOC\",\n" +
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
        JSONObject data = JSONObject.parseObject(dataStr);
        try {
            processPullInfo(data.toJSONString());
        } catch (Exception e) {
            System.out.print(e.toString());
        }

    }

    /**
     * 处理下拉接入json
     * @param jsonStr
     * @throws Exception
     */
    private void processPullInfo(String jsonStr) throws Exception {
        ExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        try {
            for (Map.Entry<String, String> m : topicS.entrySet()) {
                List<String> listJson = new ArrayList<>();
                String[] tabAndMark = null;
                if (m.getValue().indexOf(",") >= 0) {
                    tabAndMark = m.getValue().split(",");
                }
                if (tabAndMark.length < 4) {
                    continue;
                }
                String url = tabAndMark[3];


                String pullJsonString = "{\n" +
                        "  \"data\": {\n" +
                        "    \"topos\": [\n" +
                        "      {\n" +
                        "        \"clients\": [\n" +
                        "          {\n" +
                        "            \"status\": 0,\n" +
                        "            \"conn_status\": 1,\n" +
                        "            \"device_name\": \"woc3_WOC\",\n" +
                        "            \"device_id\": 224\n" +
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
                if (StringUtils.isEmpty(jsonStr)) {
                    jsonStr = pullJsonString;
                    String id = "";
                    JSONObject data = null;
                    try {
                        data = JSONObject.parseObject(jsonStr);
                        JSONObject jsonObject = JSONObject.parseObject(data.get("data").toString());
                        //替换bool值
                        JsonObjectToAttach.replaceBooleanString(jsonObject);

                        data.put("data", jsonObject);

                        jsonStr = data.toJSONString();
                        listJson.add(jsonStr);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else {
                    JSONObject target = JSONObject.parseObject(jsonStr);
                    if(null!=tabAndMark)
                        //处理特殊数据（server：clients)
                        if(tabAndMark[0].toLowerCase().indexOf("dev_server_info")>-1){
                            JSONObject data = JSONObject.parseObject(pullJsonString);
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
                                jsonArrayTarget.add(obj);
                            }
                            //重新打包json
                            jsonObject = new JSONObject();
                            for(Map.Entry<String,Object> map:server[0].entrySet()){
                                jsonObject.put(map.getKey(),map.getValue());
                            }
                            jsonObject.put("clients",jsonArrayTarget);
                            data.put("data",jsonObject);
                            target = data;

                            //替换bool值
                            JsonObjectToAttach.replaceBooleanString(target);
                            jsonStr = target.toJSONString();
                            listJson.add(jsonStr);

                        }else if(tabAndMark[0].toLowerCase().indexOf("bbc_org_info")>-1){
                            String strJson = "{\n" +
                                    "  \"data\": {\n" +
                                    "    \"org_name\": \"全部\",\n" +
                                    "    \"org_id\": 0,\n" +
                                    "    \"children\": [\n" +
                                    "      {\n" +
                                    "        \"org_name\": \"厦门\",\n" +
                                    "        \"org_id\": 8,\n" +
                                    "        \"children\": [\n" +
                                    "          {\n" +
                                    "            \"org_name\": \"泉州区\",\n" +
                                    "            \"org_id\": 81,\n" +
                                    "            \"children\": [\n" +
                                    "              {\n" +
                                    "                \"org_name\": \"泉州城区\",\n" +
                                    "                \"org_id\": 811,\n" +
                                    "                \"children\": [\n" +
                                    "                  {\n" +
                                    "                    \"org_name\": \"城关街道\",\n" +
                                    "                    \"org_id\": 8111,\n" +
                                    "                    \"children\": [\n" +
                                    "                      {\n" +
                                    "                        \"org_name\": \"公司81111\",\n" +
                                    "                        \"org_id\": 81111,\n" +
                                    "                        \"children\": [\n" +
                                    "                          {\n" +
                                    "                            \"org_name\": \"分公司811111\",\n" +
                                    "                            \"org_id\": 811111\n" +
                                    "                          }\n" +
                                    "                        ]\n" +
                                    "                      }\n" +
                                    "                    ]\n" +
                                    "                  },\n" +
                                    "                  {\n" +
                                    "                    \"org_name\": \"远郊\",\n" +
                                    "                    \"org_id\": 8112,\n" +
                                    "                    \"children\": [\n" +
                                    "                      {\n" +
                                    "                        \"org_name\": \"公司81121\",\n" +
                                    "                        \"org_id\": 81121,\n" +
                                    "                        \"children\": [\n" +
                                    "                          {\n" +
                                    "                            \"org_name\": \"分公司811211\",\n" +
                                    "                            \"org_id\": 811211\n" +
                                    "                          }\n" +
                                    "                        ]\n" +
                                    "                      }\n" +
                                    "                    ]\n" +
                                    "                  }\n" +
                                    "                ]\n" +
                                    "              }\n" +
                                    "            ]\n" +
                                    "          }\n" +
                                    "        ]\n" +
                                    "      },\n" +
                                    "      {\n" +
                                    "        \"org_name\": \"深圳\",\n" +
                                    "        \"org_id\": 1,\n" +
                                    "        \"children\": [\n" +
                                    "          {\n" +
                                    "            \"org_name\": \"南山区\",\n" +
                                    "            \"org_id\": 2,\n" +
                                    "            \"children\": [\n" +
                                    "              {\n" +
                                    "                \"org_name\": \"公司21\",\n" +
                                    "                \"org_id\": 21\n" +
                                    "              },\n" +
                                    "              {\n" +
                                    "                \"org_name\": \"公司22\",\n" +
                                    "                \"org_id\": 22\n" +
                                    "              },\n" +
                                    "              {\n" +
                                    "                \"org_name\": \"公司23\",\n" +
                                    "                \"org_id\": 23\n" +
                                    "              }\n" +
                                    "            ]\n" +
                                    "          },\n" +
                                    "          {\n" +
                                    "            \"org_name\": \"北山区\",\n" +
                                    "            \"org_id\": 3,\n" +
                                    "            \"children\": [\n" +
                                    "              {\n" +
                                    "                \"org_name\": \"公司31\",\n" +
                                    "                \"org_id\": 31\n" +
                                    "              },\n" +
                                    "              {\n" +
                                    "                \"org_name\": \"公司32\",\n" +
                                    "                \"org_id\": 32,\n" +
                                    "                \"children\": [\n" +
                                    "                  {\n" +
                                    "                    \"org_name\": \"公司321\",\n" +
                                    "                    \"org_id\": 321\n" +
                                    "                  }\n" +
                                    "                ]\n" +
                                    "              }\n" +
                                    "            ]\n" +
                                    "          }\n" +
                                    "        ]\n" +
                                    "      }\n" +
                                    "    ]\n" +
                                    "  },\n" +
                                    "  \"success\": 1\n" +
                                    "}";
                            Object obj=null;
                            JSONArray jsonObjectRtn = JsonObjectToAttach.getWhileLoopChildrens(JSONObject.parseObject
                                            (JSONObject.parseObject(strJson).get("data").toString()),
                                    "children","org_id",obj);
                            target = new JSONObject();
                            target.put("data",jsonObjectRtn);
                            //替换bool值
                            JsonObjectToAttach.replaceBooleanString(target);
                            JSONArray jsonArray = JSONArray.parseArray(target.get("data").toString());

                            for(Object o:jsonArray){
                                listJson.add(o.toString());
                            }
                        }

                }



                SaveDevDataStatic saveDataStatic = new SaveDevDataStatic(m.getKey(), tabAndMark == null ? m.getValue() : tabAndMark[0],
                        tabAndMark == null ? "false" : tabAndMark[1], tabAndMark == null ? "false" : tabAndMark[2],
                        listJson);
                executorService.execute(saveDataStatic);


            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
