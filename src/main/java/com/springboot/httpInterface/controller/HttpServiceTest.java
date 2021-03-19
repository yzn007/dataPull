package com.springboot.httpInterface.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by yzn00 on 2019/6/18.
 */

@RestController
@RequestMapping("httpService/")
public class HttpServiceTest {
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
