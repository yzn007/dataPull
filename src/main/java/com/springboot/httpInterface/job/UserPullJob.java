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

    //取得静态表
    static Map<String, String> topicS = new HashMap<>();

    final String topicName = UserPullJob.class.getSimpleName();

    HttpServiceTest httpServiceTest = null;

    public UserPullJob() {
        if (topicS.size() == 0)
            //取得静态表
            topicS = JsonObjectToAttach.getValidProperties(topicName, null, null, true);

    }


    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        String systemCode = JsonObjectToAttach.config.get("systemCode").toString();
        String integrationKey = JsonObjectToAttach.config.get("integrationKey").toString()+"1";
        String urlInject = JsonObjectToAttach.config.get("urlInject").toString();
        String conCode = JsonObjectToAttach.config.get("conCode").toString();
        PullUtil pullUtil = new PullUtil(urlInject, conCode);
        OutParam outParam = new OutParam();

        //systemCode:对应《6.2术语解释》的SYSTEMCODE，一体化平台申请
        //integrationKey:集成客户端会自动使用MD5加密，由统一身份安全平台提供
        outParam = pullUtil.login(systemCode, integrationKey);
        String tokenId = outParam.getTokenId();
        if (outParam.getStatus() == 1) {
            System.out.println("登录成功");
        } else {
            System.out.println("登录失败");
            //模拟测试
            try {
                processPullInfo(null);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("下拉完成失败，错误详见日志！");
                if(tokenId!=null && !StringUtils.isEmpty(tokenId)) {
                    System.out.println("\n注销token开始……");
                    pullUtil.logout(tokenId);
                    System.out.println("注销token结束……");
                }
            }
            return;
        }

        int i = 0;
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
//            System.out.println("业务系统处理数据业务逻辑并入库");
            JSONObject data = JSONObject.parseObject(outParam.getData().toString());

            try {
                processPullInfo(data.toJSONString());
            } catch (Exception e) {
                System.out.print(e.toString());
                System.out.println("下拉完成失败");
                System.out.println("\n注销token开始……");
                pullUtil.logout(tokenId);
                System.out.println("注销token结束……");
                break;
            }

//            System.out.println("将入库后的id主键返回并赋值给id");
            String id = "";
            JSONObject jsonObject = null;
            try {
                jsonObject = JSONObject.parseObject(data.toJSONString());
                id = jsonObject.get("systemOrgId").toString();
            } catch (Exception ex) {
                id = jsonObject.get("systemUserId").toString();
            }
            outParam = pullUtil.pullFinish(tokenId,outParam.getTaskId(), id);
            if (outParam.getStatus() == 0) {
                System.out.println("下拉完成失败");
                break;
            } else {
                System.out.println("\n");
//                System.out.printf("下拉完成成功一条{%d}",i);
            }
            if (i++ >= 200)//测试200条
                break;
        }
        System.out.printf("\n下拉完成成功{%d}条数据！", i>0?--i:i);
        //注销token
        System.out.println("\n注销token开始……");
        pullUtil.logout(tokenId);
        System.out.println("注销token结束……");

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

                String[] tabAndMark = null;
                if (m.getValue().indexOf(",") >= 0) {
                    tabAndMark = m.getValue().split(",");
                }
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
                        "  \"success\": true,\n" +
                        "  \"interrupt\": false,\n" +
                        "  \"timestamp\": 1615431765508,\n" +
                        "  \"taskId\": \"20210310162940670-2E26-DC7EEF45E\",\n" +
                        "  \"objectType\": \"TARGET_ORGANIZATION\",\n" +
                        "  \"objectCode\": \"xxgtjc_Org\",\n" +
                        "  \"effectOn\": \"CREATED\",\n" +
                        "\"data\":{\n" +
                        "  \"code\": \"20200715100151100-1459-5F4D21351\",\n" +
                        "  \"updateAt\": \"2021-03-10 16:30:11.000\",\n" +
                        "  \"type\": \"2\",\n" +
                        "  \"systemOrgId\": \"20200715100151100-1459-888888888\",\n" +
                        "  \"createAt\": \"2021-03-10 16:29:40.000\",\n" +
                        "  \"effectOn\": \"CREATED\",\n" +
                        "  \"objectType\": \"TARGET_ORGANIZATION\",\n" +
                        "  \"sequence\": 0,\n" +
                        "  \"_parent\": \"中国铁路工程集团有限公司/中国中铁股份有限公司\",\n" +
                        "  \"_organization\": \"中国铁路工程集团有限公司/中国中铁股份有限公司/所属分支机构\",\n" +
                        "  \"name\": \"所属分支机构\",\n" +
                        "  \"fullname\": \"所属分支机构\",\n" +
                        "  \"isDisabled\": false\n" +
                        "  },\n" +
                        "  \"id\": \"20210310162940633-88D5-C4FEAF2A9\"\n" +
                        "}";
                if (StringUtils.isEmpty(jsonStr)) {
                    jsonStr = pullJsonString;
                    String id = "";
                    JSONObject data = null;
                    try {
                        data = JSONObject.parseObject(jsonStr);
                        JSONObject jsonObject = JSONObject.parseObject(data.get("data").toString());
//                        //插入id
//                        id = data.get("id").toString();
//                        jsonObject.put("id", id);
                        //替换bool值
                        JsonObjectToAttach.replaceBooleanString(jsonObject);

                        data.put("data", jsonObject);

                        jsonStr = data.toJSONString();

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else {
                    JSONObject target = JSONObject.parseObject(jsonStr);
                    //替换bool值
                    JsonObjectToAttach.replaceBooleanString(target);
                    jsonStr = target.toJSONString();
                }

                List<String> listJson = new ArrayList<>();
                listJson.add(jsonStr);
                SaveDataStatic saveDataStatic = new SaveDataStatic(m.getKey(), tabAndMark == null ? m.getValue() : tabAndMark[0],
                        tabAndMark == null ? "false" : tabAndMark[1], tabAndMark == null ? "false" : tabAndMark[2],
                        listJson);
//                executorService.execute(saveDataStatic);
                saveDataStatic.run();


            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
    }
}
