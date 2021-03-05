package com.springboot.httpInterface.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.springboot.common.JsonObjectToAttach;
import com.springboot.common.KafkaProducer;
import com.springboot.common.Ret;
import com.springboot.httpInterface.entity.RyDataLarge;
import com.springboot.httpInterface.services.PersonService;
import com.springboot.httpInterface.services.RyDataLargeService;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import scala.annotation.meta.param;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Created by yzn00 on 2019/6/18.
 */

@RestController
@RequestMapping("httpService/")
public class HttpServiceTest {
    @Resource
    PersonService personService;
    @RequestMapping(value = "sendPostDataByMap", method = RequestMethod.POST)
    public String sendPostDataByMap(HttpServletRequest request, HttpServletResponse response) {
        String result = "调用Post(map)成功：数据是 " + "name:" + request.getParameter("name") + " city:" + request.getParameter("city");
        return JSON.toJSONString(result);
    }

    @RequestMapping(value = "sendPostDataByJson", method = RequestMethod.POST)
    public String sendPostDataByJson(HttpServletRequest request, HttpServletResponse response, @RequestBody String requestBody) {
        JSONObject jsonObject = JSONObject.parseObject(requestBody);
        String result = "调用Post(json)成功：数据是 " + "name:" + jsonObject.getString("name") + " city:" + jsonObject.getString("status");
        return JSON.toJSONString(result);
    }



