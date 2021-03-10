package com.springboot.httpInterface.job;

import cn.crec.pojo.OutParam;
import cn.crec.pull.PullUtil;
import com.alibaba.fastjson.JSONObject;
import com.springboot.common.JsonObjectToAttach;
import com.springboot.common.ReadPropertiesUtils;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.springboot.common.SaveDataStatic;
import com.springboot.httpInterface.controller.HttpServiceTest;
import org.apache.commons.lang3.StringUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by yzn00 on 2021/3/9.
 */
public class UserPullJob implements BaseJob {

    private static Logger _log = LoggerFactory.getLogger(UserPullJob.class);

    final static int NUM_PROCESS = 6;
//    static Map<String, String> config = new HashMap<String, String>();

    //取得静态表
    static Map<String, String> topicS = new HashMap<>();

    private String jsonStr = "";
    private String table = "GTGCDM.PUB_UNIFIED_IDENTITY_USER";
    //令牌地址
    static String accessUrl = "http://183.66.65.155:9002/api/Token?appid=001&secret=ABCDEFG";

    final String topicName = UserPullJob.class.getSimpleName();
//    final String configName = "project.properties";

    HttpServiceTest httpServiceTest = null;

    public UserPullJob() {
//        try {
//            if (config.size() == 0)
//                config.putAll(ReadPropertiesUtils.readConfig(configName));
            if (topicS.size() == 0)
                //取得静态表
                topicS = JsonObjectToAttach.getValidProperties(topicName, null, null, true);

//            HttpServiceTest httpServiceTest = new HttpServiceTest();
//            this.jsonStr = httpServiceTest.getJsonData("http://localhost/httpService/sendGetData?RayData=CurrTotlCnt", "utf-8");

//        } catch (IOException e) {
//            System.out.println(e.toString());
//        }
    }


    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        String systemCode = JsonObjectToAttach.config.get("systemCode").toString();
        String integrationKey = JsonObjectToAttach.config.get("integrationKey").toString();
        String urlInject = JsonObjectToAttach.config.get("urlInject").toString();
        String conCode = JsonObjectToAttach.config.get("conCode").toString();
        PullUtil pullUtil = new PullUtil(urlInject, conCode);
        OutParam outParam = new OutParam();

        //systemCode:对应《6.2术语解释》的SYSTEMCODE，一体化平台申请
        //integrationKey:集成客户端会自动使用MD5加密，由统一身份安全平台提供

