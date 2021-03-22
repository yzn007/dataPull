package com.springboot.httpInterface.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.springboot.common.JsonObjectToAttach;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;


/**
 * Created by yzn00 on 2019/6/18.
 */

@RestController
@RequestMapping("httpService/")
public class HttpServiceTest {
//    final String url = "http://10.2.38.90:9095/cn.crec.group.sso.integration.api.json";
    final String systemCode = JsonObjectToAttach.config.get("systemCode").toString();
    final  String integrationKey = JsonObjectToAttach.config.get("integrationKey").toString();
    final String urlInject = JsonObjectToAttach.config.get("urlInject").toString();
    final String conCode = JsonObjectToAttach.config.get("conCode").toString();
    final String operationCode = JsonObjectToAttach.config.get("operationCode").toString();
    @RequestMapping(value = "sendGetDataByMap", method = RequestMethod.GET)
    public String sendGetDataByMap(HttpServletRequest request, HttpServletResponse response) {
        String result = "调用失败";
        HttpSession httpSession = request.getSession();
        if(httpSession!=null){
            String code = request.getParameter("code");
            if(code!=null && !StringUtils.isEmpty(code))
                httpSession.setAttribute("code",code);
            else
                httpSession.removeAttribute("code");
            return JSON.toJSONString(httpSession);
        }
        try{

//            Map headMap = new HashedMap();
//            headMap.put("consumerCode",conCode);
//            headMap.put("operationCode",operationCode);
//            Map map = new HashedMap();
//            map.put("method","login");
//            JSONObject jsonUser = new JSONObject();
//            jsonUser.put("systemCode",systemCode);
//            jsonUser.put("integrationKey",integrationKey);
//            jsonUser.put("force",true);
//            jsonUser.put("timestamp",new Date().getTime());
//            map.put("request",jsonUser.toJSONString());
//            result = sendPostDataByMap(urlInject,headMap,map,"utf-8");
//            JSONObject jsonObject = JSONObject.parseObject(result);
            String tokenId = getTokenId();
            System.out.println(tokenId);
            result = "获取并注销token{"+tokenId+"}成功！";

            //注销tokenId
//            JSONObject jsonToken = new JSONObject();
//            jsonToken.put("tokenId",jsonObject.get("tokenId"));
//            jsonToken.put("timestamp",new Date().getTime());
//            map = new HashedMap();
//            map.put("method","logout");
//            map.put("request",jsonToken.toJSONString());
//            result = HttpServiceTest.sendPostDataByMap(urlInject,headMap,map,"utf-8");
//            System.out.println(result);
            destroyToken(tokenId);
        }catch (Exception o){
            o.printStackTrace();
        }
        return JSON.toJSONString(result);
    }

    @RequestMapping(value = "sendPostDataByJson", method = RequestMethod.POST)
    public String sendPostDataByJson(HttpServletRequest request, HttpServletResponse response, @RequestBody String requestBody) {
        JSONObject jsonObject = JSONObject.parseObject(requestBody);
        String result = "调用Post(json)成功：数据是 " + "name:" + jsonObject.getString("name") + " city:" + jsonObject.getString("status");
        return JSON.toJSONString(result);
    }

    /**
     * 获取tokenid
     * @return
     */
    public String getTokenId(){
        String tokenIdStr = "非法操作";
        try{
            Map headMap = new HashedMap();
            headMap.put("consumerCode",conCode);
            headMap.put("operationCode",operationCode);
            Map map = new HashedMap();
            map.put("method","login");
            JSONObject jsonUser = new JSONObject();
            jsonUser.put("systemCode",systemCode);
            jsonUser.put("integrationKey",integrationKey);
            jsonUser.put("force",true);
            jsonUser.put("timestamp",new Date().getTime());
            map.put("request",jsonUser.toJSONString());
            String result = sendPostDataByMap(urlInject,headMap,map,"utf-8");
            JSONObject jsonObject = JSONObject.parseObject(result);
            tokenIdStr = jsonObject.get("tokenId").toString();
//            System.out.println(tokenIdStr);
        }catch (IOException e){
            e.printStackTrace();
        }
        return tokenIdStr;
    }