    public Object getBusTest(String param){
        String jsonStr = "";
        Map <String,String> m = new HashMap();
        if(param.equals("routePlan")){ //公交测试数据-线路计划时间
             jsonStr = "[\n" +
                     "  {\n" +
                     "    \"RouteId\": 1,\n" +
                     "    \"RouteName\": \"XXX\",\n" +
                     "    \"RouteCode\": \"X001\",\n" +
                     "    \"Times\": [\n" +
                     "      {\n" +
                     "        \"Time\": \"10:00:00\",\n" +
                     "        \"PlateNum\": \"CNG-9876-1\",\n" +
                     "        \"VehNum\": \"CNG-9876-1\"\n" +
                     "      },\n" +
                     "      {\n" +
                     "        \"Time\": \"10:05:00\",\n" +
                     "        \"PlateNum\": \"CNG-9876-2\",\n" +
                     "        \"VehNum\": \"CNG-9876-2\"\n" +
                     "      }\n" +
                     "    ]\n" +
                     "  },\n" +
                     "  {\n" +
                     "    \"RouteId\": 2,\n" +
                     "    \"RouteName\": \"222\",\n" +
                     "    \"RouteCode\": \"X002\",\n" +
                     "    \"Times\": [\n" +
                     "      {\n" +
                     "        \"Time\": \"11:00:00\",\n" +
                     "        \"PlateNum\": \"CNG-9877-3\",\n" +
                     "        \"VehNum\": \"CNG-9877-6\"\n" +
                     "      },\n" +
                     "      {\n" +
                     "        \"Time\": \"12:05:00\",\n" +
                     "        \"PlateNum\": \"CNG-9877-4\",\n" +
                     "        \"VehNum\": \"CNG-9877-5\"\n" +
                     "      }\n" +
                     "    ]\n" +
                     "  }\n" +
                     "]";
        }else if(param.equals("vehicle")){ //公交测试数据-车辆信息
             jsonStr = "[{\n" +
                    "\"VehId\":1,\n" +
                    "\"VehNum\":\"XXX\",\n" +
                    "\"PlateNum\":\"X001\",\n" +
                    "\"OwnRoute\":{\n" +
                    "\"ID\":1,\n" +
                    "\"Code\":\"01\",\n" +
                    "\"Name\":\"Cargyi Gate\"\n" +
                    "},\n" +
                    "\"RunRoute\":{\n" +
                    "\"ID\":1,\n" +
                    "\"Code\":\"01\",\n" +
                    "\"Name\":\"Cargyi Gate\"\n" +
                    "}\n" +
                    "}]\n";
        }else if(param.equals("route")){ //公交测试数据-线路
             jsonStr = "[\n" +
                     "  {\n" +
                     "    \"ID\": 1,\n" +
                     "    \"Code\": \"01\",\n" +
                     "    \"Name\": \" Cargyi Gate\",\n" +
                     "    \"State\": 0,\n" +
                     "    \"DepartTime\": \"05:00:00\",\n" +
                     "    \"ReturnTime\": \"22:00:00\",\n" +
                     "    \"TicketPrice\": 0.0,\n" +
                     "    \"StartSite\": {\n" +
                     "      \"Name\": \"XX\",\n" +
                     "      \"Latitude\": 29,\n" +
                     "      \"Longitude\": 110\n" +
                     "    },\n" +
                     "    \"EndSite\": {\n" +
                     "      \"Name\": \"YY\",\n" +
                     "      \"Latitude\": 29,\n" +
                     "      \"Longitude\": 110\n" +
                     "    }\n" +
                     "  },\n" +
                     "  {\n" +
                     "    \"ID\": 2,\n" +
                     "    \"Code\": \"01\",\n" +
                     "    \"Name\": \" fffCargyi Gate\",\n" +
                     "    \"State\": 1,\n" +
                     "    \"DepartTime\": \"06:00:00\",\n" +
                     "    \"ReturnTime\": \"23:00:00\",\n" +
                     "    \"TicketPrice\": 0.0,\n" +
                     "    \"StartSite\": {\n" +
                     "      \"Name\": \"XX\",\n" +
                     "      \"Latitude\": 30,\n" +
                     "      \"Longitude\": 20\n" +
                     "    },\n" +
                     "    \"EndSite\": {\n" +
                     "      \"Name\": \"YY\",\n" +
                     "      \"Latitude\": 219,\n" +
                     "      \"Longitude\": 12\n" +
                     "    }\n" +
                     "  }\n" +
                     "]";
        }else if(param.equals("station")){ //公交测试数据-站点
             jsonStr = "[{\n" +
                    "  \"RouteId\": 2,\n" +
                    "  \"RouteName\": \"XXX\",\n" +
                    "  \"RouteCode\": \"X001\",\n" +
                    "  \"Sites\": {\n" +
                     "   \"Milage\":100,\n"+
                    "    \"Name\": \"S1\",\n" +
                    "    \"Direct\": 0,\n" +
                    "    \"Num\": 1,\n" +
                    "    \"Lat\": 16,\n" +
                    "    \"Lng\": 96,\n" +
                    "    \"Attr\": 0\n" +
                    "  },\n" +
                     "    \"Track\": \"96,16;96,15.8;95.8,17.2\"\n" +
                    "}]";
        }else if(param.equals("routeVehicle")){ //公交测试数据-线路车辆
             jsonStr = "[\n" +
                     "  {\n" +
                     "    \"Id\": 1,\n" +
                     "    \"PlateNum\": \" CNG-9876\",\n" +
                     "    \"VehNum\": \"CNG-9876\",\n" +
                     "    \"Position\": {\n" +
                     "      \"Lat\": 29,\n" +
                     "      \"Lng\": \"111.34\",\n" +
                     "      \"Direct\": 1,\n" +
                     "      \"Site\": 3\n" +
                     "    }\n" +
                     "  },\n" +
                     "  {\n" +
                     "    \"Id\": 2,\n" +
                     "    \"PlateNum\": \" CNG-9877\",\n" +
                     "    \"VehNum\": \"CNG-9877\",\n" +
                     "    \"Position\": {\n" +
                     "      \"Lat\": 30,\n" +
                     "      \"Lng\": \"111.34\",\n" +
                     "      \"Direct\": 1,\n" +
                     "      \"Site\": 4\n" +
                     "    }\n" +
                     "  }\n" +
                     "]";
        }else{//公交测试数据-令牌
            jsonStr = "{\"access_token\":\"ACCESS_TOKEN\",\"expires_in\":7200}";
        }
        JSONObject jsonObject = null;
        try {
            jsonObject =JSONObject.parseObject(jsonStr);
        }catch (Exception e){
            JSONArray jsonArray = JSONArray.parseArray(jsonStr);
            return jsonArray;
        }
        return jsonObject;
    }

    @Autowired
    RyDataLargeService ryDataLargeService;

