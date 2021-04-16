package com.springboot.httpInterface.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.springboot.common.IgnoreSSL;
import com.springboot.common.JsonObjectToAttach;
import com.springboot.httpInterface.job.BBCJob;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.jasper.tagplugins.jstl.core.Url;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.net.ssl.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;


/**
 * Created by yzn00 on 2019/6/18.
 */

@RestController
@RequestMapping("httpService/")
public class HttpServiceTest {
    private static SSLContextBuilder builder = null;
    private static SSLConnectionSocketFactory sslsf = null;
    private static final String HTTP = "http";
    private static final String HTTPS = "https";
    private static PoolingHttpClientConnectionManager cm = null;
    static {
        try {
            builder = new SSLContextBuilder();
            // 全部信任 不做身份鉴定
            builder.loadTrustMaterial(null, new TrustStrategy() {
                @Override
                public boolean isTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                    return true;
                }
            });
            sslsf = new SSLConnectionSocketFactory(builder.build(), new String[]{"SSLv2Hello", "SSLv3", "TLSv1", "TLSv1.2"}, null, NoopHostnameVerifier.INSTANCE);
            Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                    .register(HTTP, new PlainConnectionSocketFactory())
                    .register(HTTPS, sslsf)
                    .build();
            cm = new PoolingHttpClientConnectionManager(registry);
            cm.setMaxTotal(200);//max connection
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
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

    public static CloseableHttpClient getHttpClient() throws Exception {
        CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLSocketFactory(sslsf)
                .setConnectionManager(cm)
                .setConnectionManagerShared(true)
                .build();
        return httpClient;
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
        }catch (Exception e){
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
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    /**
     * get模式获取数据
     * @param url
     * @param param
     * @return
     */
    public static String sendGet(String url, String param) throws Exception {
        String result = "";
        BufferedReader in = null;
//        HttpURLConnection con = null;

//        try {
            String urlNameString = url + (!StringUtils.isEmpty(param)? "?" + param:"");
//            URL realUrl = new URL(urlNameString);

            HttpGet httpGet = new HttpGet(urlNameString);
            httpGet.setHeader("Connection", "close");


//                trustAllHosts();
//            URLConnection connection = realUrl.openConnection();


//                HttpsURLConnection https = (HttpsURLConnection) realUrl.openConnection();
//                if (realUrl.getProtocol().toLowerCase().equals("https")) {
//                    https.setHostnameVerifier(DO_NOT_VERIFY);
//                    con = https;
//
//                } else {
//                    con = (HttpURLConnection) realUrl.openConnection();
//                }

                // 创建httpclient对象
                CloseableHttpClient httpClient = getHttpClient();

//                // 设置通用的请求属性
//                con.setRequestProperty("accept", "*/*");
//                con.setRequestProperty("connection", "Keep-Alive");
//                con.setRequestProperty("user-agent",
//                        "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
        try {
                HttpResponse httpResponse = httpClient.execute(httpGet);

                int statusCode = httpResponse.getStatusLine().getStatusCode();
//                System.out.println(statusCode);
                if (statusCode == HttpStatus.SC_OK) {
                    HttpEntity resEntity = httpResponse.getEntity();
                    result = EntityUtils.toString(resEntity);
                } else {
                    _log.info(httpResponse.toString());
                }
            }catch (Exception ee){
                throw ee;
            }

            // 获取所有响应头字段
//            Map<String, List<String>> map = con.getHeaderFields();
            // 遍历所有的响应头字段
//            for (String key : map.keySet()) {
//                System.out.println(key + "--->" + map.get(key));
//            }
            // 定义 BufferedReader输入流来读取URL的响应
//            in = new BufferedReader(new InputStreamReader(
//                    con.getInputStream()));
//            String line;
//            while ((line = in.readLine()) != null) {
//                result += line;
//            }


//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (httpClient != null) {
                    httpClient.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }

        }
        return result;
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
    public static String sendPostDataByMap(String url,Map<String,String> headMap, Map<String, String> bodyMap, String encoding) throws Exception {
        String result = "";

        // 创建httpclient对象
        CloseableHttpClient httpClient = getHttpClient();

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

//        HttpURLConnection con = null;
//        trustAllHosts();
//        URL httpsUrl = null;
//        try {
//            HttpsURLConnection https = (HttpsURLConnection) httpsUrl.openConnection();
//            if (httpsUrl.getProtocol().toLowerCase().equals("https")) {
//                https.setHostnameVerifier(DO_NOT_VERIFY);
//                con = https;
//
//            } else {
//                con = (HttpURLConnection) url.openConnection();
//            }
//        }catch (Exception ee){
//            throw ee;
//        }


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
        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    /**
     * 获取https的get接口
     * @param httpUrl
     * @param params
     * @return
     */
    public static String httpGet(String httpUrl,String params) throws Exception {
        BufferedReader input = null;
        StringBuilder sb = null;
        URL url = null;
        HttpURLConnection con = null;
        try {
            if(!StringUtils.isEmpty(params))
                url = new URL(httpUrl+"?"+params);
            else
                url = new URL(httpUrl);
            try {
                // trust all hosts
                trustAllHosts();
//                IgnoreSSL.ignoreSsl();
                HttpsURLConnection https = (HttpsURLConnection)url.openConnection();
                if (url.getProtocol().toLowerCase().equals("https")) {
                    https.setHostnameVerifier(DO_NOT_VERIFY);
                    con = https;
                } else {
                    con = (HttpURLConnection)url.openConnection();
                }
                input = new BufferedReader(new InputStreamReader(con.getInputStream()));
                sb = new StringBuilder();
                String s;
                while ((s = input.readLine()) != null) {
                    sb.append(s).append("\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
            throw e1;
        } finally {
            // close buffered
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }}

            // disconnecting releases the resources held by a connection so they may be closed or reused
            if (con != null) {
                con.disconnect();
            }
        }
        return sb == null ? null : sb.toString();
    }

    private static Logger _log = LoggerFactory.getLogger(HttpServiceTest.class);

    final static HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };
    /**
     * Trust every server - dont check for any certificate
     */
    private static void trustAllHosts() {
        final String TAG = "trustAllHosts";
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[] {};
            }
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
//                _log.info(TAG, "checkClientTrusted");
            }
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
//                _log.info(TAG, "checkServerTrusted");
            }
        } };
        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
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
