package com.springboot.httpInterface.job;

import com.alibaba.druid.sql.ast.statement.SQLIfStatement;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.springboot.common.JsonObjectToAttach;
import com.springboot.common.SaveDevDataStatic;
import com.springboot.common.SecurityUtils;
import com.springboot.httpInterface.controller.HttpServiceTest;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.HttpGet;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by yzn00 on 2021/3/9.
 */
public class BBCJob implements BaseJob {

    private static Logger _log = LoggerFactory.getLogger(BBCJob.class);

    //取得静态表
    static Map<String, String> topicS = new HashMap<>();

    final String topicName = BBCJob.class.getSimpleName();

    @Autowired
    HttpServiceTest httpServiceTest;

    public BBCJob() {
        if (topicS.size() == 0)
            //取得静态表
            topicS = JsonObjectToAttach.getValidProperties(topicName, null, null, true);

    }


    static String tokenId = "";

    /**
     * 获取访问公钥
     *
     * @return
     */
    private String getPublicKey() {
        String publicKey = "";
        try {
            String result = httpServiceTest.httpGet(JsonObjectToAttach.config.get("hostIp").toString() +
                    JsonObjectToAttach.config.get("publicKeyUrl").toString(), "");
            JSONObject jsonObject = JSONObject.parseObject(result);
            if (jsonObject != null && jsonObject.get("success").equals(1))
                publicKey = jsonObject.get("data").toString();
        } catch (Exception e) {
            _log.error("获取公钥异常:{}", publicKey, e);
            throw e;
        } finally {
            return publicKey;
        }
    }


    /**
     * 获取token
     *
     * @return
     */
    private String getAccessedToken() throws Exception {
        String accessTokenId = "";
        try {
            accessTokenId = httpServiceTest.httpGet(JsonObjectToAttach.config.get("hostIp").toString() +
                            JsonObjectToAttach.config.get("accessTokenUrl").toString(),
                    "username=" + JsonObjectToAttach.config.get("userName").toString() + "&password=" +
                            SecurityUtils.encryptBase16(JsonObjectToAttach.config.get("password").toString(), getPublicKey()));
            JSONObject token = JSONObject.parseObject(accessTokenId);
            if (null != token && token.get("success").equals(1))
                accessTokenId = token.get("data").toString();
            else
                accessTokenId = "";
        } catch (Exception e) {
            _log.error("获取token出现异常：{}", accessTokenId, e);
           throw  e;
        }
        return accessTokenId;
    }


    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

        //获取token
        if (StringUtils.isEmpty(tokenId))
            try {
                tokenId = getAccessedToken();
            } catch (Exception e) {
                e.printStackTrace();
            }
        else{
            try {
                if(tokenId.indexOf("-")<0){
                    JSONObject jsonObject = JSONObject.parseObject(tokenId);
                    if(null!=jsonObject.get("success") && !jsonObject.get("success").toString().equals(1))
                        tokenId = getAccessedToken();
                }
            }catch (Exception ex){
                _log.info("accesstoken异常：{}",tokenId,ex);
            }
        }