    private List getResultTable(String param){
        List<Map> result = new ArrayList<>();

        if(param.indexOf("GuestStatus")>-1 && (
                param.split("#")[0].split(";")[0].equals("GuestStatus") ||
                        param.split(",")[0].split(";")[0].equalsIgnoreCase("GuestStatus") )){//嘉宾
            Map map = new HashedMap();
//            map.put("limit",1000);//返回最后1000条记录
            if(param.indexOf("#")<0) {
                map.put("checkTime", "2019-08-26");//写死8月26号
                if(param.split(",").length>2) {
                    map.put("start", param.split(",")[1]);
                    map.put("endrow", param.split(",")[2]);
                }
                if(param.split(",")[0].split(";").length>2){
                    map.put("BeginTime",param.split(",")[0].split(";")[1]);
                    map.put("EndTime",param.split(",")[0].split(";")[2]);
                }
            }else {
                String tmp = param.split("#")[1];
                if(tmp.indexOf(",")>-1 && tmp.split(",").length>2){
                    map.put("start",tmp.split(",")[1]);
                    map.put("endrow",tmp.split(",")[2]);
                    map.put("checkTime",tmp.split(",")[0]);
                }else
                    map.put("checkTime",tmp );
                if(param.split("#")[0].split(";").length>2){
                    map.put("BeginTime",param.split("#")[0].split(";")[1]);
                    map.put("EndTime",param.split("#")[0].split(";")[2]);
                }
            }

            List<Map> listGuest = ryDataLargeService.getAllGuest(map);
//            if(listGuest!=null && listGuest.size()>0)
//                result.add(new HashedMap(){{put("cnt",listGuest.get(0).get("cnt"));}});
            for(Map ma:listGuest){
                Map m = new HashedMap();
                m.put("id",ma.get("id"));
                m.put("cname",ma.get("cname"));
                m.put("ename",ma.get("ename"));
                m.put("roleType",ma.get("roleType"));
                m.put("certificateType",ma.get("certificateType"));
                m.put("certificateNum",ma.get("certificateNum"));
                m.put("certificateLevel",ma.get("certificateLevel"));
                m.put("gateStatus",ma.get("gateStatus"));

                result.add(m);
            }
        }
        //每个闸机当前进入人数
        else if(param.equals("CurrEnterCnt")){
            String gate = "";
            String currEnterCnt = "";
            Map map = new HashedMap();
            map.put("indexId",new ArrayList<String>(){{add("100001");}});
            List<RyDataLarge> listRyData = ryDataLargeService.getRyDataById(map);
            for(RyDataLarge y:listRyData){
                gate =y.getGroupNm();
                currEnterCnt = y.getIndexVal();
                Map <String,String> m = new HashMap();
                m.put("Gate",gate);
                m.put("Curr_Enter_Cnt",currEnterCnt);
                result.add(m);
            }

        }else if (param.equals("ParkPrecnQty")){//停车场当前车位预约中数量
            Map map = new HashedMap();
            map.put("indexId",new ArrayList<String>(){{add("102004");}});
            List<RyDataLarge> listRyData = ryDataLargeService.getRyDataById(map);
            String park = "";
            String precnQty = "";
            float qty = 0.0f;
            for(RyDataLarge y:listRyData){
//                park = y.getGroupNm();
                precnQty = y.getIndexVal();
//                Map <String,String> m = new HashMap();
//                m.put("Park",park);
//                m.put("Precn_Qty",precnQty);
//                result.add(m);
                try{
                    qty += Float.parseFloat(precnQty);
                }catch (Exception ep){

                }
            }
            Map <String,Object> m = new HashMap();
            m.put("Precn_Qty",qty);
            result.add(m);

        }else if (param.equals("CurrTotlCnt")){//当前闸机进入总人数
            String currTotlCnt = "";
            Map map = new HashedMap();
            map.put("indexId",new ArrayList<String>(){{add("100002");}});
            List<RyDataLarge> listRyData = ryDataLargeService.getRyDataById(map);
            for (RyDataLarge y :listRyData){
                Map <String,String> m = new HashMap();
                currTotlCnt =y.getIndexVal();
                m.put("Curr_Totl_Cnt",currTotlCnt);
                result.add(m);
            }

        }else if (param.equals("GateAccmEnterCnt")){//每个闸机当日累计进入人数
            String todayAccmEnterCnt = "";
            String gate = "";
            Map map = new HashedMap();
            map.put("indexId",new ArrayList<String>(){{add("101001");}});
            List<RyDataLarge> listRyData = ryDataLargeService.getRyDataById(map);
            for(RyDataLarge y:listRyData){
                todayAccmEnterCnt =y.getIndexVal();
                gate = y.getGroupNm();
                Map m = new HashedMap();
                m.put("Gate",gate);
                m.put("Today_Accm_Enter_Cnt",todayAccmEnterCnt);
                result.add(m);
            }

        }else if (param.equals("CertAccmEnterCnt")){//按证件当日累计进入人数-list
            Map map = new HashedMap();
            String todayAccmEnterCnt = "";
            String cert = "";
            map.put("indexId",new ArrayList<String>(){{add("101002");}});
            List<RyDataLarge> listRyData = ryDataLargeService.getRyDataById(map);
            for(RyDataLarge y:listRyData){
                todayAccmEnterCnt =y.getIndexVal();
                cert = y.getGroupNm();
                Map m = new HashedMap();
                m.put("Cert",cert);
                m.put("Today_Accm_Enter_Cnt",todayAccmEnterCnt);
                result.add(m);
            }

        }else if (param.equals("ParkTotlQty")){//停车位总数量-删除
//            m.put("Park","1");
//            m.put("Park_Totl_Qty","5");
//            result.add(m);
        }else if (param.equals("MetroInOutCnt")){//周边轨道交通情况-删除
//            m.put("Stat","1");
//            m.put("Metro_Enter_Cnt","6");
//            m.put("Metro_Leave_Cnt","1");
//            result.add(m);
        }else if (param.equals("NextDayWeat")){//天气情况
            Map map = new HashedMap();
            map.put("limit",1);//返回明天天气温度
            List<Map> listRoute = ryDataLargeService.getWeather(map);
            for(Map ma:listRoute){
                Map m = new HashedMap();
                m.put("Next_Day_Weat",ma.get("tempt"));
                result.add(m);
            }

        }else if (param.equals("Rout")){//周边交通路线
            Map map = new HashedMap();
            map.put("limit",1000);//返回最后1000条记录
            List<Map> listRoute = ryDataLargeService.getAllRoute(map);
            for(Map ma:listRoute){
                Map m = new HashedMap();
                m.put("Rout_Nm",ma.get("routeNm"));
                m.put("Term_Stat",ma.get("termStat"));
                result.add(m);
            }
        }else if (param.equals("ParkCurrUseQty")){//停车位当前使用数量
            String park = "";
            String parkCurrUseQty = "";
            Map map = new HashedMap();
            map.put("indexId",new ArrayList<String>(){{add("102003");}});
            List<RyDataLarge> listRyData = ryDataLargeService.getRyDataById(map);
            float qty = 0.0f;
            for(RyDataLarge y:listRyData){
                parkCurrUseQty =y.getIndexVal();
//                park = y.getGroupNm();
//                m.put("Park",park);
//                m.put("Park_Curr_Use_Qty",parkCurrUseQty);

                try{
                    qty+= Float.parseFloat(parkCurrUseQty);
                }catch (Exception ec){

                }
            }
            Map m = new HashedMap();
            m.put("Park_Curr_Use_Qty",qty);
            result.add(m);

        }else if (param.equals("ParkLicnEvent")){//停车场车辆进出数据
            Map map = new HashedMap();
            map.put("limit",10);//返回最后10条记录
            List<Map> listRyData = ryDataLargeService.getAllPark(map);
            for(Map ma:listRyData){
                Map m = new HashedMap();
                m.put("Licn",ma.get("platNo"));
                m.put("Enter_Tm",ma.get("drvInTm"));
                m.put("Leave_Tm",ma.get("startOutTm"));
                result.add(m);
            }
        }else if (param.equals("SecuQty")){//安保人员数量-9
            Map map = new HashedMap();
            String todayAccmEnterCnt = "";
            map.put("indexId",new ArrayList<String>(){{add("101002");}});
            map.put("groupId","9");//安保角色
            List<RyDataLarge> listRyData = ryDataLargeService.getRyDataById(map);
            for(RyDataLarge y:listRyData) {
                todayAccmEnterCnt = y.getIndexVal();
                Map m = new HashedMap();
                m.put("Secu_Qty",todayAccmEnterCnt);
                result.add(m);
            }

        }else if (param.equals("ExhiCnt")){//昨日、今日累计人数
            String yestdAccmCnt = "";
            String todayAccmCnt = "";
            Map map = new HashedMap();
            List<String> ids = new ArrayList<String>();
            ids.add("101003");
            ids.add("101004");
            map.put("indexId",ids);
            List<RyDataLarge> listRyData = ryDataLargeService.getRyDataById(map);
            Map m = new HashedMap();
            int i= 0;
            for(RyDataLarge y:listRyData){
                if(y.getIndexId().equalsIgnoreCase("101003")){
                    yestdAccmCnt =y.getIndexVal();
                    m.put("Yestd_Accm_Cnt",yestdAccmCnt);
                }
                else {
                    todayAccmCnt = y.getIndexVal();
                    m.put("Today_Accm_Cnt", todayAccmCnt);
                }
                i++;
                if(i%2==0) {
                    result.add(m);
                    m = new HashedMap();
                }
            }
        }else if (param.equals("TodayPrecnExhiCnt")){//当日预约观展人数
            String todayPrecnExhiCnt = "";
            Map map = new HashedMap();
            map.put("indexId",new ArrayList<String>(){{add("105001");}});
            List<RyDataLarge> listRyData = ryDataLargeService.getRyDataById(map);
            for(RyDataLarge y:listRyData){
                todayPrecnExhiCnt =y.getIndexVal();
                Map m = new HashedMap();
                m.put("Today_Precn_Exhi_Cnt",todayPrecnExhiCnt);
                result.add(m);
            }

        }else if (param.equals("TodayExhiCnt")){//当日观展人数
            String todayExhiCnt = "";
            Map map = new HashedMap();
            map.put("indexId",new ArrayList<String>(){{add("101004");}});
            List<RyDataLarge> listRyData = ryDataLargeService.getRyDataById(map);
            for(RyDataLarge y:listRyData){
                todayExhiCnt =y.getIndexVal();
                Map m = new HashedMap();
                m.put("Today_Exhi_Cnt",todayExhiCnt);
                result.add(m);
            }

        }else if (param.equals("StffTotlCnt")){//工作人员总数
            Map map = new HashedMap();
            map.put("indexId",new ArrayList<String>(){{add("107002");}});
            List<RyDataLarge> listRyData = ryDataLargeService.getRyDataById(map);
            for(RyDataLarge y:listRyData){
                String stffTotlCnt =y.getIndexVal();
                Map m = new HashedMap();
                m.put("Stff_Totl_Cnt",stffTotlCnt);
                result.add(m);
            }

        }else if (param.equals("CurrSecuQty")){//当前安保人员数量
            Map map = new HashedMap();
            map.put("indexId",new ArrayList<String>(){{add("107001");}});
            List<RyDataLarge> listRyData = ryDataLargeService.getRyDataById(map);
            for(RyDataLarge y:listRyData){
                String currSecuQty =y.getIndexVal();
                Map m = new HashedMap();
                m.put("Curr_Secu_Qty",currSecuQty);
                result.add(m);
            }

        }else if (param.equals("PubTrafCnt")){//观展人员到达方式（当日驾车人数、当日公共交通人数）
            Map m = new HashedMap();
            Map map = new HashedMap();
            map.put("indexId",new ArrayList<String>(){{add("105003");add("105004");}});
            List<RyDataLarge> listRyData = ryDataLargeService.getRyDataById(map);
            int k = 0;
            for(RyDataLarge y:listRyData){
                if(y.getIndexId().equalsIgnoreCase("105003")){
                    m.put("Today_Pub_Traf_Cnt",y.getIndexVal());
                }else {
                    m.put("Today_Driv_Cnt",y.getIndexVal());
                }
                k++;
                if(k%2==0){
                    result.add(m);
                    m = new HashedMap();
                }
            }

        }else if (param.equals("TmpStatQty")){//临时站点数量
            String tmpStatQty = "";
            Map map = new HashedMap();
            map.put("indexId",new ArrayList<String>(){{add("106001");}});
            List<RyDataLarge> listRyData = ryDataLargeService.getRyDataById(map);
            for(RyDataLarge y:listRyData){
                tmpStatQty =y.getIndexVal();
                Map m = new HashedMap();
                m.put("Tmp_Stat_Qty",tmpStatQty);
                result.add(m);
            }
        }else if (param.equals("TodayDispCnt")){//公交当日调度次数-删除
//            m.put("Today_Disp_Cnt","19");
//            result.add(m);
        }else if (param.equals("VenCurrCnt")){//各场馆实时人数-删除
//            m.put("Ven","1");
//            m.put("Curr_Cnt","20");
//            result.add(m);
        }else{//其它
            Map m = new HashedMap();
            m.put("Gate","0");
            m.put("Curr_Enter_Cnt","0");
            result.add(m);
        }
        return result;
    }