        outParam = pullUtil.login(systemCode,integrationKey);
        if (outParam.getStatus() == 1) {
            System.out.println("登录成功");
        } else {
            System.out.println("登录失败");
            //模拟测试
            processPullInfo(null);
            return;
        }
        String tokenId = outParam.getTokenId();
        while (true) {
            outParam = pullUtil.pullTask(tokenId);
            if (outParam.getStatus() == 2) {
                System.out.println("暂时没有数据可以下拉");
                System.out.println("退出登录");
                break;
            } else if (outParam.getStatus() == 0) {
                System.out.println("拉取数据失败");
                break;
            }
            System.out.println("业务系统处理数据业务逻辑并入库");
            JSONObject data = JSONObject.parseObject(outParam.getData().toString());

            processPullInfo(data.toJSONString());

            System.out.println("将入库后的id主键返回并赋值给id");
            String id = "";
            try {
                JSONObject jsonObject = JSONObject.parseObject(data.get("data").toString());
                id = jsonObject.get("id").toString();
            } catch (Exception ex) {

            }
            outParam = pullUtil.pullFinish(outParam.getTokenId(), tokenId, id);
            if (outParam.getStatus() == 0) {
                System.out.println("下拉完成失败");
                break;
            } else {
                System.out.println("下拉完成成功一条");
            }
        }
        //注销token
        pullUtil.logout(tokenId);


    }

    private void processPullInfo(String jsonStr) {
        ExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        try {
//    this.jsonStr = httpServiceTest.getJsonData("http://localhost/httpService/sendGetData?RayData=CurrTotlCnt", "utf-8");
            Date insertDate = null;
            for (Map.Entry<String, String> m : topicS.entrySet()) {

                String[] tabAndMark = null;
                if (m.getValue().indexOf(",") >= 0) {
                    tabAndMark = m.getValue().split(",");
                }
//                String tableNm = tabAndMark == null ? m.getValue() : tabAndMark[0];
                if (tabAndMark.length < 4) {
                    continue;
                }
                String url = tabAndMark[3];


//                String pullJsonString = "{\n" +
//                        "  \"code\": \"1\",\n" +
//                        "  \"message\": \"success\",\n" +
//                        "  \"data\": {\n" +
//                        "    \"interrupt\": false,\n" +
//                        "    \"timestamp\": 1608537202840,\n" +
//                        "    \"taskId\": \"20201221154223919-14E7-734636690\",\n" +
//                        "    \"objectType\": \"TARGET_ACCOUNT\",\n" +
//                        "    \"objectCode\": \"testdemo_TargetAccount\",\n" +
//                        "    \"effectOn\": \"CREATED\",\n" +
//                        "    \"data\": {\n" +
//                        "      \"_user\": \"zhangsan\",\n" +
//                        "      \"_organization\": null,\n" +
//                        "      \"username\": \"zhangsan\",\n" +
//                        "      \"password\": null,\n" +
//                        "      \"fullname\": \"张三测试\",\n" +
//                        "      \"isDisabled\": false,\n" +
//                        "      \"isLocked\": false,\n" +
//                        "      \"createAt\": \"2020-12-21 15:42:23.000\",\n" +
//                        "      \"updateAt\": \"2020-12-21 15:42:23.000\",\n" +
//                        "      \"isSystem\": false,\n" +
//                        "      \"isPublic\": false,\n" +
//                        "      \"isMaster\": true,\n" +
//                        "      \"email\": \"zhangsan@crecg.com\",\n" +
//                        "      \"employeeNo\": null,\n" +
//                        "      \"mobile\": \"13247703738\",\n" +
//                        "      \"sex\": \"1\"\n" +
//                        "    },\n" +
//                        "    \"id\": \"20201221154223828-3236-DD9FB740B\"\n" +
//                        "  }\n" +
//                        "}";
                String pullJsonString = "{\n" +
                        "  \"code\": \"1\",\n" +
                        "  \"message\": \"success\",\n" +
                        "  \"data\": {\n" +
                        "    \"interrupt\": false,\n" +
                        "    \"timestamp\": 1608537202840,\n" +
                        "    \"taskId\": \"20201221154223919-14E7-734636690\",\n" +
                        "    \"objectType\": \"TARGET_ORGANIZATION\",\n" +
                        "    \"objectCode\": \"testdemo_TargetOragnization\",\n" +
                        "    \"effectOn\": \"CREATED\",\n" +
                        "    \"data\": {\n" +
                        "      \"parentId\": null,\n" +
                        "      \"fullName\": \"机构一\",\n" +
                        "      \"orgName\": \"test\",\n" +
                        "      \"isDisabled\": false,\n" +
                        "      \"createAt\": \"2020-12-21 15:42:23.000\",\n" +
                        "      \"updateAt\": \"2020-12-21 15:42:23.000\",\n" +
                        "      \"effectOn\": true,\n" +
                        "      \"orgFullName\": \"测试机构全名\"\n" +
                        "    },\n" +
                        "    \"id\": \"20201221154223828-3236-DD9FB740C\"\n" +
                        "  }\n" +
                        "}";

                if (StringUtils.isEmpty(jsonStr))
                    jsonStr = pullJsonString;

                String id = "";
                JSONObject data = null;
                try{
                    data = JSONObject.parseObject(jsonStr);
                    JSONObject jsonObject =JSONObject.parseObject(data.get("data").toString());
                    //插入id
                    id = jsonObject.get("id").toString();
                    JSONObject target = JSONObject.parseObject(jsonObject.get("data").toString());
                    target.put("id",id);
                    //替换bool值
                    JsonObjectToAttach.replaceBooleanString(target);
                    jsonObject.put("data",target);

                    data.put("data",jsonObject);

                    jsonStr = data.toJSONString();

                }catch (Exception ex){
                    ex.printStackTrace();
                }


                //String getJson = httpServiceTest.getJsonData(url, "utf-8", "RouteId", "", true);

                List<String> listJson = new ArrayList<>();
                //listJson.add(getJson);
                listJson.add(jsonStr);
                SaveDataStatic saveDataStatic = new SaveDataStatic(m.getKey(), tabAndMark == null ? m.getValue() : tabAndMark[0],
                        tabAndMark == null ? "false" : tabAndMark[1], tabAndMark == null ? "false" : tabAndMark[2],
                        listJson);
                executorService.execute(saveDataStatic);


            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