        try {
            if(JsonObjectToAttach.config.get("isTest").toString().toLowerCase().equals("true"))
                processBBCInfo(true);
            else
                processBBCInfo(false);
        } catch (Exception e) {
            _log.error("获取BBC接口信息异常：{}", tokenId, e);
//            System.out.print(e.toString());
        }

    }


    public static void main(String[] args) throws Exception {
        Map keyM = new HashedMap();
        String pass = "Shanghai";
        String hexStr = SecurityUtils.encryptBase16(pass, "");
        System.out.println(hexStr);
        String encrypPss = SecurityUtils.decryptBase16(hexStr);
        System.out.println(encrypPss);
        System.out.println("\n");


    }


    /**
     * 处理接入json
     *
     * @param isTest
     * @throws Exception
     */
    private void processBBCInfo(boolean isTest) throws Exception {
//        ExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        String jsonStr = "";

        for (Map.Entry<String, String> m : topicS.entrySet()) {
            try {
                List<String> listJson = new ArrayList<>();
                String[] tabAndMark = null;
                if (m.getValue().indexOf(",") >= 0) {
                    tabAndMark = m.getValue().split(",");
                }
                if (tabAndMark.length < 4) {
                    continue;
                }
                String url = JsonObjectToAttach.config.get("hostIp").toString() + tabAndMark[3];


                if (null != tabAndMark)
                    //处理特殊数据（server：clients嵌套)-状态信息
                    if (tabAndMark[0].toLowerCase().indexOf("dev_server_info") > -1) {
                        jsonStr = "";
                        jsonStr = httpServiceTest.httpGet(url, "access_token=" + tokenId);
                        if (isTest && StringUtils.isEmpty(jsonStr))
                            jsonStr = "{\n" +
                                    "  \"data\": {\n" +
                                    "    \"topos\": [\n" +
                                    "      {\n" +
                                    "        \"clients\": [\n" +
                                    "          {\n" +
                                    "            \"status\": 0,\n" +
                                    "            \"conn_status\": 1,\n" +
                                    "            \"device_name\": \"woc223_WOC\",\n" +
                                    "            \"device_id\": 223,\n" +
                                    "             \"line_type_name\":1\n" +
                                    "          },\n" +
                                    "          {\n" +
                                    "            \"status\": 0,\n" +
                                    "            \"conn_status\": 1,\n" +
                                    "            \"device_name\": \"woc223_WOC\",\n" +
                                    "            \"device_id\": 223,\n" +
                                    "             \"line_type_name\":2\n" +
                                    "          },\n" +
                                    "          {\n" +
                                    "            \"status\": 0,\n" +
                                    "            \"conn_status\": 2,\n" +
                                    "            \"device_name\": \"woc224_WOC\",\n" +
                                    "            \"device_id\": 224,\n" +
                                    "             \"line_type_name\":2\n" +
                                    "          }\n" +
                                    "        ],\n" +
                                    "        \"server\": {\n" +
                                    "          \"vpn_status\": 2,\n" +
                                    "          \"acc_status\": 1,\n" +
                                    "          \"device_id\": 221,\n" +
                                    "          \"device_name\": \"woc1_WOC\"\n" +
                                    "        }\n" +
                                    "      }\n" +
                                    "    ]\n" +
                                    "  },\n" +
                                    "\"success\":1\n" +
                                    "}";
                        JSONObject target = null;
                        JSONObject data = JSONObject.parseObject(jsonStr);
                        JSONObject jsonObject = null;

                        if (data != null && data.get("success").equals(1)) {

                            jsonObject = JSONObject.parseObject(data.get("data").toString());

                            JSONArray jsonArray = jsonObject.getJSONArray("topos");

                            //server信息
                            JSONObject[] server = new JSONObject[1];
                            JSONArray[] clients = new JSONArray[1];
//                            jsonArray.forEach(c -> {
//                                JSONObject jso = (JSONObject) c;
//                                if (jso.containsKey("server"))
//                                    server[0] = (JSONObject) jso.get("server");
//                                if (jso.containsKey("clients"))
//                                    clients[0] = (JSONArray) jso.get("clients");
//                            });
                            int number = 0;
                            for( Object c :jsonArray){
                                JSONObject jso = (JSONObject) c;
                                if (jso.containsKey("server"))
                                    server[0] = (JSONObject) jso.get("server");
                                if (jso.containsKey("clients"))
                                    clients[0] = (JSONArray) jso.get("clients");
                                JSONArray jsonArrayTarget = new JSONArray();
                                for (Object o : clients[0]) {
                                    JSONObject obj = JSONObject.parseObject(o.toString());
                                    //替换bool值
                                    JsonObjectToAttach.replaceBooleanString(obj);
                                    jsonArrayTarget.add(obj);
                                    number++;
                                }
                                //重新打包json
                                jsonObject = new JSONObject();
                                for (Map.Entry<String, Object> map : server[0].entrySet()) {
                                    jsonObject.put(map.getKey(), map.getValue());
                                }
                                jsonObject.put("clients", jsonArrayTarget);
                                data.put("data", jsonObject);
                                target = data;

                                //替换bool值
                                JsonObjectToAttach.replaceBooleanString(target);
                                jsonStr = target.toJSONString();
                                listJson.add(jsonStr);
                            }
                            _log.info("----获取设备连接状态数据 服务器的数量-----"+jsonArray.size());
                            _log.info("----获取设备连接状态数据 客户端的数量-----"+number);
                        }

                    } else if (tabAndMark[0].toLowerCase().indexOf("bbc_org_info") > -1) {//机构信息
                        //获取机构信息
                        //strJson = httpServiceTest.s(url,"access_token="+tokenId);
                        Map headMap = new HashMap();
                        Map bodyMap = new HashMap();
                        bodyMap.put("access_token", tokenId);
                        jsonStr = "";
                        try {
                            //post无参数
//                            jsonStr = HttpServiceTest.sendPostDataByMap(url+"?"+"access_token="+tokenId, headMap, bodyMap, "utf-8");
                            jsonStr = HttpServiceTest.httpGet(url,"access_token="+tokenId);
                        } catch (Exception ex) {
                            if (isTest && StringUtils.isEmpty(jsonStr))
                                jsonStr = "{\n" +
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
                            _log.error("获取机构信息失败\n{}", jsonStr, ex);
                        }
                        JSONObject jsonObj = JSONObject.parseObject(jsonStr);
                        if (jsonObj != null && jsonObj.get("success").equals(1)) {
                            Object obj = null;
                            JSONArray jsonObjectRtn = JsonObjectToAttach.getWhileLoopChildrens(JSONObject.parseObject
                                            (jsonObj.get("data").toString()),
                                    "children", "org_id", obj);
                            JSONObject target = new JSONObject();
                            target.put("data", jsonObjectRtn);
                            //替换bool值
                            JsonObjectToAttach.replaceBooleanString(target);
                            JSONArray jsonArray = JSONArray.parseArray(target.get("data").toString());

                            for (Object o : jsonArray) {
                                JSONObject jsonObject = (JSONObject) o;
                                //替换bool值
                                JsonObjectToAttach.replaceBooleanString(jsonObject);
                                listJson.add(jsonObject.toString());
                            }

                        }
                        _log.info("-----获取机构信息数据共计------"+listJson.size()+"条");

                    } else if (tabAndMark[0].toLowerCase().indexOf("dev_list_info") > -1) {
                        //设备信息
                        jsonStr = "";
                        jsonStr = httpServiceTest.httpGet(url, "access_token=" + tokenId);
                        if (isTest && StringUtils.isEmpty(jsonStr))
                            jsonStr = "{\n" +
                                    "  \"data\": {\n" +
                                    "    \"total_cnt\": 1,\n" +
                                    "    \"device_list\": [\n" +
                                    "      {\n" +
                                    "        \"gateway_id\": \"C25C28C9\",\n" +
                                    "        \"branch_id\": 217,\n" +
                                    "        \"product_name\": \"WOC\",\n" +
                                    "        \"device_name\": \"CPE-1_WOC\",\n" +
                                    "        \"device_id\": 225,\n" +
                                    "        \"parent_id\": -1,\n" +
                                    "        \"status\": 1,\n" +
                                    "        \"recv\": \"87\",\n" +
                                    "        \"send\": \"20\",\n" +
                                    "        \"disk\": \"16\",\n" +
                                    "        \"cpu\": \"8\",\n" +
                                    "        \"mem\": \"62\",\n" +
                                    "        \"org_id\": 12,\n" +
                                    "        \"home\": \"中国广东省深圳市南山区\"\n" +
                                    "      }\n" +
                                    "    ]\n" +
                                    "  },\n" +
                                    "  \"success\": 1\n" +
                                    "}";
                        JSONObject data = JSONObject.parseObject(jsonStr);
                        if (data != null && data.get("success").equals(1)) {
                            JSONObject jsonData = JSONObject.parseObject(data.get("data").toString());
                            JSONArray deviceList = JSONArray.parseArray(jsonData.get("device_list").toString());
                            for (Object o : deviceList) {
                                JSONObject jsonObject = (JSONObject) o;
                                //替换bool值
                                JsonObjectToAttach.replaceBooleanString(jsonObject);
                                listJson.add(jsonObject.toString());
                            }
                        }
                        _log.info("-----获取设备信息数据共计------"+listJson.size()+"条");
                    }

                SaveDevDataStatic saveDataStatic = new SaveDevDataStatic(m.getKey(), tabAndMark == null ? m.getValue() : tabAndMark[0],
                        tabAndMark == null ? "false" : tabAndMark[1], tabAndMark == null ? "false" : tabAndMark[2],
                        listJson);
                saveDataStatic.run();
//                executorService.execute(saveDataStatic);

            } catch (Exception ex) {
//            ex.printStackTrace();
                _log.error("处理BBC接口异常:[table]{}：",m.getKey(), ex);
                tokenId = "";
                throw ex;
            }
        }
    }
}