    /**
     * get请求传输数据
     *
     * @param url
     * @param encoding
     * @param id
     * @param value
     * @param needTrack
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    public String getJsonData(String url, String encoding,String id,String value,boolean needTrack) throws ClientProtocolException, IOException {
        String result = "";

        // 创建httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();

        // 创建get方式请求对象
//        HttpGet httpGet = new HttpGet(url);
        HttpPost post = new HttpPost(url);
        post.addHeader("Content-Type", "application/json");
        if(!StringUtils.isEmpty(id)){

            Map<String,String> map = new HashMap();

            if(needTrack)
                map.put("NeedTrack",Boolean.toString(needTrack));
            else
                map.put(id,value);

            //设置参数发送
//            List<BasicNameValuePair> pairs = new ArrayList<>();
//            for(Map.Entry<String,String> entry : map.entrySet())	         {
//                pairs.add(new BasicNameValuePair(entry.getKey(),entry.getValue()));
//            }
//            post.setEntity(new UrlEncodedFormEntity(pairs,"UTF-8"));
            post.setEntity(new StringEntity(JSONObject.toJSONString(map)));
        }


//        httpGet.addHeader("Content-type", "application/json");
        // 通过请求对象获取响应对象
//        CloseableHttpResponse response = httpClient.execute(httpGet);
        CloseableHttpResponse response = null;
        try{
            response = httpClient.execute(post);
            // 获取结果实体
            // 判断网络连接状态码是否正常(0--200都数正常)
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                result = EntityUtils.toString(response.getEntity(), "utf-8");
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try{
                httpClient.close();
                if(response!=null){
                    // 释放链接
                    response.close();
                }
            }catch (Exception ee){
                ee.printStackTrace();
            }
        }
        return  result.replaceAll("null","\"\"");
    }

    /**
     * raydatas数据接口，定时推送
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "sendGetData")
    public Ret sendGetData(HttpServletRequest request, HttpServletResponse response) {
        String result = "调用成功：数据是 " + "name:" + request.getParameter("name") + " city:" + request.getParameter("city");
        Map m = new HashMap();
        boolean isConstract = false;
        String param = request.getParameter("RayData");
        if(StringUtils.isEmpty(param)) {
            String startTime = request.getParameter("BeginTime");
            String endTime = request.getParameter("EndTime");
            param = request.getParameter("Contractor") + ";"+startTime + ";"+endTime +
                    (!StringUtils.isEmpty(request.getParameter("checkTime")) ? "#" + request.getParameter("checkTime") : "");
            String page =  request.getParameter("page");
            String pageCnt = request.getParameter("pagenum");

            try{
                int pg = Integer.parseInt(page);
                int cnt = Integer.parseInt(pageCnt);
                int start = (pg-1<0?0:pg-1)*cnt;
                int end = cnt*(pg<1?1:pg);
                param += ","+ start + ","+end;
            }catch (Exception e){

            }
            isConstract = true;
        }
        List mapLists = getResultTable(param);

//        List list = personService.selectAllPerson();
//        m.put("results",list);
        m.put("results",mapLists);
        m.put("status","ok");
//        if(isConstract && mapLists.size()>0) {
//            m.put("totalCount", ((Map) mapLists.get(0)).get("cnt"));
//            mapLists.remove(0);
//        }
//        String listJson = JSON.toJSONString(list);
//        return Ret.ok(listJson,"OK");
        return Ret.ok(m);
    }

    /**
     * 公交数据取得接口-定时取得
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "getBusTestData")
    public Object getBusTestData(HttpServletRequest request, HttpServletResponse response) {
        Map map = new HashedMap();
        try{
            //access token
           JSONObject jsonObject = (JSONObject) getBusTest(request.getParameter("bus"));
           return jsonObject;
        }catch (Exception e){
            map.put("results",getBusTest(request.getParameter("bus")));
        }
        return map;
    }


    /**
     * （对方=》生产者）定时推送数据
     * @param request
     * @param response
     * @param requestBody
     * @return
     */
    @RequestMapping(value = "getPostData",method=RequestMethod.POST)
    public Ret sendPostData(HttpServletRequest request, HttpServletResponse response, @RequestBody String requestBody) {
        Map m = new HashMap();
        String jsonString = "";
        String textCode ="";
        try {
            JSONObject jsonObject = JSONObject.parseObject(requestBody);
//            String [] b = JsonObjectToAttach.getJsonList(jsonObject.toJSONString());
//            for(int i = 0;i<b.length;i++){
//                String []a = JsonObjectToAttach.getColumsAndValues( JSONObject.parseObject(b[i]));
//                String c = JsonObjectToAttach.getPropertyRelation(a[0],"web_data_profil",null);
//                System.out.print(c);
//            }

            jsonString = jsonObject.toJSONString();
            textCode = (String) jsonObject.get("tx_code");
        }catch (Exception e){
            JSONArray jsonArray = JSONArray.parseArray(requestBody);
            jsonString = jsonArray.toJSONString();
        }

        //取得有效主题（全部生产者）
        Map<String,String> topicM = JsonObjectToAttach.getValidProperties("",null,textCode,false);
        if(topicM.keySet().size()>0) {
            ExecutorService executorService = Executors.newFixedThreadPool(6);
            for (Map.Entry<String, String> map : topicM.entrySet()) {
                KafkaProducer kafkaProducer = new KafkaProducer(map.getKey(), jsonString);
                executorService.execute(kafkaProducer);
            }
            executorService.shutdown();
        }

//        KafkaProducer kafkaProducer = new KafkaProducer("bingfu",jsonString);
//        kafkaProducer.run();
//
        m.put("results",jsonString);
        m.put("status","ok");
        return Ret.ok(m);
    }


