package com.springboot.httpInterface.job;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.springboot.common.JsonObjectToAttach;
import com.springboot.common.ReadPropertiesUtils;
import com.springboot.common.SaveDataStatic;
import com.springboot.common.UpdTblProducter;
import com.springboot.httpInterface.controller.HttpServiceTest;
import com.springboot.httpInterface.entity.Route;
import com.springboot.httpInterface.entity.RouteVehicle;
import com.springboot.httpInterface.entity.Token;
import com.springboot.httpInterface.services.RouteService;
import com.springboot.httpInterface.services.RouteVehicleService;
import com.springboot.httpInterface.services.TokenService;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StaticJobPer1Min implements BaseJob {

    private static Logger _log = LoggerFactory.getLogger(StaticJobPer1Min.class);

    final static int NUM_PROCESS = 6;
    static Map<String, String> config = new HashMap<String, String>();

    //取得静态表
    static Map<String, String> topicS = new HashMap<>();

    private String jsonStr = "";
    private String table = "BUS_VEHIC_LCTN_MSG";
    //令牌地址
    static String accessUrl = "http://183.66.65.155:9002/api/Token?appid=001&secret=ABCDEFG";

    final String topicName = StaticJobPer1Min.class.getSimpleName();
    final String configName = "project.properties";

    @Autowired
    TokenService tokenService;

    private Token token;

    HttpServiceTest httpServiceTest = null;


    private Token getToken(){
//        if(token== null ){
//            List<Token> listT = tokenService.getAllToken();
//            if (listT != null && listT.size() > 0) {
//                token = listT.get(0);
//                if(token.getDateExp()==null){
//                    token.SetDateExp();
//                }
//            }
//        }
//        return token;
        try {

            for (Map.Entry<String, String> m : topicS.entrySet()) {
                if(!m.getKey().equalsIgnoreCase("BUS_ACCESS_TOKEN"))
                    continue;

                String[] tabAndMark = null;
                if (m.getValue().indexOf(",") >= 0) {
                    tabAndMark = m.getValue().split(",");
                }
//                String tableNm = tabAndMark == null ? m.getValue() : tabAndMark[0];
                if (tabAndMark.length < 4) {
                    continue;
                }
                String url = tabAndMark[3];
                if(!StringUtils.isEmpty(url))
                    accessUrl = url;
                JSONObject jsonObject = JSONObject.parseObject(httpServiceTest.getJsonData(accessUrl, "utf-8", "RouteId","",false));
                token = new Token();
                token.setAccessToken(jsonObject.get("access_token").toString());
                token.setDataTm(new Date());
                token.setExpiresIn(Integer.parseInt(jsonObject.get("expires_in").toString()));
                token.SetDateExp();

//                if(tabAndMark[3].indexOf("Token?")>-1){//获取公交访问令牌
                if (url.indexOf("Token") > -1) {//获取公交访问令牌
                    List<String> arrayJson = new ArrayList<>();
                    arrayJson.add(jsonObject.toJSONString());
                    SaveDataStatic saveDataStatic = new SaveDataStatic(m.getKey(), tabAndMark == null ? m.getValue() : tabAndMark[0],
                            tabAndMark == null ? "false" : tabAndMark[1], tabAndMark == null ? "false" : tabAndMark[2], arrayJson);

//                        executorService.execute(saveDataStatic);
                    saveDataStatic.run();
                }
                break;
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            return token;
        }
    }

    public StaticJobPer1Min() {
//        this.jsonStr = josonStr;
//        this.table = table;
        try {
            if (config.size() == 0)
                config.putAll(ReadPropertiesUtils.readConfig(configName));
            if (topicS.size() == 0)
                //取得静态表
                topicS = JsonObjectToAttach.getValidProperties(topicName, null, null, true);

//            HttpServiceTest httpServiceTest = new HttpServiceTest();
//            this.jsonStr = httpServiceTest.getJsonData("http://localhost/httpService/sendGetData?RayData=CurrTotlCnt", "utf-8");

        } catch (IOException e) {
            System.out.println(e.toString());
        }
    }

    @Autowired
    RouteService routeService;
    @Autowired
    RouteVehicleService routeVehicleService;

    @Override
    public void execute(JobExecutionContext context)
            throws JobExecutionException {
//        _log.error("New Job执行时间: " + new Date());

//
        ExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        if(httpServiceTest==null)
            httpServiceTest = new HttpServiceTest();
        try {
//    this.jsonStr = httpServiceTest.getJsonData("http://localhost/httpService/sendGetData?RayData=CurrTotlCnt", "utf-8");
            token = getToken();
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
//                if(tabAndMark[3].indexOf("Token?")>-1){//获取公交访问令牌
                if(url.indexOf("Token")>-1 ){//获取公交访问令牌

                }else if(url.indexOf("GetRoutes")>-1){//获取所有线路
                    if(url.indexOf("?")>-1 ) {
                        url += "&access_token=" + token.getAccessToken();
                    }else {
                        url += "?access_token=" + token.getAccessToken();
                    }

                    Map map =new HashedMap();
                    map.put("table_name",(tabAndMark == null ? m.getValue(): tabAndMark[0]));
                    routeService.deleAllRec(map);

                    List<String> listJson = new ArrayList<>();

                    String getJson = httpServiceTest.getJsonData(url, "utf-8","RouteId","",true);

                    int k = 0;
                    while(getJson.indexOf("errcode")>-1) {
                        //token失效，再次拉取
                        token = getToken();
                        getJson = httpServiceTest.getJsonData(url.substring(0,url.indexOf("access_token"))+"access_token="+token.getAccessToken(), "utf-8","RouteId","",true);
//                        token = null;
                        Thread.sleep(1000);
                        if(k++>30)
                            break;
                    }
                    if(getJson.indexOf("errcode")<0) {
                        listJson.add(getJson);
                        SaveDataStatic saveDataStatic = new SaveDataStatic(m.getKey(), tabAndMark == null ? m.getValue() : tabAndMark[0],
                                tabAndMark == null ? "false" : tabAndMark[1], tabAndMark == null ? "false" : tabAndMark[2], listJson);
                    executorService.execute(saveDataStatic);
//                        saveDataStatic.run();
                    }else {
                        System.out.println(url+"["+getJson+"]");
                    }
                }
                else if(url.indexOf("Token")<0){//除令牌外
                    Map map =new HashedMap();
                    //删除站点明细数据
                    if(tabAndMark[0].indexOf("cqyl_pre.BUS_STATION")>-1 ){
                        map.put("table_name","cqyl_pre.BUS_ROUTE_STATION");
                        routeService.deleAllRec(map);
                    }else if(tabAndMark[0].indexOf("cqyl_pre.BUS_ROUTE_DISPT")>-1){//删除
                        map.put("table_name","cqyl_pre.BUS_ROUTE_DISPT_DTL");
                        routeService.deleAllRec(map);
                    }

                    map.put("table_name",(tabAndMark == null ? m.getValue(): tabAndMark[0]));
                    routeService.deleAllRec(map);
                    if(url.indexOf("Vehicle/Info")<0){//车辆信息在线路信息后取得
                        List<Route> listR = routeService.getAllRoute();
                        int ret = 0;
                        while(listR==null ||listR.size()==0 ){
                            Thread.sleep(1000);
                            listR = routeService.getAllRoute();
                            if(ret ++ > 180)//三分钟
                                break;
                        }
                        insertDate = listR != null && listR.size()>0 ?listR.get(0).getDataTm():null;

                        if(url.indexOf("?")>-1 ) {
                            url += "&access_token=" + token.getAccessToken();
                        }else {
                            url += "?access_token=" + token.getAccessToken();
                        }
                        int k = 0;
                        List<String> listJson = new ArrayList<>();

                        for (Route r : listR) {
                            String getJson = httpServiceTest.getJsonData(url, "utf-8","RouteId",r.getId(),false);
                            int j = 0;
                            while(getJson.indexOf("errcode")>-1) {
                                //token失效
                                token = getToken();
//                                k--;
                                getJson = httpServiceTest.getJsonData(url.substring(0,url.indexOf("access_token"))+"access_token="+token.getAccessToken(), "utf-8","RouteId",r.getId(),false);
//                                saveDataStatic = new SaveDataStatic(m.getKey(), tabAndMark == null ? m.getValue() : tabAndMark[0],
//                                        tabAndMark == null || k++ > 0 ? "false" : tabAndMark[1], tabAndMark == null ? "false" : tabAndMark[2],
//                                        getJson);
//                            token = null;
                                if(j++>30)
                                    break;
                            }
                            if(getJson.indexOf("errcode")<0) {
                                if (url.indexOf("GetVehicles") > -1){//线路车辆，需要凭借线路Id，json没有返回
                                    JSONArray jsonArray = null;
                                    try{
                                        jsonArray = JSONObject.parseArray(getJson);
                                        for(Object obj:jsonArray){
                                            JSONObject jsonObject = (JSONObject)obj;
                                            if(null==jsonObject.get("RouteId")){
                                                jsonObject.put("RouteId",r.getId());
                                            }
                                        }
                                        getJson = JSONObject.toJSONString(jsonArray);
                                    }catch (Exception ee){
                                        //不是json数组的情况不处理
                                    }
                                }
                                listJson.add(getJson);
                            }
                            else
                                System.out.println(url+"["+getJson+"]");


                        }

                        SaveDataStatic saveDataStatic = new SaveDataStatic(m.getKey(), tabAndMark == null ? m.getValue() : tabAndMark[0],
                                tabAndMark == null || k++ > 0 ? "false" : tabAndMark[1], tabAndMark == null ? "false" : tabAndMark[2],
                                listJson);
//                        saveDataStatic.run();
                        executorService.execute(saveDataStatic);
                    }else{
                        List<RouteVehicle> routeVehicleList = routeVehicleService.getAllVehicle();
                        int rt = 0;
                        while (routeVehicleList== null ||routeVehicleList.size()==0){
                            Thread.sleep(1000);
                            routeVehicleList = routeVehicleService.getAllVehicle();
                            if(rt++ >180)//三分钟
                                break;
                        }

                        if(url.indexOf("?")>-1 ) {
                            url += "&access_token=" + token.getAccessToken();
                        }else {
                            url += "?access_token=" + token.getAccessToken();
                        }
                        int k = 0;
                        List<String> listJson = new ArrayList<>();
                        for(RouteVehicle rv:routeVehicleList){
                            String getJson = httpServiceTest.getJsonData(url, "utf-8","VehId",rv.getId(),false);

                            int j = 0;
                            while(getJson.indexOf("errcode")>-1) {
                                //token失效
                                token = getToken();
//                                k--;
                                getJson = httpServiceTest.getJsonData(url.substring(0,url.indexOf("access_token"))+"access_token="+token.getAccessToken(), "utf-8","VehId",rv.getId(),false);
//                                saveDataStatic = new SaveDataStatic(m.getKey(), tabAndMark == null ? m.getValue() : tabAndMark[0],
//                                        tabAndMark == null || k++ > 0 ? "false" : tabAndMark[1], tabAndMark == null ? "false" : tabAndMark[2],
//                                        getJson);
//                            token = null;
                                if(j++>30)
                                    break;
                            }
                            if(getJson.indexOf("errcode")<0)
                                listJson.add(getJson);
                            else
                                System.out.println(url+"["+getJson+"]");


                        }
                        SaveDataStatic saveDataStatic = new SaveDataStatic(m.getKey(), tabAndMark == null ? m.getValue() : tabAndMark[0],
                                tabAndMark == null || k++ > 0 ? "false" : tabAndMark[1], tabAndMark == null ? "false" : tabAndMark[2],
                                listJson);
//                        saveDataStatic.run();
                        executorService.execute(saveDataStatic);
                    }

                }
                //站点后执行写入更新信息
                if(m.getKey().equalsIgnoreCase("BUS_STATION")) {
                    UpdTblProducter updTblProducter = new UpdTblProducter(tabAndMark == null ? m.getValue() : tabAndMark[0], insertDate);
                    updTblProducter = null;
                }
            }

//            executorService.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}