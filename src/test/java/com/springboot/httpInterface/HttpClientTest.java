package com.springboot.httpInterface;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.springboot.httpInterface.entity.Person;
import com.springboot.httpInterface.services.PersonService;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


/**
 * Created by yzn00 on 2019/6/18.
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class HttpClientTest {
    @Autowired
    PersonService personService;
    /**
     * post请求传输map数据
     *
     * @param url
     * @param map
     * @param encoding
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static String sendPostDataByMap(String url, Map<String, String> map, String encoding) throws ClientProtocolException, IOException {
        String result = "";

        // 创建httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        // 创建post方式请求对象
        HttpPost httpPost = new HttpPost(url);

        // 装填参数
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        if (map != null) {
            for (Entry<String, String> entry : map.entrySet()) {
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

    /**
     * post请求传输json数据
     *
     * @param url
     * @param json
     * @param encoding
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    public static String sendPostDataByJson(String url, String json, String encoding) throws ClientProtocolException, IOException {
        String result = "";

        // 创建httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();

        // 创建post方式请求对象
        HttpPost httpPost = new HttpPost(url);

        // 设置参数到请求对象中
//        StringEntity stringEntity = new StringEntity(json, ContentType.APPLICATION_JSON);
        StringEntity stringEntity = new StringEntity(json,Charset.forName("UTF-8"));
        stringEntity.setContentEncoding("utf-8");

        try{
            JSONObject jsonObject = JSONObject.parseObject(json);
            stringEntity.setContentType("application/json; charset=UTF-8");
        }catch (Exception e){
            stringEntity.setContentType("application/x-www-form-urlencoded; charset=UTF-8");
        }

        httpPost.setEntity(stringEntity);

        // 执行请求操作，并拿到结果（同步阻塞）
        CloseableHttpResponse response = httpClient.execute(httpPost);

        // 获取结果实体
        // 判断网络连接状态码是否正常(0--200都数正常)
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            result = EntityUtils.toString(response.getEntity(), "utf-8");
        }
        // 释放链接
        response.close();
        httpClient.close();
        return result;
    }

    /**
     * get请求传输数据
     *
     * @param url
     * @param encoding
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     */
    public String sendGetData(String url, String encoding) throws ClientProtocolException, IOException {
        String result = "";

        // 创建httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();

        // 创建get方式请求对象
        HttpGet httpGet = new HttpGet(url);

        httpGet.addHeader("Content-type", "application/json");
        // 通过请求对象获取响应对象
        CloseableHttpResponse response = httpClient.execute(httpGet);

        // 获取结果实体
        // 判断网络连接状态码是否正常(0--200都数正常)
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            result = EntityUtils.toString(response.getEntity(), "utf-8");
        }
        // 释放链接
        response.close();

        return result;
    }


    public static String sendPostDataByJsonArray(String url, String json, String encoding) throws ClientProtocolException, IOException {
        String result = "";

        // 创建httpclient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();

        // 创建post方式请求对象
        HttpPost httpPost = new HttpPost(url);

        // 设置参数到请求对象中
//        StringEntity stringEntity = new StringEntity(json, ContentType.APPLICATION_JSON);
        StringEntity stringEntity = new StringEntity(json,Charset.forName("UTF-8"));
        stringEntity.setContentEncoding("utf-8");

        try{
            JSONObject jsonObject = JSONObject.parseObject(json);
            stringEntity.setContentType("application/json; charset=UTF-8");
        }catch (Exception e){
            stringEntity.setContentType("application/x-www-form-urlencoded; charset=UTF-8");
        }

        httpPost.setEntity(stringEntity);

        // 执行请求操作，并拿到结果（同步阻塞）
        CloseableHttpResponse response = httpClient.execute(httpPost);

        // 获取结果实体
        // 判断网络连接状态码是否正常(0--200都数正常)
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
            result = EntityUtils.toString(response.getEntity(), "utf-8");
        }
        // 释放链接
        response.close();
        httpClient.close();
        return result;
    }

    public static String post(String uri){
        String result = "";
        try{
            CloseableHttpClient httpclient = HttpClients.createDefault();
            HttpClientContext context = HttpClientContext.create();
            HttpPost httpPost = new HttpPost(uri);
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            nvps.add(new BasicNameValuePair("userid", "123"));
            nvps.add(new BasicNameValuePair("userpw", "123456"));
            nvps.add(new BasicNameValuePair("userjym", "1234"));
            //httpPost.setHeader("Accept", "*/*");
            httpPost.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
            httpPost.setHeader("Cookie", "JSESSIONID=0000ggAoZo_9hWdCE_rfuiva3ua:1970rfatb");
            //httpPost.setHeader("Cookie", "_gscu_1802614880=71314674hyhnt310; _gscu_198866649=71331570wvfe9p10; JSESSIONID=0000rqX2fBoKrVgaT1WCxBbjcLj:1970rfatb");
            httpPost.setHeader("Accept-Encoding","gzip,deflate");
            httpPost.setHeader("Accept-Language","zh-CN,zh;q=0.8");
            httpPost.setHeader("Cache-Control","max-age=0");
            httpPost.setHeader("Connection", "keep-alive");
            //httpPost.setHeader("Content-Length","27");
            httpPost.setHeader("Content-Type","application/x-www-form-urlencoded");
            httpPost.setHeader("Upgrade-Insecure-Requests","1");
            httpPost.setHeader("Host", "www.12333sh.gov.cn");
            httpPost.setHeader("Origin", "http://www.12333sh.gov.cn");
            httpPost.setHeader("Referer", "http://www.12333sh.gov.cn/sbsjb/wzb/129.jsp");
            httpPost.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.110 Safari/537.36");
            //httpPost.setHeader("User-Agent", "jakarta commons-httpclient/4.5");
            httpPost.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));
            CloseableHttpResponse response = httpclient.execute(httpPost, context);
            /*StringBuffer buffer = new StringBuffer();
            BufferedReader reader = null;
            // 定义BufferedReader输入流来读取URL的响应
            reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "utf-8"));
            String line = null;
            while ((line = reader.readLine()) != null){
                buffer.append(line);
            }
            result = buffer.toString();*/
            HttpEntity entity = response.getEntity();
            result = EntityUtils.toString(entity, "UTF-8");
            int status = response.getStatusLine().getStatusCode();

            System.out.println(result);
        }catch(ClientProtocolException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
        return result;
    }

    @Test
    public void testPost(){
        String url = "http://localhost:8080/httpService/sendPostDataByJson";
        Map<String, String> map = new HashMap<String, String>();
        List <Person>list = personService.selectAllPerson();
        String body = post(url);
//        String body = sendPostDataByJson(url, JSON.toJSONString(map), "utf-8");
        System.out.println("响应结果：" + body);
    }



    @Test
    public void testHello() {
        String ADD_URL = "http://localhost:8080/person/all";

        try {              //创建连接
            URL url = new URL(ADD_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.connect();
            //POST请求
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            JSONObject user = new JSONObject();
            user.put("name", "赵云");
            user.put("age", "20");
            JSONObject user2 = new JSONObject();
            user2.put("name", "马超");
            user2.put("age", "30");
            JSONArray userArray = new JSONArray();
            userArray.add(user);
            userArray.add(user2);
            out.write(userArray.toString().getBytes("UTF-8"));
            out.flush();
            out.close();
            //读取响应
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String lines;
            StringBuffer sb = new StringBuffer("");
            while ((lines = reader.readLine()) != null) {
                lines = new String(lines.getBytes(), "utf-8");
                sb.append(lines);
            }
            System.out.println(sb);
            reader.close();              // 断开连接
            connection.disconnect();
        } catch (MalformedURLException e) {              // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {              // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {              // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


    @Test
    public void testSendPostDataByMap() throws ClientProtocolException, IOException {
        int i = 0;
        while(1==1) {
            i++;
            String url = "http://localhost:8082/httpService/sendPostDataByMap";
            Map<String, String> map = new HashMap<String, String>();
            map.put("name", "wyj"+i);
            map.put("city", "南京"+i);
            String body = sendPostDataByMap(url, map, "utf-8");
            System.out.println("响应结果：" + body);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void testSendPostDataByJson() throws ClientProtocolException, IOException {
        String url = "http://localhost:8082/httpService/sendPostDataByJson";
        Map<String, String> map = new HashMap<String, String>();
        List <Person>list = personService.selectAllPerson();
//        map.put("name", "wyj");
//        map.put("city", "南京");
        list.forEach(person->{
            String body = null;
            try {
                body = sendPostDataByJson(url, JSON.toJSONString(person), "utf-8");
            } catch (IOException e) {
                e.printStackTrace();
            }
//        String body = sendPostDataByJson(url, JSON.toJSONString(map), "utf-8");
            System.out.println("响应结果：" + body);
        });
        String body = sendPostDataByJson(url, JSONArray.toJSONString(list),"utf-8");
//        String body = sendPostDataByJson(url, JSON.toJSONString(map), "utf-8");
        System.out.println("响应结果：" + body);
    }

    @Test
    public void testSendGetData() throws ClientProtocolException, IOException {
        String url = "http://localhost:8082/httpService/sendGetData?name=wyj&city=南京";
        String body = sendGetData(url, "utf-8");
        System.out.println("响应结果：" + body);
    }


}