    public String hello(HttpServletRequest request, HttpServletResponse response) throws IOException {
//        String result = "调用成功：数据是 " + "name:" + request.getParameter("name") + " city:" + request.getParameter("age");
        request.setCharacterEncoding("UTF-8");
        BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"));
        String line = "";
        String res = "";
        while (null != (line = br.readLine())) {
            res += line;
        }
        JSONArray array = JSONArray.parseArray(res);
        for (int i = 0; i < array.size(); i++) {
            JSONObject user = array.getJSONObject(i);
            System.out.println(String.format("name=%s age=%s", user.getString("name"), user.getString("age")));
        }
        response.setCharacterEncoding("utf-8");
        response.getWriter().append("Served at: ").append(res);
        return JSON.toJSONString(res);
    }


        protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            request.setCharacterEncoding("UTF-8");
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"));
            String line = "";
            String res = "";
            while (null != (line = br.readLine())) {
                res += line;
            }
            JSONArray array = JSONArray.parseArray(res);
            for (int i = 0; i < array.size(); i++) {
                JSONObject user = array.getJSONObject(i);
                System.out.println(String.format("name=%s age=%s", user.getString("name"), user.getString("age")));
            }
            response.setCharacterEncoding("utf-8");
            response.getWriter().append("Served at: ").append(res);
        }
    @RequestMapping(value = "hello", method = RequestMethod.POST)
        protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            doGet(request, response);
        }
//    }

}