    /**
     * 注销token
     * @param tokenId
     */
    public void destroyToken(String tokenId){
        try{
            //注销tokenId
            JSONObject jsonToken = new JSONObject();
            jsonToken.put("tokenId",tokenId);
            jsonToken.put("timestamp",new Date().getTime());
            Map map = new HashedMap();
            map.put("method","logout");
            map.put("request",jsonToken.toJSONString());
            Map headMap = new HashedMap();
            headMap.put("consumerCode",conCode);
            headMap.put("operationCode",operationCode);
            String result = HttpServiceTest.sendPostDataByMap(urlInject,headMap,map,"utf-8");
            JSONObject jsonObj = JSONObject.parseObject(result);
            System.out.println("注销tokenId{"+tokenId+"}："+jsonObj.get("success"));
        }catch (IOException e){
            e.printStackTrace();
        }
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
     * post请求
     * @param url
     * @param headMap
     * @param bodyMap
     * @param encoding
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static String sendPostDataByMap(String url,Map<String,String> headMap, Map<String, String> bodyMap, String encoding) throws ClientProtocolException, IOException {
        String result = "";

        // 创建httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        // 创建post方式请求对象
        HttpPost httpPost = new HttpPost(url);
        if(StringUtils.isEmpty(encoding))
            encoding="UTF-8";

        // 装填参数

        if(null!=headMap){
            List<Header> headers = new ArrayList<>();
            for (Map.Entry<String, String> entry : headMap.entrySet()) {
                headers.add(new Header() {
                    @Override
                    public String getName() {
                        return entry.getKey();
                    }

                    @Override
                    public String getValue() {
                        return entry.getValue();
                    }

                    @Override
                    public HeaderElement[] getElements() throws ParseException {
                        return this.getElements();
                    }
                });
            }
            Header [] headers1 = new Header[headers.toArray().length];
            int i = 0;
            for(Header h:headers){
                headers1[i++] = h;
            }
            httpPost.setHeaders(headers1);
        }
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        if (bodyMap != null) {
            for (Map.Entry<String, String> entry : bodyMap.entrySet()) {
                nameValuePairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
        }


        // 设置参数到请求对象中
        httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, encoding));

        // 设置header信息
        // 指定报文头【Content-type】、【User-Agent】
        httpPost.setHeader("Content-type", "application/x-www-form-urlencoded");
        httpPost.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

        // 执行请求操作，并拿到结果（同步阻塞）
        CloseableHttpResponse response = httpClient.execute(httpPost);
        // 获取结果实体
        // 判断网络连接状态码是否正常(0--200都数正常)
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            result = EntityUtils.toString(response.getEntity(), "utf-8");
        }
        // 释放链接
        response.close();

        return result;
    }

    public static void main(String []args){
        String result= null;
        http://10.81.106.123/bim-server/integration/api.json?method=login&request={"systemCode":"xxgtjc","integrationKey":"P@ssw0rd","force":true,"timestamp":1615366037000}
        try{
            final String url = "http://10.2.38.90:9095/cn.crec.group.sso.integration.api.json";
            Map headMap = new HashedMap();
            headMap.put("consumerCode","cn.crec.group.test");
            headMap.put("operationCode","cn.crec.group.sso.integration.api.json");
            Map map = new HashedMap();
            map.put("method","login");
            map.put("request","{\"systemCode\":\"xxgtjc\",\"integrationKey\":\"P@ssw0rd\",\"force\":true,\"timestamp\":1615341076000}");
            result = HttpServiceTest.sendPostDataByMap(url,headMap,map,"utf-8");
            JSONObject jsonObject = JSONObject.parseObject(result);
            System.out.println(jsonObject.get("tokenId"));
            //注销tokenId
            JSONObject jsonToken = new JSONObject();
            jsonToken.put("tokenId",jsonObject.get("tokenId"));
            jsonToken.put("timestamp",new Date().getTime());
            map = new HashedMap();
            map.put("method","logout");
            map.put("request",jsonToken.toJSONString());
            result = HttpServiceTest.sendPostDataByMap(url,headMap,map,"utf-8");
        }catch (IOException e){
            e.printStackTrace();
        }

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
    public static String getJsonData(String url, String encoding,String id,String value,boolean needTrack) throws ClientProtocolException, IOException {
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
